package controller.indexes;

import view.menu.IndicesMenuView;
import view.indexes.ImprovedMultilevelIndexView;
import view.ConfigurationDialog;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImprovedMultilevelIndexController {

    private ImprovedMultilevelIndexView view;
    private List<IndexRecord> dataRecords;
    private List<List<IndexLevel>> indexLevels;
    private IndicesMenuView indicesMenuView;
    private Object algorithmMenuView;

    // Technical parameters for realistic simulation
    private int blockSizeBytes = 1024;
    private int dataRecordSizeBytes = 64;
    private int indexRecordSizeBytes = 12;
    private int recordCount = 15;

    // Calculated values
    private int dataRecordsPerBlock;
    private int indexRecordsPerBlock;
    private int totalLevels;

    public ImprovedMultilevelIndexController(ImprovedMultilevelIndexView view) {
        this(view, null);
    }

    public ImprovedMultilevelIndexController(ImprovedMultilevelIndexView view, ConfigurationDialog.ConfigurationData initialConfig) {
        this.view = view;
        this.dataRecords = new ArrayList<>();
        this.indexLevels = new ArrayList<>();

        if (initialConfig != null) {
            recordCount = initialConfig.getRecordCount();
            blockSizeBytes = initialConfig.getBlockSizeBytes();
            dataRecordSizeBytes = initialConfig.getDataRecordSizeBytes();
            indexRecordSizeBytes = initialConfig.getIndexRecordSizeBytes();
        }

        calculateConfiguration();

        initComponents();

        if (initialConfig != null) {
            generateNewData(recordCount);
        } else {
            loadDataFromFile();
        }

        // Build all index levels dynamically
        buildAllIndexLevels();

        // Display data in tables
        displayDataInTables();

        // Display current configuration
        updateConfigurationDisplay();

        // Update input fields with current configuration
        updateInputFields();
    }

    /**
     * Calculates the configuration based on the current parameters
     */
    private void calculateConfiguration() {
        dataRecordsPerBlock = Math.max(1, blockSizeBytes / dataRecordSizeBytes);
        indexRecordsPerBlock = Math.max(1, blockSizeBytes / indexRecordSizeBytes);

        // Calculate total levels needed for the index
        totalLevels = MultilevelCalculator.calculateRequiredLevels(
                recordCount, dataRecordsPerBlock, indexRecordsPerBlock);

        System.out.println("=== CONFIGURACIÓN CALCULADA ===");
        System.out.println("- Registros totales: " + String.format("%,d", recordCount));
        System.out.println("- Tamaño del bloque: " + blockSizeBytes + " bytes");
        System.out.println("- Tamaño registro datos: " + dataRecordSizeBytes + " bytes");
        System.out.println("- Tamaño registro índice: " + indexRecordSizeBytes + " bytes");
        System.out.println("- Registros de datos por bloque: " + dataRecordsPerBlock);
        System.out.println("- Registros de índice por bloque: " + indexRecordsPerBlock);
        System.out.println("- Niveles de índice necesarios: " + (totalLevels - 1));
        System.out.println("- Niveles totales (incluyendo datos): " + totalLevels);

        // Print index structure
        MultilevelCalculator.printIndexStructure(recordCount, dataRecordsPerBlock, indexRecordsPerBlock);
    }

    /**
     * Builds all index levels dynamically based on the data records
     */
    private void buildAllIndexLevels() {
        indexLevels.clear();

        if (dataRecords.isEmpty()) {
            return;
        }

        System.out.println("\n=== Calculo ===");

        // Level 1: Índice primario (apunta a bloques de datos)
        List<IndexLevel> level1 = buildLevel1Index();
        indexLevels.add(level1);
        System.out.println("Nivel 1 construido: " + level1.size() + " entradas");

        // Build upper levels until the last level is smaller than indexRecordsPerBlock
        int currentLevel = 2;
        List<IndexLevel> currentLevelData = level1;

        while (currentLevelData.size() > indexRecordsPerBlock) {
            List<IndexLevel> nextLevel = buildUpperLevel(currentLevelData, currentLevel);
            indexLevels.add(nextLevel);
            System.out.println("Nivel " + currentLevel + " construido: " + nextLevel.size() + " entradas");

            currentLevelData = nextLevel;
            currentLevel++;
        }

        System.out.println("Construcción completada. Total de niveles de índice: " + indexLevels.size());
    }

    /**
     * Builda el primer nivel de índices (índice primario)
     */
    private List<IndexLevel> buildLevel1Index() {
        List<IndexLevel> level1 = new ArrayList<>();

        for (int i = 0; i < dataRecords.size(); i += dataRecordsPerBlock) {
            int endIndex = Math.min(i + dataRecordsPerBlock - 1, dataRecords.size() - 1);
            int blockId = i / dataRecordsPerBlock;
            int firstKey = dataRecords.get(i).getId();
            int lastKey = dataRecords.get(endIndex).getId();

            level1.add(new IndexLevel(1, blockId, firstKey, lastKey, i, "DATOS"));
        }

        return level1;
    }

    /**
     * Builds an upper level of indices based on the lower level
     */
    private List<IndexLevel> buildUpperLevel(List<IndexLevel> lowerLevel, int levelNumber) {
        List<IndexLevel> upperLevel = new ArrayList<>();

        for (int i = 0; i < lowerLevel.size(); i += indexRecordsPerBlock) {
            int endIndex = Math.min(i + indexRecordsPerBlock - 1, lowerLevel.size() - 1);
            int blockId = i / indexRecordsPerBlock;
            int firstKey = lowerLevel.get(i).getFirstKey();
            int lastKey = lowerLevel.get(endIndex).getLastKey();

            upperLevel.add(new IndexLevel(levelNumber, blockId, firstKey, lastKey, i,
                    "NIVEL_" + (levelNumber - 1)));
        }

        return upperLevel;
    }

    /**
     * Searches for a record in the multilevel index
     */
    private SearchResult performMultilevelSearch(int searchId, boolean animated) {
        SearchResult result = new SearchResult();
        result.levelAccesses = new ArrayList<>();

        // Inicialite accesss for each level
        for (int i = 0; i < indexLevels.size(); i++) {
            result.levelAccesses.add(0);
        }

        int currentPointer = -1;

        // Search through index levels from top to bottom
        for (int level = indexLevels.size() - 1; level >= 0; level--) {
            List<IndexLevel> currentLevel = indexLevels.get(level);

            if (animated) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Determine the range of records to search in this level
            int startIndex = (level == indexLevels.size() - 1) ? 0 : currentPointer;
            int endIndex = (level == indexLevels.size() - 1) ?
                    currentLevel.size() :
                    Math.min(startIndex + indexRecordsPerBlock, currentLevel.size());

            boolean found = false;
            for (int i = startIndex; i < endIndex; i++) {
                IndexLevel indexEntry = currentLevel.get(i);
                result.levelAccesses.set(level, result.levelAccesses.get(level) + 1);

                if (animated) {
                    highlightIndexLevel(level, i);
                    updateSearchProgress(String.format(
                            "Nivel %d - Acceso %d: Bloque %d (rango: %,d-%,d)",
                            level + 1, result.levelAccesses.get(level),
                            indexEntry.getBlockId(), indexEntry.getFirstKey(), indexEntry.getLastKey()));

                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (searchId >= indexEntry.getFirstKey() && searchId <= indexEntry.getLastKey()) {
                    currentPointer = indexEntry.getPointer();
                    found = true;

                    if (animated) {
                        updateSearchProgress(String.format(
                                "Nivel %d - Encontrado: Bloque %d (rango: %,d-%,d)",
                                level + 1, currentPointer, indexEntry.getTargetType()));
                    }
                    break;
                }
            }

            if (!found) {
                return result; // Not found in this level, exit early
            }
        }

        // Search in the data records block
        if (animated) {
            updateSearchProgress("Recorriendo registros de claves");
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int endData = Math.min(currentPointer + dataRecordsPerBlock, dataRecords.size());
        for (int i = currentPointer; i < endData; i++) {
            IndexRecord record = dataRecords.get(i);
            result.dataAccesses++;

            if (animated) {
                highlightDataRecord(i);
                updateSearchProgress(String.format(
                        "Datos - Acceso %d: Registro %d (ID: %,d)",
                        result.dataAccesses, i, record.getId()));

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (record.getId() == searchId) {
                result.record = record;
                return result;
            }
        }

        return result;
    }

    private void updateConfigurationDisplay() {
        String configInfo = String.format(
                "Configuración: %,d registros | Bloque=%d bytes | Datos=%d bytes | Índice=%d bytes | " +
                        "Reg/Bloque: Datos=%d, Índices=%d | Niveles de índice=%d",
                recordCount, blockSizeBytes, dataRecordSizeBytes, indexRecordSizeBytes,
                dataRecordsPerBlock, indexRecordsPerBlock, indexLevels.size()
        );
        view.setConfigurationInfo(configInfo);
    }

    private void updateInputFields() {
        view.setInputValues(
                String.valueOf(recordCount),
                String.valueOf(blockSizeBytes),
                String.valueOf(dataRecordSizeBytes),
                String.valueOf(indexRecordSizeBytes)
        );
    }

    // Auxiliary methods for highlighting and updating UI during search
    private void highlightIndexLevel(int level, int index) {
        if (level == 0) {
            view.highlightPrimaryIndex(index);
        } else if (level == indexLevels.size() - 1) {
            view.highlightSecondaryIndex(index);
        }
    }

    private void highlightDataRecord(int index) {
        if (index < 1000 || dataRecords.size() <= 50000) {
            view.highlightDataRecord(index);
        }
    }

    private void updateSearchProgress(String message) {
        SwingUtilities.invokeLater(() -> view.setSearchProgress(message));
    }

    private void displayDataInTables() {
        boolean isLargeDataset = dataRecords.size() > 50000;
        int maxDisplayRows = isLargeDataset ? 1000 : dataRecords.size();

        if (isLargeDataset) {
            view.setResultMessage("Dataset grande detectado (" + String.format("%,d", dataRecords.size()) +
                    " registros). Mostrando primeros " + maxDisplayRows + " para mejor rendimiento.", true);
        }

        // Display data records
        Object[][] dataTableData = new Object[Math.min(maxDisplayRows, dataRecords.size())][5];
        for (int i = 0; i < Math.min(maxDisplayRows, dataRecords.size()); i++) {
            IndexRecord record = dataRecords.get(i);
            int blockId = i / dataRecordsPerBlock;
            int positionInBlock = i % dataRecordsPerBlock;

            dataTableData[i][0] = blockId;
            dataTableData[i][1] = positionInBlock;
            dataTableData[i][2] = record.getId();
            dataTableData[i][3] = record.getName();
            dataTableData[i][4] = record.getAge();
        }
        view.setDataTableData(dataTableData);

        // Display index levels (mostrar solo los dos niveles más relevantes para la vista actual)
        if (!indexLevels.isEmpty()) {
            // Show the first level as primary index
            List<IndexLevel> level1 = indexLevels.get(0);
            Object[][] primaryTableData = new Object[level1.size()][7];
            for (int i = 0; i < level1.size(); i++) {
                IndexLevel index = level1.get(i);
                int recordsInBlock = Math.min(dataRecordsPerBlock, dataRecords.size() - index.getPointer());
                int blockUtilization = (recordsInBlock * dataRecordSizeBytes * 100) / blockSizeBytes;

                primaryTableData[i][0] = "L1-" + index.getBlockId();
                primaryTableData[i][1] = index.getFirstKey();
                primaryTableData[i][2] = index.getLastKey();
                primaryTableData[i][3] = index.getPointer();
                primaryTableData[i][4] = recordsInBlock;
                primaryTableData[i][5] = blockUtilization + "%";
                primaryTableData[i][6] = index.getTargetType();
            }
            view.setPrimaryIndexTableData(primaryTableData);

            // Show the secondary index level if it exists
            if (indexLevels.size() > 1) {
                List<IndexLevel> topLevel = indexLevels.get(indexLevels.size() - 1);
                Object[][] secondaryTableData = new Object[topLevel.size()][7];
                for (int i = 0; i < topLevel.size(); i++) {
                    IndexLevel index = topLevel.get(i);
                    int targetLevelSize = (indexLevels.size() > 1) ?
                            indexLevels.get(indexLevels.size() - 2).size() : level1.size();
                    int indexesInBlock = Math.min(indexRecordsPerBlock, targetLevelSize - index.getPointer());
                    int blockUtilization = (indexesInBlock * indexRecordSizeBytes * 100) / blockSizeBytes;

                    secondaryTableData[i][0] = "L" + indexLevels.size() + "-" + index.getBlockId();
                    secondaryTableData[i][1] = index.getFirstKey();
                    secondaryTableData[i][2] = index.getLastKey();
                    secondaryTableData[i][3] = index.getPointer();
                    secondaryTableData[i][4] = indexesInBlock;
                    secondaryTableData[i][5] = blockUtilization + "%";
                    secondaryTableData[i][6] = index.getTargetType();
                }
                view.setSecondaryIndexTableData(secondaryTableData);
            }
        }

        updateStatistics();
    }

    private void updateStatistics() {
        int totalDataBlocks = (int) Math.ceil((double) dataRecords.size() / dataRecordsPerBlock);
        int totalIndexBlocks = 0;

        for (List<IndexLevel> level : indexLevels) {
            totalIndexBlocks += (int) Math.ceil((double) level.size() / indexRecordsPerBlock);
        }

        double storageEfficiency = calculateStorageEfficiency();

        String stats = String.format(
                "Estadísticas: %,d registros | %d niveles índice | Bloques: %,d datos, %,d índices | Eficiencia: %.1f%%",
                dataRecords.size(), indexLevels.size(), totalDataBlocks, totalIndexBlocks, storageEfficiency
        );
        view.setStatistics(stats);
    }

    private double calculateStorageEfficiency() {
        int usedDataBytes = dataRecords.size() * dataRecordSizeBytes;
        int totalDataBlocks = (int) Math.ceil((double) dataRecords.size() / dataRecordsPerBlock);
        int allocatedDataBytes = totalDataBlocks * blockSizeBytes;

        return allocatedDataBytes > 0 ? (double) usedDataBytes / allocatedDataBytes * 100 : 0;
    }

    private void initComponents() {
        // Add action listeners to buttons
        view.addSearchListener(e -> performSearch());

        view.addConfigureListener(e -> {
            String recordCountInput = view.getRecordCount();
            String blockSizeInput = view.getBlockSizeBytes();
            String dataRecordSizeInput = view.getDataRecordSizeBytes();
            String indexRecordSizeInput = view.getIndexRecordSizeBytes();

            if (!recordCountInput.isEmpty() && !blockSizeInput.isEmpty() &&
                    !dataRecordSizeInput.isEmpty() && !indexRecordSizeInput.isEmpty()) {
                try {
                    int newRecordCount = Integer.parseInt(recordCountInput);
                    int newBlockSize = Integer.parseInt(blockSizeInput);
                    int newDataRecordSize = Integer.parseInt(dataRecordSizeInput);
                    int newIndexRecordSize = Integer.parseInt(indexRecordSizeInput);

                    // Validaciones básicas
                    if (newBlockSize < 32) {
                        view.setResultMessage("El tamaño del bloque debe ser al menos 32 bytes", false);
                        return;
                    }

                    if (newDataRecordSize < 4) {
                        view.setResultMessage("El tamaño del registro de datos debe ser al menos 4 bytes", false);
                        return;
                    }

                    if (newIndexRecordSize < 4) {
                        view.setResultMessage("El tamaño del registro de índice debe ser al menos 4 bytes", false);
                        return;
                    }

                    if (newRecordCount < 1) {
                        view.setResultMessage("El número de registros debe ser mayor a 0", false);
                        return;
                    }

                    if (newBlockSize < newDataRecordSize) {
                        view.setResultMessage("El bloque debe ser mas grande para contener los datos", false);
                        return;
                    }

                    if (newBlockSize < newIndexRecordSize) {
                        view.setResultMessage("El bloque debe ser mas grande para contener los indices", false);
                        return;
                    }

                    // Warning for large configurations
                    if (newRecordCount > 100000) {
                        view.setResultMessage("Configuración grande: " + String.format("%,d", newRecordCount) +
                                " registros. Puede tomar tiempo en cargar.", true);
                    }

                    // Update configuration
                    recordCount = newRecordCount;
                    blockSizeBytes = newBlockSize;
                    dataRecordSizeBytes = newDataRecordSize;
                    indexRecordSizeBytes = newIndexRecordSize;

                    calculateConfiguration();
                    updateConfigurationDisplay();

                    // Show calculated levels in the view
                    String levelInfo = String.format(
                            "Niveles calculados: %d índices + 1 datos = %d total | " +
                                    "Accesos máximos: %d | Mejora vs secuencial: %.1fx",
                            totalLevels - 1, totalLevels, totalLevels,
                            (double) recordCount / 2 / totalLevels
                    );
                    view.setLevelInfo(levelInfo);

                    // Generate new data if the record count is large
                    if (recordCount > 10000) {
                        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                publish("Generando " + String.format("%,d", recordCount) + " registros...");
                                generateNewData(recordCount);
                                publish("Construyendo " + (totalLevels - 1) + " niveles de índices...");
                                buildAllIndexLevels();
                                publish("Actualizando vista...");
                                return null;
                            }

                            @Override
                            protected void process(List<String> chunks) {
                                view.setResultMessage(chunks.get(chunks.size() - 1), true);
                            }

                            @Override
                            protected void done() {
                                displayDataInTables();
                                view.setResultMessage("Configuración aplicada exitosamente: " +
                                        String.format("%,d", recordCount) + " registros generados con " +
                                        (totalLevels - 1) + " niveles de índice", true);
                            }
                        };
                        worker.execute();
                    } else {
                        generateNewData(recordCount);
                        buildAllIndexLevels();
                        displayDataInTables();
                        view.setResultMessage("Configuración aplicada exitosamente", true);
                    }

                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese valores numéricos válidos", false);
                }
            } else {
                view.setResultMessage("Por favor complete todos los campos de configuración", false);
            }
        });

        view.addInsertRecordListener(e -> {
            String idInput = view.getInsertId();
            String nameInput = view.getInsertName();
            String ageInput = view.getInsertAge();

            if (!idInput.isEmpty() && !nameInput.isEmpty() && !ageInput.isEmpty()) {
                try {
                    int id = Integer.parseInt(idInput);
                    int age = Integer.parseInt(ageInput);

                    try {
                        insertRecord(id, nameInput, age);
                    } catch (IllegalArgumentException ex) {
                        view.setResultMessage(ex.getMessage(), false);
                    }
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese valores numéricos válidos para ID y edad", false);
                }
            } else {
                view.setResultMessage("Por favor complete todos los campos", false);
            }
        });

        view.addDeleteRecordListener(e -> {
            String input = view.getDeleteId();
            if (!input.isEmpty()) {
                try {
                    int id = Integer.parseInt(input);
                    deleteRecord(id);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese un ID numérico válido", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese un ID para eliminar", false);
            }
        });

        view.addBackListener(e -> goBack());
    }

    private void performSearch() {
        if (view.isVisualizationEnabled()) {
            performAnimatedSearch();
        } else {
            performNormalSearch();
        }
    }

    private void performNormalSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese un ID para buscar", false);
            return;
        }

        try {
            int searchId = Integer.parseInt(input);
            view.clearHighlights();

            SearchResult result = performMultilevelSearch(searchId, false);

            if (result.record != null) {
                int position = dataRecords.indexOf(result.record);
                if (position < 1000 || dataRecords.size() <= 50000) {
                    view.highlightFoundRecord(position);
                }

                int totalBytes = result.getTotalBytesRead(blockSizeBytes, dataRecordSizeBytes);
                view.setResultMessage(String.format(
                        "ID %,d encontrado: %s, %d años | Niveles: %d accesos | Datos: %d accesos | Total: %,d bytes",
                        searchId, result.record.getName(), result.record.getAge(),
                        result.levelAccesses.stream().mapToInt(Integer::intValue).sum(),
                        result.dataAccesses, totalBytes), true);
            } else {
                view.setResultMessage("Registro con ID " + searchId + " no encontrado", false);
            }

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese un ID numérico válido", false);
        }
    }

    private void performAnimatedSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese un ID para buscar", false);
            return;
        }

        try {
            int searchId = Integer.parseInt(input);
            view.clearHighlights();

            SwingWorker<SearchResult, String> worker = new SwingWorker<SearchResult, String>() {
                @Override
                protected SearchResult doInBackground() throws Exception {
                    publish("Iniciando búsqueda multinivel (" + (totalLevels - 1) + " niveles de índice)...");
                    Thread.sleep(1000);

                    return performMultilevelSearch(searchId, true);
                }

                @Override
                protected void process(List<String> chunks) {
                    String message = chunks.get(chunks.size() - 1);
                    view.setSearchProgress(message);
                }

                @Override
                protected void done() {
                    try {
                        SearchResult result = get();

                        if (result.record != null) {
                            int position = dataRecords.indexOf(result.record);

                            if (position < 1000 || dataRecords.size() <= 50000) {
                                view.highlightFoundRecord(position);
                            }

                            int totalBytes = result.getTotalBytesRead(blockSizeBytes, dataRecordSizeBytes);
                            int levelAccesses = result.levelAccesses.stream().mapToInt(Integer::intValue).sum();

                            view.setResultMessage(String.format(
                                    "ID %,d encontrado: %s, %d años | Niveles: %d accesos | Datos: %d accesos | " +
                                            "Total: %,d bytes | Mejora: %.1fx vs secuencial",
                                    result.record.getId(), result.record.getName(), result.record.getAge(),
                                    levelAccesses, result.dataAccesses, totalBytes,
                                    (double) (searchId / 10) * dataRecordSizeBytes / totalBytes), true);
                        } else {
                            view.clearHighlights();
                            view.setResultMessage("Registro no encontrado después de buscar en " +
                                    (totalLevels - 1) + " niveles", false);
                        }
                        view.setSearchProgress("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            worker.execute();

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese un ID numérico válido", false);
        }
    }

    private void loadDataFromFile() {
        dataRecords.clear();

        try {
            File file = new File("src/utilities/datos-indice-multinivel.txt");

            if (!file.exists()) {
                System.err.println("El archivo de datos no existe: " + file.getAbsolutePath());
                createDefaultData();
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        try {
                            int id = Integer.parseInt(parts[0].trim());
                            String name = parts[1].trim();
                            int age = Integer.parseInt(parts[2].trim());
                            dataRecords.add(new IndexRecord(id, name, age));
                        } catch (NumberFormatException e) {
                            System.err.println("Error al parsear línea: " + line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            createDefaultData();
        }

        Collections.sort(dataRecords, (r1, r2) -> Integer.compare(r1.getId(), r2.getId()));
    }

    private void createDefaultData() {
        dataRecords.clear();
        String[] names = {"Ana", "Carlos", "María", "José", "Laura", "Pedro", "Sofía", "Diego",
                "Carmen", "Roberto", "Elena", "Miguel", "Isabel", "Andrés", "Patricia"};

        for (int i = 0; i < recordCount; i++) {
            int id = (i + 1) * 10;
            String name = names[i % names.length] + (i + 1);
            int age = 20 + (i * 2);
            dataRecords.add(new IndexRecord(id, name, age));
        }

        saveDataToFile();
    }

    private void saveDataToFile() {
        try {
            File directory = new File("src/utilities");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File("src/utilities/datos-indice-multinivel.txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                for (IndexRecord record : dataRecords) {
                    writer.println(record.getId() + "," + record.getName() + "," + record.getAge());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateNewData(int recordCount) {
        dataRecords.clear();

        String[] names = {"Ana", "Carlos", "María", "José", "Laura", "Pedro", "Sofía", "Diego",
                "Carmen", "Roberto", "Elena", "Miguel", "Isabel", "Andrés", "Patricia",
                "Lucía", "Fernando", "Valeria", "Ricardo", "Natalia"};

        for (int i = 1; i <= recordCount; i++) {
            String name = names[(i - 1) % names.length];
            int age = 18 + (i % 50);
            dataRecords.add(new IndexRecord(i * 10, name + i, age));

            if (recordCount > 100000 && i % 50000 == 0) {
                System.out.println("Generados " + String.format("%,d", i) + " de " + String.format("%,d", recordCount) + " registros...");
            }
        }

        Collections.sort(dataRecords, (r1, r2) -> Integer.compare(r1.getId(), r2.getId()));
        saveDataToFile();
    }

    public void insertRecord(int id, String name, int age) throws IllegalArgumentException {
        for (IndexRecord record : dataRecords) {
            if (record.getId() == id) {
                throw new IllegalArgumentException("El ID " + id + " ya existe en los registros");
            }
        }

        dataRecords.add(new IndexRecord(id, name, age));
        Collections.sort(dataRecords, (r1, r2) -> Integer.compare(r1.getId(), r2.getId()));

        // Recalculate levels and configuration
        calculateConfiguration();

        saveDataToFile();
        buildAllIndexLevels();
        displayDataInTables();

        view.setResultMessage("Registro insertado correctamente. Niveles recalculados: " + (totalLevels - 1), true);
    }

    public void deleteRecord(int id) {
        boolean found = false;
        for (int i = 0; i < dataRecords.size(); i++) {
            if (dataRecords.get(i).getId() == id) {
                dataRecords.remove(i);
                found = true;
                break;
            }
        }

        if (found) {
            // Recalcute the configuration and levels
            recordCount = dataRecords.size();
            calculateConfiguration();

            saveDataToFile();
            buildAllIndexLevels();
            displayDataInTables();
            view.setResultMessage("Registro eliminado correctamente. Niveles recalculados: " + (totalLevels - 1), true);
        } else {
            view.setResultMessage("Registro con ID " + id + " no encontrado", false);
        }
    }

    private void goBack() {
        view.dispose();

        // Show the parent view based on navigation configuration
        if (indicesMenuView != null) {
            indicesMenuView.setVisible(true);
        } else if (algorithmMenuView != null) {
            try {
                algorithmMenuView.getClass().getMethod("setVisible", boolean.class).invoke(algorithmMenuView, true);
            } catch (Exception e) {
                System.err.println("Error calling setVisible on algorithmMenuView: " + e.getMessage());
            }
        } else {
            // Fallback: si no hay navegación configurada, mostrar mensaje
            System.out.println("Cerrando vista de índices multinivel - no hay vista padre configurada");
        }
    }

    // Setter methods for navigation
    public void setIndicesMenuView(IndicesMenuView indicesMenuView) {
        this.indicesMenuView = indicesMenuView;
    }

    public void setAlgorithmMenuView(Object algorithmMenuView) {
        this.algorithmMenuView = algorithmMenuView;
    }

    /**
     * Class for calculating multilevel index structure
     */
    public static class MultilevelCalculator {
        public static int calculateRequiredLevels(int totalRecords,
                                                  int dataRecordsPerBlock,
                                                  int indexRecordsPerBlock) {
            if (totalRecords <= 0) return 1;

            int currentBlocks = (int) Math.ceil((double) totalRecords / dataRecordsPerBlock);
            int levels = 1;

            while (currentBlocks > indexRecordsPerBlock) {
                currentBlocks = (int) Math.ceil((double) currentBlocks / indexRecordsPerBlock);
                levels++;
            }

            return levels;
        }

        public static void printIndexStructure(int totalRecords,
                                               int dataRecordsPerBlock,
                                               int indexRecordsPerBlock) {
            System.out.println("\n=== ESTRUCTURA DE ÍNDICES MULTINIVEL ===");

            int currentBlocks = (int) Math.ceil((double) totalRecords / dataRecordsPerBlock);
            int level = 0;

            System.out.printf("Nivel %d (Datos): %,d registros en %,d bloques%n",
                    level, totalRecords, currentBlocks);

            level = 1;
            while (currentBlocks > indexRecordsPerBlock) {
                int indexEntries = currentBlocks;
                currentBlocks = (int) Math.ceil((double) currentBlocks / indexRecordsPerBlock);

                System.out.printf("Nivel %d (Índice): %,d entradas en %,d bloques%n",
                        level, indexEntries, currentBlocks);
                level++;
            }

            if (currentBlocks > 1) {
                System.out.printf("Nivel %d (Índice Raíz): %,d entradas en 1 bloque%n",
                        level, currentBlocks);
            }

            System.out.printf("Total de niveles: %d%n", level + 1);
            System.out.println("=======================================\n");
        }
    }

    /**
     * Class to represent an index level in the multilevel index structure
     */
    public static class IndexLevel {
        private int level;
        private int blockId;
        private int firstKey;
        private int lastKey;
        private int pointer;
        private String targetType;

        public IndexLevel(int level, int blockId, int firstKey, int lastKey, int pointer, String targetType) {
            this.level = level;
            this.blockId = blockId;
            this.firstKey = firstKey;
            this.lastKey = lastKey;
            this.pointer = pointer;
            this.targetType = targetType;
        }

        // Getters
        public int getLevel() { return level; }
        public int getBlockId() { return blockId; }
        public int getFirstKey() { return firstKey; }
        public int getLastKey() { return lastKey; }
        public int getPointer() { return pointer; }
        public String getTargetType() { return targetType; }
    }

    /**
     * Results of a search operation in the multilevel index
     */
    public static class SearchResult {
        public IndexRecord record = null;
        public List<Integer> levelAccesses = new ArrayList<>();
        public int dataAccesses = 0;

        public int getTotalAccesses() {
            return levelAccesses.stream().mapToInt(Integer::intValue).sum() + dataAccesses;
        }

        public int getTotalBytesRead(int blockSize, int dataRecordSize) {
            int indexBytes = levelAccesses.stream().mapToInt(Integer::intValue).sum() * blockSize;
            int dataBytes = dataAccesses * dataRecordSize;
            return indexBytes + dataBytes;
        }
    }

    /**
     * Class to represent a record in the index data
     */
    public static class IndexRecord {
        private int id;
        private String name;
        private int age;

        public IndexRecord(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getAge() { return age; }
    }
}
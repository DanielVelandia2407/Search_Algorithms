package controller.indexes;

import view.menu.IndicesMenuView;
import view.indexes.ImprovedMultilevelIndexView;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImprovedMultilevelIndexController {

    private ImprovedMultilevelIndexView view;
    private List<SimpleRecord> dataRecords;
    private List<List<SimpleIndex>> indexLevels; // Múltiples niveles de índices
    private IndicesMenuView indicesMenuView;

    // Parámetros de configuración
    private int recordCount = 15;
    private int blockSize = 1024;
    private int dataRecordSize = 64;
    private int indexRecordSize = 12;
    private int indexPerBlock = 85;
    private int dataPerBlock = 16;

    public ImprovedMultilevelIndexController(ImprovedMultilevelIndexView view) {
        this.view = view;
        this.dataRecords = new ArrayList<>();
        this.indexLevels = new ArrayList<>();

        initComponents();
        generateInitialData();
        buildMultilevelIndexes();
        displayDataInTables();
    }

    private void initComponents() {
        view.addConfigureListener(e -> configureStructure());
        view.addInsertRecordListener(e -> insertRecord());
        view.addSearchListener(e -> performSearch());
        view.addDeleteRecordListener(e -> deleteRecord());
        view.addBackListener(e -> goBack());
    }

    private void generateInitialData() {
        dataRecords.clear();

        String[] names = {"Ana", "Carlos", "María", "José", "Laura", "Pedro", "Sofía", "Diego",
                "Carmen", "Roberto", "Elena", "Miguel", "Isabel", "Andrés", "Patricia"};

        for (int i = 0; i < recordCount; i++) {
            int id = (i + 1) * 10; // IDs: 10, 20, 30, ...
            String name = names[i % names.length];
            if (i >= names.length) {
                name += (i / names.length + 1);
            }
            dataRecords.add(new SimpleRecord(id, name, 20 + (i % 40)));
        }

        Collections.sort(dataRecords, (r1, r2) -> Integer.compare(r1.getId(), r2.getId()));
    }

    private void buildMultilevelIndexes() {
        indexLevels.clear();

        if (dataRecords.isEmpty()) {
            return;
        }

        System.out.println("\n=== CONSTRUCCIÓN DE ÍNDICES MULTINIVEL ===");
        System.out.println("Registros de datos: " + dataRecords.size());
        System.out.println("Registros de datos por bloque: " + dataPerBlock);
        System.out.println("Registros de índice por bloque: " + indexPerBlock);

        // Calcular número de entradas para el nivel 1 basado en registros de datos
        // Cada registro necesita una entrada en el índice (peor caso para índice secundario)
        int dataBlocks = (int) Math.ceil((double) dataRecords.size() / dataPerBlock);
        System.out.println("Bloques de datos necesarios: " + dataBlocks);

        // Nivel 1: Una entrada por cada registro (simulando índice secundario)
        List<SimpleIndex> level1 = buildLevel1IndexFixed();
        indexLevels.add(level1);
        System.out.println("Nivel 1 construido: " + level1.size() + " entradas (una por registro)");

        // Construir niveles superiores basándose en el número de registros del nivel anterior
        int currentLevelRecords = level1.size();
        int levelNumber = 2;

        // Continuar mientras necesitemos más de un bloque para el nivel actual
        while (currentLevelRecords > indexPerBlock) {
            // Calcular cuántos bloques necesita el nivel actual
            int currentLevelBlocks = (int) Math.ceil((double) currentLevelRecords / indexPerBlock);
            System.out.println("Nivel " + (levelNumber-1) + " tiene " + currentLevelRecords +
                    " registros y necesita " + currentLevelBlocks + " bloques");

            // Crear el siguiente nivel con una entrada por cada bloque del nivel anterior
            List<SimpleIndex> nextLevel = buildUpperLevelFixed(currentLevelRecords, levelNumber);
            indexLevels.add(nextLevel);

            System.out.println("Nivel " + levelNumber + " construido: " + nextLevel.size() + " entradas");

            // Actualizar para el siguiente ciclo
            currentLevelRecords = nextLevel.size();
            levelNumber++;

            // Protección contra bucles infinitos
            if (levelNumber > 10) {
                System.out.println("ADVERTENCIA: Deteniendo construcción en nivel 10");
                break;
            }
        }

        System.out.println("Nivel final (" + (levelNumber-1) + ") tiene " + currentLevelRecords +
                " registros que caben en 1 bloque (≤ " + indexPerBlock + ")");
        System.out.println("Total de niveles de índice: " + indexLevels.size());

        // Recrear paneles en la vista
        view.recreateIndexPanels(indexLevels.size());
    }

    private List<SimpleIndex> buildLevel1IndexFixed() {
        List<SimpleIndex> level1 = new ArrayList<>();

        // Crear una entrada de índice por cada registro (simulando índice secundario)
        // Esto simula el peor caso donde cada registro tiene un valor único de clave secundaria
        for (int i = 0; i < dataRecords.size(); i++) {
            int recordId = dataRecords.get(i).getId();
            level1.add(new SimpleIndex(recordId, i, "DATOS"));
        }

        return level1;
    }

    private List<SimpleIndex> buildUpperLevelFixed(int lowerLevelRecords, int levelNumber) {
        List<SimpleIndex> upperLevel = new ArrayList<>();

        // Calcular cuántos bloques necesita el nivel inferior
        int lowerLevelBlocks = (int) Math.ceil((double) lowerLevelRecords / indexPerBlock);

        // Crear una entrada por cada bloque del nivel inferior
        for (int blockIndex = 0; blockIndex < lowerLevelBlocks; blockIndex++) {
            int startPosition = blockIndex * indexPerBlock;

            // Obtener el primer valor del bloque (si existe)
            if (startPosition < indexLevels.get(levelNumber - 2).size()) {
                int firstValue = indexLevels.get(levelNumber - 2).get(startPosition).getValue();
                String targetType = "NIVEL_" + (levelNumber - 1);
                upperLevel.add(new SimpleIndex(firstValue, startPosition, targetType));
            }
        }

        return upperLevel;
    }

    // Método legacy mantenido para compatibilidad (no se usa)
    private List<SimpleIndex> buildLevel1Index() {
        List<SimpleIndex> level1 = new ArrayList<>();

        // Crear índices que apuntan a bloques de datos
        for (int i = 0; i < dataRecords.size(); i += dataPerBlock) {
            int firstRecordId = dataRecords.get(i).getId();
            level1.add(new SimpleIndex(firstRecordId, i, "DATOS"));
        }

        return level1;
    }

    // Método legacy mantenido para compatibilidad (no se usa)
    private List<SimpleIndex> buildUpperLevelExact(List<SimpleIndex> lowerLevel, int levelNumber, int indexesPerBlock) {
        List<SimpleIndex> upperLevel = new ArrayList<>();

        // Crear una entrada de índice por cada bloque del nivel inferior
        for (int i = 0; i < lowerLevel.size(); i += indexesPerBlock) {
            int firstIndexValue = lowerLevel.get(i).getValue();
            String targetType = "NIVEL_" + (levelNumber - 1);
            upperLevel.add(new SimpleIndex(firstIndexValue, i, targetType));
        }

        return upperLevel;
    }

    // Método legacy mantenido para compatibilidad (no se usa)
    private List<SimpleIndex> buildUpperLevel(List<SimpleIndex> lowerLevel, int levelNumber) {
        return buildUpperLevelExact(lowerLevel, levelNumber, indexPerBlock);
    }

    private void displayDataInTables() {
        System.out.println("\n=== MOSTRANDO DATOS EN TABLAS ===");

        // Mostrar datos
        Object[][] dataTableData = new Object[dataRecords.size()][3];
        for (int i = 0; i < dataRecords.size(); i++) {
            SimpleRecord record = dataRecords.get(i);
            dataTableData[i][0] = record.getId();
            dataTableData[i][1] = record.getName();
            dataTableData[i][2] = record.getAge();
        }
        view.setDataTableData(dataTableData);
        System.out.println("Datos mostrados: " + dataRecords.size() + " registros");

        // Mostrar todos los niveles de índices
        System.out.println("Niveles de índices disponibles: " + indexLevels.size());

        for (int level = 0; level < indexLevels.size(); level++) {
            List<SimpleIndex> currentLevelIndexes = indexLevels.get(level);
            Object[][] indexTableData = new Object[currentLevelIndexes.size()][2];

            System.out.println("Procesando nivel " + (level + 1) + " con " + currentLevelIndexes.size() + " entradas");

            for (int i = 0; i < currentLevelIndexes.size(); i++) {
                SimpleIndex index = currentLevelIndexes.get(i);
                indexTableData[i][0] = index.getValue();

                if (level == 0) {
                    // Nivel 1 apunta directamente a registros de datos
                    indexTableData[i][1] = "R" + (index.getPointer() + 1);
                } else {
                    // Niveles superiores apuntan a bloques de índices del nivel inferior
                    int blockNumber = (index.getPointer() / indexPerBlock) + 1;
                    indexTableData[i][1] = "B" + blockNumber;
                }
            }

            view.setIndexTableData(level, indexTableData);
            System.out.println("✓ Nivel " + (level + 1) + " enviado a la vista");
        }

        // Mostrar información de la estructura generada
        String structureInfo = String.format(
                "Estructura generada: %,d registros, %d niveles de índice, " +
                        "%d registros/bloque (datos), %d registros/bloque (índices)",
                dataRecords.size(), indexLevels.size(), dataPerBlock, indexPerBlock);

        view.setResultMessage(structureInfo, true);
        System.out.println("=== FIN MOSTRAR DATOS ===\n");
    }

    private void configureStructure() {
        String recordCountInput = view.getRecordCount();
        String blockSizeInput = view.getBlockSize();
        String dataRecordSizeInput = view.getDataRecordSize();
        String indexRecordSizeInput = view.getIndexRecordSize();
        String indexPerBlockInput = view.getIndexPerBlock();
        String dataPerBlockInput = view.getDataPerBlock();

        if (recordCountInput.isEmpty() || blockSizeInput.isEmpty() ||
                dataRecordSizeInput.isEmpty() || indexRecordSizeInput.isEmpty() ||
                indexPerBlockInput.isEmpty() || dataPerBlockInput.isEmpty()) {
            view.setResultMessage("Por favor complete todos los campos de configuración", false);
            return;
        }

        try {
            int newRecordCount = Integer.parseInt(recordCountInput);
            int newBlockSize = Integer.parseInt(blockSizeInput);
            int newDataRecordSize = Integer.parseInt(dataRecordSizeInput);
            int newIndexRecordSize = Integer.parseInt(indexRecordSizeInput);
            int newIndexPerBlock = Integer.parseInt(indexPerBlockInput);
            int newDataPerBlock = Integer.parseInt(dataPerBlockInput);

            // Validaciones
            if (newRecordCount < 1) {
                view.setResultMessage("El número de registros debe ser mayor a 0", false);
                return;
            }

            if (newBlockSize < 32) {
                view.setResultMessage("El tamaño del bloque debe ser al menos 32 bytes", false);
                return;
            }

            if (newDataRecordSize < 4 || newIndexRecordSize < 4) {
                view.setResultMessage("Los tamaños de registro deben ser al menos 4 bytes", false);
                return;
            }

            if (newIndexPerBlock < 1 || newDataPerBlock < 1) {
                view.setResultMessage("Los registros por bloque deben ser al menos 1", false);
                return;
            }

            // Actualizar configuración
            recordCount = newRecordCount;
            blockSize = newBlockSize;
            dataRecordSize = newDataRecordSize;
            indexRecordSize = newIndexRecordSize;
            indexPerBlock = newIndexPerBlock;
            dataPerBlock = newDataPerBlock;

            // Regenerar estructura
            generateDataForCount(recordCount);
            buildMultilevelIndexes();
            displayDataInTables();

            view.setResultMessage(String.format(
                    "Estructura generada: %d registros, %d niveles de índice, " +
                            "%d registros/bloque (datos), %d registros/bloque (índices)",
                    recordCount, indexLevels.size(), dataPerBlock, indexPerBlock), true);

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese valores numéricos válidos", false);
        }
    }

    private void generateDataForCount(int count) {
        dataRecords.clear();
        String[] names = {"Ana", "Carlos", "María", "José", "Laura", "Pedro", "Sofía", "Diego",
                "Carmen", "Roberto", "Elena", "Miguel", "Isabel", "Andrés", "Patricia",
                "Fernando", "Lucía", "Ricardo", "Valeria", "Sebastián", "Camila", "Mateo",
                "Valentina", "Santiago", "Isabella", "Nicolás", "Gabriela", "Daniel"};

        for (int i = 0; i < count; i++) {
            int id = (i + 1) * 10;
            String name = names[i % names.length];
            if (i >= names.length) {
                name += (i / names.length + 1);
            }
            dataRecords.add(new SimpleRecord(id, name, 20 + (i % 40)));
        }

        Collections.sort(dataRecords, (r1, r2) -> Integer.compare(r1.getId(), r2.getId()));
    }

    private void insertRecord() {
        String idInput = view.getInsertId();
        String nameInput = view.getInsertName();
        String ageInput = view.getInsertAge();

        if (idInput.isEmpty() || nameInput.isEmpty() || ageInput.isEmpty()) {
            view.setResultMessage("Por favor complete todos los campos", false);
            return;
        }

        try {
            int id = Integer.parseInt(idInput);
            int age = Integer.parseInt(ageInput);

            // Verificar si el ID ya existe
            for (SimpleRecord record : dataRecords) {
                if (record.getId() == id) {
                    view.setResultMessage("El ID " + id + " ya existe", false);
                    return;
                }
            }

            dataRecords.add(new SimpleRecord(id, nameInput, age));
            Collections.sort(dataRecords, (r1, r2) -> Integer.compare(r1.getId(), r2.getId()));

            buildMultilevelIndexes();
            displayDataInTables();

            view.setResultMessage("Registro insertado. Nueva estructura: " +
                    indexLevels.size() + " niveles de índice", true);

        } catch (NumberFormatException e) {
            view.setResultMessage("ID y edad deben ser números válidos", false);
        }
    }

    private void deleteRecord() {
        String idInput = view.getDeleteId();

        if (idInput.isEmpty()) {
            view.setResultMessage("Por favor ingrese un ID para eliminar", false);
            return;
        }

        try {
            int id = Integer.parseInt(idInput);
            boolean found = false;

            for (int i = 0; i < dataRecords.size(); i++) {
                if (dataRecords.get(i).getId() == id) {
                    dataRecords.remove(i);
                    found = true;
                    break;
                }
            }

            if (found) {
                recordCount = dataRecords.size();
                buildMultilevelIndexes();
                displayDataInTables();
                view.setResultMessage("Registro eliminado. Nueva estructura: " +
                        indexLevels.size() + " niveles de índice", true);
            } else {
                view.setResultMessage("Registro con ID " + id + " no encontrado", false);
            }

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese un ID numérico válido", false);
        }
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

            if (result.foundRecord != null) {
                view.highlightFoundRecord(result.dataPosition);
                view.setResultMessage(String.format(
                        "ID %d encontrado: %s, %d años (Niveles accedidos: %d)",
                        searchId, result.foundRecord.getName(), result.foundRecord.getAge(),
                        result.levelsAccessed), true);
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
                    publish("Iniciando búsqueda multinivel del ID " + searchId + "...");
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

                        if (result.foundRecord != null) {
                            view.highlightFoundRecord(result.dataPosition);
                            view.setResultMessage(String.format(
                                    "ID %d encontrado: %s, %d años (Proceso completado en %d niveles)",
                                    result.foundRecord.getId(), result.foundRecord.getName(),
                                    result.foundRecord.getAge(), result.levelsAccessed), true);
                        } else {
                            view.clearHighlights();
                            view.setResultMessage("Registro no encontrado después de " +
                                    result.levelsAccessed + " niveles", false);
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

    private SearchResult performMultilevelSearch(int searchId, boolean animated) {
        SearchResult result = new SearchResult();
        result.levelsAccessed = 0;

        try {
            int currentPointer = 0;

            // Navegar por todos los niveles de índices desde el más alto
            for (int level = indexLevels.size() - 1; level >= 0; level--) {
                result.levelsAccessed++;

                if (animated) {
                    updateSearchProgress("Buscando en nivel " + (level + 1) + " de índices...");
                    Thread.sleep(800);
                }

                int indexPosition = findInIndexLevel(searchId, level, currentPointer, animated);
                if (indexPosition == -1) {
                    return result; // No encontrado
                }

                // Obtener el puntero para el siguiente nivel
                currentPointer = indexLevels.get(level).get(indexPosition).getPointer();
            }

            // Buscar en los datos
            result.levelsAccessed++;

            if (animated) {
                updateSearchProgress("Buscando en registros de datos...");
                Thread.sleep(800);
            }

            SimpleRecord foundRecord = findInData(searchId, currentPointer, animated);
            if (foundRecord != null) {
                result.foundRecord = foundRecord;
                result.dataPosition = dataRecords.indexOf(foundRecord);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return result;
    }

    private int findInIndexLevel(int searchId, int level, int startPointer, boolean animated) throws InterruptedException {
        List<SimpleIndex> currentLevel = indexLevels.get(level);

        // Para el nivel más alto, empezamos desde 0
        // Para niveles inferiores, usamos el startPointer del nivel superior
        int startIndex = (level == indexLevels.size() - 1) ? 0 : startPointer;
        int endIndex = Math.min(startIndex + indexPerBlock, currentLevel.size());

        for (int i = startIndex; i < endIndex; i++) {
            if (animated) {
                view.highlightIndexRecord(level, i);
                updateSearchProgress("Revisando índice nivel " + (level + 1) +
                        ", entrada " + (i + 1) + " (valor: " + currentLevel.get(i).getValue() +
                        ") → apunta a " + currentLevel.get(i).getTargetType());
                Thread.sleep(600);
            }

            // Encontrar el índice correcto basado en el valor
            // Si es el último del rango o el siguiente valor es mayor, este es el correcto
            if (i == endIndex - 1 ||
                    (i + 1 < currentLevel.size() && searchId < currentLevel.get(i + 1).getValue())) {
                if (animated) {
                    updateSearchProgress("✓ Encontrado en nivel " + (level + 1) +
                            ": valor " + currentLevel.get(i).getValue() +
                            " → siguiente nivel");
                    Thread.sleep(400);
                }
                return i;
            }
        }

        return -1;
    }

    private SimpleRecord findInData(int searchId, int startPointer, boolean animated) throws InterruptedException {
        // Para el nivel 1 que apunta directamente a registros
        if (startPointer < dataRecords.size()) {
            if (animated) {
                view.highlightDataRecord(startPointer);
                updateSearchProgress("Revisando registro " + (startPointer + 1) +
                        " (ID: " + dataRecords.get(startPointer).getId() + ")");
                Thread.sleep(600);
            }

            if (dataRecords.get(startPointer).getId() == searchId) {
                return dataRecords.get(startPointer);
            }
        }

        return null;
    }

    private void updateSearchProgress(String message) {
        SwingUtilities.invokeLater(() -> view.setSearchProgress(message));
    }

    private void goBack() {
        view.dispose();
        if (indicesMenuView != null) {
            indicesMenuView.setVisible(true);
        }
    }

    public void setIndicesMenuView(IndicesMenuView indicesMenuView) {
        this.indicesMenuView = indicesMenuView;
    }

    // Clases auxiliares
    public static class SimpleRecord {
        private int id;
        private String name;
        private int age;

        public SimpleRecord(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getAge() { return age; }
    }

    public static class SimpleIndex {
        private int value;
        private int pointer;
        private String targetType;

        public SimpleIndex(int value, int pointer, String targetType) {
            this.value = value;
            this.pointer = pointer;
            this.targetType = targetType;
        }

        public int getValue() { return value; }
        public int getPointer() { return pointer; }
        public String getTargetType() { return targetType; }
    }

    public static class SearchResult {
        public SimpleRecord foundRecord = null;
        public int dataPosition = -1;
        public int levelsAccessed = 0;
    }
}
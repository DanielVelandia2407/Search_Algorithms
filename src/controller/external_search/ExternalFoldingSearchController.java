package controller.external_search;

import view.external_search.ExternalFoldingSearchView;
import view.menu.ExternalHashAlgorithmView;
import view.external_search.ExternalColisionView;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ExternalFoldingSearchController {

    private final ExternalFoldingSearchView view;
    private final List<List<Integer>> hashTable; // Each position contains a block (list of integers)
    private ExternalHashAlgorithmView hashAlgorithmView;
    private int tableSize;
    private int blockSize = 5; // Number of slots per block
    private int digitLimit = 2;
    private int blockAccessCount = 0; // Counter for block accesses (I/O operations)

    public ExternalFoldingSearchController(ExternalFoldingSearchView view) {
        this.view = view;
        this.hashTable = new ArrayList<>();

        // Initialize components
        initComponents();

        // Load data from file when creating the controller
        loadDataFromFile();

        if (hashTable.isEmpty()) {
            this.tableSize = 10;
            initializeEmptyHashTable();
        } else {
            this.tableSize = Math.max(10, hashTable.size());

            // Ensure the table has exactly tableSize rows
            while (hashTable.size() < this.tableSize) {
                List<Integer> block = new ArrayList<>();
                for (int i = 0; i < blockSize; i++) {
                    block.add(-1);
                }
                hashTable.add(block);
            }
        }

        // Display data in the table
        displayDataInTable();
    }

    public void setExternalHashAlgorithmView(ExternalHashAlgorithmView hashAlgorithmView) {
        this.hashAlgorithmView = hashAlgorithmView;
    }

    private void initComponents() {
        view.addSearchListener(e -> performSearch());

        view.addGenerateTableListener(e -> {
            String tableSizeInput = view.getTableSize();
            String blockSizeInput = view.getBlockSize();
            String digitLimitInput = view.getDigitLimit();

            if (!tableSizeInput.isEmpty() && !blockSizeInput.isEmpty() && !digitLimitInput.isEmpty()) {
                try {
                    int newTableSize = Integer.parseInt(tableSizeInput);
                    int newBlockSize = Integer.parseInt(blockSizeInput);
                    digitLimit = Integer.parseInt(digitLimitInput);

                    if (digitLimit < 1 || digitLimit > 5) {
                        view.setResultMessage("El límite de dígitos debe estar entre 1 y 5", false);
                        return;
                    }

                    if (newTableSize < 5 || newTableSize > 50) {
                        view.setResultMessage("El tamaño de la tabla debe estar entre 5 y 50", false);
                        return;
                    }

                    if (newBlockSize < 1 || newBlockSize > 10) {
                        view.setResultMessage("El tamaño del bloque debe estar entre 1 y 10", false);
                        return;
                    }

                    blockSize = newBlockSize;
                    generateNewHashTable(newTableSize);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese valores numéricos válidos", false);
                }
            } else {
                view.setResultMessage("Por favor complete todos los campos de configuración", false);
            }
        });

        view.addInsertValueListener(e -> {
            String input = view.getInsertValue();
            String digitLimitInput = view.getDigitLimit();

            if (!input.isEmpty() && !digitLimitInput.isEmpty()) {
                try {
                    int value = Integer.parseInt(input);
                    digitLimit = Integer.parseInt(digitLimitInput);

                    if (!isValidDigitCount(value, digitLimit)) {
                        view.setResultMessage("La clave debe tener exactamente " + digitLimit + " dígito(s)", false);
                        return;
                    }

                    insertValue(value);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese valores numéricos válidos", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese una clave para insertar y el límite de dígitos", false);
            }
        });

        view.addDeleteValueListener(e -> {
            String input = view.getDeleteValue();
            if (!input.isEmpty()) {
                try {
                    int value = Integer.parseInt(input);
                    deleteValue(value);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese una clave numérica válida", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese una clave para eliminar", false);
            }
        });

        view.addBackListener(e -> goBack());
    }

    // Method to validate the number of digits
    private boolean isValidDigitCount(int value, int digitLimit) {
        String valueStr = String.valueOf(Math.abs(value));
        return valueStr.length() == digitLimit;
    }

    // Method to get the minimum range based on the digit limit
    private int getMinValue(int digitLimit) {
        if (digitLimit == 1) return 0;
        return (int) Math.pow(10, digitLimit - 1);
    }

    // Method to get the maximum range based on the digit limit
    private int getMaxValue(int digitLimit) {
        return (int) Math.pow(10, digitLimit) - 1;
    }

    // Method to load data from file
    private void loadDataFromFile() {
        hashTable.clear();

        try {
            File file = new File("src/utilities/datos-hash-plegamiento-externo.txt");

            if (!file.exists()) {
                System.out.println("El archivo de datos no existe, se creará uno nuevo: " + file.getAbsolutePath());
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("#")) continue;

                    // Remove brackets if present
                    line = line.replace("[", "").replace("]", "");

                    // Split by comma
                    String[] values = line.split(",");

                    List<Integer> block = new ArrayList<>();
                    for (String value : values) {
                        try {
                            int num = Integer.parseInt(value.trim());
                            block.add(num);
                        } catch (NumberFormatException e) {
                            // Add -1 for empty slots
                            block.add(-1);
                        }
                    }

                    // Ensure block has exactly blockSize elements
                    while (block.size() < blockSize) {
                        block.add(-1);
                    }

                    hashTable.add(block);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveDataToFile() {
        try {
            File file = new File("src/utilities/datos-hash-plegamiento-externo.txt");
            try (PrintWriter writer = new PrintWriter(file)) {
                for (List<Integer> block : hashTable) {
                    writer.println(block.toString());
                }
                writer.println("# Formato: cada línea representa un bloque de la tabla hash externa");
                writer.println("# [valor1, valor2, valor3, valor4, valor5] para cada bloque");
                writer.println("# [-1] para slots vacíos en el bloque");
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeEmptyHashTable() {
        hashTable.clear();

        for (int i = 0; i < tableSize; i++) {
            List<Integer> block = new ArrayList<>();
            for (int j = 0; j < blockSize; j++) {
                block.add(-1); // Empty slot
            }
            hashTable.add(block);
        }
        System.out.println("Tabla hash externa plegamiento inicializada con " + tableSize + " bloques");
    }

    private void displayDataInTable() {
        // Create data for table with position, block info, and slot columns
        Object[][] tableData = new Object[tableSize][blockSize + 2];

        for (int i = 0; i < tableSize; i++) {
            List<Integer> block = i < hashTable.size() ? hashTable.get(i) : null;
            tableData[i][0] = i + 1; // Position (starting from 1)

            // Block identifier and status
            int validCount = 0;
            if (block != null) {
                for (Integer value : block) {
                    if (value != null && value != -1) {
                        validCount++;
                    }
                }
            }
            tableData[i][1] = "B" + (i + 1) + " (" + validCount + "/" + blockSize + ")";

            // Fill slots in the block
            for (int j = 0; j < blockSize; j++) {
                if (block != null && j < block.size() && block.get(j) != -1) {
                    tableData[i][j + 2] = block.get(j);
                } else {
                    tableData[i][j + 2] = ""; // Empty slot
                }
            }
        }

        // Create dynamic headers
        String[] headers = new String[blockSize + 2];
        headers[0] = "Posición";
        headers[1] = "Bloque";
        for (int i = 0; i < blockSize; i++) {
            headers[i + 2] = "Slot " + (i + 1);
        }

        // Set data to table
        view.setTableData(tableData, headers);
        view.setHashFunction("plegamiento % " + tableSize);
    }

    /**
     * Implementación del método de plegamiento para calcular el hash externo
     */
    private int calculateFoldingHash(int value) {
        String valueStr = String.valueOf(value);
        int sum = 0;

        if (valueStr.length() <= 1) {
            return value % tableSize;
        }

        // Dividir en grupos de 2 dígitos
        for (int i = 0; i < valueStr.length(); i += 2) {
            if (i + 1 < valueStr.length()) {
                String chunk = valueStr.substring(i, i + 2);
                sum += Integer.parseInt(chunk);
            } else {
                sum += Integer.parseInt(valueStr.substring(i));
            }
        }

        if (sum >= tableSize) {
            String sumStr = String.valueOf(sum);
            int lastDigit = Integer.parseInt(sumStr.substring(sumStr.length() - 1));

            if (lastDigit == 0 && tableSize > 9) {
                if (sumStr.length() > 1) {
                    lastDigit = Integer.parseInt(sumStr.substring(sumStr.length() - 2, sumStr.length() - 1));
                }
            }

            if (lastDigit < tableSize) {
                return lastDigit;
            }
        }

        return sum % tableSize;
    }

    private String generateFoldingCalculationString(int value) {
        String foldingCalc = "Cálculo del hash por plegamiento para " + value + ": ";
        String valueStr = String.valueOf(value);
        int sum = 0;

        StringBuilder calcSteps = new StringBuilder(foldingCalc);

        for (int i = 0; i < valueStr.length(); i += 2) {
            if (i + 1 < valueStr.length()) {
                String chunk = valueStr.substring(i, i + 2);
                int chunkValue = Integer.parseInt(chunk);
                sum += chunkValue;
                calcSteps.append(chunk);
                if (i + 2 < valueStr.length()) {
                    calcSteps.append(" + ");
                }
            } else {
                String chunk = valueStr.substring(i);
                int chunkValue = Integer.parseInt(chunk);
                sum += chunkValue;
                calcSteps.append(chunk);
            }
        }

        calcSteps.append(" = ").append(sum);

        if (sum >= tableSize) {
            String sumStr = String.valueOf(sum);
            int lastDigit = Integer.parseInt(sumStr.substring(sumStr.length() - 1));
            calcSteps.append(" (valor fuera de rango, tomamos el último dígito: ").append(lastDigit).append(")");
        } else {
            calcSteps.append(" % ").append(tableSize);
        }

        int hashPosition = calculateFoldingHash(value);
        calcSteps.append(" = ").append(hashPosition + 1);

        return calcSteps.toString();
    }

    // Method to perform search
    private void performSearch() {
        if (view.isVisualizationEnabled()) {
            performAnimatedSearch();
        } else {
            performNormalSearch();
        }
    }

    // Search with no animation
    private void performNormalSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese una clave para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);
            int hashPosition = calculateFoldingHash(valueToSearch);

            // Reset counters and highlights
            blockAccessCount = 0;
            view.setBlockAccessCount(blockAccessCount);
            view.clearHighlights();

            // Perform external hash search
            SearchResult result = externalHashSearch(valueToSearch);

            String foldingCalc = generateFoldingCalculationString(valueToSearch);

            if (result.found) {
                view.highlightFoundItem(result.blockIndex);
                view.setResultMessage("Clave " + valueToSearch + " encontrada en Bloque " +
                        (result.blockIndex + 1) + ", Slot " + (result.slotIndex + 1) +
                        ". Accesos a bloques: " + blockAccessCount +
                        (result.blockIndex == hashPosition ? " (posición hash original)" :
                                " (reubicada por colisión, hash original: " + (hashPosition + 1) + ")") +
                        "\n" + foldingCalc, true);
            } else {
                view.setResultMessage("Clave " + valueToSearch + " no encontrada. " + foldingCalc +
                        ". Accesos a bloques: " + blockAccessCount, false);
            }

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese una clave numérica válida", false);
        }
    }

    // Animation for search
    private void performAnimatedSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese una clave para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);
            int hashPosition = calculateFoldingHash(valueToSearch);

            // Reset counters and highlights
            blockAccessCount = 0;
            view.setBlockAccessCount(blockAccessCount);
            view.setCurrentOperation("Búsqueda hash plegamiento externa");
            view.clearHighlights();

            SwingWorker<SearchResult, SearchProgress> worker = new SwingWorker<SearchResult, SearchProgress>() {
                @Override
                protected SearchResult doInBackground() throws Exception {
                    // Step 1: Access the hash position block
                    blockAccessCount++;
                    publish(new SearchProgress(hashPosition, blockAccessCount, "Accediendo bloque hash plegamiento"));
                    Thread.sleep(600);

                    List<Integer> block = hashTable.get(hashPosition);

                    // Search within the original hash block
                    for (int slotIndex = 0; slotIndex < block.size(); slotIndex++) {
                        if (block.get(slotIndex) == valueToSearch) {
                            return new SearchResult(true, hashPosition, slotIndex);
                        }
                    }

                    // Step 2: If not found, try collision resolution (sequential probing)
                    int currentPosition = (hashPosition + 1) % tableSize;
                    int attempts = 0;

                    while (currentPosition != hashPosition && attempts < tableSize - 1) {
                        blockAccessCount++;
                        publish(new SearchProgress(currentPosition, blockAccessCount, "Resolviendo colisión secuencial"));
                        Thread.sleep(500);

                        List<Integer> currentBlock = hashTable.get(currentPosition);
                        for (int slotIndex = 0; slotIndex < currentBlock.size(); slotIndex++) {
                            if (currentBlock.get(slotIndex) == valueToSearch) {
                                return new SearchResult(true, currentPosition, slotIndex);
                            }
                        }

                        currentPosition = (currentPosition + 1) % tableSize;
                        attempts++;
                    }

                    // Step 3: Try exponential probing
                    int i = 1;
                    attempts = 0;

                    while (attempts < tableSize) {
                        int expPosition = (hashPosition + (i * i)) % tableSize;

                        if (expPosition != hashPosition) {
                            blockAccessCount++;
                            publish(new SearchProgress(expPosition, blockAccessCount, "Resolviendo colisión exponencial"));
                            Thread.sleep(500);

                            List<Integer> expBlock = hashTable.get(expPosition);
                            for (int slotIndex = 0; slotIndex < expBlock.size(); slotIndex++) {
                                if (expBlock.get(slotIndex) == valueToSearch) {
                                    return new SearchResult(true, expPosition, slotIndex);
                                }
                            }
                        }

                        i++;
                        attempts++;
                    }

                    return new SearchResult(false, -1, -1);
                }

                @Override
                protected void process(List<SearchProgress> chunks) {
                    SearchProgress progress = chunks.get(chunks.size() - 1);
                    view.setBlockAccessCount(progress.blockAccessCount);
                    view.setCurrentOperation(progress.operation);
                    view.highlightBlockAccess(progress.blockIndex);
                }

                @Override
                protected void done() {
                    try {
                        SearchResult result = get();
                        int hashPosition = calculateFoldingHash(valueToSearch);
                        String foldingCalc = generateFoldingCalculationString(valueToSearch);

                        if (result.found) {
                            view.highlightFoundItem(result.blockIndex);
                            view.setResultMessage("Clave " + valueToSearch + " encontrada en Bloque " +
                                    (result.blockIndex + 1) + ", Slot " + (result.slotIndex + 1) +
                                    ". Accesos a bloques: " + blockAccessCount +
                                    (result.blockIndex == hashPosition ? " (posición hash original)" :
                                            " (reubicada por colisión, hash original: " + (hashPosition + 1) + ")") +
                                    "\n" + foldingCalc, true);
                        } else {
                            view.clearHighlights();
                            view.setResultMessage("Clave " + valueToSearch + " no encontrada. " + foldingCalc +
                                    ". Accesos a bloques: " + blockAccessCount, false);
                        }
                        view.setCurrentOperation("Completada");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese una clave numérica válida", false);
        }
    }

    // External hash search algorithm implementation
    private SearchResult externalHashSearch(int target) {
        int hashPosition = calculateFoldingHash(target);

        // Access the hash position block (I/O operation)
        blockAccessCount++;
        List<Integer> block = hashTable.get(hashPosition);

        // Search within the block
        for (int slotIndex = 0; slotIndex < block.size(); slotIndex++) {
            if (block.get(slotIndex) == target) {
                return new SearchResult(true, hashPosition, slotIndex);
            }
        }

        // If not found in hash position, try collision resolution (sequential)
        int currentPosition = (hashPosition + 1) % tableSize;
        int attempts = 0;

        while (currentPosition != hashPosition && attempts < tableSize - 1) {
            // Another I/O operation
            blockAccessCount++;
            List<Integer> currentBlock = hashTable.get(currentPosition);

            for (int slotIndex = 0; slotIndex < currentBlock.size(); slotIndex++) {
                if (currentBlock.get(slotIndex) == target) {
                    return new SearchResult(true, currentPosition, slotIndex);
                }
            }

            currentPosition = (currentPosition + 1) % tableSize;
            attempts++;
        }

        // Try exponential probing
        int i = 1;
        attempts = 0;

        while (attempts < tableSize) {
            int expPosition = (hashPosition + (i * i)) % tableSize;

            if (expPosition != hashPosition) {
                blockAccessCount++;
                List<Integer> expBlock = hashTable.get(expPosition);

                for (int slotIndex = 0; slotIndex < expBlock.size(); slotIndex++) {
                    if (expBlock.get(slotIndex) == target) {
                        return new SearchResult(true, expPosition, slotIndex);
                    }
                }
            }

            i++;
            attempts++;
        }

        return new SearchResult(false, -1, -1);
    }

    private void generateNewHashTable(int newTableSize) {
        this.tableSize = newTableSize;

        hashTable.clear();

        for (int i = 0; i < newTableSize; i++) {
            List<Integer> block = new ArrayList<>();
            for (int j = 0; j < blockSize; j++) {
                block.add(-1);
            }
            hashTable.add(block);
        }

        saveDataToFile();
        displayDataInTable();

        int minValue = getMinValue(digitLimit);
        int maxValue = getMaxValue(digitLimit);
        view.setResultMessage("Tabla hash plegamiento externa generada. Rango permitido: " + minValue + " - " + maxValue, true);
        view.setBlockAccessCount(0);
        view.setCurrentOperation("Ninguna");
    }

    // Method to insert values using external folding hash function
    public void insertValue(int value) {
        // Check if value already exists
        for (List<Integer> block : hashTable) {
            for (Integer existingValue : block) {
                if (existingValue != null && existingValue != -1 && existingValue == value) {
                    view.setResultMessage("La clave " + value + " ya existe en la tabla hash", false);
                    return;
                }
            }
        }

        int hashPosition = calculateFoldingHash(value);
        List<Integer> block = hashTable.get(hashPosition);

        // Try to find an empty slot in the hash position block
        for (int slotIndex = 0; slotIndex < block.size(); slotIndex++) {
            if (block.get(slotIndex) == -1) {
                block.set(slotIndex, value);
                saveDataToFile();
                displayDataInTable();
                String foldingCalc = generateFoldingCalculationString(value);
                view.setResultMessage("Clave " + value + " insertada en Bloque " + (hashPosition + 1) +
                        ", Slot " + (slotIndex + 1) + "\n" + foldingCalc, true);
                return;
            }
        }

        // If the hash position block is full, handle collision
        handleExternalCollision(value, hashPosition);
    }

    private void handleExternalCollision(int valueToInsert, int hashPosition) {
        List<Integer> block = hashTable.get(hashPosition);
        int slotsUsed = 0;
        for (Integer value : block) {
            if (value != null && value != -1) {
                slotsUsed++;
            }
        }

        ExternalColisionView colisionView = new ExternalColisionView();
        colisionView.setExternalCollisionInfo(valueToInsert, hashPosition, slotsUsed, blockSize);

        view.setResultMessage("Colisión detectada: Bloque " + (hashPosition + 1) + " está lleno (" +
                slotsUsed + "/" + blockSize + " slots utilizados)", false);

        colisionView.addSequentialSolutionListener(e -> {
            solveCollisionSequential(valueToInsert, hashPosition);
            colisionView.dispose();
        });

        colisionView.addExponentialSolutionListener(e -> {
            solveCollisionQuadratic(valueToInsert, hashPosition);
            colisionView.dispose();
        });

        colisionView.addOverflowAreaSolutionListener(e -> {
            solveCollisionOverflowArea(valueToInsert, hashPosition);
            colisionView.dispose();
        });

        colisionView.addCancelListener(e -> {
            colisionView.dispose();
            view.setResultMessage("Inserción cancelada: colisión en Bloque " + (hashPosition + 1), false);
        });

        colisionView.showWindow();
    }

    private void solveCollisionSequential(int valueToInsert, int originalHashPos) {
        int currentPosition = (originalHashPos + 1) % tableSize;
        int attempts = 0;

        while (currentPosition != originalHashPos && attempts < tableSize - 1) {
            List<Integer> currentBlock = hashTable.get(currentPosition);

            // Check if there's an empty slot in this block
            for (int slotIndex = 0; slotIndex < currentBlock.size(); slotIndex++) {
                if (currentBlock.get(slotIndex) == -1) {
                    currentBlock.set(slotIndex, valueToInsert);
                    saveDataToFile();
                    displayDataInTable();
                    view.setResultMessage("Clave " + valueToInsert + " insertada en Bloque " +
                            (currentPosition + 1) + ", Slot " + (slotIndex + 1) +
                            " mediante sondeo secuencial (colisión en Bloque " + (originalHashPos + 1) + ")", true);
                    return;
                }
            }

            currentPosition = (currentPosition + 1) % tableSize;
            attempts++;
        }
        view.setResultMessage("No se pudo insertar " + valueToInsert + ": tabla hash llena", false);
    }

    private void solveCollisionQuadratic(int valueToInsert, int originalHashPos) {
        int i = 1;
        int attempts = 0;

        while (attempts < tableSize) {
            int position = (originalHashPos + (i * i)) % tableSize;

            if (position >= 0 && position < hashTable.size()) {
                List<Integer> block = hashTable.get(position);
                for (int slotIndex = 0; slotIndex < block.size(); slotIndex++) {
                    if (block.get(slotIndex) == -1) {
                        block.set(slotIndex, valueToInsert);
                        saveDataToFile();
                        displayDataInTable();
                        view.setResultMessage("Clave " + valueToInsert + " insertada en Bloque " +
                                (position + 1) + ", Slot " + (slotIndex + 1) +
                                " mediante sondeo cuadrático (colisión en Bloque " + (originalHashPos + 1) + ")", true);
                        return;
                    }
                }
            }

            i++;
            attempts++;
        }
        view.setResultMessage("No se pudo insertar " + valueToInsert + " usando sondeo cuadrático", false);
    }

    private void solveCollisionOverflowArea(int valueToInsert, int originalHashPos) {
        // Find the first block with available space (simulate overflow area)
        for (int blockIndex = 0; blockIndex < hashTable.size(); blockIndex++) {
            List<Integer> block = hashTable.get(blockIndex);
            for (int slotIndex = 0; slotIndex < block.size(); slotIndex++) {
                if (block.get(slotIndex) == -1) {
                    block.set(slotIndex, valueToInsert);
                    saveDataToFile();
                    displayDataInTable();
                    view.setResultMessage("Clave " + valueToInsert + " insertada en área de desbordamiento: Bloque " +
                            (blockIndex + 1) + ", Slot " + (slotIndex + 1) +
                            " (colisión original en Bloque " + (originalHashPos + 1) + ")", true);
                    return;
                }
            }
        }
        view.setResultMessage("No se pudo insertar " + valueToInsert + ": no hay espacio disponible en área de desbordamiento", false);
    }

    // Method to delete a value
    public void deleteValue(int value) {
        boolean found = false;
        int foundBlock = -1;
        int foundSlot = -1;

        for (int blockIndex = 0; blockIndex < hashTable.size(); blockIndex++) {
            List<Integer> block = hashTable.get(blockIndex);
            for (int slotIndex = 0; slotIndex < block.size(); slotIndex++) {
                if (block.get(slotIndex) != null && block.get(slotIndex) == value) {
                    found = true;
                    foundBlock = blockIndex;
                    foundSlot = slotIndex;
                    break;
                }
            }
            if (found) break;
        }

        if (found) {
            List<Integer> block = hashTable.get(foundBlock);
            block.set(foundSlot, -1);

            saveDataToFile();
            displayDataInTable();

            int hashPosition = calculateFoldingHash(value);
            String foldingCalc = generateFoldingCalculationString(value);

            if (foundBlock == hashPosition) {
                view.setResultMessage("Clave " + value + " eliminada del Bloque " + (foundBlock + 1) +
                        ", Slot " + (foundSlot + 1) + " (posición hash plegamiento original)\n" + foldingCalc, true);
            } else {
                view.setResultMessage("Clave " + value + " eliminada del Bloque " + (foundBlock + 1) +
                        ", Slot " + (foundSlot + 1) +
                        " (hash original: " + (hashPosition + 1) + ")\n" + foldingCalc, true);
            }
        } else {
            view.setResultMessage("Clave " + value + " no encontrada en la tabla hash", false);
        }
    }

    private void goBack() {
        // Close current view
        view.dispose();

        // Show hash algorithm view if available
        if (hashAlgorithmView != null) {
            hashAlgorithmView.setVisible(true);
        }
    }

    // Helper classes for search results and progress
    private static class SearchResult {
        boolean found;
        int blockIndex;
        int slotIndex;

        SearchResult(boolean found, int blockIndex, int slotIndex) {
            this.found = found;
            this.blockIndex = blockIndex;
            this.slotIndex = slotIndex;
        }
    }

    private static class SearchProgress {
        int blockIndex;
        int blockAccessCount;
        String operation;

        SearchProgress(int blockIndex, int blockAccessCount, String operation) {
            this.blockIndex = blockIndex;
            this.blockAccessCount = blockAccessCount;
            this.operation = operation;
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExternalFoldingSearchView newView = new ExternalFoldingSearchView();
            ExternalFoldingSearchController controller = new ExternalFoldingSearchController(newView);
            newView.showWindow();
        });
    }
}

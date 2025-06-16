package controller.external_search;

import view.external_search.ExternalBinarySearchView;
import view.menu.ExternalSearchMenuView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;
import java.util.List;

public class ExternalBinarySearchController {

    private ExternalBinarySearchView view;
    private ExternalSearchMenuView menuView;
    private List<List<Integer>> blocks;
    private int blockSize = 5;
    private int digitLimit = 2;
    private int blockAccessCount = 0;
    private String currentFilePath = "src/utilities/datos-busqueda-binaria-externa.txt";

    public ExternalBinarySearchController(ExternalBinarySearchView view) {
        this.view = view;
        this.blocks = new ArrayList<>();

        // Initialize components
        initComponents();

        // Load data from file when creating the controller
        loadDataFromFile();

        // Display data in the table
        displayDataInTable();
    }

    // Setter for the menu view to return to
    public void setMenuView(ExternalSearchMenuView menuView) {
        this.menuView = menuView;
    }

    private void initComponents() {
        // Add action listeners to buttons
        view.addSearchListener(e -> performSearch());

        view.addGenerateBlocksListener(e -> {
            String blockCountInput = view.getBlockCount();
            String blockSizeInput = view.getBlockSize();
            String digitLimitInput = view.getDigitLimit();

            if (!blockCountInput.isEmpty() && !blockSizeInput.isEmpty() && !digitLimitInput.isEmpty()) {
                try {
                    int newBlockCount = Integer.parseInt(blockCountInput);
                    int newBlockSize = Integer.parseInt(blockSizeInput);
                    digitLimit = Integer.parseInt(digitLimitInput);

                    if (digitLimit < 1 || digitLimit > 5) {
                        view.setResultMessage("El límite de dígitos debe estar entre 1 y 5", false);
                        return;
                    }

                    if (newBlockCount < 1 || newBlockCount > 20) {
                        view.setResultMessage("El número de bloques debe estar entre 1 y 20", false);
                        return;
                    }

                    if (newBlockSize < 1 || newBlockSize > 10) {
                        view.setResultMessage("El tamaño del bloque debe estar entre 1 y 10", false);
                        return;
                    }

                    blockSize = newBlockSize;
                    generateNewBlocks(newBlockCount);
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

                    try {
                        insertValue(value);
                    } catch (IllegalArgumentException ex) {
                        view.setResultMessage(ex.getMessage(), false);
                    }
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

        view.addLoadFromFileListener(e -> loadFromExternalFile());
        view.addBackListener(e -> goBack());
    }

    // Método para cargar archivo externo
    private void loadFromExternalFile() {
        JFileChooser fileChooser = new JFileChooser();

        // Configurar el filtro para archivos de texto
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Establecer directorio inicial
        fileChooser.setCurrentDirectory(new File("src/utilities"));

        // Configurar título del diálogo
        fileChooser.setDialogTitle("Seleccionar archivo de datos de búsqueda binaria externa");

        int result = fileChooser.showOpenDialog(view);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Intentar cargar datos del archivo seleccionado
                List<List<Integer>> newBlocks = loadDataFromExternalFile(selectedFile);

                if (!newBlocks.isEmpty()) {
                    // Actualizar los bloques de datos
                    blocks.clear();
                    blocks.addAll(newBlocks);

                    // Actualizar el archivo actual
                    currentFilePath = selectedFile.getAbsolutePath();

                    // Mostrar datos en la tabla
                    displayDataInTable();

                    // Limpiar highlights y contadores
                    view.clearHighlights();
                    view.setBlockAccessCount(0);
                    view.setCurrentOperation("Ninguna");
                    view.setSearchPhase("Inactiva");

                    // Contar total de registros válidos
                    int totalRecords = 0;
                    for (List<Integer> block : blocks) {
                        for (Integer value : block) {
                            if (value != null && value != -1) {
                                totalRecords++;
                            }
                        }
                    }

                    // Verificar si los bloques están ordenados
                    boolean sorted = areBlocksSorted();
                    String sortedStatus = sorted ? " (ordenados)" : " (requieren ordenamiento)";

                    view.setResultMessage("Datos cargados desde: " + selectedFile.getName() +
                            " (" + blocks.size() + " bloques, " + totalRecords + " registros)" + sortedStatus, true);
                } else {
                    view.setResultMessage("El archivo seleccionado está vacío o no contiene datos válidos", false);
                }

            } catch (IOException ex) {
                view.setResultMessage("Error al leer el archivo: " + ex.getMessage(), false);
            } catch (Exception ex) {
                view.setResultMessage("Error inesperado al cargar el archivo: " + ex.getMessage(), false);
            }
        }
    }

    // Método para cargar datos desde un archivo externo específico
    private List<List<Integer>> loadDataFromExternalFile(File file) throws IOException {
        List<List<Integer>> newBlocks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue; // Saltar líneas vacías
                }

                // Si la línea contiene corchetes, procesarla como bloque
                if (line.contains("[") && line.contains("]")) {
                    line = line.replace("[", "").replace("]", "");
                }

                // Dividir por comas, espacios o ambos
                String[] values = line.split("[,\\s]+");

                List<Integer> block = new ArrayList<>();
                for (String value : values) {
                    value = value.trim();

                    if (!value.isEmpty()) {
                        try {
                            int num = Integer.parseInt(value);
                            block.add(num);
                        } catch (NumberFormatException e) {
                            // Si no es un número válido, agregar como -1 (vacío)
                            if (!value.equals("-1")) {
                                System.err.println("Valor no numérico ignorado: " + value);
                            } else {
                                block.add(-1);
                            }
                        }
                    }
                }

                // Solo agregar bloques que tengan al menos un elemento
                if (!block.isEmpty()) {
                    newBlocks.add(block);
                }
            }
        }

        return newBlocks;
    }

    private boolean isValidDigitCount(int value, int digitLimit) {
        String valueStr = String.valueOf(Math.abs(value));
        return valueStr.length() == digitLimit;
    }

    private int getMinValue(int digitLimit) {
        if (digitLimit == 1) return 0;
        return (int) Math.pow(10, digitLimit - 1);
    }

    private int getMaxValue(int digitLimit) {
        return (int) Math.pow(10, digitLimit) - 1;
    }

    private void loadDataFromFile() {
        blocks.clear();

        try {
            File file = new File(currentFilePath);

            if (!file.exists()) {
                System.out.println("El archivo de datos no existe, se creará uno nuevo: " + file.getAbsolutePath());
                return;
            }

            List<List<Integer>> loadedBlocks = loadDataFromExternalFile(file);
            blocks.addAll(loadedBlocks);

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveDataToFile() {
        try {
            File file = new File(currentFilePath);

            // Crear directorios padre si no existen
            file.getParentFile().mkdirs();

            try (PrintWriter writer = new PrintWriter(file)) {
                for (List<Integer> block : blocks) {
                    writer.println(block.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayDataInTable() {
        // Create data for table with block, min-max range, and record columns
        Object[][] tableData = new Object[blocks.size()][blockSize + 2];

        for (int i = 0; i < blocks.size(); i++) {
            List<Integer> block = blocks.get(i);
            tableData[i][0] = "Bloque " + (i + 1); // Block identifier

            // Calculate min-max range for the block
            String range = calculateBlockRange(block);
            tableData[i][1] = range;

            // Fill records in the block
            for (int j = 0; j < blockSize; j++) {
                if (j < block.size() && block.get(j) != -1) {
                    tableData[i][j + 2] = block.get(j);
                } else {
                    tableData[i][j + 2] = ""; // Empty slot
                }
            }
        }

        // Set data to table
        view.setTableData(tableData);
    }

    private String calculateBlockRange(List<Integer> block) {
        List<Integer> validValues = new ArrayList<>();
        for (Integer value : block) {
            if (value != null && value != -1) {
                validValues.add(value);
            }
        }

        if (validValues.isEmpty()) {
            return "Vacío";
        }

        int min = Collections.min(validValues);
        int max = Collections.max(validValues);

        if (min == max) {
            return String.valueOf(min);
        }

        return min + "-" + max;
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
            view.setResultMessage("Por favor ingrese una clave para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);

            // Verify blocks are sorted
            if (!areBlocksSorted()) {
                view.setResultMessage("Los bloques deben estar ordenados para búsqueda binaria", false);
                return;
            }

            // Reset counters and highlights
            blockAccessCount = 0;
            view.setBlockAccessCount(blockAccessCount);
            view.setCurrentOperation("Ninguna");
            view.setSearchPhase("Inactiva");
            view.clearHighlights();

            // Perform external binary search
            SearchResult result = externalBinarySearch(valueToSearch);

            if (result.found) {
                view.highlightFoundItem(result.blockIndex, result.recordIndex);
                view.setResultMessage("Clave " + valueToSearch + " encontrada en Bloque " +
                        (result.blockIndex + 1) + ", Registro " + (result.recordIndex + 1) +
                        ". Accesos a bloques: " + blockAccessCount, true);
            } else {
                view.setResultMessage("Clave " + valueToSearch + " no encontrada. Accesos a bloques: " +
                        blockAccessCount, false);
            }

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese una clave numérica válida", false);
        }
    }

    private void performAnimatedSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese una clave para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);

            // Verify blocks are sorted
            if (!areBlocksSorted()) {
                view.setResultMessage("Los bloques deben estar ordenados para búsqueda binaria", false);
                return;
            }

            // Reset counters and highlights
            blockAccessCount = 0;
            view.setBlockAccessCount(blockAccessCount);
            view.setCurrentOperation("Búsqueda binaria");
            view.setSearchPhase("Buscando bloque");
            view.clearHighlights();

            // Create SwingWorker for animation
            SwingWorker<SearchResult, SearchProgress> worker = new SwingWorker<SearchResult, SearchProgress>() {
                @Override
                protected SearchResult doInBackground() throws Exception {
                    // Phase 1: Binary search among blocks to find the correct block
                    int left = 0;
                    int right = blocks.size() - 1;
                    int targetBlockIndex = -1;

                    while (left <= right) {
                        int mid = left + (right - left) / 2;
                        List<Integer> midBlock = blocks.get(mid);

                        // Access block and increment counter
                        blockAccessCount++;
                        publish(new SearchProgress(left, right, mid, -1, blockAccessCount, "Comparando bloque", false));
                        Thread.sleep(800); // Pause to show block comparison

                        // Get range of the middle block
                        BlockRange range = getBlockRange(midBlock);

                        if (range.isEmpty()) {
                            // Skip empty blocks
                            left = mid + 1;
                            continue;
                        }

                        // Check if target is in this block's range
                        if (valueToSearch >= range.min && valueToSearch <= range.max) {
                            targetBlockIndex = mid;
                            break;
                        } else if (valueToSearch < range.min) {
                            right = mid - 1;
                        } else {
                            left = mid + 1;
                        }
                    }

                    if (targetBlockIndex == -1) {
                        return new SearchResult(false, -1, -1);
                    }

                    // Phase 2: Binary search within the found block
                    List<Integer> targetBlock = blocks.get(targetBlockIndex);
                    blockAccessCount++;
                    publish(new SearchProgress(targetBlockIndex, targetBlockIndex, targetBlockIndex, -1,
                            blockAccessCount, "Buscando en bloque", false));
                    Thread.sleep(500);

                    // Binary search within the block
                    int blockLeft = 0;
                    int blockRight = targetBlock.size() - 1;

                    while (blockLeft <= blockRight) {
                        int blockMid = blockLeft + (blockRight - blockLeft) / 2;

                        // Skip empty slots (-1)
                        if (targetBlock.get(blockMid) == -1) {
                            // Find a valid value to the left or right
                            int tempMid = blockMid;
                            while (tempMid >= blockLeft && targetBlock.get(tempMid) == -1) {
                                tempMid--;
                            }
                            if (tempMid < blockLeft) {
                                tempMid = blockMid + 1;
                                while (tempMid <= blockRight && targetBlock.get(tempMid) == -1) {
                                    tempMid++;
                                }
                                if (tempMid > blockRight) {
                                    break;
                                }
                            }
                            blockMid = tempMid;
                        }

                        publish(new SearchProgress(targetBlockIndex, targetBlockIndex, targetBlockIndex,
                                blockMid, blockAccessCount, "Examinando registro", false));
                        Thread.sleep(400);

                        if (targetBlock.get(blockMid) == valueToSearch) {
                            return new SearchResult(true, targetBlockIndex, blockMid);
                        }

                        if (targetBlock.get(blockMid) < valueToSearch) {
                            blockLeft = blockMid + 1;
                        } else {
                            blockRight = blockMid - 1;
                        }
                    }

                    return new SearchResult(false, -1, -1);
                }

                @Override
                protected void process(List<SearchProgress> chunks) {
                    SearchProgress progress = chunks.get(chunks.size() - 1);
                    view.setBlockAccessCount(progress.blockAccessCount);
                    view.setCurrentOperation(progress.operation);

                    if (progress.recordIndex != -1) {
                        view.setSearchPhase("Búsqueda en bloque");
                        view.highlightBinarySearchProgress(progress.left, progress.right, progress.mid, progress.recordIndex);
                    } else {
                        view.setSearchPhase("Búsqueda entre bloques");
                        view.highlightBinarySearchProgress(progress.left, progress.right, progress.mid, -1);
                    }
                }

                @Override
                protected void done() {
                    try {
                        SearchResult result = get();

                        if (result.found) {
                            view.highlightFoundItem(result.blockIndex, result.recordIndex);
                            view.setResultMessage("Clave " + valueToSearch + " encontrada en Bloque " +
                                    (result.blockIndex + 1) + ", Registro " + (result.recordIndex + 1) +
                                    ". Accesos a bloques: " + blockAccessCount, true);
                            view.setSearchPhase("Encontrada");
                        } else {
                            view.clearHighlights();
                            view.setResultMessage("Clave " + valueToSearch + " no encontrada. Accesos a bloques: " +
                                    blockAccessCount, false);
                            view.setSearchPhase("No encontrada");
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

    // External binary search algorithm implementation
    private SearchResult externalBinarySearch(int target) {
        // Phase 1: Binary search among blocks
        int left = 0;
        int right = blocks.size() - 1;
        int targetBlockIndex = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            List<Integer> midBlock = blocks.get(mid);
            blockAccessCount++; // Increment block access counter

            BlockRange range = getBlockRange(midBlock);

            if (range.isEmpty()) {
                left = mid + 1;
                continue;
            }

            // Check if target is in this block's range
            if (target >= range.min && target <= range.max) {
                targetBlockIndex = mid;
                break;
            } else if (target < range.min) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        if (targetBlockIndex == -1) {
            return new SearchResult(false, -1, -1);
        }

        // Phase 2: Binary search within the found block
        List<Integer> targetBlock = blocks.get(targetBlockIndex);
        blockAccessCount++; // Another block access for searching within

        int blockLeft = 0;
        int blockRight = targetBlock.size() - 1;

        while (blockLeft <= blockRight) {
            int blockMid = blockLeft + (blockRight - blockLeft) / 2;

            // Skip empty slots (-1)
            if (targetBlock.get(blockMid) == -1) {
                int tempMid = blockMid;
                while (tempMid >= blockLeft && targetBlock.get(tempMid) == -1) {
                    tempMid--;
                }
                if (tempMid < blockLeft) {
                    tempMid = blockMid + 1;
                    while (tempMid <= blockRight && targetBlock.get(tempMid) == -1) {
                        tempMid++;
                    }
                    if (tempMid > blockRight) {
                        break;
                    }
                }
                blockMid = tempMid;
            }

            if (targetBlock.get(blockMid) == target) {
                return new SearchResult(true, targetBlockIndex, blockMid);
            }

            if (targetBlock.get(blockMid) < target) {
                blockLeft = blockMid + 1;
            } else {
                blockRight = blockMid - 1;
            }
        }

        return new SearchResult(false, -1, -1);
    }

    private BlockRange getBlockRange(List<Integer> block) {
        List<Integer> validValues = new ArrayList<>();
        for (Integer value : block) {
            if (value != null && value != -1) {
                validValues.add(value);
            }
        }

        if (validValues.isEmpty()) {
            return new BlockRange(true);
        }

        return new BlockRange(Collections.min(validValues), Collections.max(validValues));
    }

    private boolean areBlocksSorted() {
        Integer prevMax = null;

        for (List<Integer> block : blocks) {
            BlockRange range = getBlockRange(block);

            if (range.isEmpty()) {
                continue;
            }

            // Check if the block itself is sorted
            List<Integer> validValues = new ArrayList<>();
            for (Integer value : block) {
                if (value != null && value != -1) {
                    validValues.add(value);
                }
            }

            for (int i = 1; i < validValues.size(); i++) {
                if (validValues.get(i - 1) > validValues.get(i)) {
                    return false; // Block is not internally sorted
                }
            }

            // Check if blocks are sorted relative to each other
            if (prevMax != null && prevMax > range.min) {
                return false;
            }

            prevMax = range.max;
        }

        return true;
    }

    private void generateNewBlocks(int blockCount) {
        blocks.clear();

        for (int i = 0; i < blockCount; i++) {
            List<Integer> block = new ArrayList<>();
            for (int j = 0; j < blockSize; j++) {
                block.add(-1); // Initialize with empty slots
            }
            blocks.add(block);
        }

        // Save to file
        saveDataToFile();

        // Display in table
        displayDataInTable();

        // Show information about allowed range
        int minValue = getMinValue(digitLimit);
        int maxValue = getMaxValue(digitLimit);
        view.setResultMessage("Bloques generados. Rango permitido: " + minValue + " - " + maxValue, true);
        view.setBlockAccessCount(0);
        view.setCurrentOperation("Ninguna");
        view.setSearchPhase("Inactiva");
    }

    public void insertValue(int value) throws IllegalArgumentException {
        // Check if value already exists
        for (int blockIndex = 0; blockIndex < blocks.size(); blockIndex++) {
            List<Integer> block = blocks.get(blockIndex);
            for (Integer record : block) {
                if (record != null && record != -1 && record == value) {
                    throw new IllegalArgumentException("La clave " + value + " ya existe en el archivo");
                }
            }
        }

        // Find first available slot
        boolean inserted = false;
        for (int blockIndex = 0; blockIndex < blocks.size() && !inserted; blockIndex++) {
            List<Integer> block = blocks.get(blockIndex);
            for (int recordIndex = 0; recordIndex < block.size(); recordIndex++) {
                if (block.get(recordIndex) == -1) {
                    block.set(recordIndex, value);
                    inserted = true;
                    break;
                }
            }
        }

        if (inserted) {
            // Sort all blocks to maintain binary search property
            sortAllBlocks();
            view.setResultMessage("Clave " + value + " insertada correctamente", true);
        } else {
            view.setResultMessage("El archivo está lleno", false);
        }
    }

    public void deleteValue(int value) {
        boolean found = false;

        // Search for the value and delete it
        for (int blockIndex = 0; blockIndex < blocks.size() && !found; blockIndex++) {
            List<Integer> block = blocks.get(blockIndex);
            for (int recordIndex = 0; recordIndex < block.size(); recordIndex++) {
                if (block.get(recordIndex) != null && block.get(recordIndex) == value) {
                    block.set(recordIndex, -1);
                    found = true;
                    break;
                }
            }
        }

        if (found) {
            // Sort blocks after deletion
            sortAllBlocks();
            view.setResultMessage("Clave " + value + " eliminada correctamente", true);
        } else {
            view.setResultMessage("Clave " + value + " no encontrada en el archivo", false);
        }
    }

    private void sortAllBlocks() {
        // Collect all valid values
        List<Integer> allValues = new ArrayList<>();
        for (List<Integer> block : blocks) {
            for (Integer value : block) {
                if (value != null && value != -1) {
                    allValues.add(value);
                }
            }
        }

        // Sort all values
        Collections.sort(allValues);

        // Clear all blocks
        for (List<Integer> block : blocks) {
            Collections.fill(block, -1);
        }

        // Redistribute sorted values across blocks to maintain sorted order between blocks
        int valueIndex = 0;
        for (int blockIndex = 0; blockIndex < blocks.size() && valueIndex < allValues.size(); blockIndex++) {
            List<Integer> block = blocks.get(blockIndex);
            for (int recordIndex = 0; recordIndex < blockSize && valueIndex < allValues.size(); recordIndex++) {
                block.set(recordIndex, allValues.get(valueIndex));
                valueIndex++;
            }
        }

        // Save to file
        saveDataToFile();

        // Update display
        displayDataInTable();
    }

    private void goBack() {
        // Close current view
        view.dispose();

        // Show menu view if available
        if (menuView != null) {
            menuView.setVisible(true);
        }
    }

    // Helper classes
    private static class SearchResult {
        boolean found;
        int blockIndex;
        int recordIndex;

        SearchResult(boolean found, int blockIndex, int recordIndex) {
            this.found = found;
            this.blockIndex = blockIndex;
            this.recordIndex = recordIndex;
        }
    }

    private static class SearchProgress {
        int left, right, mid;
        int recordIndex;
        int blockAccessCount;
        String operation;
        boolean found;

        SearchProgress(int left, int right, int mid, int recordIndex, int blockAccessCount, String operation, boolean found) {
            this.left = left;
            this.right = right;
            this.mid = mid;
            this.recordIndex = recordIndex;
            this.blockAccessCount = blockAccessCount;
            this.operation = operation;
            this.found = found;
        }
    }

    private static class BlockRange {
        int min, max;
        boolean empty;

        BlockRange(boolean empty) {
            this.empty = empty;
        }

        BlockRange(int min, int max) {
            this.min = min;
            this.max = max;
            this.empty = false;
        }

        boolean isEmpty() {
            return empty;
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExternalBinarySearchView newView = new ExternalBinarySearchView();
            ExternalBinarySearchController controller = new ExternalBinarySearchController(newView);
            newView.showWindow();
        });
    }
}
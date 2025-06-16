package controller.external_search;

import view.external_search.ExternalSequentialSearchView;
import view.menu.ExternalSearchMenuView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;
import java.util.List;

public class ExternalSequentialSearchController {

    private ExternalSequentialSearchView view;
    private ExternalSearchMenuView menuView;
    private List<List<Integer>> blocks;
    private int blockSize = 5;
    private int digitLimit = 2;
    private int blockAccessCount = 0;
    private String currentFilePath = "src/utilities/datos-busqueda-secuencial-externa.txt";

    public ExternalSequentialSearchController(ExternalSequentialSearchView view) {
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

        // NUEVA FUNCIONALIDAD: Listener para cargar archivo externo
        view.addLoadFromFileListener(e -> loadFromExternalFile());

        view.addBackListener(e -> goBack());
    }

    // NUEVO MÉTODO: Cargar archivo externo
    private void loadFromExternalFile() {
        JFileChooser fileChooser = new JFileChooser();

        // Configurar el filtro para archivos de texto
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Establecer directorio inicial
        fileChooser.setCurrentDirectory(new File("src/utilities"));

        // Configurar título del diálogo
        fileChooser.setDialogTitle("Seleccionar archivo de datos de búsqueda secuencial externa");

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
                    view.setCurrentBlock("Ninguno");

                    // Contar total de registros válidos
                    int totalRecords = 0;
                    for (List<Integer> block : blocks) {
                        for (Integer value : block) {
                            if (value != null && value != -1) {
                                totalRecords++;
                            }
                        }
                    }

                    view.setResultMessage("Datos cargados desde: " + selectedFile.getName() +
                            " (" + blocks.size() + " bloques, " + totalRecords + " registros)", true);
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

    // NUEVO MÉTODO: Cargar datos desde un archivo externo específico
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

            // Reset counters and highlights
            blockAccessCount = 0;
            view.setBlockAccessCount(blockAccessCount);
            view.setSearchPhase("Inactiva");
            view.clearHighlights();

            // Perform external sequential search
            SearchResult result = externalSequentialSearch(valueToSearch);

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

            // Reset counters and highlights
            blockAccessCount = 0;
            view.setBlockAccessCount(blockAccessCount);
            view.setSearchPhase("Iniciando búsqueda optimizada");
            view.clearHighlights();

            // Create SwingWorker for animation
            SwingWorker<SearchResult, SearchProgress> worker = new SwingWorker<SearchResult, SearchProgress>() {
                @Override
                protected SearchResult doInBackground() throws Exception {
                    for (int blockIndex = 0; blockIndex < blocks.size(); blockIndex++) {
                        List<Integer> block = blocks.get(blockIndex);

                        // Simulate block access for range check
                        blockAccessCount++;
                        publish(new SearchProgress(blockIndex, -1, blockAccessCount, false, "Verificando rango"));
                        Thread.sleep(400); // Pause to show range verification

                        // Get the range of the current block
                        BlockRange range = getBlockRange(block);

                        // Skip empty blocks
                        if (range.isEmpty()) {
                            publish(new SearchProgress(blockIndex, -1, blockAccessCount, false, "Bloque vacío - saltando"));
                            Thread.sleep(200);
                            continue;
                        }

                        // OPTIMIZACIÓN: Si el target es mayor que el máximo del bloque, continuar al siguiente
                        if (valueToSearch > range.max) {
                            publish(new SearchProgress(blockIndex, -1, blockAccessCount, false,
                                    "Target > Max (" + range.max + ") - saltando"));
                            Thread.sleep(300);
                            continue; // Skip this block entirely
                        }

                        // OPTIMIZACIÓN: Si el target es menor que el mínimo del bloque, no está en ningún bloque posterior
                        if (valueToSearch < range.min) {
                            publish(new SearchProgress(blockIndex, -1, blockAccessCount, false,
                                    "Target < Min (" + range.min + ") - terminando búsqueda"));
                            Thread.sleep(300);
                            break; // No need to check further blocks
                        }

                        // El target podría estar en este bloque, buscar dentro del bloque
                        publish(new SearchProgress(blockIndex, -1, blockAccessCount, false,
                                "Rango válido [" + range.min + "-" + range.max + "] - buscando dentro"));
                        Thread.sleep(300);

                        // Search within the block
                        for (int recordIndex = 0; recordIndex < block.size(); recordIndex++) {
                            if (block.get(recordIndex) == -1) continue; // Skip empty slots

                            // Publish current record being examined
                            publish(new SearchProgress(blockIndex, recordIndex, blockAccessCount, false, "Examinando registro"));
                            Thread.sleep(250); // Pause to show record examination

                            if (block.get(recordIndex) == valueToSearch) {
                                return new SearchResult(true, blockIndex, recordIndex);
                            }
                        }
                    }
                    return new SearchResult(false, -1, -1);
                }

                @Override
                protected void process(List<SearchProgress> chunks) {
                    SearchProgress progress = chunks.get(chunks.size() - 1);
                    view.setBlockAccessCount(progress.blockAccessCount);
                    view.setCurrentBlock("Bloque " + (progress.blockIndex + 1));
                    view.setSearchPhase(progress.phase);
                    view.highlightBlockAccess(progress.blockIndex, progress.recordIndex);
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
                        view.setCurrentBlock("Ninguno");
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

    // External sequential search algorithm implementation with range optimization
    private SearchResult externalSequentialSearch(int target) {
        for (int blockIndex = 0; blockIndex < blocks.size(); blockIndex++) {
            List<Integer> block = blocks.get(blockIndex);
            blockAccessCount++; // Increment block access counter for range check

            // Get the range of the current block
            BlockRange range = getBlockRange(block);

            // Skip empty blocks
            if (range.isEmpty()) {
                continue;
            }

            // OPTIMIZACIÓN: Si el target es mayor que el máximo del bloque, continuar al siguiente
            if (target > range.max) {
                continue; // Skip this block entirely
            }

            // OPTIMIZACIÓN: Si el target es menor que el mínimo del bloque, no está en ningún bloque posterior
            // (asumiendo que los bloques están ordenados)
            if (target < range.min) {
                break; // No need to check further blocks
            }

            // El target podría estar en este bloque, buscar dentro del bloque
            for (int recordIndex = 0; recordIndex < block.size(); recordIndex++) {
                if (block.get(recordIndex) != -1 && block.get(recordIndex) == target) {
                    return new SearchResult(true, blockIndex, recordIndex);
                }
            }
        }
        return new SearchResult(false, -1, -1);
    }

    // Helper method to get block range
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
        view.setCurrentBlock("Ninguno");
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
            // Sort all blocks to maintain order
            sortBlocks();
            view.setResultMessage("Clave " + value + " insertada correctamente", true);

            // NUEVO: Actualizar automáticamente la tabla
            displayDataInTable();
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
            sortBlocks();
            view.setResultMessage("Clave " + value + " eliminada correctamente", true);

            // NUEVO: Actualizar automáticamente la tabla
            displayDataInTable();
        } else {
            view.setResultMessage("Clave " + value + " no encontrada en el archivo", false);
        }
    }

    private void sortBlocks() {
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

        // Redistribute sorted values across blocks
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

    // Helper classes for search results and progress
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
        int blockIndex;
        int recordIndex;
        int blockAccessCount;
        boolean found;
        String phase;

        SearchProgress(int blockIndex, int recordIndex, int blockAccessCount, boolean found, String phase) {
            this.blockIndex = blockIndex;
            this.recordIndex = recordIndex;
            this.blockAccessCount = blockAccessCount;
            this.found = found;
            this.phase = phase;
        }

        SearchProgress(int blockIndex, int recordIndex, int blockAccessCount, boolean found) {
            this(blockIndex, recordIndex, blockAccessCount, found, "Buscando");
        }
    }

    // Helper class for block range
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
            ExternalSequentialSearchView newView = new ExternalSequentialSearchView();
            ExternalSequentialSearchController controller = new ExternalSequentialSearchController(newView);
            newView.showWindow();
        });
    }
}
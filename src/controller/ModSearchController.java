package controller;

import view.HashAlgorithmView;
import view.ModSearchView;
import view.ColisionView;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModSearchController {

    private final ModSearchView view;
    private final List<List<Integer>> hashTable;
    private HashAlgorithmView hashAlgorithmView;
    private int tableSize;
    private int digitLimit = 2;
    private int maxColumns = 1;


    public ModSearchController(ModSearchView view) {
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
                List<Integer> row = new ArrayList<>();
                row.add(-1);
                hashTable.add(row);
            }
        }

        // Display data in the table
        displayDataInTable();
    }

    public void setHashAlgorithmView(HashAlgorithmView hashAlgorithmView) {
        this.hashAlgorithmView = hashAlgorithmView;
    }

    private void initComponents() {
        view.addSearchListener(e -> performSearch());
        view.addGenerateArrayListener(e -> {
            String input = view.getArraySize();
            String digitLimitInput = view.getDigitLimit();

            if (!input.isEmpty() && !digitLimitInput.isEmpty()) {
                try {
                    int newSize = Integer.parseInt(input);
                    digitLimit = Integer.parseInt(digitLimitInput);

                    if (digitLimit < 1 || digitLimit > 5) {
                        view.setResultMessage("El límite de dígitos debe estar entre 1 y 5", false);
                        return;
                    }

                    generateNewHashTable(newSize);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese valores numéricos válidos", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese el tamaño de la tabla y el límite de dígitos", false);
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
            File file = new File("src/utilities/datos-hash-modulo.txt");

            if (!file.exists()) {
                System.err.println("Data file does not exist: " + file.getAbsolutePath());
                return;
            }

            System.out.println("=== FILE LOADING DEBUG ===");
            System.out.println("File found: " + file.getAbsolutePath());

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String firstLine = reader.readLine();
                System.out.println("First line: " + firstLine);

                if (firstLine == null || firstLine.trim().isEmpty()) {
                    System.out.println("First line empty, nothing to load");
                    return;
                }

                if (firstLine.startsWith("[") && firstLine.endsWith("]") && !firstLine.contains("],")) {
                    System.out.println("Old format detected");
                    loadOldFormat(firstLine);
                } else {
                    System.out.println("New format detected");
                    loadNewFormat(firstLine, reader);
                }
            }

            System.out.println("Total rows loaded: " + hashTable.size());
            System.out.println("Loaded content:");
            for (int i = 0; i < hashTable.size(); i++) {
                System.out.println("  Row " + i + ": " + hashTable.get(i));
            }
            System.out.println("=== END LOADING DEBUG ===");

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadOldFormat(String line) {
        System.out.println("Loading old format: " + line);
        line = line.replace("[", "").replace("]", "");
        String[] values = line.split(",");

        maxColumns = 1;

        for (String value : values) {
            try {
                int num = Integer.parseInt(value.trim());
                List<Integer> row = new ArrayList<>();
                row.add(num);
                hashTable.add(row);
                System.out.println("  Added row: [" + num + "]");
            } catch (NumberFormatException e) {
                System.err.println("Non-numeric value found: " + value);
                List<Integer> row = new ArrayList<>();
                row.add(-1);
                hashTable.add(row);
            }
        }

        System.out.println("Loaded " + hashTable.size() + " rows from old format");
    }

    private void loadNewFormat(String firstLine, BufferedReader reader) throws IOException {
        maxColumns = 1;
        int lineNumber = 1;

        // Process first line
        if (!firstLine.startsWith("#")) {
            System.out.println("Processing line " + lineNumber + ": " + firstLine);
            processRowLine(firstLine);
        } else {
            System.out.println("Skipping comment line " + lineNumber + ": " + firstLine);
        }

        // Process remaining lines
        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (!line.trim().isEmpty() && !line.startsWith("#")) {
                System.out.println("Processing line " + lineNumber + ": " + line);
                processRowLine(line);
            } else if (line.startsWith("#")) {
                System.out.println("Skipping comment line " + lineNumber + ": " + line);
            } else {
                System.out.println("Skipping empty line " + lineNumber);
            }
        }

        System.out.println("Total lines processed: " + lineNumber);
        System.out.println("Rows added to hashTable: " + hashTable.size());
    }

    private void processRowLine(String line) {
        System.out.println("Processing line: '" + line + "'");
        List<Integer> row = new ArrayList<>();

        // Remove brackets if present
        line = line.replace("[", "").replace("]", "");

        if (!line.trim().isEmpty()) {
            // Split by comma
            String[] values = line.split(",");

            // Convert to integers and add to row
            for (String value : values) {
                try {
                    int num = Integer.parseInt(value.trim());
                    row.add(num);
                    System.out.println("  Added value: " + num);
                } catch (NumberFormatException e) {
                    System.err.println("Non-numeric value found: " + value);
                    row.add(-1);
                }
            }
        }

        // If the row is empty, add -1
        if (row.isEmpty()) {
            row.add(-1);
            System.out.println("  Empty row, adding -1");
        }

        maxColumns = Math.max(maxColumns, row.size());
        hashTable.add(row);
        System.out.println("  Fila agregada: " + row + ", maxColumns=" + maxColumns);
    }

    private void initializeEmptyHashTable() {
        hashTable.clear();
        maxColumns = 1;

        for (int i = 0; i < tableSize; i++) {
            List<Integer> row = new ArrayList<>();
            row.add(-1); // Posición vacía
            hashTable.add(row);
        }
        System.out.println("Tabla hash inicializada con " + tableSize + " filas vacías");
    }

    private void displayDataInTable() {
        System.out.println("=== DEBUGGING TABLA ===");
        System.out.println("tableSize=" + tableSize);
        System.out.println("hashTable.size()=" + hashTable.size());
        System.out.println("maxColumns=" + maxColumns);

        // Mostrar contenido de hashTable
        for (int i = 0; i < hashTable.size(); i++) {
            System.out.println("Fila " + i + ": " + hashTable.get(i));
        }

        // Crear encabezados dinámicos basados en el número máximo de columnas
        String[] headers = new String[maxColumns + 1]; // +1 para la columna de posición
        headers[0] = "Posición";
        for (int i = 1; i <= maxColumns; i++) {
            headers[i] = "Clave " + i;
        }

        System.out.println("Headers: " + java.util.Arrays.toString(headers));

        // Crear datos de la tabla
        Object[][] tableData = new Object[tableSize][maxColumns + 1];

        for (int i = 0; i < tableSize; i++) {
            tableData[i][0] = i + 1; // Posición (empezando desde 1)

            List<Integer> row = i < hashTable.size() ? hashTable.get(i) : null;

            // Llenar las columnas de claves
            for (int j = 1; j <= maxColumns; j++) {
                int valueIndex = j - 1;
                if (row != null && valueIndex < row.size()) {
                    Integer value = row.get(valueIndex);
                    tableData[i][j] = (value != null && value == -1) ? "" : value;
                } else {
                    tableData[i][j] = "";
                }
            }

            System.out.println("TableData fila " + i + ": " + java.util.Arrays.toString(tableData[i]));
        }

        System.out.println("Enviando a vista: " + tableData.length + " filas");
        view.setTableData(tableData, headers);
        System.out.println("=== FIN DEBUGGING ===");
    }

    // Method to perform search
    private void performSearch() {
        if (view.isVisualizationEnabled()) {
            performAnimatedSearch();
        } else {
            performNormalSearch();
        }
    }

    // Sarch with no animation
    private void performNormalSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese una clave para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);
            int originalHashPosition = valueToSearch % tableSize;

            // Limpiar highlights anteriores
            view.clearHighlights();

            Integer foundPosition = searchInOriginalPosition(valueToSearch, originalHashPosition);

            if (foundPosition == null) {
                foundPosition = searchSequential(valueToSearch, originalHashPosition);
            }

            if (foundPosition == null) {
                foundPosition = searchExponential(valueToSearch, originalHashPosition);
            }

            if (foundPosition == null) {
                foundPosition = searchInExtendedTable(valueToSearch, originalHashPosition);
            }

            if (foundPosition != null) {
                view.highlightFoundItem(foundPosition);
                view.setResultMessage("Clave " + valueToSearch + " encontrada en la posición " +
                                (foundPosition + 1) +
                                (foundPosition == originalHashPosition ?
                                        " (posición hash original)" :
                                        " (reubicada por colisión, hash original: " + (originalHashPosition + 1) + ")"),
                        true);
            } else {
                view.setResultMessage("Clave " + valueToSearch + " no encontrada. Función hash: " +
                        valueToSearch + " % " + tableSize + " = " + (originalHashPosition + 1) +
                        ". Se buscó en todas las posiciones posibles.", false);
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
            int originalHashPosition = valueToSearch % tableSize;

            view.clearHighlights();

            SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    publish(originalHashPosition);
                    Thread.sleep(500);

                    Integer foundPosition = searchInOriginalPosition(valueToSearch, originalHashPosition);
                    if (foundPosition != null) {
                        return foundPosition;
                    }

                    int position = (originalHashPosition + 1) % tableSize;
                    int attempts = 0;

                    while (position != originalHashPosition && attempts < tableSize) {
                        publish(position);
                        Thread.sleep(400);

                        if (position < hashTable.size()) {
                            List<Integer> row = hashTable.get(position);
                            for (Integer value : row) {
                                if (value == valueToSearch) {
                                    return position;
                                }
                            }
                        }
                        position = (position + 1) % tableSize;
                        attempts++;
                    }

                    int i = 1;
                    attempts = 0;

                    while (attempts < tableSize) {
                        int expPosition = (originalHashPosition + (i * i)) % tableSize;
                        publish(expPosition);
                        Thread.sleep(400);

                        if (expPosition >= 0 && expPosition < hashTable.size()) {
                            List<Integer> row = hashTable.get(expPosition);
                            for (Integer value : row) {
                                if (value == valueToSearch) {
                                    return expPosition;
                                }
                            }
                        }

                        i++;
                        attempts++;
                    }

                    for (int j = tableSize - 1; j >= 0; j--) {
                        if (j >= hashTable.size()) {
                            continue;
                        }

                        publish(j);
                        Thread.sleep(400);

                        List<Integer> row = hashTable.get(j);
                        for (Integer value : row) {
                            if (value == valueToSearch) {
                                return j;
                            }
                        }
                    }

                    return -1;
                }

                @Override
                protected void process(List<Integer> chunks) {
                    int currentIndex = chunks.get(chunks.size() - 1);
                    view.highlightSearchProgress(currentIndex);
                }

                @Override
                protected void done() {
                    try {
                        int position = get();
                        int originalHashPosition = valueToSearch % tableSize;

                        if (position != -1) {
                            // Encontrado - resaltar en verde
                            view.highlightFoundItem(position);
                            view.setResultMessage("Clave " + valueToSearch + " encontrada en la posición " +
                                            (position + 1) +
                                            (position == originalHashPosition ?
                                                    " (posición hash original)" :
                                                    " (reubicada por colisión, hash original: " + (originalHashPosition + 1) + ")"),
                                    true);
                        } else {
                            // No encontrado - limpiar highlights
                            view.clearHighlights();
                            view.setResultMessage("Clave " + valueToSearch + " no encontrada. Función hash: " +
                                    valueToSearch + " % " + tableSize + " = " + (originalHashPosition + 1) +
                                    ". Se buscó en todas las posiciones posibles.", false);
                        }
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

    private Integer searchInOriginalPosition(int valueToSearch, int hashPosition) {
        if (hashPosition < hashTable.size()) {
            List<Integer> row = hashTable.get(hashPosition);
            for (int i = 0; i < row.size(); i++) {
                if (row.get(i) == valueToSearch) {
                    return hashPosition;
                }
            }
        }
        return null;
    }

    private Integer searchSequential(int valueToSearch, int originalHashPos) {
        int position = (originalHashPos + 1) % tableSize;
        int attempts = 0;

        while (position != originalHashPos && attempts < tableSize) {
            if (position < hashTable.size()) {
                List<Integer> row = hashTable.get(position);
                for (Integer value : row) {
                    if (value == valueToSearch) {
                        return position;
                    }
                }
            }
            position = (position + 1) % tableSize;
            attempts++;
        }

        return null;
    }

    private Integer searchExponential(int valueToSearch, int originalHashPos) {
        int i = 1;
        int attempts = 0;

        while (attempts < tableSize) {
            int position = (originalHashPos + (i * i)) % tableSize;

            if (position >= 0 && position < hashTable.size()) {
                List<Integer> row = hashTable.get(position);
                for (Integer value : row) {
                    if (value == valueToSearch) {
                        return position;
                    }
                }
            }

            i++;
            attempts++;
        }
        return null;
    }

    private Integer searchInExtendedTable(int valueToSearch, int originalHashPos) {
        for (int i = tableSize - 1; i >= 0; i--) {
            if (i >= hashTable.size()) {
                continue;
            }

            List<Integer> row = hashTable.get(i);
            for (Integer value : row) {
                if (value == valueToSearch) {
                    return i;
                }
            }
        }

        return null;
    }

    private void generateNewHashTable(int newSize) {
        this.tableSize = newSize;

        hashTable.clear();
        maxColumns = 1;

        for (int i = 0; i < newSize; i++) {
            List<Integer> row = new ArrayList<>();
            row.add(-1);
            hashTable.add(row);
        }

        saveDataToFile();
        displayDataInTable();

        int minValue = getMinValue(digitLimit);
        int maxValue = getMaxValue(digitLimit);
        view.setResultMessage("Tabla hash generada. Rango permitido: " + minValue + " - " + maxValue, true);
    }

    // Method to insert values using hash function
    public void insertValue(int value) {
        for (List<Integer> row : hashTable) {
            for (Integer existingValue : row) {
                if (existingValue != null && existingValue != -1 && existingValue == value) {
                    view.setResultMessage("La clave " + value + " ya existe en la tabla hash", false);
                    return;
                }
            }
        }

        int hashPosition = value % tableSize;

        if (hashPosition < hashTable.size()) {
            List<Integer> row = hashTable.get(hashPosition);

            if (row.get(0) == -1) {
                row.set(0, value);
                saveDataToFile();
                displayDataInTable();
                view.setResultMessage("Clave " + value + " insertada en la posición hash " + (hashPosition + 1), true);
            } else {
                handleCollision(value, hashPosition);
            }
        } else {
            view.setResultMessage("Posición hash " + (hashPosition + 1) + " fuera de rango", false);
        }
    }

    private void handleCollision(int valueToInsert, int hashPosition) {
        List<Integer> row = hashTable.get(hashPosition);
        int currentValue = row.get(0);

        ColisionView colisionView = new ColisionView();
        colisionView.setCollisionInfo(valueToInsert, hashPosition);

        view.setResultMessage("Colisión detectada: Clave " + valueToInsert +
                " debería ir en la posición " + (hashPosition + 1) +
                " que ya está ocupada por " + currentValue, false);

        colisionView.addSequentialSolutionListener(e -> {
            solveCollisionSequential(valueToInsert, hashPosition);
            colisionView.dispose();
        });

        colisionView.addExponentialSolutionListener(e -> {
            solveCollisionExponential(valueToInsert, hashPosition);
            colisionView.dispose();
        });

        colisionView.addTableSolutionListener(e -> {
            solveCollisionWithAdditionalColumn(valueToInsert, hashPosition);
            colisionView.dispose();
        });

        colisionView.addCancelListener(e -> {
            colisionView.dispose();
            view.setResultMessage("Inserción cancelada: colisión en posición " + (hashPosition + 1), false);
        });

        colisionView.showWindow();
    }

    private void solveCollisionSequential(int valueToInsert, int originalHashPos) {
        int position = (originalHashPos + 1) % tableSize;
        int attempts = 0;

        while (position != originalHashPos && attempts < tableSize) {
            List<Integer> row = hashTable.get(position);
            if (row.get(0) == -1) {
                row.set(0, valueToInsert);
                saveDataToFile();
                displayDataInTable();
                view.setResultMessage("Clave " + valueToInsert + " insertada en posición " + (position + 1) +
                        " mediante solución secuencial (colisión en posición " + (originalHashPos + 1) + ")", true);
                return;
            }
            position = (position + 1) % tableSize;
            attempts++;
        }
        view.setResultMessage("No se pudo insertar " + valueToInsert + ": tabla llena", false);
    }

    private void solveCollisionExponential(int valueToInsert, int originalHashPos) {
        int i = 1;
        int attempts = 0;

        while (attempts < tableSize) {
            int position = (originalHashPos + (i * i)) % tableSize;

            if (position >= 0 && position < hashTable.size()) {
                List<Integer> row = hashTable.get(position);
                if (row.get(0) == -1) {
                    row.set(0, valueToInsert);
                    saveDataToFile();
                    displayDataInTable();
                    view.setResultMessage("Clave " + valueToInsert + " insertada en posición " + (position + 1) +
                            " mediante solución exponencial (colisión en posición " + (originalHashPos + 1) + ")", true);
                    return;
                }
            }

            i++;
            attempts++;
        }
        view.setResultMessage("No se pudo insertar " + valueToInsert + " usando prueba cuadrática", false);
    }

    private void solveCollisionWithAdditionalColumn(int valueToInsert, int originalHashPos) {
        List<Integer> row = hashTable.get(originalHashPos);

        row.add(valueToInsert);

        maxColumns = Math.max(maxColumns, row.size());

        saveDataToFile();
        displayDataInTable();

        view.setResultMessage("Clave " + valueToInsert + " insertada en posición " + (originalHashPos + 1) +
                ", columna " + row.size() + " (solución por columnas adicionales)", true);
    }

    private void saveDataToFile() {
        try {
            File file = new File("src/utilities/datos-hash-modulo.txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                for (List<Integer> row : hashTable) {
                    writer.println(row.toString());
                }

                writer.println("# Formato: cada línea representa una fila de la tabla hash");
                writer.println("# [valor1, valor2, ...] para múltiples valores en la misma posición");
                writer.println("# [-1] para posiciones vacías");
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to delete a value
    public void deleteValue(int value) {
        boolean found = false;
        int foundRow = -1;
        int foundColumn = -1;

        for (int i = 0; i < hashTable.size(); i++) {
            List<Integer> row = hashTable.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) != null && row.get(j) == value) {
                    found = true;
                    foundRow = i;
                    foundColumn = j;
                    break;
                }
            }
            if (found) break;
        }

        if (found) {
            List<Integer> row = hashTable.get(foundRow);

            if (foundColumn == 0 && row.size() > 1) {
                row.remove(0);
            } else if (foundColumn == row.size() - 1) {
                row.remove(foundColumn);
            } else {
                row.set(foundColumn, -1);
            }

            if (row.isEmpty()) {
                row.add(-1);
            }

            maxColumns = 1;
            for (List<Integer> tableRow : hashTable) {
                maxColumns = Math.max(maxColumns, tableRow.size());
            }

            saveDataToFile();
            displayDataInTable();

            int hashPosition = value % tableSize;
            if (foundRow == hashPosition) {
                view.setResultMessage("Clave " + value + " eliminada de la posición hash original " + (foundRow + 1) +
                        ", columna " + (foundColumn + 1), true);
            } else {
                view.setResultMessage("Clave " + value + " eliminada de la posición " + (foundRow + 1) +
                        ", columna " + (foundColumn + 1) +
                        " (hash original: " + (hashPosition + 1) + ")", true);
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
}

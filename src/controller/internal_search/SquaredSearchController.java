package controller.internal_search;

import view.menu.HashAlgorithmView;
import view.colision.ColisionView;
import view.internal_search.SquaredSearchView;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SquaredSearchController {

    private final SquaredSearchView view;
    private final List<Integer> dataArray;
    private HashAlgorithmView hashAlgorithmView;
    private int tableSize;
    private int digitLimit = 2; // NUEVA: Límite de dígitos

    public SquaredSearchController(SquaredSearchView view) {
        this.view = view;
        this.dataArray = new ArrayList<>();

        // Initialize components
        initComponents();

        // Load data from file when creating the controller
        loadDataFromFile();

        // Create hash table
        this.tableSize = dataArray.isEmpty() ? 10 : dataArray.size();

        // Display data in the table
        displayDataInTable();
    }

    public void setHashAlgorithmView(HashAlgorithmView hashAlgorithmView) {
        this.hashAlgorithmView = hashAlgorithmView;
    }

    private void initComponents() {
        // Add action listeners to buttons
        view.addSearchListener(e -> performSearch());

        view.addGenerateArrayListener(e -> {
            String input = view.getArraySize();
            String digitLimitInput = view.getDigitLimit(); // NUEVA: Obtener límite de dígitos

            if (!input.isEmpty() && !digitLimitInput.isEmpty()) {
                try {
                    int newSize = Integer.parseInt(input);
                    digitLimit = Integer.parseInt(digitLimitInput); // NUEVA: Establecer límite

                    // NUEVA: Validar rango de dígitos
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
            String digitLimitInput = view.getDigitLimit(); // NUEVA: Obtener límite de dígitos

            if (!input.isEmpty() && !digitLimitInput.isEmpty()) {
                try {
                    int value = Integer.parseInt(input);
                    digitLimit = Integer.parseInt(digitLimitInput); // NUEVA: Establecer límite

                    // NUEVA: Validar cantidad de dígitos
                    if (!isValidDigitCount(value, digitLimit)) {
                        view.setResultMessage("El valor debe tener exactamente " + digitLimit + " dígito(s)", false);
                        return;
                    }

                    insertValue(value);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese valores numéricos válidos", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese un valor para insertar y el límite de dígitos", false);
            }
        });

        view.addDeleteValueListener(e -> {
            String input = view.getDeleteValue();
            if (!input.isEmpty()) {
                try {
                    int value = Integer.parseInt(input);
                    deleteValue(value);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese un valor numérico válido", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese un valor para eliminar", false);
            }
        });
        view.addBackListener(e -> goBack());
    }

    // NUEVA: Validar cantidad de dígitos
    private boolean isValidDigitCount(int value, int digitLimit) {
        String valueStr = String.valueOf(Math.abs(value));
        return valueStr.length() == digitLimit;
    }

    // NUEVA: Obtener valor mínimo basado en el límite de dígitos
    private int getMinValue(int digitLimit) {
        if (digitLimit == 1) return 0;
        return (int) Math.pow(10, digitLimit - 1);
    }

    // NUEVA: Obtener valor máximo basado en el límite de dígitos
    private int getMaxValue(int digitLimit) {
        return (int) Math.pow(10, digitLimit) - 1;
    }

    private void loadDataFromFile() {
        dataArray.clear();

        try {
            File file = new File("src/utilities/datos-hash-cuadrado.txt");

            if (!file.exists()) {
                System.err.println("El archivo de datos no existe: " + file.getAbsolutePath());
                for (int i = 0; i < tableSize; i++) {
                    dataArray.add(-1);
                }
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();

                if (line != null) {
                    // Remove brackets if present
                    line = line.replace("[", "").replace("]", "");

                    // Split by comma
                    String[] values = line.split(",");

                    // Convert to integers and add to array
                    for (String value : values) {
                        try {
                            int num = Integer.parseInt(value.trim());
                            dataArray.add(num);
                        } catch (NumberFormatException e) {
                            System.err.println("Valor no numérico encontrado: " + value);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
        }

        if (dataArray.isEmpty()) {
            for (int i = 0; i < tableSize; i++) {
                dataArray.add(-1);
            }
        }
    }

    private void createHashTable() {
        if (dataArray.isEmpty()) {
            for (int i = 0; i < tableSize; i++) {
                dataArray.add(-1);
            }
        }

        if (dataArray.size() < tableSize) {
            int toAdd = tableSize - dataArray.size();
            for (int i = 0; i < toAdd; i++) {
                dataArray.add(-1);
            }
        } else if (dataArray.size() > tableSize) {
            // Reducir el tamaño si es necesario
            while (dataArray.size() > tableSize) {
                dataArray.remove(dataArray.size() - 1);
            }
        }
    }

    private void displayDataInTable() {
        Object[][] tableData = new Object[tableSize][2];

        for (int i = 0; i < tableSize; i++) {
            tableData[i][0] = i + 1;
            // Si el valor es -1, mostramos una celda vacía
            Integer value = i < dataArray.size() ? dataArray.get(i) : -1;
            tableData[i][1] = (value != null && value == -1) ? "" : value;
        }
        view.setTableData(tableData);
    }

    private int calculateMiddleSquareHash(int value) {
        long squared = (long) value * value;

        String squaredStr = String.valueOf(squared);
        int len = squaredStr.length();

        if (len < 2) {
            squaredStr = "0" + squaredStr;
            len = squaredStr.length();
        }

        int tableSizeDigits = String.valueOf(tableSize).length();

        int mid = len / 2;
        int digitsToTake = Math.min(tableSizeDigits, len);
        int start = Math.max(0, mid - (digitsToTake / 2));
        int end = Math.min(len, start + digitsToTake);

        if (start >= end) {
            end = start + 1;
        }

        String middleDigits = squaredStr.substring(start, end);

        int hashValue;
        try {
            hashValue = Integer.parseInt(middleDigits);
        } catch (NumberFormatException e) {
            hashValue = 0;
        }

        return hashValue % tableSize;
    }

    // MODIFICADA: Método de búsqueda que decide entre normal o animada
    private void performSearch() {
        if (view.isVisualizationEnabled()) {
            performAnimatedSearch();
        } else {
            performNormalSearch();
        }
    }

    // NUEVA: Búsqueda sin animación (método original mejorado)
    private void performNormalSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese un valor para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);
            int originalHashPosition = calculateMiddleSquareHash(valueToSearch);

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
                view.setResultMessage("Valor " + valueToSearch + " encontrado en la posición " + (foundPosition + 1) +
                        (foundPosition == originalHashPosition ? " (posición hash original cuadrado medio)" :
                                " (reubicado por colisión, hash original cuadrado medio: " + (originalHashPosition + 1) + ")"), true);
            } else {
                view.setResultMessage("Valor " + valueToSearch + " no encontrado. Función hash cuadrado medio.", false);
            }

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese un valor numérico válido", false);
        }
    }

    // NUEVA: Búsqueda con animación visual
    private void performAnimatedSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese un valor para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);
            int originalHashPosition = calculateMiddleSquareHash(valueToSearch);

            view.clearHighlights();

            SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    // 1. Buscar en posición original
                    publish(originalHashPosition);
                    Thread.sleep(500);

                    Integer foundPosition = searchInOriginalPosition(valueToSearch, originalHashPosition);
                    if (foundPosition != null) {
                        return foundPosition;
                    }

                    // 2. Búsqueda secuencial
                    int position = (originalHashPosition + 1) % tableSize;
                    int attempts = 0;

                    while (position != originalHashPosition && attempts < tableSize) {
                        publish(position);
                        Thread.sleep(400);

                        if (position < dataArray.size() && dataArray.get(position) == valueToSearch) {
                            return position;
                        }
                        position = (position + 1) % tableSize;
                        attempts++;
                    }

                    // 3. Búsqueda exponencial
                    int i = 1;
                    attempts = 0;

                    while (attempts < tableSize) {
                        int expPosition = (originalHashPosition + (i * i)) % tableSize;
                        publish(expPosition);
                        Thread.sleep(400);

                        if (expPosition >= 0 && expPosition < dataArray.size() &&
                                dataArray.get(expPosition) == valueToSearch) {
                            return expPosition;
                        }

                        i++;
                        attempts++;
                    }

                    // 4. Búsqueda en tabla extendida
                    for (int j = tableSize - 1; j >= 0; j--) {
                        if (j >= dataArray.size()) {
                            continue;
                        }

                        publish(j);
                        Thread.sleep(400);

                        if (dataArray.get(j) == valueToSearch) {
                            return j;
                        }
                    }

                    return -1; // No encontrado
                }

                @Override
                protected void process(List<Integer> chunks) {
                    // Actualizar la vista con la posición actual siendo evaluada
                    int currentIndex = chunks.get(chunks.size() - 1);
                    view.highlightSearchProgress(currentIndex);
                }

                @Override
                protected void done() {
                    try {
                        int position = get();
                        int originalHashPosition = calculateMiddleSquareHash(valueToSearch);

                        if (position != -1) {
                            // Encontrado - resaltar en verde
                            view.highlightFoundItem(position);
                            view.setResultMessage("Valor " + valueToSearch + " encontrado en la posición " +
                                    (position + 1) + (position == originalHashPosition ?
                                    " (posición hash original cuadrado medio)" :
                                    " (reubicado por colisión, hash original cuadrado medio: " + (originalHashPosition + 1) + ")"), true);
                        } else {
                            // No encontrado - limpiar highlights
                            view.clearHighlights();
                            view.setResultMessage("Valor " + valueToSearch + " no encontrado. Función hash cuadrado medio.", false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese un valor numérico válido", false);
        }
    }

    private Integer searchInOriginalPosition(int valueToSearch, int hashPosition) {
        if (hashPosition < dataArray.size() && dataArray.get(hashPosition) == valueToSearch) {
            return hashPosition;
        }
        return null;
    }

    private Integer searchSequential(int valueToSearch, int originalHashPos) {
        int position = (originalHashPos + 1) % tableSize;
        int attempts = 0;

        while (position != originalHashPos && attempts < tableSize) {
            if (position < dataArray.size() && dataArray.get(position) == valueToSearch) {
                return position;
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

            if (position >= 0 && position < dataArray.size() && dataArray.get(position) == valueToSearch) {
                return position;
            }

            i++;
            attempts++;
        }

        return null;
    }

    private Integer searchInExtendedTable(int valueToSearch, int originalHashPos) {
        for (int i = tableSize - 1; i >= 0; i--) {
            if (i >= dataArray.size()) {
                continue;
            }

            if (dataArray.get(i) == valueToSearch) {
                return i;
            }
        }

        return null;
    }

    private void generateNewHashTable(int newSize) {
        this.tableSize = newSize;

        dataArray.clear();
        for (int i = 0; i < newSize; i++) {
            dataArray.add(-1);
        }
        try {
            File file = new File("src/utilities/datos-hash-cuadrado.txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println(dataArray.toString());
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }

        // NUEVA: Mostrar rango permitido
        int minValue = getMinValue(digitLimit);
        int maxValue = getMaxValue(digitLimit);
        view.setResultMessage("Tabla hash generada. Rango permitido: " + minValue + " - " + maxValue, true);

        // Display the new array in the table
        displayDataInTable();
    }

    // Method to insert values using hash function
    public void insertValue(int value) {
        int hashPosition = calculateMiddleSquareHash(value);

        if (hashPosition >= dataArray.size()) {
            int oldSize = dataArray.size();
            while (dataArray.size() <= hashPosition) {
                dataArray.add(-1);
            }
            tableSize = dataArray.size();
            view.setResultMessage("La tabla hash se ha ampliado de " + oldSize + " a " + tableSize + " posiciones para acomodar el nuevo valor", true);
            displayDataInTable();
        }

        if (dataArray.get(hashPosition) != -1) {
            handleCollision(value, hashPosition);
            return;
        }

        dataArray.set(hashPosition, value);
        saveDataToFile();
        displayDataInTable();
        view.setResultMessage("Valor " + value + " insertado en la posición hash " + (hashPosition + 1) + " (hash cuadrado medio)", true);
    }

    private void handleCollision(int valueToInsert, int hashPosition) {
        int currentValue = dataArray.get(hashPosition);
        ColisionView colisionView = new ColisionView();
        colisionView.setCollisionInfo(valueToInsert, hashPosition);

        view.setResultMessage("Colisión detectada: Valor " + valueToInsert + " debería ir en la posición " + (hashPosition + 1) + " que ya está ocupada por " + currentValue, false);

        colisionView.addSequentialSolutionListener(e -> {
            solveCollisionSequential(valueToInsert, hashPosition);
            colisionView.dispose();
        });

        colisionView.addExponentialSolutionListener(e -> {
            solveCollisionExponential(valueToInsert, hashPosition);
            colisionView.dispose();
        });

        colisionView.addTableSolutionListener(e -> {
            solveCollisionWithTable(valueToInsert, hashPosition);
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
            if (dataArray.get(position) == -1) {
                dataArray.set(position, valueToInsert);
                saveDataToFile();
                displayDataInTable();
                view.setResultMessage("Valor " + valueToInsert + " insertado en posición " + (position + 1) + " mediante solución secuencial (colisión en posición " + (originalHashPos + 1) + ")", true);
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

            if (position >= 0 && position < dataArray.size() && dataArray.get(position) == -1) {
                dataArray.set(position, valueToInsert);
                saveDataToFile();
                displayDataInTable();
                view.setResultMessage("Valor " + valueToInsert + " insertado en posición " + (position + 1) + " mediante solución exponencial (colisión en posición " + (originalHashPos + 1) + ")", true);
                return;
            }

            i++;
            attempts++;
        }
        view.setResultMessage("No se pudo insertar " + valueToInsert + " usando prueba cuadrática", false);
    }

    private void solveCollisionWithTable(int valueToInsert, int originalHashPos) {
        int newSize = tableSize + 1;

        while (dataArray.size() < newSize) {
            dataArray.add(-1);
        }

        int overflowPosition = tableSize;
        dataArray.set(overflowPosition, valueToInsert);

        tableSize = newSize;

        saveDataToFile();
        displayDataInTable();
        view.setResultMessage("Valor " + valueToInsert + " insertado en posición " + (overflowPosition + 1) + " mediante solución de tabla (colisión en posición " + (originalHashPos + 1) + ")", true);
    }

    private void saveDataToFile() {
        try {
            File file = new File("src/utilities/datos-hash-cuadrado.txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println(dataArray.toString());
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to delete a value
    public void deleteValue(int value) {
        int hashPosition = calculateMiddleSquareHash(value);

        if (hashPosition < dataArray.size() && dataArray.get(hashPosition) == value) {
            dataArray.set(hashPosition, -1);

            saveDataToFile();
            displayDataInTable();
            view.setResultMessage("Valor " + value + " eliminado de la posición hash cuadrado medio " + (hashPosition + 1), true);
        } else {
            view.setResultMessage("Valor " + value + " no encontrado en la posición hash cuadrado medio calculada " + (hashPosition + 1), false);
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
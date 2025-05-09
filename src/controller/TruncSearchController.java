package controller;

import view.HashAlgorithmView;
import view.ColisionView;
import view.TruncSearchView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TruncSearchController {

    private final TruncSearchView view;
    private final List<Integer> dataArray;
    private HashAlgorithmView hashAlgorithmView;
    private int tableSize;

    public TruncSearchController(TruncSearchView view) {
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
            if (!input.isEmpty()) {
                try {
                    int newSize = Integer.parseInt(input);
                    generateNewHashTable(newSize);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese un tamaño válido", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese un tamaño para la tabla", false);
            }
        });
        view.addInsertValueListener(e -> {
            String input = view.getInsertValue();
            if (!input.isEmpty()) {
                try {
                    int value = Integer.parseInt(input);
                    insertValue(value);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese un valor numérico válido", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese un valor para insertar", false);
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

    private void loadDataFromFile() {
        dataArray.clear();

        try {
            File file = new File("src/utilities/datos-hash-truncamiento.txt");

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
            while (dataArray.size() > tableSize) {
                dataArray.remove(dataArray.size() - 1);
            }
        }
    }

    private void displayDataInTable() {
        Object[][] tableData = new Object[tableSize][2];

        for (int i = 0; i < tableSize; i++) {
            tableData[i][0] = i + 1;
            Integer value = i < dataArray.size() ? dataArray.get(i) : -1;
            tableData[i][1] = (value != null && value == -1) ? "" : value;
        }
        view.setTableData(tableData);
    }

    private int getHashByDigitExtraction(int value) {
        String valueStr = String.valueOf(value);
        int[] positions = view.getSelectedDigitPositions();

        // Verificar que todas las posiciones existan en el valor
        for (int position : positions) {
            int index = position - 1;
            if (index >= valueStr.length()) {
                view.setResultMessage("Error: La posición " + position + " no existe en el valor " + value +
                        " que solo tiene " + valueStr.length() + " dígitos.", false);
                return -1; // Valor especial para indicar error
            }
        }

        StringBuilder hashBuilder = new StringBuilder();
        for (int position : positions) {
            int index = position - 1;
            hashBuilder.append(valueStr.charAt(index));
        }

        int hash;
        try {
            hash = Integer.parseInt(hashBuilder.toString());
        } catch (NumberFormatException e) {
            hash = 0;
        }

        return hash % tableSize;
    }

    private String getHashCalculationDescription(int value) {
        String valueStr = String.valueOf(value);
        int[] positions = view.getSelectedDigitPositions();

        // Verificar que todas las posiciones existan
        for (int position : positions) {
            int index = position - 1;
            if (index >= valueStr.length()) {
                return "No se puede calcular el hash: La posición " + position +
                        " no existe en el valor " + value + " que solo tiene " + valueStr.length() + " dígitos.";
            }
        }

        StringBuilder extractedDigits = new StringBuilder();
        StringBuilder positionsDescription = new StringBuilder();

        for (int i = 0; i < positions.length; i++) {
            int position = positions[i];
            int index = position - 1;

            if (i > 0) {
                positionsDescription.append(", ");
            }
            positionsDescription.append(position);
            extractedDigits.append(valueStr.charAt(index));
        }

        int hash = Integer.parseInt(extractedDigits.toString()) % tableSize;

        return "Hash de " + value + ": extrayendo dígitos en posiciones [" + positionsDescription +
                "] = " + hash;
    }

    private void performSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese un valor para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);
            int hashPosition = getHashByDigitExtraction(valueToSearch);

            // Verificar si hubo error en la extracción de dígitos
            if (hashPosition == -1) {
                return; // El mensaje de error ya fue mostrado en getHashByDigitExtraction
            }

            String hashDescription = getHashCalculationDescription(valueToSearch);

            // Si la descripción indica error, mostrarla y salir
            if (hashDescription.startsWith("No se puede calcular el hash")) {
                view.setResultMessage(hashDescription, false);
                return;
            }

            Integer foundPosition = searchInOriginalPosition(valueToSearch, hashPosition);

            if (foundPosition == null) {
                foundPosition = searchSequential(valueToSearch, hashPosition);
            }

            if (foundPosition == null) {
                foundPosition = searchExponential(valueToSearch, hashPosition);
            }

            if (foundPosition == null) {
                foundPosition = searchInExtendedTable(valueToSearch, hashPosition);
            }

            if (foundPosition != null) {
                view.setResultMessage("Valor " + valueToSearch + " encontrado en la posición " + foundPosition +
                        (foundPosition == hashPosition ? " (posición hash original)" :
                                " (reubicado por colisión, hash original: " + hashPosition + ")") +
                        ". " + hashDescription, true);
                view.highlightRow(foundPosition);
            } else {
                view.setResultMessage("Valor " + valueToSearch + " no encontrado. " + hashDescription +
                        ". Se buscó en todas las posiciones posibles.", false);
            }

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
            File file = new File("src/utilities/datos-hash-truncamiento.txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println(dataArray.toString());
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }
        // Display the new array in the table
        displayDataInTable();
        view.setResultMessage("Tabla hash de tamaño " + newSize + " generada correctamente", true);
    }

    // Method to insert values using digit extraction hash function
    public void insertValue(int value) {
        int hashPosition = getHashByDigitExtraction(value);

        // Verificar si hubo error en la extracción de dígitos
        if (hashPosition == -1) {
            return; // El mensaje de error ya fue mostrado en getHashByDigitExtraction
        }

        String hashDescription = getHashCalculationDescription(value);

        // Si la descripción indica error, mostrarla y salir
        if (hashDescription.startsWith("No se puede calcular el hash")) {
            view.setResultMessage(hashDescription, false);
            return;
        }

        if (hashPosition < dataArray.size() && dataArray.get(hashPosition) != -1) {
            handleCollision(value, hashPosition, hashDescription);
            return;
        }

        if (hashPosition < dataArray.size()) {
            dataArray.set(hashPosition, value);
            saveDataToFile();
            displayDataInTable();
            view.setResultMessage("Valor " + value + " insertado en la posición hash " + hashPosition + ". " + hashDescription, true);
        } else {
            view.setResultMessage("Posición hash " + hashPosition + " fuera de rango. " + hashDescription, false);
        }
    }

    private void handleCollision(int valueToInsert, int hashPosition, String hashDescription) {
        int currentValue = dataArray.get(hashPosition);
        ColisionView colisionView = new ColisionView();
        colisionView.setCollisionInfo(valueToInsert, hashPosition);

        view.setResultMessage("Colisión detectada: Valor " + valueToInsert + " debería ir en la posición " + hashPosition +
                " que ya está ocupada por " + currentValue + ". " + hashDescription, false);

        colisionView.addSequentialSolutionListener(e -> {
            solveCollisionSequential(valueToInsert, hashPosition, hashDescription);
            colisionView.dispose();
        });

        colisionView.addExponentialSolutionListener(e -> {
            solveCollisionExponential(valueToInsert, hashPosition, hashDescription);
            colisionView.dispose();
        });

        colisionView.addTableSolutionListener(e -> {
            solveCollisionWithTable(valueToInsert, hashPosition, hashDescription);
            colisionView.dispose();
        });

        colisionView.addCancelListener(e -> {
            colisionView.dispose();
            view.setResultMessage("Inserción cancelada: colisión en posición " + hashPosition + ". " + hashDescription, false);
        });

        colisionView.showWindow();
    }

    private void solveCollisionSequential(int valueToInsert, int originalHashPos, String hashDescription) {
        int position = (originalHashPos + 1) % tableSize;
        int attempts = 0;

        while (position != originalHashPos && attempts < tableSize) {
            if (dataArray.get(position) == -1) {
                dataArray.set(position, valueToInsert);
                saveDataToFile();
                displayDataInTable();
                view.setResultMessage("Valor " + valueToInsert + " insertado en posición " + position +
                        " mediante solución secuencial (colisión en posición " + originalHashPos + ")." +
                        " " + hashDescription, true);
                return;
            }
            position = (position + 1) % tableSize;
            attempts++;
        }
        view.setResultMessage("No se pudo insertar " + valueToInsert + ": tabla llena. " + hashDescription, false);
    }

    private void solveCollisionExponential(int valueToInsert, int originalHashPos, String hashDescription) {
        int i = 1;
        int attempts = 0;

        while (attempts < tableSize) {
            int position = (originalHashPos + (i * i)) % tableSize;

            if (position >= 0 && position < dataArray.size() && dataArray.get(position) == -1) {
                dataArray.set(position, valueToInsert);
                saveDataToFile();
                displayDataInTable();
                view.setResultMessage("Valor " + valueToInsert + " insertado en posición " + position +
                        " mediante solución exponencial (colisión en posición " + originalHashPos + ")." +
                        " " + hashDescription, true);
                return;
            }

            i++;
            attempts++;
        }
        view.setResultMessage("No se pudo insertar " + valueToInsert + " usando prueba cuadrática. " + hashDescription, false);
    }

    private void solveCollisionWithTable(int valueToInsert, int originalHashPos, String hashDescription) {
        int newSize = tableSize + 1;

        while (dataArray.size() < newSize) {
            dataArray.add(-1);
        }

        int overflowPosition = tableSize;
        dataArray.set(overflowPosition, valueToInsert);

        tableSize = newSize;

        saveDataToFile();
        displayDataInTable();
        view.setResultMessage("Valor " + valueToInsert + " insertado en posición " + overflowPosition +
                " mediante solución de tabla (colisión en posición " + originalHashPos + ")." +
                " " + hashDescription, true);
    }

    private void saveDataToFile() {
        try {
            File file = new File("src/utilities/datos-hash-truncamiento.txt");
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
        // Calcular posición hash usando función de extracción de dígitos
        int hashPosition = getHashByDigitExtraction(value);

        // Verificar si hubo error en la extracción de dígitos
        if (hashPosition == -1) {
            return; // El mensaje de error ya fue mostrado en getHashByDigitExtraction
        }

        String hashDescription = getHashCalculationDescription(value);

        // Si la descripción indica error, mostrarla y salir
        if (hashDescription.startsWith("No se puede calcular el hash")) {
            view.setResultMessage(hashDescription, false);
            return;
        }

        // Verificar si el valor está en esa posición
        if (hashPosition < dataArray.size() && dataArray.get(hashPosition) == value) {
            dataArray.set(hashPosition, -1); // Marcar como vacío

            // Guardar en el archivo
            saveDataToFile();

            // Actualizar la tabla
            displayDataInTable();
            view.setResultMessage("Valor " + value + " eliminado de la posición hash " + hashPosition + ". " + hashDescription, true);
        } else {
            // Si no está en la posición hash, intentar buscarlo en otra posición
            Integer foundPosition = searchSequential(value, hashPosition);

            if (foundPosition == null) {
                foundPosition = searchExponential(value, hashPosition);
            }

            if (foundPosition == null) {
                foundPosition = searchInExtendedTable(value, hashPosition);
            }

            if (foundPosition != null) {
                dataArray.set(foundPosition, -1); // Eliminarlo
                saveDataToFile();
                displayDataInTable();
                view.setResultMessage("Valor " + value + " eliminado de la posición " + foundPosition +
                        " (reubicado por colisión, hash original: " + hashPosition + ")." +
                        " " + hashDescription, true);
            } else {
                view.setResultMessage("Valor " + value + " no encontrado. " + hashDescription, false);
            }
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
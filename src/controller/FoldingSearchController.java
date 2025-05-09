package controller;

import view.FoldingSearchView;
import view.HashAlgorithmView;
import view.TruncSearchView;
import view.ColisionView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoldingSearchController {

    private final FoldingSearchView view;
    private final List<Integer> dataArray;
    private HashAlgorithmView hashAlgorithmView;
    private int tableSize;

    public FoldingSearchController(FoldingSearchView view) {
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
            File file = new File("src/utilities/datos-hash-plegamiento.txt");

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
        // Si la lista de datos está vacía, inicializar con -1 (posiciones vacías)
        if (dataArray.isEmpty()) {
            for (int i = 0; i < tableSize; i++) {
                dataArray.add(-1);
            }
        }

        // Asegurar que la tabla tiene el tamaño correcto
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

    /**
     * Implementación del método de plegamiento para calcular el hash
     *
     * @param value El valor para el cual calcular el hash
     * @return La posición hash calculada mediante plegamiento
     */
    private int calculateFoldingHash(int value) {
        // Convertir el valor a String para dividirlo en dígitos
        String valueStr = String.valueOf(value);
        int sum = 0;

        // Si el valor tiene menos de 2 dígitos, simplemente usamos el valor como hash
        if (valueStr.length() <= 1) {
            return value % tableSize;
        }

        // Dividir en grupos de 2 dígitos
        for (int i = 0; i < valueStr.length(); i += 2) {
            if (i + 1 < valueStr.length()) {
                // Tomar 2 dígitos y sumarlos
                String chunk = valueStr.substring(i, i + 2);
                sum += Integer.parseInt(chunk);
            } else {
                // Si queda un solo dígito al final
                sum += Integer.parseInt(valueStr.substring(i));
            }
        }

        // Si la suma es mayor o igual al tamaño de la tabla, tomar el último dígito
        if (sum >= tableSize) {
            String sumStr = String.valueOf(sum);
            int lastDigit = Integer.parseInt(sumStr.substring(sumStr.length() - 1));

            // Si el último dígito es 0 y no hay posición 0 (empiezan en 1), usamos el penúltimo dígito
            // si está disponible, o aplicamos módulo si no hay más dígitos
            if (lastDigit == 0 && tableSize > 9) {
                if (sumStr.length() > 1) {
                    lastDigit = Integer.parseInt(sumStr.substring(sumStr.length() - 2, sumStr.length() - 1));
                }
            }

            // Asegurarse de que el dígito esté dentro del rango válido
            if (lastDigit < tableSize) {
                return lastDigit;
            }
        }

        // Si no se pudo usar el último dígito o la suma es menor que el tamaño de la tabla
        // Aplicar módulo para asegurar que el hash esté dentro del rango de la tabla
        return sum % tableSize;
    }

    private void performSearch() {
        String input = view.getSearchValue();

        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese un valor para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);
            int hashPosition = calculateFoldingHash(valueToSearch);

            // Mostrar el cálculo del hash para depuración
            String foldingCalc = "Cálculo del hash por plegamiento para " + valueToSearch + ": ";
            String valueStr = String.valueOf(valueToSearch);
            int sum = 0;

            StringBuilder calcSteps = new StringBuilder(foldingCalc);

            // Mostrar los pasos del cálculo
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

            // Si la suma es mayor o igual al tamaño de la tabla, explicar cómo se tomó el último dígito
            if (sum >= tableSize) {
                String sumStr = String.valueOf(sum);
                int lastDigit = Integer.parseInt(sumStr.substring(sumStr.length() - 1));
                calcSteps.append(" (valor fuera de rango, tomamos el último dígito: ").append(lastDigit).append(")");
            } else {
                calcSteps.append(" % ").append(tableSize);
            }

            calcSteps.append(" = ").append(hashPosition + 1);

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
                view.setResultMessage("Valor " + valueToSearch + " encontrado en la posición " + (foundPosition + 1) +
                        (foundPosition == hashPosition ? " (posición hash original)" : " (reubicado por colisión, hash original: " + (hashPosition + 1) + ")") +
                        "\n" + calcSteps, true);
                view.highlightRow(foundPosition);
            } else {
                view.setResultMessage("Valor " + valueToSearch + " no encontrado. " + calcSteps + ". Se buscó en todas las posiciones posibles.", false);
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
            File file = new File("src/utilities/datos-hash-plegamiento.txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println(dataArray.toString());
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }
        // Display the new array in the table
        displayDataInTable();
    }

    // Method to insert values using folding hash function
    public void insertValue(int value) {
        int hashPosition = calculateFoldingHash(value);

        if (hashPosition < dataArray.size() && dataArray.get(hashPosition) != -1) {
            handleCollision(value, hashPosition);
            return;
        }

        if (hashPosition < dataArray.size()) {
            dataArray.set(hashPosition, value);
            saveDataToFile();
            displayDataInTable();

            // Mostrar el cálculo del hash para depuración
            String foldingCalc = "Cálculo del hash por plegamiento para " + value + ": ";
            String valueStr = String.valueOf(value);
            int sum = 0;

            StringBuilder calcSteps = new StringBuilder(foldingCalc);

            // Mostrar los pasos del cálculo
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

            // Si la suma es mayor o igual al tamaño de la tabla, explicar cómo se tomó el último dígito
            if (sum >= tableSize) {
                String sumStr = String.valueOf(sum);
                int lastDigit = Integer.parseInt(sumStr.substring(sumStr.length() - 1));
                calcSteps.append(" (valor fuera de rango, tomamos el último dígito: ").append(lastDigit).append(")");
            } else {
                calcSteps.append(" % ").append(tableSize);
            }

            calcSteps.append(" = ").append(hashPosition + 1);

            view.setResultMessage("Valor " + value + " insertado en la posición hash " + (hashPosition + 1) + "\n" + calcSteps, true);
        } else {
            view.setResultMessage("Posición hash " + (hashPosition + 1) + " fuera de rango", false);
        }
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
            File file = new File("src/utilities/datos-hash-plegamiento.txt");
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
        // Calcular posición hash usando función de plegamiento
        int hashPosition = calculateFoldingHash(value);

        // Verificar si el valor está en esa posición
        if (hashPosition < dataArray.size() && dataArray.get(hashPosition) == value) {
            dataArray.set(hashPosition, -1); // Marcar como vacío

            // Guardar en el archivo
            saveDataToFile();

            // Actualizar la tabla
            displayDataInTable();

            // Mostrar el cálculo del hash para depuración
            String foldingCalc = "Cálculo del hash por plegamiento para " + value + ": ";
            String valueStr = String.valueOf(value);
            int sum = 0;

            StringBuilder calcSteps = new StringBuilder(foldingCalc);

            // Mostrar los pasos del cálculo
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

            // Si la suma es mayor o igual al tamaño de la tabla, explicar cómo se tomó el último dígito
            if (sum >= tableSize) {
                String sumStr = String.valueOf(sum);
                int lastDigit = Integer.parseInt(sumStr.substring(sumStr.length() - 1));
                calcSteps.append(" (valor fuera de rango, tomamos el último dígito: ").append(lastDigit).append(")");
            } else {
                calcSteps.append(" % ").append(tableSize);
            }

            calcSteps.append(" = ").append(hashPosition + 1);

            view.setResultMessage("Valor " + value + " eliminado de la posición hash " + (hashPosition + 1) + "\n" + calcSteps, true);
        } else {
            // Si no está en la posición hash, intentar buscar en otras posiciones
            Integer foundPosition = searchSequential(value, hashPosition);

            if (foundPosition == null) {
                foundPosition = searchExponential(value, hashPosition);
            }

            if (foundPosition == null) {
                foundPosition = searchInExtendedTable(value, hashPosition);
            }

            if (foundPosition != null) {
                dataArray.set(foundPosition, -1); // Marcar como vacío
                saveDataToFile();
                displayDataInTable();
                view.setResultMessage("Valor " + value + " eliminado de la posición " + (foundPosition + 1) + " (reubicado por colisión)", true);
            } else {
                view.setResultMessage("Valor " + value + " no encontrado en la tabla", false);
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
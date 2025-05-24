package controller;

import view.AlgorithmMenuView;
import view.SequentialSearchView;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SequentialSearchController {

    private SequentialSearchView view;
    private List<Integer> dataArray;
    private AlgorithmMenuView algorithmMenuView;
    private int digitLimit = 2; // Límite de dígitos por defecto

    public SequentialSearchController(SequentialSearchView view) {
        this.view = view;
        this.dataArray = new ArrayList<>();

        // Initialize components
        initComponents();

        // Load data from file when creating the controller
        loadDataFromFile();

        // Display data in the table
        displayDataInTable();
    }

    // Setter for the menu view to return to
    public void setAlgorithmMenuView(AlgorithmMenuView algorithmMenuView) {
        this.algorithmMenuView = algorithmMenuView;
    }

    private void initComponents() {
        // Add action listeners to buttons
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

                    generateNewArray(newSize);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese valores numéricos válidos", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese el tamaño del arreglo y el límite de dígitos", false);
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
        view.addBackListener(e -> goBack());
    }

    // Método para validar la cantidad de dígitos
    private boolean isValidDigitCount(int value, int digitLimit) {
        String valueStr = String.valueOf(Math.abs(value));
        return valueStr.length() == digitLimit;
    }

    // Método para obtener el rango mínimo según el límite de dígitos
    private int getMinValue(int digitLimit) {
        if (digitLimit == 1) return 0;
        return (int) Math.pow(10, digitLimit - 1);
    }

    // Método para obtener el rango máximo según el límite de dígitos
    private int getMaxValue(int digitLimit) {
        return (int) Math.pow(10, digitLimit) - 1;
    }

    // Método para ordenar el arreglo manteniendo los -1 al final
    private void sortArray() {
        List<Integer> validValues = new ArrayList<>();
        int emptyCount = 0;

        // Separar valores válidos de los -1
        for (Integer value : dataArray) {
            if (value != null && value != -1) {
                validValues.add(value);
            } else {
                emptyCount++;
            }
        }

        // Ordenar solo los valores válidos
        Collections.sort(validValues);

        // Reconstruir el arreglo
        dataArray.clear();
        dataArray.addAll(validValues);

        // Agregar los -1 al final
        for (int i = 0; i < emptyCount; i++) {
            dataArray.add(-1);
        }

        // Guardar en el archivo
        saveArrayToFile();

        // Actualizar la vista
        displayDataInTable();
    }

    // Método para guardar el arreglo en el archivo
    private void saveArrayToFile() {
        try {
            File file = new File("src/utilities/datos-busqueda-secuencial.txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println(dataArray.toString());
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDataFromFile() {
        dataArray.clear();

        try {
            // Path to the file
            File file = new File("src/utilities/datos-busqueda-secuencial.txt");

            if (!file.exists()) {
                System.err.println("El archivo de datos no existe: " + file.getAbsolutePath());
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
    }

    private void displayDataInTable() {
        // Create data for table with position and value columns
        Object[][] tableData = new Object[dataArray.size()][2];

        for (int i = 0; i < dataArray.size(); i++) {
            tableData[i][0] = i + 1;  // Position (starting from 1)
            // Si el valor es -1, mostramos una celda vacía
            Integer value = dataArray.get(i);
            tableData[i][1] = (value != null && value == -1) ? "" : value;
        }

        // Set data to table
        view.setTableData(tableData);
    }

    // Método principal de búsqueda que decide si animar o no
    private void performSearch() {
        if (view.isVisualizationEnabled()) {
            performAnimatedSearch();
        } else {
            performNormalSearch();
        }
    }

    // Búsqueda normal sin animación
    private void performNormalSearch() {
        String input = view.getSearchValue();

        // Validate input
        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese una clave para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);

            // Limpiar highlights anteriores
            view.clearHighlights();

            // Realizar búsqueda secuencial
            int position = sequentialSearch(valueToSearch);

            if (position != -1) {
                // Encontrado
                view.highlightFoundItem(position);
                view.setResultMessage("Clave " + valueToSearch + " encontrada en la posición " + (position + 1), true);
            } else {
                // No encontrado
                view.setResultMessage("Clave " + valueToSearch + " no encontrada en el arreglo", false);
            }

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese una clave numérica válida", false);
        }
    }

    // Búsqueda animada con visualización del proceso
    private void performAnimatedSearch() {
        String input = view.getSearchValue();

        // Validate input
        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese una clave para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);

            // Limpiar highlights anteriores
            view.clearHighlights();

            // Crear un SwingWorker para la animación
            SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    for (int i = 0; i < dataArray.size(); i++) {
                        // Publicar el índice actual
                        publish(i);

                        // Pausa para visualizar la búsqueda
                        Thread.sleep(300); // 300ms de pausa entre cada elemento

                        if (dataArray.get(i) == valueToSearch) {
                            return i; // Encontrado
                        }
                    }
                    return -1; // No encontrado
                }

                @Override
                protected void process(List<Integer> chunks) {
                    // Tomar el último índice procesado
                    int currentIndex = chunks.get(chunks.size() - 1);
                    view.highlightSearchProgress(currentIndex);
                }

                @Override
                protected void done() {
                    try {
                        int position = get();

                        if (position != -1) {
                            // Encontrado - resaltar en verde
                            view.highlightFoundItem(position);
                            view.setResultMessage("Clave " + valueToSearch + " encontrada en la posición " + (position + 1), true);
                        } else {
                            // No encontrado - limpiar highlights
                            view.clearHighlights();
                            view.setResultMessage("Clave " + valueToSearch + " no encontrada en el arreglo", false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            // Iniciar la búsqueda animada
            worker.execute();

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese una clave numérica válida", false);
        }
    }

    // Sequential search algorithm implementation
    private int sequentialSearch(int target) {
        for (int i = 0; i < dataArray.size(); i++) {
            if (dataArray.get(i) != null && dataArray.get(i) == target) {
                return i;
            }
        }
        return -1;  // Not found
    }

    private void generateNewArray(int newSize) {
        dataArray.clear();
        for (int i = 0; i < newSize; i++) {
            dataArray.add(-1);
        }
        // Save the new array to the file
        try {
            File file = new File("src/utilities/datos-busqueda-secuencial.txt");
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println(dataArray.toString());
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }

        // Display the new array in the table
        displayDataInTable();

        // Mostrar mensaje informativo sobre el rango permitido
        int minValue = getMinValue(digitLimit);
        int maxValue = getMaxValue(digitLimit);
        view.setResultMessage("Arreglo generado. Rango permitido: " + minValue + " - " + maxValue, true);
    }

    // Method to insert values
    public void insertValue(int value) throws IllegalArgumentException {
        // Primero verificar si el valor ya existe
        for (Integer num : dataArray) {
            if (num != null && num != -1 && num == value) {
                throw new IllegalArgumentException("La clave " + value + " ya existe en el arreglo");
            }
        }

        // Buscar la primera posición disponible (-1)
        int index = -1;
        for (int i = 0; i < dataArray.size(); i++) {
            if (dataArray.get(i) == -1) { // -1 indica posición vacía
                index = i;
                break;
            }
        }

        // Si encontramos una posición disponible
        if (index != -1) {
            dataArray.set(index, value);

            // Ordenar el arreglo después de insertar
            sortArray();

            view.setResultMessage("Clave " + value + " insertada correctamente", true);
        } else {
            view.setResultMessage("El arreglo está lleno", false);
        }
    }

    // Method to delete a value
    public void deleteValue(int value) {
        // Buscar el valor en el arreglo
        int index = -1;
        for (int i = 0; i < dataArray.size(); i++) {
            if (dataArray.get(i) != null && dataArray.get(i) == value) {
                index = i;
                break;
            }
        }

        // Si encontramos el valor
        if (index != -1) {
            dataArray.set(index, -1); // Marcar como vacío

            // Ordenar el arreglo después de eliminar
            sortArray();

            view.setResultMessage("Clave " + value + " eliminada correctamente", true);
        } else {
            view.setResultMessage("Clave " + value + " no encontrada en el arreglo", false);
        }
    }

    private void goBack() {
        // Close current view
        view.dispose();

        // Show algorithm menu view if available
        if (algorithmMenuView != null) {
            algorithmMenuView.setVisible(true);
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            SequentialSearchView newView = new SequentialSearchView();
            SequentialSearchController controller = new SequentialSearchController(newView);
            newView.showWindow();
        });
    }
}
package controller;

import view.AlgorithmMenuView;
import view.SequentialSearchView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SequentialSearchController {

    private SequentialSearchView view;
    private List<Integer> dataArray;
    private AlgorithmMenuView algorithmMenuView;

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
            if (!input.isEmpty()) {
                try {
                    int newSize = Integer.parseInt(input);
                    generateNewArray(newSize);
                } catch (NumberFormatException ex) {
                    view.setResultMessage("Por favor ingrese un tamaño válido", false);
                }
            } else {
                view.setResultMessage("Por favor ingrese un tamaño para el arreglo", false);
            }
        });
        view.addInsertValueListener(e -> {
            String input = view.getInsertValue();
            if (!input.isEmpty()) {
                try {
                    int value = Integer.parseInt(input);
                    try {
                        insertValue(value);
                    } catch (IllegalArgumentException ex) {
                        view.setResultMessage(ex.getMessage(), false);
                    }
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

    private void performSearch() {
        String input = view.getSearchValue();

        // Validate input
        if (input.isEmpty()) {
            view.setResultMessage("Por favor ingrese un valor para buscar", false);
            return;
        }

        try {
            int valueToSearch = Integer.parseInt(input);

            // Perform sequential search
            int position = sequentialSearch(valueToSearch);

            if (position != -1) {
                // Found
                view.setResultMessage("Valor " + valueToSearch + " encontrado en la posición " + (position + 1), true);
                view.highlightRow(position);
            } else {
                // Not found
                view.setResultMessage("Valor " + valueToSearch + " no encontrado en el arreglo", false);
            }

        } catch (NumberFormatException e) {
            view.setResultMessage("Por favor ingrese un valor numérico válido", false);
        }
    }

    // Sequential search algorithm implementation
    private int sequentialSearch(int target) {
        for (int i = 0; i < dataArray.size(); i++) {
            if (dataArray.get(i) == target) {
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
                writer.println(dataArray.toString()); // Esto guardará el arreglo en formato [0,0,0,...]
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
            e.printStackTrace();
        }

        // Display the new array in the table
        displayDataInTable();
    }

    // Method to insert values
    public void insertValue(int value) throws IllegalArgumentException {
        // Primero verificar si el valor ya existe
        for (Integer num : dataArray) {
            if (num != null && num != -1 && num == value) {
                throw new IllegalArgumentException("El valor " + value + " ya existe en el arreglo");
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

            // Guardar en el archivo
            try {
                File file = new File("src/utilities/datos-busqueda-secuencial.txt");
                try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                    writer.println(dataArray.toString());
                }
            } catch (IOException e) {
                System.err.println("Error al escribir en el archivo: " + e.getMessage());
                e.printStackTrace();
            }

            // Actualizar la tabla
            displayDataInTable();
            view.setResultMessage("Valor " + value + " insertado en la posición " + (index + 1), true);
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

            // Guardar en el archivo
            try {
                File file = new File("src/utilities/datos-busqueda-secuencial.txt");
                try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                    writer.println(dataArray.toString());
                }
            } catch (IOException e) {
                System.err.println("Error al escribir en el archivo: " + e.getMessage());
                e.printStackTrace();
            }

            // Actualizar la tabla
            displayDataInTable();
            view.setResultMessage("Valor " + value + " eliminado de la posición " + (index + 1), true);
        } else {
            view.setResultMessage("Valor " + value + " no encontrado en el arreglo", false);
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
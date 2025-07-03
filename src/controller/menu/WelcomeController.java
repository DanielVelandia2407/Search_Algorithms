package controller.menu;

import view.menu.WelcomeView;
import view.menu.MainView;
import controller.graph.GraphController;

public class WelcomeController {
    private WelcomeView welcomeView;
    private MainController mainController;
    private GraphController graphController;

    public WelcomeController() {
        this.welcomeView = new WelcomeView();
        initializeControllers();
        initializeListeners();
    }

    private void initializeControllers() {
        // Crear el controlador principal (algoritmos de búsqueda)
        MainView mainView = new MainView();
        this.mainController = new MainController(mainView);
        this.mainController.setWelcomeView(welcomeView); // Establecer referencia al menú de bienvenida

        // Crear el controlador de grafos
        this.graphController = new GraphController();
        this.graphController.setWelcomeView(welcomeView); // Para regresar al menú de bienvenida
    }

    private void initializeListeners() {
        // Botón "Algoritmos de Búsqueda" -> MainView
        welcomeView.addSearchAlgorithmsListener(e -> {
            System.out.println("Algoritmos de Búsqueda clicked"); // Debug
            openSearchAlgorithms();
        });

        // Botón "Grafos" -> GraphView
        welcomeView.addGraphsListener(e -> {
            System.out.println("Grafos clicked"); // Debug
            openGraphs();
        });
    }

    /**
     * Abrir el módulo de algoritmos de búsqueda
     */
    private void openSearchAlgorithms() {
        welcomeView.setVisible(false);
        mainController.showView();
    }

    /**
     * Abrir el módulo de grafos
     */
    private void openGraphs() {
        welcomeView.setVisible(false);
        graphController.run();
    }

    /**
     * Mostrar la vista de bienvenida
     */
    public void showView() {
        welcomeView.setVisible(true);
    }

    /**
     * Obtener la vista de bienvenida
     */
    public WelcomeView getWelcomeView() {
        return welcomeView;
    }

    /**
     * Obtener el controlador principal
     */
    public MainController getMainController() {
        return mainController;
    }

    /**
     * Obtener el controlador de grafos
     */
    public GraphController getGraphController() {
        return graphController;
    }

    /**
     * Método principal para ejecutar la aplicación
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            System.out.println("Iniciando WelcomeController..."); // Debug
            WelcomeController controller = new WelcomeController();
            controller.showView();
        });
    }
}
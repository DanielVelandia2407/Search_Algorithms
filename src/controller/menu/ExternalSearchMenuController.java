package controller.menu;

import controller.external_search.ExternalBinarySearchController;
import controller.external_search.ExternalSequentialSearchController;
import view.menu.ExternalSearchMenuView;
import view.menu.MainView;
import view.menu.SearchMenuView;
import view.external_search.ExternalSequentialSearchView;
import view.external_search.ExternalBinarySearchView;
import view.menu.ExternalHashAlgorithmView;

public class ExternalSearchMenuController {
    private ExternalSearchMenuView externalSearchMenuView;
    private MainView mainView;
    private SearchMenuController searchMenuController; // NUEVO: Para búsqueda dinámica

    public ExternalSearchMenuController(ExternalSearchMenuView externalSearchMenuView) {
        this.externalSearchMenuView = externalSearchMenuView;
        initializeControllers(); // NUEVO: Inicializar sub-controladores
        initializeListeners();
    }

    /**
     * Initialize sub-controllers (NUEVO)
     */
    private void initializeControllers() {
        // Create Search Menu Controller para manejar búsqueda dinámica
        SearchMenuView searchMenuView = new SearchMenuView();
        this.searchMenuController = new SearchMenuController(searchMenuView);
        // La referencia se establece después de que mainView esté disponible
    }

    /**
     * Sets the reference to the main view for navigation purposes
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
        // NUEVO: También pasar las referencias al controlador de búsqueda dinámica
        if (searchMenuController != null) {
            searchMenuController.setMainView(mainView);
            searchMenuController.setExternalSearchMenuView(this.externalSearchMenuView);
        }
    }

    /**
     * Initialize all button listeners for the external search menu
     */
    private void initializeListeners() {
        // Back button - returns to main menu
        this.externalSearchMenuView.addBackListener(e -> goBackToMainMenu());

        // Sequential Search button
        this.externalSearchMenuView.addSequentialSearchListener(e -> openExternalSequentialSearch());

        // Binary Search button
        this.externalSearchMenuView.addBinarySearchListener(e -> openBinarySearch());

        // Hash Search button
        this.externalSearchMenuView.addHashSearchListener(e -> openHashAlgorithmView());

        // NUEVO: Dynamic Search button
        this.externalSearchMenuView.addDynamicSearchListener(e -> openDynamicSearch());
    }

    /**
     * Navigate back to the main menu
     */
    private void goBackToMainMenu() {
        if (mainView != null) {
            externalSearchMenuView.setVisible(false);
            mainView.setVisible(true);
        }
    }

    /**
     * Open External Sequential Search view with its controller
     */
    private void openExternalSequentialSearch() {
        try {
            // Create the external sequential search view
            ExternalSequentialSearchView sequentialView = new ExternalSequentialSearchView();

            // Create the controller and link it with the view
            ExternalSequentialSearchController sequentialController =
                    new ExternalSequentialSearchController(sequentialView);

            // Set the reference to this menu for back navigation
            sequentialController.setMenuView(this.externalSearchMenuView);

            // Hide current menu and show the sequential search view
            this.externalSearchMenuView.setVisible(false);
            sequentialView.showWindow();

        } catch (Exception e) {
            System.err.println("Error al abrir la búsqueda secuencial externa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open External Binary Search view with its controller
     */
    private void openBinarySearch() {
        try {
            // Create the external binary search view
            ExternalBinarySearchView binaryView = new ExternalBinarySearchView();

            // Create the controller and link it with the view
            ExternalBinarySearchController binaryController =
                    new ExternalBinarySearchController(binaryView);

            // Set the reference to this menu for back navigation
            binaryController.setMenuView(this.externalSearchMenuView);

            // Hide current menu and show the binary search view
            this.externalSearchMenuView.setVisible(false);
            binaryView.showWindow();

        } catch (Exception e) {
            System.err.println("Error al abrir la búsqueda binaria externa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open Hash Algorithm view with its controller
     */
    private void openHashAlgorithmView() {
        try {
            // Create the external hash algorithm view
            ExternalHashAlgorithmView hashView = new ExternalHashAlgorithmView();

            // Create the controller and link it with the view
            ExternalHashAlgorithmController hashController =
                    new ExternalHashAlgorithmController(hashView);

            // Set the reference to this menu for back navigation
            hashController.setExternalSearchMenuView(this.externalSearchMenuView);

            // Hide current menu and show the hash algorithm view
            this.externalSearchMenuView.setVisible(false);
            hashView.showWindow();

        } catch (Exception e) {
            System.err.println("Error al abrir las funciones hash externas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open Dynamic Search menu (NUEVO)
     */
    private void openDynamicSearch() {
        try {
            // Hide current menu and show the dynamic search menu
            this.externalSearchMenuView.setVisible(false);
            searchMenuController.showView();

        } catch (Exception e) {
            System.err.println("Error al abrir la búsqueda dinámica: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show the external search menu view
     */
    public void showView() {
        externalSearchMenuView.showWindow();
    }

    /**
     * Get the external search menu view instance
     */
    public ExternalSearchMenuView getView() {
        return externalSearchMenuView;
    }

    /**
     * Get the search menu controller (NUEVO)
     */
    public SearchMenuController getSearchMenuController() {
        return searchMenuController;
    }
}
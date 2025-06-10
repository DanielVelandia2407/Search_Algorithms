package controller.external_search;

import view.external_search.ExternalSearchMenuView;
import view.menu.MainView;
import view.external_search.ExternalSequentialSearchView;
import view.external_search.ExternalBinarySearchView;
import view.external_search.ExternalHashAlgorithmView;

public class ExternalSearchMenuController {
    private ExternalSearchMenuView externalSearchMenuView;
    private MainView mainView;

    public ExternalSearchMenuController(ExternalSearchMenuView externalSearchMenuView) {
        this.externalSearchMenuView = externalSearchMenuView;
        initializeListeners();
    }

    /**
     * Sets the reference to the main view for navigation purposes
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
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
}
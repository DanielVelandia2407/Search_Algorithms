package controller.menu;

import controller.external_search.ExternalFoldingSearchController;
import controller.external_search.ExternalModSearchController;
import controller.external_search.ExternalSquaredSearchController;
import controller.external_search.ExternalTruncSearchController;
import view.menu.ExternalHashAlgorithmView;
import view.menu.ExternalSearchMenuView;
import view.external_search.ExternalModSearchView;
import view.external_search.ExternalSquaredSearchView;
import view.external_search.ExternalTruncSearchView;
import view.external_search.ExternalFoldingSearchView;

public class ExternalHashAlgorithmController {

    private ExternalHashAlgorithmView view;
    private ExternalSearchMenuView externalSearchMenuView;

    public ExternalHashAlgorithmController(ExternalHashAlgorithmView view) {
        this.view = view;
        initComponents();
    }

    private void initComponents() {
        // Add listeners for all hash function buttons
        view.addModSearchListener(e -> openExternalModSearch());
        view.addSquaredSearchListener(e -> openExternalSquaredSearch());
        view.addTruncatedSearchListener(e -> openExternalTruncSearch());
        view.addFoldingSearchListener(e -> openExternalFoldingSearch());
        view.addBackListener(e -> goBack());
    }

    /**
     * Set the reference to the external search menu view for navigation
     */
    public void setExternalSearchMenuView(ExternalSearchMenuView externalSearchMenuView) {
        this.externalSearchMenuView = externalSearchMenuView;
    }

    /**
     * Open External Modulo Search
     */
    public void openExternalModSearch() {
        try {
            // Close current view
            view.setVisible(false);

            // Create and show ExternalModSearchView
            ExternalModSearchView modSearchView = new ExternalModSearchView();
            ExternalModSearchController modSearchController = new ExternalModSearchController(modSearchView);
            modSearchController.setExternalHashAlgorithmView(view);
            modSearchView.showWindow();
        } catch (Exception e) {
            System.err.println("Error al abrir la función módulo externa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open External Squared Search
     */
    public void openExternalSquaredSearch() {
        try {
            // Close current view
            view.setVisible(false);

            // Create and show ExternalSquaredSearchView
            ExternalSquaredSearchView squaredSearchView = new ExternalSquaredSearchView();
            ExternalSquaredSearchController squaredSearchController = new ExternalSquaredSearchController(squaredSearchView);
            squaredSearchController.setExternalHashAlgorithmView(view);
            squaredSearchView.showWindow();
        } catch (Exception e) {
            System.err.println("Error al abrir la función cuadrado externa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open External Truncated Search
     */
    public void openExternalTruncSearch() {
        try {
            // Close current view
            view.setVisible(false);

            // Create and show ExternalTruncSearchView
            ExternalTruncSearchView truncSearchView = new ExternalTruncSearchView();
            ExternalTruncSearchController truncSearchController = new ExternalTruncSearchController(truncSearchView);
            truncSearchController.setExternalHashAlgorithmView(view);
            truncSearchView.showWindow();
        } catch (Exception e) {
            System.err.println("Error al abrir la función truncamiento externa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open External Folding Search
     */
    public void openExternalFoldingSearch() {
        try {
            // Close current view
            view.setVisible(false);

            // Create and show ExternalFoldingSearchView
            ExternalFoldingSearchView foldingSearchView = new ExternalFoldingSearchView();
            ExternalFoldingSearchController foldingSearchController = new ExternalFoldingSearchController(foldingSearchView);
            foldingSearchController.setExternalHashAlgorithmView(view);
            foldingSearchView.showWindow();
        } catch (Exception e) {
            System.err.println("Error al abrir la función plegamiento externa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate back to the external search menu
     */
    private void goBack() {
        // Close current view
        view.setVisible(false);

        // Show external search menu view if available
        if (externalSearchMenuView != null) {
            externalSearchMenuView.setVisible(true);
        }
    }

    /**
     * Show the external hash algorithm view
     */
    public void showView() {
        view.showWindow();
    }

    /**
     * Get the external hash algorithm view instance
     */
    public ExternalHashAlgorithmView getView() {
        return view;
    }
}
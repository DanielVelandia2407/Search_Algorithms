package controller;

import view.IndicesMenuView;
import view.MainView;
import javax.swing.JOptionPane;
// import view.PrimaryIndexView;
// import view.SecondaryIndexView;
// import view.MultilevelPrimaryView;
// import view.MultilevelSecondaryView;

public class IndicesMenuController {
    private IndicesMenuView indicesMenuView;
    private MainView mainView;

    public IndicesMenuController(IndicesMenuView indicesMenuView) {
        this.indicesMenuView = indicesMenuView;
        initializeListeners();
    }

    /**
     * Sets the reference to the main view for navigation purposes
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    /**
     * Initialize all button listeners for the indices menu
     */
    private void initializeListeners() {
        // Back button - returns to main menu
        this.indicesMenuView.addBackListener(e -> goBackToMainMenu());

        // Primary Index button
        this.indicesMenuView.addPrimaryIndexListener(e -> openPrimaryIndex());

        // Secondary Index button
        this.indicesMenuView.addSecondaryIndexListener(e -> openSecondaryIndex());

        // Multilevel Primary button
        this.indicesMenuView.addMultilevelPrimaryListener(e -> openMultilevelPrimary());

        // Multilevel Secondary button
        this.indicesMenuView.addMultilevelSecondaryListener(e -> openMultilevelSecondary());
    }

    /**
     * Navigate back to the main menu
     */
    private void goBackToMainMenu() {
        if (mainView != null) {
            indicesMenuView.setVisible(false);
            mainView.setVisible(true);
        }
    }

    /**
     * Open Primary Index view with its controller
     */
    private void openPrimaryIndex() {
        indicesMenuView.setVisible(false);

        // TODO: Uncomment when PrimaryIndexView is created
        /*
        PrimaryIndexView primaryIndexView = new PrimaryIndexView();
        PrimaryIndexController controller = new PrimaryIndexController(primaryIndexView);
        controller.setIndicesMenuView(indicesMenuView);
        primaryIndexView.showWindow();
        */

        // Temporary message - remove when implementing the actual view
        JOptionPane.showMessageDialog(indicesMenuView,
                "Funcionalidad de Índice Principal en desarrollo",
                "En construcción",
                JOptionPane.INFORMATION_MESSAGE);
        indicesMenuView.setVisible(true);
    }

    /**
     * Open Secondary Index view with its controller
     */
    private void openSecondaryIndex() {
        indicesMenuView.setVisible(false);

        // TODO: Uncomment when SecondaryIndexView is created
        /*
        SecondaryIndexView secondaryIndexView = new SecondaryIndexView();
        SecondaryIndexController controller = new SecondaryIndexController(secondaryIndexView);
        controller.setIndicesMenuView(indicesMenuView);
        secondaryIndexView.showWindow();
        */

        // Temporary message - remove when implementing the actual view
        JOptionPane.showMessageDialog(indicesMenuView,
                "Funcionalidad de Índice Secundario en desarrollo",
                "En construcción",
                JOptionPane.INFORMATION_MESSAGE);
        indicesMenuView.setVisible(true);
    }

    /**
     * Open Multilevel Primary view with its controller
     */
    private void openMultilevelPrimary() {
        indicesMenuView.setVisible(false);

        // TODO: Uncomment when MultilevelPrimaryView is created
        /*
        MultilevelPrimaryView multilevelPrimaryView = new MultilevelPrimaryView();
        MultilevelPrimaryController controller = new MultilevelPrimaryController(multilevelPrimaryView);
        controller.setIndicesMenuView(indicesMenuView);
        multilevelPrimaryView.showWindow();
        */

        // Temporary message - remove when implementing the actual view
        JOptionPane.showMessageDialog(indicesMenuView,
                "Funcionalidad de Multinivel con Principal en desarrollo",
                "En construcción",
                JOptionPane.INFORMATION_MESSAGE);
        indicesMenuView.setVisible(true);
    }

    /**
     * Open Multilevel Secondary view with its controller
     */
    private void openMultilevelSecondary() {
        indicesMenuView.setVisible(false);

        // TODO: Uncomment when MultilevelSecondaryView is created
        /*
        MultilevelSecondaryView multilevelSecondaryView = new MultilevelSecondaryView();
        MultilevelSecondaryController controller = new MultilevelSecondaryController(multilevelSecondaryView);
        controller.setIndicesMenuView(indicesMenuView);
        multilevelSecondaryView.showWindow();
        */

        // Temporary message - remove when implementing the actual view
        JOptionPane.showMessageDialog(indicesMenuView,
                "Funcionalidad de Multinivel con Secundario en desarrollo",
                "En construcción",
                JOptionPane.INFORMATION_MESSAGE);
        indicesMenuView.setVisible(true);
    }

    /**
     * Show the indices menu view
     */
    public void showView() {
        indicesMenuView.showWindow();
    }

    /**
     * Get the indices menu view instance
     */
    public IndicesMenuView getView() {
        return indicesMenuView;
    }
}
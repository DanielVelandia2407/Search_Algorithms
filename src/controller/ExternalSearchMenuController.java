package controller;

import view.ExternalSearchMenuView;
import view.MainView;
import view.SequentialSearchView;
import view.BinarySearchView;
import view.HashAlgorithmView;

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
        this.externalSearchMenuView.addSequentialSearchListener(e -> openSequentialSearch());

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
     * Open Sequential Search view with its controller
     */
    private void openSequentialSearch() {
    }

    /**
     * Open Binary Search view with its controller
     */
    private void openBinarySearch() {
    }

    /**
     * Open Hash Algorithm view with its controller
     */
    private void openHashAlgorithmView() {
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
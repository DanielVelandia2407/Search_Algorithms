package controller;

import view.AlgorithmMenuView;
import view.MainView;
import view.SequentialSearchView;
import view.BinarySearchView;
import view.HashAlgorithmView;

public class AlgorithmMenuController {
    private AlgorithmMenuView algorithmMenuView;
    private MainView mainView;

    public AlgorithmMenuController(AlgorithmMenuView algorithmMenuView) {
        this.algorithmMenuView = algorithmMenuView;
        initializeListeners();
    }

    /**
     * Sets the reference to the main view for navigation purposes
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    /**
     * Initialize all button listeners for the algorithm menu
     */
    private void initializeListeners() {
        // Back button - returns to main menu
        this.algorithmMenuView.addBackListener(e -> goBackToMainMenu());

        // Sequential Search button
        this.algorithmMenuView.addSequentialSearchListener(e -> openSequentialSearch());

        // Binary Search button
        this.algorithmMenuView.addBinarySearchListener(e -> openBinarySearch());

        // Hash Search button
        this.algorithmMenuView.addHashSearchListener(e -> openHashAlgorithmView());
    }

    /**
     * Navigate back to the main menu
     */
    private void goBackToMainMenu() {
        if (mainView != null) {
            algorithmMenuView.setVisible(false);
            mainView.setVisible(true);
        }
    }

    /**
     * Open Sequential Search view with its controller
     */
    private void openSequentialSearch() {
        algorithmMenuView.setVisible(false);
        SequentialSearchView sequentialSearchView = new SequentialSearchView();
        SequentialSearchController controller = new SequentialSearchController(sequentialSearchView);
        controller.setAlgorithmMenuView(algorithmMenuView);
        sequentialSearchView.showWindow();
    }

    /**
     * Open Binary Search view with its controller
     */
    private void openBinarySearch() {
        algorithmMenuView.setVisible(false);
        BinarySearchView binarySearchView = new BinarySearchView();
        BinarySearchController controller = new BinarySearchController(binarySearchView);
        controller.setAlgorithmMenuView(algorithmMenuView);
        binarySearchView.showWindow();
    }

    /**
     * Open Hash Algorithm view with its controller
     */
    private void openHashAlgorithmView() {
        algorithmMenuView.setVisible(false);
        HashAlgorithmView hashAlgorithmView = new HashAlgorithmView();
        HashAlgorithmController controller = new HashAlgorithmController(hashAlgorithmView);
        controller.setAlgorithmMenuView(algorithmMenuView);
        hashAlgorithmView.showWindow();
    }

    /**
     * Show the algorithm menu view
     */
    public void showView() {
        algorithmMenuView.showWindow();
    }

    /**
     * Get the algorithm menu view instance
     */
    public AlgorithmMenuView getView() {
        return algorithmMenuView;
    }
}
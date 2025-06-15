package controller.menu;

import controller.internal_search.BinarySearchController;
import controller.internal_search.SequentialSearchController;
import view.menu.AlgorithmMenuView;
import view.menu.MainView;
import view.internal_search.SequentialSearchView;
import view.internal_search.BinarySearchView;
import view.menu.HashAlgorithmView;
import view.menu.TreeView;

/**
 * Controller for the Algorithm Menu View
 * Handles navigation between different search algorithm implementations
 */
public class AlgorithmMenuController {
    private AlgorithmMenuView algorithmMenuView;
    private MainView mainView;

    /**
     * Constructor that initializes the controller with the algorithm menu view
     * @param algorithmMenuView The view this controller manages
     */
    public AlgorithmMenuController(AlgorithmMenuView algorithmMenuView) {
        this.algorithmMenuView = algorithmMenuView;
        initializeListeners();
    }

    /**
     * Sets the reference to the main view for navigation purposes
     * @param mainView The main view reference
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

        // Tree Search button
        this.algorithmMenuView.addTreeSearchListener(e -> openTreeView());
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
        try {
            algorithmMenuView.setVisible(false);
            SequentialSearchView sequentialSearchView = new SequentialSearchView();
            SequentialSearchController controller = new SequentialSearchController(sequentialSearchView);
            controller.setAlgorithmMenuView(algorithmMenuView);
            sequentialSearchView.showWindow();
        } catch (Exception e) {
            System.err.println("Error opening Sequential Search: " + e.getMessage());
            algorithmMenuView.setVisible(true);
        }
    }

    /**
     * Open Binary Search view with its controller
     */
    private void openBinarySearch() {
        try {
            algorithmMenuView.setVisible(false);
            BinarySearchView binarySearchView = new BinarySearchView();
            BinarySearchController controller = new BinarySearchController(binarySearchView);
            controller.setAlgorithmMenuView(algorithmMenuView);
            binarySearchView.showWindow();
        } catch (Exception e) {
            System.err.println("Error opening Binary Search: " + e.getMessage());
            algorithmMenuView.setVisible(true);
        }
    }

    /**
     * Open Hash Algorithm view with its controller
     */
    private void openHashAlgorithmView() {
        try {
            algorithmMenuView.setVisible(false);
            HashAlgorithmView hashAlgorithmView = new HashAlgorithmView();
            HashAlgorithmController controller = new HashAlgorithmController(hashAlgorithmView);
            controller.setAlgorithmMenuView(algorithmMenuView);
            hashAlgorithmView.showWindow();
        } catch (Exception e) {
            System.err.println("Error opening Hash Algorithm: " + e.getMessage());
            algorithmMenuView.setVisible(true);
        }
    }

    /**
     * Open Tree view with its controller
     */
    private void openTreeView() {
        try {
            algorithmMenuView.setVisible(false);
            TreeView treeView = new TreeView();
            TreeController treeController = new TreeController(treeView);
            treeController.setAlgorithmMenuView(algorithmMenuView);
            treeView.showWindow();
        } catch (Exception e) {
            System.err.println("Error opening Tree View: " + e.getMessage());
            algorithmMenuView.setVisible(true);
        }
    }

    /**
     * Show the algorithm menu view
     */
    public void showView() {
        if (algorithmMenuView != null) {
            algorithmMenuView.showWindow();
        }
    }

    /**
     * Get the algorithm menu view instance
     * @return The algorithm menu view
     */
    public AlgorithmMenuView getView() {
        return algorithmMenuView;
    }
}
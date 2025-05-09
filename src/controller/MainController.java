package controller;

import view.MainView;
import view.AlgorithmMenuView;
import view.SequentialSearchView;
import view.BinarySearchView;
import view.HashAlgorithmView;
import view.TreeView;

public class MainController {
    private MainView mainView;
    private AlgorithmMenuView algorithmMenuView;
    private TreeView treeView;

    public MainController(MainView view) {
        this.mainView = view;
        this.algorithmMenuView = new AlgorithmMenuView();
        this.treeView = new TreeView();

        // MainView -> AlgorithmMenuView
        this.mainView.addInternalSearchListener(e -> {
            mainView.setVisible(false);
            algorithmMenuView.setVisible(true);
        });

        // AlgorithmMenuView -> MainView
        this.algorithmMenuView.addBackListener(e -> {
            algorithmMenuView.setVisible(false);
            mainView.setVisible(true);
        });

        // AlgorithmMenuView -> TreeView
        this.mainView.addTreeSearchListener(e -> {
            mainView.setVisible(false);
            treeView = new TreeView();
            TreeController treeController = new TreeController(treeView);
            treeController.setMainView(mainView);
            treeView.showWindow();
        });

        // AlgorithmMenuView -> SequentialSearchView
        this.algorithmMenuView.addSequentialSearchListener(e -> openSequentialSearch());

        // AlgorithmMenuView -> BinarySearchView
        this.algorithmMenuView.addBinarySearchListener(e -> openBinarySearch());

        // AlgorithmMenuView -> HashAlgorithmView
        this.algorithmMenuView.addHashSearchListener(e -> openHashAlgorithmView());
    }

    // Extracted method to open SequentialSearch view
    private void openSequentialSearch() {
        algorithmMenuView.setVisible(false);
        SequentialSearchView sequentialSearchView = new SequentialSearchView();
        SequentialSearchController controller = new SequentialSearchController(sequentialSearchView);
        controller.setAlgorithmMenuView(algorithmMenuView);

        sequentialSearchView.showWindow();
    }

    // Extracted method to open BinarySearch view
    private void openBinarySearch() {
        algorithmMenuView.setVisible(false);
        BinarySearchView binarySearchView = new BinarySearchView();
        BinarySearchController controller = new BinarySearchController(binarySearchView);
        controller.setAlgorithmMenuView(algorithmMenuView);

        binarySearchView.showWindow();
    }

    // Method to show the Hash view
    private void openHashAlgorithmView() {
        algorithmMenuView.setVisible(false);
        HashAlgorithmView hashAlgorithmView = new HashAlgorithmView();
        HashAlgorithmController controller = new HashAlgorithmController(hashAlgorithmView);
        controller.setAlgorithmMenuView(algorithmMenuView);

        hashAlgorithmView.showWindow();
    }

    // Method to show the Tree view
    private void openTreeView() {
        algorithmMenuView.setVisible(false);
        treeView = new TreeView();
        treeView.showWindow();
    }


    // Main method for demonstration
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            MainController controller = new MainController(mainView);
            mainView.setVisible(true);
        });
    }
}
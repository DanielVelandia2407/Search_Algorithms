package controller;

import view.MainView;
import view.AlgorithmMenuView;
import view.IndicesMenuView;
import view.TreeView;

public class MainController {
    private MainView mainView;
    private AlgorithmMenuController algorithmMenuController;
    private IndicesMenuController indicesMenuController;
    private TreeView treeView;

    public MainController(MainView mainView) {
        this.mainView = mainView;
        initializeControllers();
        initializeListeners();
    }

    /**
     * Initialize sub-controllers
     */
    private void initializeControllers() {
        // Create Algorithm Menu Controller
        AlgorithmMenuView algorithmMenuView = new AlgorithmMenuView();
        this.algorithmMenuController = new AlgorithmMenuController(algorithmMenuView);
        this.algorithmMenuController.setMainView(mainView);

        // Create Indices Menu Controller
        IndicesMenuView indicesMenuView = new IndicesMenuView();
        this.indicesMenuController = new IndicesMenuController(indicesMenuView);
        this.indicesMenuController.setMainView(mainView);
    }

    /**
     * Initialize main view listeners
     */
    private void initializeListeners() {
        // MainView -> AlgorithmMenuView (Internal Search)
        this.mainView.addInternalSearchListener(e -> openAlgorithmMenu());

        // MainView -> TreeView
        this.mainView.addTreeSearchListener(e -> openTreeView());

        // MainView -> IndicesMenuView
        this.mainView.addIndicesListener(e -> openIndicesMenu());

        // TODO: Add listeners for other menu options when implemented
        // this.mainView.addExternalSearchListener(e -> openExternalSearchMenu());
        // this.mainView.addDynamicSearchListener(e -> openDynamicSearchMenu());
    }

    /**
     * Open the algorithm menu (Internal Search)
     */
    private void openAlgorithmMenu() {
        mainView.setVisible(false);
        algorithmMenuController.getView().setVisible(true);
    }

    /**
     * Open the indices menu
     */
    private void openIndicesMenu() {
        mainView.setVisible(false);
        indicesMenuController.getView().setVisible(true);
    }

    /**
     * Open the tree view with its controller
     */
    private void openTreeView() {
        mainView.setVisible(false);
        treeView = new TreeView();
        TreeController treeController = new TreeController(treeView);
        treeController.setMainView(mainView);
        treeView.showWindow();
    }

    /**
     * Show the main view
     */
    public void showView() {
        mainView.setVisible(true);
    }

    /**
     * Get the main view instance
     */
    public MainView getMainView() {
        return mainView;
    }

    /**
     * Get the algorithm menu controller
     */
    public AlgorithmMenuController getAlgorithmMenuController() {
        return algorithmMenuController;
    }

    /**
     * Get the indices menu controller
     */
    public IndicesMenuController getIndicesMenuController() {
        return indicesMenuController;
    }

    /**
     * Main method for demonstration
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            MainController controller = new MainController(mainView);
            controller.showView();
        });
    }
}
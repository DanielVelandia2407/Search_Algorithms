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
    private HashExpansionController hashExpansionController; // Nuevo controlador

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

        // Initialize Hash Expansion Controller (pero no lo creamos aquí, se crea cuando se necesite)
        this.hashExpansionController = null;
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

        // MainView -> HashExpansionView (Dynamic Search) - NUEVA FUNCIONALIDAD
        this.mainView.addDynamicSearchListener(e -> openHashExpansionView());

        // TODO: Add listeners for other menu options when implemented
        // this.mainView.addExternalSearchListener(e -> openExternalSearchMenu());
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
     * Open the hash expansion view (Dynamic Search) - NUEVO MÉTODO
     */
    private void openHashExpansionView() {
        mainView.setVisible(false);

        // Crear una nueva instancia cada vez para permitir diferentes configuraciones
        hashExpansionController = new HashExpansionController();
        hashExpansionController.setMainView(mainView);

        // Mostrar la vista si la inicialización fue exitosa
        if (hashExpansionController.getView() != null) {
            hashExpansionController.showView();
        }
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
     * Get the hash expansion controller
     */
    public HashExpansionController getHashExpansionController() {
        return hashExpansionController;
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
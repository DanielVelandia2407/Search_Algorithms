package controller;

import view.MainView;
import view.AlgorithmMenuView;
import view.ExternalSearchMenuView;
import view.IndicesMenuView;
import view.SearchMenuView;

public class MainController {
    private MainView mainView;
    private AlgorithmMenuController algorithmMenuController;
    private ExternalSearchMenuController externalSearchMenuController;
    private IndicesMenuController indicesMenuController;
    private SearchMenuController searchMenuController;

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

        // Create External Search Menu Controller
        ExternalSearchMenuView externalSearchMenuView = new ExternalSearchMenuView();
        this.externalSearchMenuController = new ExternalSearchMenuController(externalSearchMenuView);
        this.externalSearchMenuController.setMainView(mainView);

        // Create Indices Menu Controller
        IndicesMenuView indicesMenuView = new IndicesMenuView();
        this.indicesMenuController = new IndicesMenuController(indicesMenuView);
        this.indicesMenuController.setMainView(mainView);

        // Create Search Menu Controller
        SearchMenuView searchMenuView = new SearchMenuView();
        this.searchMenuController = new SearchMenuController(searchMenuView);
        this.searchMenuController.setMainView(mainView);
    }

    /**
     * Initialize main view listeners
     */
    private void initializeListeners() {
        // MainView -> AlgorithmMenuView (Internal Search)
        this.mainView.addInternalSearchListener(e -> openAlgorithmMenu());

        // MainView -> ExternalSearchMenuView (External Search)
        this.mainView.addExternalSearchListener(e -> openExternalSearchMenu());

        // MainView -> IndicesMenuView
        this.mainView.addIndicesListener(e -> openIndicesMenu());

        // MainView -> SearchMenuView (Dynamic Search)
        this.mainView.addDynamicSearchListener(e -> openSearchMenu());

        // NOTA: addTreeSearchListener fue removido porque el botón de árboles
        // ahora está en AlgorithmMenuView, no en MainView
    }

    /**
     * Open the algorithm menu (Internal Search)
     */
    private void openAlgorithmMenu() {
        mainView.setVisible(false);
        algorithmMenuController.getView().setVisible(true);
    }

    /**
     * Open the external search menu
     */
    private void openExternalSearchMenu() {
        mainView.setVisible(false);
        externalSearchMenuController.getView().setVisible(true);
    }

    /**
     * Open the indices menu
     */
    private void openIndicesMenu() {
        mainView.setVisible(false);
        indicesMenuController.getView().setVisible(true);
    }

    /**
     * Open the search menu
     */
    private void openSearchMenu() {
        mainView.setVisible(false);
        searchMenuController.showView();
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
     * Get the external search menu controller
     */
    public ExternalSearchMenuController getExternalSearchMenuController() {
        return externalSearchMenuController;
    }

    /**
     * Get the indices menu controller
     */
    public IndicesMenuController getIndicesMenuController() {
        return indicesMenuController;
    }

    /**
     * Get the search menu controller
     */
    public SearchMenuController getSearchMenuController() {
        return searchMenuController;
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
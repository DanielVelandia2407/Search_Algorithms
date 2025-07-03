package controller.menu;

import view.menu.MainView;
import view.menu.AlgorithmMenuView;
import view.menu.ExternalSearchMenuView;
import view.menu.IndicesMenuView;
import view.menu.WelcomeView;
import javax.swing.JFrame;

public class MainController {
    private MainView mainView;
    private JFrame parentView; // Referencia genérica al menú padre
    private AlgorithmMenuController algorithmMenuController;
    private ExternalSearchMenuController externalSearchMenuController;
    private IndicesMenuController indicesMenuController;

    public MainController(MainView mainView) {
        this.mainView = mainView;
        initializeControllers();
        initializeListeners();
    }

    /**
     * Set the welcome view reference for navigation
     */
    public void setWelcomeView(WelcomeView welcomeView) {
        this.parentView = welcomeView;
    }

    /**
     * Set the main view reference for navigation (for compatibility)
     */
    public void setParentView(MainView parentMainView) {
        this.parentView = parentMainView;
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

        // MainView -> WelcomeView (Volver)
        this.mainView.addBackListener(e -> backToWelcome());
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
     * Return to welcome menu
     */
    public void backToWelcome() {
        mainView.setVisible(false);
        if (parentView != null) {
            parentView.setVisible(true);
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
     * Main method for demonstration (ahora debe usar WelcomeController)
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Ahora la aplicación debe iniciarse desde WelcomeController
            System.out.println("Use WelcomeController.main() para iniciar la aplicación completa");
        });
    }
}
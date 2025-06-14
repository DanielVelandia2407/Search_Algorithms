package controller.menu;

import controller.external_search.HashExpansionController;
import controller.external_search.PartialExpansionController;
import view.menu.SearchMenuView;
import view.menu.MainView;
import view.menu.ExternalSearchMenuView; // NUEVO: Para navegación de vuelta

public class SearchMenuController {
    private SearchMenuView view;
    private MainView mainView;
    private ExternalSearchMenuView externalSearchMenuView; // NUEVO: Referencia al menú padre
    private HashExpansionController hashExpansionController;
    private PartialExpansionController partialExpansionController;

    public SearchMenuController(SearchMenuView view) {
        this.view = view;
        initializeListeners();
    }

    /**
     * Establece la referencia al menú principal
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    /**
     * NUEVO: Establece la referencia al menú de búsqueda externa (menú padre)
     */
    public void setExternalSearchMenuView(ExternalSearchMenuView externalSearchMenuView) {
        this.externalSearchMenuView = externalSearchMenuView;
    }

    /**
     * Obtiene la vista del menú de búsquedas
     */
    public SearchMenuView getView() {
        return view;
    }

    /**
     * Inicializa los listeners de los botones
     */
    private void initializeListeners() {
        // Botón Búsqueda Parcial
        view.addPartialSearchListener(e -> openPartialSearch());

        // Botón Búsqueda Total -> Hash Expansion
        view.addTotalSearchListener(e -> openTotalSearch());

        // MODIFICADO: Botón Volver -> ExternalSearchMenuView (no MainView)
        view.addBackListener(e -> returnToParentMenu());
    }

    /**
     * Abre la funcionalidad de búsqueda parcial (Hash Expansion Parcial)
     */
    private void openPartialSearch() {
        view.setVisible(false);

        // Crear una nueva instancia del controlador de expansión parcial
        partialExpansionController = new PartialExpansionController();
        partialExpansionController.setMainView(mainView);
        partialExpansionController.setSearchMenuView(view);

        // Mostrar la vista si la inicialización fue exitosa
        if (partialExpansionController.getView() != null) {
            partialExpansionController.showView();
        } else {
            // Si hay error, volver a mostrar este menú
            view.setVisible(true);
        }
    }

    /**
     * Abre la búsqueda total (Hash Expansion)
     */
    private void openTotalSearch() {
        view.setVisible(false);

        // Crear una nueva instancia del controlador de hash expansion
        hashExpansionController = new HashExpansionController();
        hashExpansionController.setMainView(mainView);
        hashExpansionController.setSearchMenuView(view);

        // Mostrar la vista si la inicialización fue exitosa
        if (hashExpansionController.getView() != null) {
            hashExpansionController.showView();
        } else {
            // Si hay error, volver a mostrar este menú
            view.setVisible(true);
        }
    }

    /**
     * MODIFICADO: Regresa al menú padre (ExternalSearchMenuView) en lugar del menú principal
     */
    private void returnToParentMenu() {
        view.setVisible(false);
        if (externalSearchMenuView != null) {
            // Prioridad: Regresar al menú de búsqueda externa
            externalSearchMenuView.setVisible(true);
        } else if (mainView != null) {
            // Fallback: Si no hay referencia al menú externo, ir al principal
            mainView.setVisible(true);
        }
    }

    /**
     * Muestra la vista del menú de búsquedas
     */
    public void showView() {
        view.setVisible(true);
    }

    /**
     * Obtiene el controlador de hash expansion actual
     */
    public HashExpansionController getHashExpansionController() {
        return hashExpansionController;
    }

    /**
     * Obtiene el controlador de expansión parcial actual
     */
    public PartialExpansionController getPartialExpansionController() {
        return partialExpansionController;
    }

    /**
     * NUEVO: Obtiene la referencia al menú de búsqueda externa
     */
    public ExternalSearchMenuView getExternalSearchMenuView() {
        return externalSearchMenuView;
    }
}
package controller;

import view.SearchMenuView;
import view.MainView;

public class SearchMenuController {
    private SearchMenuView view;
    private MainView mainView;
    private HashExpansionController hashExpansionController;
    private PartialExpansionController partialExpansionController; // NUEVO

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

        // Botón Volver -> MainView
        view.addBackListener(e -> returnToMainView());
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
        hashExpansionController.setSearchMenuView(view); // NUEVO: Para que regrese a este menú

        // Mostrar la vista si la inicialización fue exitosa
        if (hashExpansionController.getView() != null) {
            hashExpansionController.showView();
        } else {
            // Si hay error, volver a mostrar este menú
            view.setVisible(true);
        }
    }

    /**
     * Regresa al menú principal
     */
    private void returnToMainView() {
        view.setVisible(false);
        if (mainView != null) {
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
}
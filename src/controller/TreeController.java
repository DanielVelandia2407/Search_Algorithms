package controller;

import model.DigitalTreeModel;
import model.ResiduoSimpleModel;
import model.ResiduoMultipleModel;  // ← Cambiado por la clase de tu amigo
import model.HuffmanTreeModel;
import view.AlgorithmMenuView;
import view.TreeView;
import view.DigitalTreeView;
import view.TrieResiduoSimpleView;
import view.MultipleResiduoView;     // ← Cambiado por la clase de tu amigo
import view.HuffmanTreeView;

public class TreeController {
    private TreeView view;
    private AlgorithmMenuView algorithmMenuView;

    public TreeController(TreeView view) {
        this.view = view;
        initComponents();

        // TreeView -> DigitalTreeView
        view.addDigitalTreeListener(e -> openDigitalTree());

        // TreeView -> TrieResiduoSimpleView (Árbol por Residuos)
        view.addWastedTreeListener(e -> openResidueSimple());

        // TreeView -> MultipleResiduoView (Árbol por Residuos Múltiples) ← Actualizado
        view.addMultipleWastedTreeListener(e -> openMultipleResidueTree());

        // TreeView -> HuffmanTreeView (Árbol Huffman)
        view.addHuffmanTreeListener(e -> openHuffmanTree());
    }

    private void initComponents() {
        // Initialize components here if needed
        view.addBackListener(e -> goBack());
    }

    public void openDigitalTree() {
        // Ocultar esta vista (no cerrarla completamente)
        view.setVisible(false);

        DigitalTreeModel digitalTreeModel = new DigitalTreeModel();
        DigitalTreeView digitalTreeView = new DigitalTreeView();

        // Pasar las referencias al controlador y a la vista actual
        DigitalTreeController digitalTreeController = new DigitalTreeController(
                digitalTreeModel,
                digitalTreeView,
                this,  // Pasar referencia a este controlador
                view   // Pasar referencia a esta vista
        );

        digitalTreeController.initView();
    }

    // Método para abrir la vista de residuo simple
    public void openResidueSimple() {
        // Ocultar esta vista (no cerrarla completamente)
        view.setVisible(false);

        // Crear modelo y vista para el residuo simple
        ResiduoSimpleModel residueSimpleModel = new ResiduoSimpleModel();
        TrieResiduoSimpleView residueSimpleView = new TrieResiduoSimpleView();

        // Crear el controlador de residuo simple
        ResiduoSimpleController residueSimpleController = new ResiduoSimpleController(
                residueSimpleModel,
                residueSimpleView
        );

        // Inicializar la vista
        residueSimpleController.init();
    }

    // Método actualizado para usar las clases de tu amigo
    public void openMultipleResidueTree() {
        // Ocultar esta vista (no cerrarla completamente)
        view.setVisible(false);

        // Usar un tamaño de grupo para residuos múltiples (tu amigo usa tamGrupo)
        int tamGrupo = 2; // Puedes ajustar este valor según necesites
        ResiduoMultipleModel multipleResidueModel = new ResiduoMultipleModel(tamGrupo);
        MultipleResiduoView multipleResidueView = new MultipleResiduoView(tamGrupo);

        // Crear el controlador de tu amigo
        ResiduoMultipleController multipleResidueController = new ResiduoMultipleController(
                multipleResidueModel,
                multipleResidueView
        );

        // Inicializar la vista usando el método de tu amigo
        multipleResidueController.init();
    }

    public void openHuffmanTree() {
        view.setVisible(false);

        HuffmanTreeModel huffmanModel = new HuffmanTreeModel();
        HuffmanTreeView huffmanView = new HuffmanTreeView();

        HuffmanTreeController huffmanController = new HuffmanTreeController(
                huffmanModel,
                huffmanView,
                this,
                view
        );

        huffmanController.initView();
    }

    /**
     * Sets the reference to the algorithm menu view for navigation purposes
     */
    public void setAlgorithmMenuView(AlgorithmMenuView algorithmMenuView) {
        this.algorithmMenuView = algorithmMenuView;
    }

    /**
     * Navigate back to the algorithm menu
     */
    private void goBack() {
        view.dispose();

        if (algorithmMenuView != null) {
            algorithmMenuView.setVisible(true);
        }
    }

    /**
     * Show the tree view
     */
    public void showView() {
        view.showWindow();
    }

    /**
     * Get the tree view instance
     */
    public TreeView getView() {
        return view;
    }
}
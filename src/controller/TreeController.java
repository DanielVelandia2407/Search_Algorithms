package controller;

import model.DigitalTreeModel;
import model.ResidueHashModel;
import model.MultipleResidueTreeModel;
import model.HuffmanTreeModel;
import view.AlgorithmMenuView;
import view.MainView;
import view.TreeView;
import view.DigitalTreeView;
import view.ResidueHashView;
import view.MultipleResidueTreeView;
import view.HuffmanTreeView;

public class TreeController {
    private TreeView view;
    private MainView mainView;

    public TreeController(TreeView view) {
        this.view = view;
        initComponents();

        // TreeView -> DigitalTreeView
        view.addDigitalTreeListener(e -> openDigitalTree());

        // TreeView -> ResidueHashView (Árbol por Residuos)
        view.addWastedTreeListener(e -> openResidueHash());

        // TreeView -> MultipleResidueTreeView (Árbol por Residuos Múltiples)
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

    // Método para abrir la vista de hash residual
    public void openResidueHash() {
        // Ocultar esta vista (no cerrarla completamente)
        view.setVisible(false);

        // Crear modelo y vista para el hash residual
        ResidueHashModel residueHashModel = new ResidueHashModel(11); // Usar un número primo como tamaño inicial
        ResidueHashView residueHashView = new ResidueHashView();

        // Pasar las referencias al controlador y a la vista actual
        ResidueHashController residueHashController = new ResidueHashController(
                residueHashModel,
                residueHashView,
                this,  // Pasar referencia a este controlador
                view   // Pasar referencia a esta vista
        );

        // Inicializar la vista
        residueHashController.initView();

        // Cargar algunos datos de ejemplo (opcional)
        residueHashModel.put(25, "Veinticinco");
        residueHashModel.put(42, "Cuarenta y dos");
        residueHashModel.put(53, "Cincuenta y tres");
        residueHashController.updateVisualization();
    }

    // Método para abrir la vista de árbol de residuos múltiples
    public void openMultipleResidueTree() {
        // Ocultar esta vista (no cerrarla completamente)
        view.setVisible(false);

        // Usar varios divisores primos para el método de residuos múltiples
        int[] divisors = {11, 13, 17, 19, 23};
        MultipleResidueTreeModel multipleResidueModel = new MultipleResidueTreeModel(divisors);
        MultipleResidueTreeView multipleResidueView = new MultipleResidueTreeView();

        // Pasar las referencias al controlador y a la vista actual
        MultipleResidueTreeController multipleResidueController = new MultipleResidueTreeController(
                multipleResidueModel,
                multipleResidueView,
                this,  // Pasar referencia a este controlador
                view   // Pasar referencia a esta vista
        );

        // Inicializar la vista
        multipleResidueController.initView();

        // Cargar algunos datos de ejemplo (opcional)
        multipleResidueModel.put(25, "Veinticinco");
        multipleResidueModel.put(42, "Cuarenta y dos");
        multipleResidueModel.put(53, "Cincuenta y tres");
        multipleResidueModel.put(14, "Catorce");
        multipleResidueModel.put(36, "Treinta y seis");
        multipleResidueController.updateVisualization();
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

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    private void goBack() {
        view.dispose();

        if (mainView != null) {
            mainView.setVisible(true);
        }
    }
}
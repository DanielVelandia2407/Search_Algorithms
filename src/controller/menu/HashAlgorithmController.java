package controller.menu;

import controller.internal_search.FoldingSearchController;
import controller.internal_search.ModSearchController;
import controller.internal_search.SquaredSearchController;
import controller.internal_search.TruncSearchController;
import view.menu.AlgorithmMenuView;
import view.menu.HashAlgorithmView;
import view.internal_search.ModSearchView;
import view.internal_search.SquaredSearchView;
import view.internal_search.TruncSearchView;
import view.internal_search.FoldingSearchView;

public class HashAlgorithmController {

    private HashAlgorithmView view;
    private AlgorithmMenuView algorithmMenuView;

    public HashAlgorithmController(HashAlgorithmView view) {
        this.view = view;
        initComponents();

        // HashAlgorithmView -> ModSearchView
        view.addModSearchListener(e -> openModSearch());

        // HashAlgorithmView -> AlgorithmMenuView
        view.addBackListener(e -> goBack());

        // HashAlgorithmView -> SquaredSearchView
        view.addSquaredSearchListener(e -> openSquaredSearch());

        // HashAlgorithmView -> TruncSearchView
        view.addTruncatedSearchListener(e -> openTruncSearch());

        // HashAlgorithmView -> FoldingSearchView
        view.addFoldingSearchListener(e -> openFoldingSearch());
    }

    private void initComponents() {
        // Initialize components here if needed
        view.addBackListener(e -> goBack());
    }

    public void setAlgorithmMenuView(AlgorithmMenuView algorithmMenuView) {
        this.algorithmMenuView = algorithmMenuView;
    }

    public void openModSearch() {
        // Close current view
        view.dispose();

        // Create and show ModSearchView
        ModSearchView modSearchView = new ModSearchView();
        ModSearchController modSearchController = new ModSearchController(modSearchView);
        modSearchController.setHashAlgorithmView(view);
        modSearchView.setVisible(true);
    }

    public void openSquaredSearch() {
        // Close current view
        view.dispose();

        // Create and show ModSearchView
        SquaredSearchView squaredSearchView = new SquaredSearchView();
        SquaredSearchController squaredSearchController = new SquaredSearchController(squaredSearchView);
        squaredSearchController.setHashAlgorithmView(view);
        squaredSearchView.setVisible(true);
    }

    public void openTruncSearch() {
        // Close current view
        view.dispose();

        // Create and show ModSearchView
        TruncSearchView truncSearchView = new TruncSearchView();
        TruncSearchController truncSearchController = new TruncSearchController(truncSearchView);
        truncSearchController.setHashAlgorithmView(view);
        truncSearchView.setVisible(true);
    }

    public void openFoldingSearch() {
        // Close current view
        view.dispose();

        // Create and show ModSearchView
        FoldingSearchView foldingSearchView = new FoldingSearchView();
        FoldingSearchController foldingSearchController = new FoldingSearchController(foldingSearchView);
        foldingSearchController.setHashAlgorithmView(view);
        foldingSearchView.setVisible(true);
    }

    private void goBack() {
        // Close current view
        view.dispose();

        // Show algorithm menu view if available
        if (algorithmMenuView != null) {
            algorithmMenuView.setVisible(true);
        }
    }
}
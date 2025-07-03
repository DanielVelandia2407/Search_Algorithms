import controller.menu.WelcomeController;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomeController welcomeController = new WelcomeController();
            welcomeController.showView();
        });
    }
}
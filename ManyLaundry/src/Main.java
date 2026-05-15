import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main.java
 * Application entry point — bootstraps the LaundrySystem and launches the GUI.
 */
public class Main {

    public static void main(String[] args) {
        // Apply system look-and-feel for cleaner native widgets
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LaundrySystem system = new LaundrySystem();
            DashboardUI   ui     = new DashboardUI(system);
            ui.setVisible(true);
        });
    }
}

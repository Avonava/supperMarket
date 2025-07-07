package online_shopp;

import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set a modern background color
        getContentPane().setBackground(new Color(245, 245, 245));

        // Create a stylish title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(33, 150, 243));  // Modern blue
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Stylish tabbed pane with customized UI
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 16));
        tabbedPane.setBackground(new Color(224, 224, 224));
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return 200;  // Wider tab for sidebar style
            }
        });

        tabbedPane.addTab("🛍️ Products", new ProductManagementPanel());
        tabbedPane.addTab("📦 Orders", new OrderManagementPanel());
        tabbedPane.addTab("👥 Customers", new CustomerDetailPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Modern Logout Button
        JButton logoutButton = new JButton("Logout 🚪");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        logoutButton.setBackground(new Color(244, 67, 54));  // Red
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage();  // Back to login
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard());
    }
}

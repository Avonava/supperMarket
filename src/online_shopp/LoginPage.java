package online_shopp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton, registerButton;
    private Image backgroundImage;

    public LoginPage() {
        setTitle("Electronics Store Login");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image (replace with your image path)
        backgroundImage = new ImageIcon("C:\\Users\\Nova\\Documents\\electronics_bg3.jpg").getImage();

        // Set a custom content pane with background image
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        });
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Color fieldColor = new Color(255, 255, 255, 200); // Semi-transparent white
        Color buttonColor = new Color(0, 120, 215);       // Vibrant blue for buttons

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(Color.WHITE);
        add(userLabel, gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setBackground(fieldColor);
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(Color.WHITE);
        add(passLabel, gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setBackground(fieldColor);
        add(passwordField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(labelFont);
        roleLabel.setForeground(Color.WHITE);
        add(roleLabel, gbc);
        gbc.gridx = 1;
        String[] roles = { "Admin", "Customer" };
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBackground(fieldColor);
        add(roleComboBox, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);  // Make button panel transparent

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        styleButton(loginButton, buttonColor);
        styleButton(registerButton, buttonColor);

        btnPanel.add(loginButton);
        btnPanel.add(registerButton);
        add(btnPanel, gbc);

        // Button actions
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> openRegister());

        setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tableName = role.equals("Admin") ? "admins" : "customers";
        String sql = "SELECT * FROM " + tableName + " WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, role + " login successful!");

                if (role.equals("Admin")) {
                    new AdminDashboard();
                } else {
                    JFrame customerFrame = new JFrame("Customer Dashboard");
                    customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    customerFrame.setSize(800, 600);
                    customerFrame.setLocationRelativeTo(null);

                    CustomerDashboard dashboardPanel = new CustomerDashboard();
                    customerFrame.setContentPane(dashboardPanel);

                    customerFrame.setVisible(true);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid " + role + " credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegister() {
        new RegisterDashboard();  // You must create this class
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}

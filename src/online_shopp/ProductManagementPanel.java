package online_shopp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

public class ProductManagementPanel extends JPanel {
    private JTextField nameField;
    private JTextField priceField;
    private JTextArea descriptionArea;
    private JLabel imageLabel;
    private JButton selectImageButton;
    private File selectedImage;

    public ProductManagementPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 245, 245));  // Light background

        JLabel titleLabel = new JLabel("Product Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 26));
        titleLabel.setForeground(new Color(0, 102, 204));  // Blue accent
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(nameLabel);
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(nameField);

        JLabel priceLabel = new JLabel("Product Price:");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(priceLabel);
        priceField = new JTextField();
        priceField.setFont(new Font("Arial", Font.PLAIN, 14));
        priceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(priceField);

        JLabel descLabel = new JLabel("Product Description:");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(descLabel);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        formPanel.add(descriptionScroll);

        JLabel imageLabelTitle = new JLabel("Product Image:");
        imageLabelTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(imageLabelTitle);
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        imagePanel.setBackground(new Color(255, 255, 255));
        imageLabel = new JLabel("No image selected");
        imageLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        selectImageButton = new JButton("Select Image");
        styleButton(selectImageButton);
        selectImageButton.addActionListener(e -> chooseImage());
        imagePanel.add(imageLabel);
        imagePanel.add(selectImageButton);
        formPanel.add(imagePanel);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton addButton = new JButton("Add Product");
        JButton editButton = new JButton("Edit Product");
        JButton deleteButton = new JButton("Delete Product");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);

        addButton.addActionListener(e -> addProduct());
        editButton.addActionListener(e -> editProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedImage = fileChooser.getSelectedFile();
            imageLabel.setText(selectedImage.getName());
        }
    }

    private void addProduct() {
        String name = nameField.getText();
        String priceText = priceField.getText();
        String description = descriptionArea.getText();
        String imagePath = (selectedImage != null) ? selectedImage.getAbsolutePath() : null;

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter product name and price.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO Products (name, price, details, image_path) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setString(3, description);
            stmt.setString(4, imagePath);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                nameField.setText("");
                priceField.setText("");
                descriptionArea.setText("");
                imageLabel.setText("No image selected");
                selectedImage = null;
            }
            conn.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format. Please enter a valid number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void editProduct() {
        String productIdOrName = JOptionPane.showInputDialog(this, "Enter Product ID or Name to edit:");
        if (productIdOrName == null || productIdOrName.isEmpty()) {
            return;
        }
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM products WHERE product_id = ? OR name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productIdOrName);
            stmt.setString(2, productIdOrName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String currentName = rs.getString("name");
                double currentPrice = rs.getDouble("price");
                String currentDescription = rs.getString("details");
                String currentImage = rs.getString("image_path");

                String newName = JOptionPane.showInputDialog(this, "Enter new name:", currentName);
                String newPriceText = JOptionPane.showInputDialog(this, "Enter new price:", String.valueOf(currentPrice));
                String newDescription = JOptionPane.showInputDialog(this, "Enter new description:", currentDescription);

                String newImage = currentImage;
                int changeImage = JOptionPane.showConfirmDialog(this, "Do you want to change the image?", "Change Image", JOptionPane.YES_NO_OPTION);
                if (changeImage == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    int option = fileChooser.showOpenDialog(this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        newImage = fileChooser.getSelectedFile().getAbsolutePath();
                    }
                }

                double newPrice = currentPrice;
                try {
                    newPrice = Double.parseDouble(newPriceText);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid price format. Keeping the original price.");
                }

                String updateSQL = "UPDATE products SET name=?, price=?, details=?, image_path=? WHERE product_id = ? OR name = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setString(1, newName);
                updateStmt.setDouble(2, newPrice);
                updateStmt.setString(3, newDescription);
                updateStmt.setString(4, newImage);
                updateStmt.setString(5, productIdOrName);
                updateStmt.setString(6, productIdOrName);
                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed. Please check the Product ID or Name.");
                }
                conn.close();
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        try {
            Connection conn = DBConnection.getConnection();
            String fetchSQL = "SELECT product_id, name FROM products";
            PreparedStatement fetchStmt = conn.prepareStatement(fetchSQL);
            ResultSet rs = fetchStmt.executeQuery();

            java.util.List<String> products = new java.util.ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("name");
                products.add(id + " - " + name);
            }

            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No products found to delete.");
                conn.close();
                return;
            }

            String selectedProduct = (String) JOptionPane.showInputDialog(this, "Select a product to delete:", "Delete Product", JOptionPane.PLAIN_MESSAGE, null, products.toArray(), products.get(0));

            if (selectedProduct == null || selectedProduct.isEmpty()) {
                conn.close();
                return;
            }

            String selectedId = selectedProduct.split(" - ")[0];
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + selectedProduct + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                conn.close();
                return;
            }

            String deleteSQL = "DELETE FROM products WHERE product_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
            deleteStmt.setInt(1, Integer.parseInt(selectedId));

            int rowsDeleted = deleteStmt.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Product deletion failed.");
            }
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}

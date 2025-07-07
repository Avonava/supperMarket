package online_shopp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.*;
import java.io.File;

public class CustomerDashboard extends JPanel {
    private JTextField searchField;
    private JPanel productListPanel;
    private DefaultListModel<String> cartListModel;
    private JList<String> cartList;
    private Map<String, Product> products;
    private Map<String, Integer> cart;

    private int currentCustomerId = 1;
    
    // Modern color palette
    private final Color PRIMARY_COLOR = new Color(40, 116, 240); // Vibrant blue
    private final Color SECONDARY_COLOR = new Color(255, 87, 34); // Orange
    private final Color BACKGROUND_COLOR = new Color(250, 250, 252); // Very light gray
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    private final Color BORDER_COLOR = new Color(230, 230, 230);

    public CustomerDashboard() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        products = new LinkedHashMap<>();
        cart = new LinkedHashMap<>();
        loadProductsFromDatabase();

        // Header Panel with Search
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setBackground(PRIMARY_COLOR);
        
        // Store title
        JLabel storeTitle = new JLabel("ElectroShop");
        storeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        storeTitle.setForeground(Color.WHITE);
        headerPanel.add(storeTitle, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setOpaque(false);
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        searchField.setBackground(CARD_COLOR);

        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, PRIMARY_COLOR.darker());
        searchBtn.setPreferredSize(new Dimension(100, 40));
        searchBtn.addActionListener(e -> searchProducts());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Main Content Area
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainContent.setBackground(BACKGROUND_COLOR);

        // Product List Panel - Now in a grid layout
        productListPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        productListPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane productScroll = new JScrollPane(productListPanel);
        productScroll.setBorder(BorderFactory.createEmptyBorder());
        productScroll.getVerticalScrollBar().setUnitIncrement(16);
        productScroll.setBackground(BACKGROUND_COLOR);
        productScroll.setPreferredSize(new Dimension(600, 500));

        // Cart Panel - Modernized
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setPreferredSize(new Dimension(300, 500));
        cartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        cartPanel.setBackground(CARD_COLOR);

        // Cart Header
        JPanel cartHeader = new JPanel(new BorderLayout());
        cartHeader.setBackground(CARD_COLOR);
        JLabel cartTitle = new JLabel("Your Shopping Cart");
        cartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cartTitle.setForeground(TEXT_COLOR);
        cartHeader.add(cartTitle, BorderLayout.WEST);
        
        JLabel cartIcon = new JLabel("🛒");
        cartIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        cartHeader.add(cartIcon, BorderLayout.EAST);
        cartPanel.add(cartHeader, BorderLayout.NORTH);

        // Cart Items
        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        cartList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cartList.setBackground(CARD_COLOR);
        cartList.setSelectionBackground(PRIMARY_COLOR.brighter());
        cartList.setSelectionForeground(Color.WHITE);
        
        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(BorderFactory.createEmptyBorder());
        cartPanel.add(cartScroll, BorderLayout.CENTER);

        // Cart Buttons
        JPanel cartBtnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        cartBtnPanel.setBackground(CARD_COLOR);
        cartBtnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton removeBtn = new JButton("Remove");
        styleButton(removeBtn, new Color(220, 53, 69)); // Red
        removeBtn.addActionListener(e -> removeFromCart());

        JButton checkoutBtn = new JButton("Checkout");
        styleButton(checkoutBtn, new Color(40, 167, 69)); // Green
        checkoutBtn.addActionListener(e -> checkout());

        cartBtnPanel.add(removeBtn);
        cartBtnPanel.add(checkoutBtn);
        cartPanel.add(cartBtnPanel, BorderLayout.SOUTH);

        mainContent.add(productScroll, BorderLayout.CENTER);
        mainContent.add(cartPanel, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);

        // Footer with Logout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        footerPanel.setBackground(BACKGROUND_COLOR);

        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, TEXT_COLOR);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        footerPanel.add(logoutBtn);

        add(footerPanel, BorderLayout.SOUTH);

        displayAllProducts();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private JPanel createProductItem(Product product) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(CARD_COLOR);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Image with modern frame
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CARD_COLOR);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        imagePanel.setPreferredSize(new Dimension(150, 150));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        if (new File(product.getImagePath()).exists()) {
            ImageIcon icon = new ImageIcon(product.getImagePath());
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } else {
            imageLabel.setIcon(new ImageIcon(createPlaceholderImage(150, 150)));
        }
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        panel.add(imagePanel, BorderLayout.CENTER);

        // Product details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(CARD_COLOR);
        
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        
        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(PRIMARY_COLOR);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        
        JLabel descLabel = new JLabel("<html><p style='width:200px;color:#666;font-size:12px'>" 
            + product.getDescription() + "</p></html>");
        
        detailsPanel.add(nameLabel);
        detailsPanel.add(priceLabel);
        detailsPanel.add(descLabel);
        panel.add(detailsPanel, BorderLayout.NORTH);

        // Add to cart controls
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(CARD_COLOR);
        
        SpinnerNumberModel quantityModel = new SpinnerNumberModel(1, 1, 99, 1);
        JSpinner quantitySpinner = new JSpinner(quantityModel);
        ((JSpinner.DefaultEditor)quantitySpinner.getEditor()).getTextField().setColumns(2);
        quantitySpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JPanel spinnerPanel = new JPanel();
        spinnerPanel.setBackground(CARD_COLOR);
        spinnerPanel.add(new JLabel("Qty:"));
        spinnerPanel.add(quantitySpinner);
        
        JButton addButton = new JButton("Add to Cart");
        styleButton(addButton, SECONDARY_COLOR);
        addButton.addActionListener(e -> {
            int qty = (Integer) quantitySpinner.getValue();
            addToCart(product.getName(), qty);
        });
        
        controlsPanel.add(spinnerPanel, BorderLayout.WEST);
        controlsPanel.add(addButton, BorderLayout.EAST);
        panel.add(controlsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private Image createPlaceholderImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        
        // Gradient background
        GradientPaint gp = new GradientPaint(0, 0, new Color(240, 240, 240), 
                                           width, height, new Color(220, 220, 220));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
        
        // Product icon
        g2d.setColor(new Color(180, 180, 180));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "📱";
        int x = (width - fm.stringWidth(text)) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return img;
    }

    // ALL THE FOLLOWING METHODS REMAIN EXACTLY THE SAME AS IN THE ORIGINAL
    private void loadProductsFromDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/online_shopp", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
            while (rs.next()) {
                String name = rs.getString("name");
                String imagePath = rs.getString("image_path");
                double price = rs.getDouble("price");
                String description = rs.getString("details");
                products.put(name, new Product(name, imagePath, price, description));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products:\n" + e.getMessage());
        }
    }

    private void displayAllProducts() {
        productListPanel.removeAll();
        for (Product product : products.values()) {
            productListPanel.add(createProductItem(product));
        }
        productListPanel.revalidate();
        productListPanel.repaint();
    }

    private void searchProducts() {
        String keyword = searchField.getText().trim().toLowerCase();
        productListPanel.removeAll();
        for (Product product : products.values()) {
            if (product.getName().toLowerCase().contains(keyword)) {
                productListPanel.add(createProductItem(product));
            }
        }
        productListPanel.revalidate();
        productListPanel.repaint();
    }

    private void addToCart(String productName, int quantity) {
        cart.put(productName, cart.getOrDefault(productName, 0) + quantity);
        refreshCartList();
    }

    private void refreshCartList() {
        cartListModel.clear();
        double total = 0;
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue();
            double price = products.get(name).getPrice();
            total += price * qty;
            cartListModel.addElement(name + " - Qty: " + qty + " - $" + (price * qty));
        }
        cartListModel.addElement("Total: $" + String.format("%.2f", total));
    }

    private void removeFromCart() {
        int index = cartList.getSelectedIndex();
        if (index == -1 || index == cartListModel.size() - 1) {
            JOptionPane.showMessageDialog(this, "Please select a product to remove.");
            return;
        }
        String selected = cartListModel.getElementAt(index);
        String productName = selected.split(" - ")[0];
        cart.remove(productName);
        refreshCartList();
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Proceed to payment?", "Payment Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO orders (customer_id, total, status) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, getCurrentCustomerId());
                stmt.setDouble(2, calculateTotalAmount());
                stmt.setString(3, "Pending");
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Payment successful! Order placed.");
                cart.clear();
                refreshCartList();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error placing order:\n" + e.getMessage());
            }
        }
    }

    private int getCurrentCustomerId() {
        return currentCustomerId;
    }

    private double calculateTotalAmount() {
        return cart.entrySet().stream()
                .mapToDouble(e -> products.get(e.getKey()).getPrice() * e.getValue())
                .sum();
    }

    private void logout() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose();
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }

    private static class Product {
        private String name, imagePath, description;
        private double price;

        public Product(String name, String imagePath, double price, String description) {
            this.name = name;
            this.imagePath = imagePath;
            this.price = price;
            this.description = description;
        }

        public String getName() { return name; }
        public String getImagePath() { return imagePath; }
        public double getPrice() { return price; }
        public String getDescription() { return description; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // Additional UI improvements
                UIManager.put("Button.arc", 999);
                UIManager.put("Component.arc", 999);
                UIManager.put("TextComponent.arc", 5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Customer Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 700); // Larger window for better display
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new CustomerDashboard());
            frame.setVisible(true);
        });
    }
}
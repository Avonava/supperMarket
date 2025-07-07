package online_shopp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OrderManagementPanel extends JPanel {
    private JTable orderTable;
    private DefaultTableModel orderTableModel;

    public OrderManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250)); // Soft background color

        // Title Label
        JLabel titleLabel = new JLabel("Order Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180)); // Steel blue
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Table Styling
        String[] columns = {"Order ID", "Customer", "Total", "Status"};
        orderTableModel = new DefaultTableModel(columns, 0);
        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(30);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        orderTable.getTableHeader().setBackground(new Color(100, 149, 237)); // Cornflower Blue
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.setGridColor(new Color(200, 200, 200));

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Load Data
        loadOrdersFromDatabase();

        // Update Button Styling
        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        updateStatusButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        updateStatusButton.setForeground(Color.WHITE);
        updateStatusButton.setFocusPainted(false);
        updateStatusButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        updateStatusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateStatusButton.addActionListener(e -> updateStatus());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttonPanel.add(updateStatusButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadOrdersFromDatabase() {
        orderTableModel.setRowCount(0);

        String query = "SELECT o.order_id, o.customer_id, o.total, o.status " +
                       "FROM orders o";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String customerName = rs.getString("customer_id");
                double total = rs.getDouble("total");
                String status = rs.getString("status");

                orderTableModel.addRow(new Object[]{
                        orderId,
                        customerName,
                        String.format("$%.2f", total),
                        status
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load orders:\n" + e.getMessage());
        }
    }

    private void updateStatus() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to update.");
            return;
        }

        String newStatus = JOptionPane.showInputDialog(this, "Enter new status:");
        if (newStatus != null && !newStatus.trim().isEmpty()) {
            int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);

            if (updateOrderStatusInDatabase(orderId, newStatus.trim())) {
                orderTableModel.setValueAt(newStatus.trim(), selectedRow, 3);
                JOptionPane.showMessageDialog(this, "Order status updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order status.");
            }
        }
    }

    private boolean updateOrderStatusInDatabase(int orderId, String newStatus) {
        String updateQuery = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

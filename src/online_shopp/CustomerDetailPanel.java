package online_shopp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerDetailPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel customerTableModel;

    public CustomerDetailPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250)); // Soft background color

        // Title Label
        JLabel titleLabel = new JLabel("Customer Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180)); // Steel blue
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Table Styling
        String[] columns = {"Customer ID", "Name", "Email", "Phone"};
        customerTableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(customerTableModel);
        customerTable.setRowHeight(30);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        customerTable.getTableHeader().setBackground(new Color(100, 149, 237)); // Cornflower Blue
        customerTable.getTableHeader().setForeground(Color.WHITE);
        customerTable.setGridColor(new Color(200, 200, 200));

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Load data from database
        loadCustomerData();
    }

    private void loadCustomerData() {
        // Clear existing rows (if any)
        customerTableModel.setRowCount(0);

        String query = "SELECT id, full_name, username, phone FROM customers";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("full_name");
                String email = rs.getString("username");
                String phone = rs.getString("phone");

                customerTableModel.addRow(new Object[]{id, name, email, phone});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer data: " + e.getMessage());
        }
    }
}

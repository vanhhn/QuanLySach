package com.nhom9.libraryapp.ui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;


import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.service.AuthService; 
import com.nhom9.libraryapp.ui.dialog.EditUserDialog; 

/**
 * Panel quản lý người dùng dành cho Admin.
 */
@SuppressWarnings("serial")
public class UserManagementPanel extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton btnEdit, btnDelete, btnRefresh;
    private JTextField txtSearchUser;
    private AuthService authService; // Sử dụng UserService
    private User currentAdmin; // Nên truyền admin hiện tại vào để kiểm tra quyền

   
    public UserManagementPanel() {
        // this.currentAdmin = currentAdmin;
        this.authService = new AuthService(); 
        setLayout(new BorderLayout(10, 10));
        initComponents();
        addEventListeners();
        loadAllUsers();
    }

	private void initComponents() {
        // --- Khu vực điều khiển ---
        JPanel controlPanel = new JPanel(new BorderLayout(10, 5));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm User (Tên/Email/Username):"));
        txtSearchUser = new JTextField(20);
        searchPanel.add(txtSearchUser);
        JButton btnPerformSearch = new JButton("Tìm");
        btnPerformSearch.addActionListener(e -> searchUsers());
        searchPanel.add(btnPerformSearch);
        controlPanel.add(searchPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEdit = new JButton("Sửa User");
        btnDelete = new JButton("Xóa User");
        btnRefresh = new JButton("Làm mới");
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.NORTH);

        // --- Bảng hiển thị user ---
        String[] columnNames = {"ID", "Họ tên", "Email", "Tên đăng nhập", "Vai trò", "Ngày tạo"};
         tableModel = new DefaultTableModel(columnNames, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
             @Override
             public Class<?> getColumnClass(int columnIndex) {
                 if (columnIndex == 0) return Integer.class; // ID
                 if (columnIndex == 5) return Timestamp.class; // NgayTao
                 return String.class;
             }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setAutoCreateRowSorter(true);

         // Đặt độ rộng cột gợi ý
         userTable.getColumnModel().getColumn(0).setPreferredWidth(40);
         userTable.getColumnModel().getColumn(1).setPreferredWidth(150);
         userTable.getColumnModel().getColumn(2).setPreferredWidth(200);
         userTable.getColumnModel().getColumn(3).setPreferredWidth(120);
         userTable.getColumnModel().getColumn(4).setPreferredWidth(70);
         userTable.getColumnModel().getColumn(5).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
    }

     private void addEventListeners() {
        btnRefresh.addActionListener(e -> loadAllUsers());
        btnEdit.addActionListener(e -> openEditUserDialog());
        btnDelete.addActionListener(e -> deleteSelectedUser());

        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
     }

     private void updateButtonStates() {
          int selectedRow = userTable.getSelectedRow();
          boolean rowSelected = selectedRow != -1;
          btnEdit.setEnabled(rowSelected);
          btnDelete.setEnabled(rowSelected);

          if (rowSelected) {
              int modelRow = userTable.convertRowIndexToModel(selectedRow);
              String role = (String) tableModel.getValueAt(modelRow, 4);
              int userId = (int) tableModel.getValueAt(modelRow, 0);

              // Không cho xóa admin
              if ("admin".equalsIgnoreCase(role)) {
                  btnDelete.setEnabled(false);
              }
              // Không cho sửa admin (nếu không phải là chính mình - tùy logic)
              // if ("admin".equalsIgnoreCase(role) && (currentAdmin == null || userId != currentAdmin.getId())) {
              //    btnEdit.setEnabled(false);
              // }
              // Không cho admin tự xóa mình
              // if (currentAdmin != null && userId == currentAdmin.getId()) {
              //    btnDelete.setEnabled(false);
              // }
          }
     }

    public void loadAllUsers() {
        System.out.println("UserManagementPanel: Loading all users...");
        txtSearchUser.setText("");
        try {
            List<User> users = authService.getAllUsers(); // Gọi qua UserService
            updateTable(users);
        } catch (Exception e) {
             handleDataLoadError("tải danh sách người dùng", e);
        }
    }

     private void searchUsers() {
        String searchTerm = txtSearchUser.getText().trim();
         System.out.println("UserManagementPanel: Admin searching for users: " + searchTerm);
         try {
             List<User> users = authService.searchUsers(searchTerm); // Gọi qua UserService
             updateTable(users);
             if (users.isEmpty() && !searchTerm.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng nào khớp với '" + searchTerm + "'", "Thông báo Tìm Kiếm", JOptionPane.INFORMATION_MESSAGE);
             }
         } catch (Exception e) {
             handleDataLoadError("tìm kiếm người dùng", e);
         }
    }

     private void openEditUserDialog() {
         int selectedRow = userTable.getSelectedRow();
         if (selectedRow == -1 || !btnEdit.isEnabled()) {
             if(selectedRow == -1) JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để sửa.", "Chưa chọn", JOptionPane.INFORMATION_MESSAGE);
             return;
         }

         int modelRow = userTable.convertRowIndexToModel(selectedRow);
         int userId = (int) tableModel.getValueAt(modelRow, 0);
         System.out.println("UserManagementPanel: Opening Edit User Dialog for User ID: " + userId);

         try {
             User userToEdit = authService.getUserById(userId);
             if(userToEdit == null){
                  JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng với ID: " + userId, "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                 return;
             }

          

             EditUserDialog dialog = new EditUserDialog((Frame) SwingUtilities.getWindowAncestor(this), userToEdit, authService);
             dialog.setVisible(true);

             if (dialog.isDataChanged()) {
                  loadAllUsers(); // Tải lại danh sách sau khi sửa
             }
         } catch (Exception e) {
              System.err.println("Lỗi khi lấy thông tin user để sửa (ID: " + userId + "): " + e.getMessage());
              e.printStackTrace();
              JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin người dùng để sửa.\nLỗi: " + e.getMessage(), "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
         }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1 || !btnDelete.isEnabled()) {
             if(selectedRow == -1) JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để xóa.", "Chưa chọn", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        int userId = (int) tableModel.getValueAt(modelRow, 0);
        String userName = (String) tableModel.getValueAt(modelRow, 1);
        

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa người dùng:\n" + userName + " (ID: " + userId + ")?",
                "Xác nhận Xóa Người dùng",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("UserManagementPanel: Attempting to delete user with ID: " + userId);
            try {
                boolean success = authService.deleteUser(userId /*, currentAdmin */); // 
                if (success) {
                     JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                     loadAllUsers();
                 } else {
                     String errorMessage = authService.getLastErrorMessage();
                     JOptionPane.showMessageDialog(this, errorMessage != null ? errorMessage : "Xóa người dùng thất bại!", "Lỗi Xóa", JOptionPane.ERROR_MESSAGE);
                 }
            } catch (SQLException ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi CSDL khi xóa người dùng: " + ex.getMessage() + "\n(Người dùng này có thể đang có phiếu mượn chưa trả.)", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace();
            } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống khi xóa người dùng: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace();
            }
        }
    }

    private void updateTable(List<User> users) {
        tableModel.setRowCount(0);
        if (users != null) {
            for (User user : users) {
                Vector<Object> row = new Vector<>();
                row.add(user.getId());
                row.add(user.getHoTen());
                row.add(user.getEmail());
                row.add(user.getTenDangNhap());
                row.add(user.getVaiTro());
                row.add(user.getNgayTao());
                tableModel.addRow(row);
            }
        }
         if (userTable.getRowCount() > 0) {
            userTable.clearSelection();
         }
         updateButtonStates();
    }

    private void handleDataLoadError(String actionDescription, Exception e) {
         System.err.println("Lỗi khi " + actionDescription + ": " + e.getMessage());
         e.printStackTrace();
         JOptionPane.showMessageDialog(this, "Không thể " + actionDescription + ".\nLỗi: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
         updateTable(Collections.emptyList());
     }
}

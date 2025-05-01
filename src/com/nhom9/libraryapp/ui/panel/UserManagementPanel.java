package com.nhom9.libraryapp.ui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.swing.table.DefaultTableModel;

import com.nhom9.libraryapp.dao.UserDao;
import com.nhom9.libraryapp.model.User;

/**
 * Panel quản lý người dùng dành cho Admin.
 */
@SuppressWarnings("serial")
public class UserManagementPanel extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton btnEdit, btnDelete, btnRefresh;
    private JTextField txtSearchUser;
    private UserDao userDao; // Tạm thời dùng DAO, nên có UserService
    // private UserService userService;

    // Cần biết admin hiện tại là ai để tránh tự xóa/sửa mình (nếu cần)
    // private User currentAdmin;

    public UserManagementPanel(/* User currentAdmin */) {
        // this.currentAdmin = currentAdmin;
        this.userDao = new UserDao(); // Khởi tạo DAO
        // this.userService = new UserService();
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
                 if (columnIndex == 0) return Integer.class;
                 if (columnIndex == 5) return Timestamp.class;
                 return String.class;
             }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setAutoCreateRowSorter(true);

         // Đặt độ rộng cột gợi ý
         userTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
         userTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Họ tên
         userTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
         userTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Tên ĐN
         userTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Vai trò
         userTable.getColumnModel().getColumn(5).setPreferredWidth(130); // Ngày tạo

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

     // Cập nhật trạng thái nút Edit/Delete
     private void updateButtonStates() {
          int selectedRow = userTable.getSelectedRow();
          boolean rowSelected = selectedRow != -1;
          boolean canEdit = rowSelected;
          boolean canDelete = rowSelected;

          if (rowSelected) {
              int modelRow = userTable.convertRowIndexToModel(selectedRow);
              String role = (String) tableModel.getValueAt(modelRow, 4);
              // int userId = (int) tableModel.getValueAt(modelRow, 0);

              // Không cho xóa admin
              if ("admin".equalsIgnoreCase(role)) {
                  canDelete = false;
              }
              // Không cho sửa admin? (Tùy yêu cầu)
              // if ("admin".equalsIgnoreCase(role)) {
              //     canEdit = false;
              // }

              // Tùy chọn: Không cho admin tự xóa/sửa mình
              // if (currentAdmin != null && userId == currentAdmin.getId()) {
              //     canEdit = false;
              //     canDelete = false;
              // }
          }
          btnEdit.setEnabled(canEdit);
          btnDelete.setEnabled(canDelete);
     }

    private void loadAllUsers() {
        System.out.println("Loading all users from database for admin...");
        txtSearchUser.setText("");
        try {
            // Gọi DAO (hoặc Service nếu có)
            List<User> users = userDao.getAllUsers();
            updateTable(users);
        } catch (Exception e) {
             handleDataLoadError("tải danh sách người dùng", e);
        }
    }

     private void searchUsers() {
        String searchTerm = txtSearchUser.getText().trim();
         System.out.println("Admin searching for users: " + searchTerm);
         try {
             // Cần phương thức tìm kiếm user trong DAO/Service
             // List<User> users = userDao.searchUsers(searchTerm); // Ví dụ
             // updateTable(users);
             JOptionPane.showMessageDialog(this, "Chức năng tìm kiếm user chưa được cài đặt.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
             // Tạm thời tải lại tất cả
             loadAllUsers();
         } catch (Exception e) {
             handleDataLoadError("tìm kiếm người dùng", e);
         }
    }

     private void openEditUserDialog() {
         int selectedRow = userTable.getSelectedRow();
         if (selectedRow == -1 || !btnEdit.isEnabled()) return; // Kiểm tra nút có được bật không

         int modelRow = userTable.convertRowIndexToModel(selectedRow);
         int userId = (int) tableModel.getValueAt(modelRow, 0);
         System.out.println("Opening Edit User Dialog for User ID: " + userId);

         try {
             // --- Lấy thông tin User đầy đủ từ DB ---
             User userToEdit = userDao.getUserById(userId); // Hoặc userService.getUserById(userId);
             // --------------------------------------
             if(userToEdit == null){
                  JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng với ID: " + userId, "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                 return;
             }

             // --- Cần một Dialog riêng để sửa User ---
             // Ví dụ: EditUserDialog dialog = new EditUserDialog((Frame) SwingUtilities.getWindowAncestor(this), userToEdit);
             // dialog.setVisible(true);
             // if (dialog.isDataChanged()) {
             //      loadAllUsers();
             // }
             // --------------------------------------
             JOptionPane.showMessageDialog(this, "Chức năng sửa user đang được phát triển (Cần Dialog riêng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

         } catch (Exception e) {
              System.err.println("Lỗi khi lấy thông tin user để sửa (ID: " + userId + "): " + e.getMessage());
              e.printStackTrace();
              JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin người dùng để sửa.\nLỗi: " + e.getMessage(), "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
         }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1 || !btnDelete.isEnabled()) return; // Kiểm tra nút

        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        int userId = (int) tableModel.getValueAt(modelRow, 0);
        String userName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa người dùng:\n" + userName + " (ID: " + userId + ")?\n" +
                "Hành động này sẽ xóa toàn bộ thông tin và lịch sử mượn của người dùng!",
                "Xác nhận Xóa Người dùng",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Attempting to delete user with ID: " + userId);
            try {
                // --- Gọi UserDao (hoặc Service) để xóa user ---
                boolean success = userDao.deleteUser(userId); // deleteUser đã throws SQLException
                // -------------------------------------------
                if (success) {
                     JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                     loadAllUsers();
                 } else {
                     JOptionPane.showMessageDialog(this, "Xóa người dùng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 }
            } catch (SQLException ex) {
                 // Xử lý lỗi SQL (ví dụ: không thể xóa do ràng buộc nếu FK không là CASCADE)
                 JOptionPane.showMessageDialog(this, "Lỗi CSDL khi xóa người dùng: " + ex.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace();
            } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống khi xóa người dùng: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace();
            }
        }
    }

     // Cập nhật bảng user
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
                row.add(user.getNgayTao()); // Timestamp
                tableModel.addRow(row);
            }
        }
         if (userTable.getRowCount() > 0) {
            userTable.clearSelection();
         }
         updateButtonStates(); // Cập nhật lại nút sau khi tải
    }

     // Xử lý lỗi tải dữ liệu chung
    private void handleDataLoadError(String actionDescription, Exception e) {
         System.err.println("Lỗi khi " + actionDescription + ": " + e.getMessage());
         e.printStackTrace();
         JOptionPane.showMessageDialog(this, "Không thể " + actionDescription + ".\nLỗi: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
         updateTable(Collections.emptyList()); // Hiển thị bảng rỗng khi lỗi
     }
}
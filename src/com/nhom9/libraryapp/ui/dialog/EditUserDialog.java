package com.nhom9.libraryapp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.service.AuthService; // Sử dụng UserService

public class EditUserDialog extends JDialog {
    private JTextField txtHoTen;
    private JTextField txtEmail;
    private JTextField txtTenDangNhap; 
    private JComboBox<String> cbVaiTro;
    private JButton btnSave;
    private JButton btnCancel;

    private User userToEdit;
    private AuthService authService;
    private boolean dataChanged = false;

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public EditUserDialog(Frame owner, User userToEdit, AuthService authService) {
        super(owner, "Sửa thông tin người dùng", true);
        this.userToEdit = userToEdit;
        this.authService = authService; 

        initComponents();
        populateFields();
        addEventListeners();

        pack(); // Tự động điều chỉnh kích thước
        setMinimumSize(getSize()); // Đặt kích thước tối thiểu bằng kích thước đã pack
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // ID (Hiển thị, không cho sửa)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID Người dùng:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField txtId = new JTextField(String.valueOf(userToEdit.getId()));
        txtId.setEditable(false);
        txtId.setFocusable(false);
        formPanel.add(txtId, gbc);
        gbc.weightx = 0.0; // Reset

        // Họ tên
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtHoTen = new JTextField(25);
        formPanel.add(txtHoTen, gbc);
        gbc.weightx = 0.0;

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmail = new JTextField();
        formPanel.add(txtEmail, gbc);
        gbc.weightx = 0.0;

        // Tên đăng nhập (Hiển thị, không cho sửa)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtTenDangNhap = new JTextField();
        txtTenDangNhap.setEditable(false);
        txtTenDangNhap.setFocusable(false);
        formPanel.add(txtTenDangNhap, gbc);
        gbc.weightx = 0.0;

        // Vai trò
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Vai trò (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cbVaiTro = new JComboBox<>(new String[]{"user", "admin"});
        formPanel.add(cbVaiTro, gbc);
        gbc.weightx = 0.0;


        // Nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu thay đổi");
        btnCancel = new JButton("Hủy bỏ");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        setLayout(new BorderLayout(10,10));
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields() {
        if (userToEdit != null) {
            txtHoTen.setText(userToEdit.getHoTen() != null ? userToEdit.getHoTen() : "");
            txtEmail.setText(userToEdit.getEmail() != null ? userToEdit.getEmail() : "");
            txtTenDangNhap.setText(userToEdit.getTenDangNhap() != null ? userToEdit.getTenDangNhap() : "");
            cbVaiTro.setSelectedItem(userToEdit.getVaiTro());
        }
    }

    private void addEventListeners() {
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveUserChanges();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void saveUserChanges() {
        String hoTen = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String vaiTro = (String) cbVaiTro.getSelectedItem();

        // Validate Email
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email không được để trống.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            JOptionPane.showMessageDialog(this, "Địa chỉ email không hợp lệ.", "Lỗi Định Dạng Email", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        // Cập nhật thông tin vào đối tượng userToEdit
        // Tên đăng nhập không thay đổi
        userToEdit.setHoTen(hoTen); // Họ tên có thể rỗng
        userToEdit.setEmail(email);
        userToEdit.setVaiTro(vaiTro);
        // Mật khẩu không được sửa ở đây. Admin cần dùng chức năng "Reset mật khẩu" riêng.

        try {
            boolean success = authService.updateUserByAdmin(userToEdit);
            if (success) {
                dataChanged = true;
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                // Service đã kiểm tra và trả về false (ví dụ: email trùng, không thể đổi vai trò admin cuối cùng)
                JOptionPane.showMessageDialog(this, authService.getLastErrorMessage() != null ? authService.getLastErrorMessage() : "Cập nhật thất bại! Vui lòng kiểm tra lại thông tin.", "Lỗi Cập Nhật", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật người dùng:\n" + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isDataChanged() {
        return dataChanged;
    }
}

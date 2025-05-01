package com.nhom9.libraryapp.ui.frame;



import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.service.AuthService;

/**
 * Khung giao diện Đăng ký tài khoản mới.
 */
@SuppressWarnings("serial")
public class RegisterFrame extends JFrame {

    private JTextField txtHoTen;
    private JTextField txtEmail;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JPasswordField txtXacNhanMatKhau;
    private JButton btnRegister;
    private JButton btnBack;
    private JFrame parentFrame; // Để quay lại frame trước đó (LoginFrame)

    public RegisterFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("Đăng ký tài khoản");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Chỉ đóng cửa sổ này, không thoát ứng dụng
        setLocationRelativeTo(parentFrame); // Hiển thị gần cửa sổ cha
        setResizable(false);

        initComponents();
        addEventListeners();

        // Xử lý khi đóng cửa sổ: Hiển thị lại frame cha nếu có
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            }
        });
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Hàng 0: Tiêu đề ---
        JLabel lblTitle = new JLabel("TẠO TÀI KHOẢN MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 15, 10); // Tăng khoảng cách dưới tiêu đề
        mainPanel.add(lblTitle, gbc);
        gbc.insets = new Insets(5, 10, 5, 10); // Reset insets

        // --- Hàng 1: Họ tên ---
        JLabel lblHoTen = new JLabel("Họ và tên:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        mainPanel.add(lblHoTen, gbc);

        txtHoTen = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        mainPanel.add(txtHoTen, gbc);

        // --- Hàng 2: Email ---
        JLabel lblEmail = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(lblEmail, gbc);

        txtEmail = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(txtEmail, gbc);

        // --- Hàng 3: Tên đăng nhập ---
        JLabel lblTenDangNhap = new JLabel("Tên đăng nhập:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(lblTenDangNhap, gbc);

        txtTenDangNhap = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(txtTenDangNhap, gbc);

        // --- Hàng 4: Mật khẩu ---
        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(lblMatKhau, gbc);

        txtMatKhau = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(txtMatKhau, gbc);

        // --- Hàng 5: Xác nhận mật khẩu ---
        JLabel lblXacNhanMatKhau = new JLabel("Xác nhận mật khẩu:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(lblXacNhanMatKhau, gbc);

        txtXacNhanMatKhau = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 5;
        mainPanel.add(txtXacNhanMatKhau, gbc);


        // --- Hàng 6: Các nút ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnRegister = new JButton("Đăng ký");
        btnBack = new JButton("Quay lại");
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBack);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 10, 10, 10); // Tăng khoảng cách trên panel nút
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

     private void addEventListeners() {
        // Xử lý sự kiện khi nhấn nút Đăng ký
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hoTen = txtHoTen.getText();
                String email = txtEmail.getText();
                String tenDangNhap = txtTenDangNhap.getText();
                String matKhau = new String(txtMatKhau.getPassword());
                String xacNhanMatKhau = new String(txtXacNhanMatKhau.getPassword());

                // --- Thực hiện kiểm tra dữ liệu đầu vào ---
                if (hoTen.isEmpty() || email.isEmpty() || tenDangNhap.isEmpty() || matKhau.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!matKhau.equals(xacNhanMatKhau)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Mật khẩu và xác nhận mật khẩu không khớp!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Kiểm tra định dạng email (đơn giản)
                 if (!email.contains("@") || !email.contains(".")) {
                     JOptionPane.showMessageDialog(RegisterFrame.this, "Địa chỉ email không hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                     return;
                 }

                // --- Gọi lớp Service/DAO để thực hiện đăng ký ---
                System.out.println("Attempting registration: " + hoTen + ", " + email + ", " + tenDangNhap);
                // Ví dụ:
                 AuthService authService = new AuthService();
                 User newUser = new User(hoTen, email, tenDangNhap, matKhau, "user"); // Mật khẩu sẽ được hash trong service
                 boolean success = authService.register(newUser);
                 if (success) {
                     JOptionPane.showMessageDialog(RegisterFrame.this, "Đăng ký thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                     dispose(); // Đóng cửa sổ đăng ký
                     if (parentFrame != null) {
                         parentFrame.setVisible(true); // Hiển thị lại cửa sổ đăng nhập
                     }
                 } else {
                     JOptionPane.showMessageDialog(RegisterFrame.this, "Đăng ký thất bại! Tên đăng nhập hoặc email có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 }
                // ----------------------------------------------------

                 // Placeholder:
                 //JOptionPane.showMessageDialog(RegisterFrame.this, "Chức năng Đăng ký đang được phát triển.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            }
        });

        // Xử lý sự kiện khi nhấn nút Quay lại
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Đóng cửa sổ hiện tại
                if (parentFrame != null) {
                    parentFrame.setVisible(true); // Hiển thị lại cửa sổ cha (LoginFrame)
                }
            }
        });
    }

     // // Phương thức main để chạy thử nghiệm Frame này
     // public static void main(String[] args) {
     //     SwingUtilities.invokeLater(new Runnable() {
     //         public void run() {
     //             // Cần một JFrame giả làm parent để test hoặc truyền null
     //              JFrame dummyParent = new JFrame();
     //              dummyParent.setVisible(false);
     //              new RegisterFrame(dummyParent).setVisible(true);
     //         }
     //     });
     // }
}
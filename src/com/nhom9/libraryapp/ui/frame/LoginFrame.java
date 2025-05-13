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
import javax.swing.SwingUtilities;

import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.service.AuthService;
import com.nhom9.libraryapp.util.UIUtil;

/**
 * Khung giao diện Đăng nhập.
 */
@SuppressWarnings("serial")
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginFrame() {
        setTitle("Đăng nhập Hệ thống Thư viện");
        setSize(400, 250); // Tăng kích thước một chút
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát ứng dụng khi đóng cửa sổ này
        setLocationRelativeTo(null); // Hiển thị cửa sổ ở giữa màn hình
        setResizable(false); // Không cho phép thay đổi kích thước
        UIUtil.setFrameIcon(this, "/icons/book.png", LoginFrame.class);
        initComponents();
        addEventListeners(); // Tách riêng phần xử lý sự kiện
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Hàng 1: Tiêu đề ---
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Kéo dài 2 cột
        gbc.weightx = 1.0;
        mainPanel.add(lblTitle, gbc);

        // --- Hàng 2: Tên đăng nhập ---
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0; // Không kéo dãn label
        mainPanel.add(lblUsername, gbc);

        txtUsername = new JTextField(20); // Độ rộng gợi ý
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0; // Kéo dãn text field
        mainPanel.add(txtUsername, gbc);

        // --- Hàng 3: Mật khẩu ---
        JLabel lblPassword = new JLabel("Mật khẩu:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        mainPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        mainPanel.add(txtPassword, gbc);

        // --- Hàng 4: Các nút ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0)); // Panel chứa nút
        btnLogin = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký");
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; // Căn giữa panel nút
        gbc.fill = GridBagConstraints.NONE; // Không kéo dãn panel nút
        gbc.weightx = 1.0;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel); // Thêm mainPanel vào JFrame
    }

    private void addEventListeners() {
        // Xử lý sự kiện khi nhấn nút Đăng nhập
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                // --- Gọi lớp Service/DAO để kiểm tra đăng nhập ---
                System.out.println("Attempting login with Username: " + username + ", Password: " + password);
                AuthService authService = new AuthService();
                User loggedInUser = authService.login(username, password);
                if (loggedInUser != null) {
                    dispose(); // Đóng cửa sổ đăng nhập
                    if ("admin".equals(loggedInUser.getVaiTro())) {
                        new MainAdminFrame(loggedInUser).setVisible(true);
                    } else {
                        new MainUserFrame(loggedInUser).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Tên đăng nhập hoặc mật khẩu không đúng!",
                            "Lỗi Đăng nhập", JOptionPane.ERROR_MESSAGE);
                }
                // ----------------------------------------------------

                // Placeholder: Chỉ hiển thị thông báo tạm thời
                // JOptionPane.showMessageDialog(LoginFrame.this, "Chức năng Đăng nhập đang được
                // phát triển.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            }
        });

        // Xử lý sự kiện khi nhấn nút Đăng ký
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Register button clicked");
                // Mở cửa sổ đăng ký
                RegisterFrame registerFrame = new RegisterFrame(LoginFrame.this); // Truyền frame hiện tại để có thể
                                                                                  // quay lại
                registerFrame.setVisible(true);
                // Có thể ẩn cửa sổ đăng nhập khi mở đăng ký:
                // setVisible(false);
            }
        });
    }

    // Phương thức main để chạy thử nghiệm Frame này (có thể xóa hoặc comment lại
    // sau)
    public static void main(String[] args) {
        // Đảm bảo rằng việc tạo và hiển thị GUI được thực hiện trên Event Dispatch
        // Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}

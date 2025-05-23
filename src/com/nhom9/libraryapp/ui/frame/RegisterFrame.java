package com.nhom9.libraryapp.ui.frame;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image; // Import Image
import java.awt.Insets;
import java.awt.Toolkit; // Import Toolkit
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; // Import MouseAdapter
import java.awt.event.MouseEvent; // Import MouseEvent
import java.net.URL; // Import URL
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Cursor;
import javax.swing.BorderFactory; // Import BorderFactory
import javax.swing.ImageIcon; // Import ImageIcon
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
import com.nhom9.libraryapp.util.UIUtil;

@SuppressWarnings("serial")
public class RegisterFrame extends JFrame {

    private JTextField txtHoTen;
    private JTextField txtEmail;
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JPasswordField txtXacNhanMatKhau;
    private JButton btnRegister;
    private JButton btnBack;
    private JLabel lblTogglePasswordVisibility; // Label cho icon hiển thị/ẩn mật khẩu
    private JLabel lblToggleConfirmPasswordVisibility; // Label cho icon xác nhận mật khẩu

    private JFrame parentFrame;
    private AuthService authService;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private ImageIcon eyeOpenIcon;
    private ImageIcon eyeClosedIcon;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public RegisterFrame(JFrame parent) {
        this.parentFrame = parent;
        this.authService = new AuthService();

        loadIcons(); // Tải icon trước khi initComponents

        setTitle("Đăng ký tài khoản");
        setSize(550, 450); // Tăng kích thước để có chỗ cho icon
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);
        UIUtil.setFrameIcon(this, "/icons/book.png", RegisterFrame.class);
        setResizable(false);

        initComponents();
        addEventListeners();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            }
        });
    }

    private void loadIcons() {
        try {
            URL openURL = getClass().getResource("/icons/view.png"); 
            URL closedURL = getClass().getResource("/icons/hide.png");  

            if (openURL != null && closedURL != null) {
                eyeOpenIcon = new ImageIcon(
                        Toolkit.getDefaultToolkit().getImage(openURL).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
                eyeClosedIcon = new ImageIcon(
                        Toolkit.getDefaultToolkit().getImage(closedURL).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
            } else {
                System.err.println("Không tìm thấy file icon hiển thị/ẩn mật khẩu.");
                eyeOpenIcon = new ImageIcon();
                eyeClosedIcon = new ImageIcon();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải icon hiển thị/ẩn mật khẩu: " + e.getMessage());
            e.printStackTrace();
            eyeOpenIcon = new ImageIcon();
            eyeClosedIcon = new ImageIcon();
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("TẠO TÀI KHOẢN MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; 
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 15, 10);
        mainPanel.add(lblTitle, gbc);
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridwidth = 1; // Reset gridwidth

        // Họ tên
        JLabel lblHoTen = new JLabel("Họ và tên:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(lblHoTen, gbc);
        txtHoTen = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; // Kéo dài qua 2 cột
        mainPanel.add(txtHoTen, gbc);
        gbc.gridwidth = 1; // Reset

        // Email
        JLabel lblEmail = new JLabel("Email (*):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(lblEmail, gbc);
        txtEmail = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(txtEmail, gbc);
        gbc.gridwidth = 1;

        // Tên đăng nhập
        JLabel lblTenDangNhap = new JLabel("Tên đăng nhập (*):");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(lblTenDangNhap, gbc);
        txtTenDangNhap = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(txtTenDangNhap, gbc);
        gbc.gridwidth = 1;

        // Mật khẩu
        JLabel lblMatKhau = new JLabel("Mật khẩu (*):");
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(lblMatKhau, gbc);
        txtMatKhau = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0; // Cho JPasswordField chiếm không gian
        mainPanel.add(txtMatKhau, gbc);

        lblTogglePasswordVisibility = new JLabel(eyeClosedIcon); // Icon ban đầu là mắt đóng
        lblTogglePasswordVisibility.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblTogglePasswordVisibility.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // Khoảng đệm nhỏ
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE; // Không cho icon co giãn
        gbc.anchor = GridBagConstraints.WEST; // Căn icon về bên trái ô của nó
        mainPanel.add(lblTogglePasswordVisibility, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill cho các component khác

        // Xác nhận mật khẩu
        JLabel lblXacNhanMatKhau = new JLabel("Xác nhận mật khẩu (*):");
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(lblXacNhanMatKhau, gbc);
        txtXacNhanMatKhau = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        mainPanel.add(txtXacNhanMatKhau, gbc);

        lblToggleConfirmPasswordVisibility = new JLabel(eyeClosedIcon);
        lblToggleConfirmPasswordVisibility.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblToggleConfirmPasswordVisibility.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(lblToggleConfirmPasswordVisibility, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnRegister = new JButton("Đăng ký");
        btnBack = new JButton("Quay lại");
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBack);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3; // Kéo dài qua 3 cột
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void addEventListeners() {
        // Sự kiện cho icon hiển thị/ẩn mật khẩu
        lblTogglePasswordVisibility.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    txtMatKhau.setEchoChar((char) 0); // Hiện mật khẩu
                    lblTogglePasswordVisibility.setIcon(eyeOpenIcon);
                } else {
                    txtMatKhau.setEchoChar('•'); // Ẩn mật khẩu (ký tự mặc định là dấu chấm tròn)
                    lblTogglePasswordVisibility.setIcon(eyeClosedIcon);
                }
            }
        });

        // Sự kiện cho icon hiển thị/ẩn xác nhận mật khẩu
        lblToggleConfirmPasswordVisibility.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                if (isConfirmPasswordVisible) {
                    txtXacNhanMatKhau.setEchoChar((char) 0);
                    lblToggleConfirmPasswordVisibility.setIcon(eyeOpenIcon);
                } else {
                    txtXacNhanMatKhau.setEchoChar('•');
                    lblToggleConfirmPasswordVisibility.setIcon(eyeClosedIcon);
                }
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hoTen = txtHoTen.getText().trim();
                String email = txtEmail.getText().trim();
                String tenDangNhap = txtTenDangNhap.getText().trim();
                String matKhau = new String(txtMatKhau.getPassword());
                String xacNhanMatKhau = new String(txtXacNhanMatKhau.getPassword());

                if (hoTen.isEmpty() || email.isEmpty() || tenDangNhap.isEmpty() || matKhau.isEmpty()
                        || xacNhanMatKhau.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Vui lòng nhập đầy đủ các trường bắt buộc (*).",
                            "Lỗi Thiếu Thông Tin", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Địa chỉ email không hợp lệ.\nVui lòng nhập email đúng định dạng (ví dụ: example@domain.com).",
                            "Lỗi Định Dạng Email", JOptionPane.WARNING_MESSAGE);
                    txtEmail.requestFocus();
                    return;
                }

                String passwordValidationError = getPasswordValidationError(matKhau);
                if (passwordValidationError != null) {
                    StringBuilder errorMessage = new StringBuilder("Mật khẩu không hợp lệ:\n");
                    errorMessage.append(passwordValidationError); // Chỉ thêm thông báo lỗi cụ thể
                    errorMessage.append("\n\nYêu cầu mật khẩu:\n")
                            .append("- Độ dài từ 8 đến 20 ký tự.\n")
                            .append("- Ít nhất một chữ số (0-9).\n")
                            .append("- Ít nhất một chữ cái thường (a-z).\n")
                            .append("- Ít nhất một chữ cái hoa (A-Z).\n")
                            .append("- Ít nhất một ký tự đặc biệt (ví dụ: !@#$%^&*).");
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            errorMessage.toString(),
                            "Lỗi Định Dạng Mật Khẩu", JOptionPane.WARNING_MESSAGE);
                    txtMatKhau.requestFocus();
                    return;
                }

                if (!matKhau.equals(xacNhanMatKhau)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Mật khẩu và xác nhận mật khẩu không khớp!",
                            "Lỗi Xác Nhận Mật Khẩu", JOptionPane.WARNING_MESSAGE);
                    txtXacNhanMatKhau.requestFocus();
                    return;
                }

                User newUser = new User();
                newUser.setHoTen(hoTen);
                newUser.setEmail(email);
                newUser.setTenDangNhap(tenDangNhap);
                newUser.setMatKhau(matKhau);
                newUser.setVaiTro("user");

                try {
                    boolean success = authService.register(newUser);
                    if (success) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Đăng ký tài khoản thành công!", "Thành Công",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        if (parentFrame != null) {
                            parentFrame.setVisible(true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                "Đăng ký thất bại!\nCó thể tên đăng nhập hoặc email đã được sử dụng,\nhoặc dữ liệu không hợp lệ (kiểm tra log service).",
                                "Lỗi Đăng Ký", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Đã xảy ra lỗi trong quá trình đăng ký:\n" + ex.getMessage(), "Lỗi Hệ Thống",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private String getPasswordValidationError(String password) {
        if (password == null || password.isEmpty()) { // Kiểm tra mật khẩu rỗng ở đây luôn
            return "Mật khẩu không được để trống.";
        }
        if (password.length() < 8) {
            return "- Phải có ít nhất 8 ký tự.";
        }
        if (password.length() > 20) {
            return "- Không được vượt quá 20 ký tự.";
        }
        if (!Pattern.compile(".*[0-9].*").matcher(password).matches()) {
            return "- Phải chứa ít nhất một chữ số.";
        }
        if (!Pattern.compile(".*[a-z].*").matcher(password).matches()) {
            return "- Phải chứa ít nhất một chữ cái thường.";
        }
        if (!Pattern.compile(".*[A-Z].*").matcher(password).matches()) {
            return "- Phải chứa ít nhất một chữ cái hoa.";
        }
        // Đảm bảo ký tự đặc biệt được escape đúng cách trong regex
        if (!Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~].*").matcher(password).matches()) {
            return "- Phải chứa ít nhất một ký tự đặc biệt.";
        }
        return null;
    }
}
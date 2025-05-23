package com.nhom9.libraryapp.ui.frame;

import java.awt.Cursor; 
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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

@SuppressWarnings("serial")
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblTogglePasswordVisibility; // Icon cho mật khẩu

    private boolean isPasswordVisible = false;
    private ImageIcon eyeOpenIcon;
    private ImageIcon eyeClosedIcon;

    public LoginFrame() {
        loadIcons(); // Tải icon trước

        setTitle("Đăng nhập Hệ thống Thư viện");
        setSize(450, 280); // Tăng kích thước một chút để có chỗ cho icon
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        UIUtil.setFrameIcon(this, "/icons/book.png", LoginFrame.class);

        initComponents();
        addEventListeners();
    }

    private void loadIcons() {
        try {
            URL openURL = getClass().getResource("/icons/view.png");
            URL closedURL = getClass().getResource("/icons/hide.png");

            if (openURL != null && closedURL != null) {
                eyeOpenIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(openURL).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
                eyeClosedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(closedURL).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
            } else {
                System.err.println("Không tìm thấy file icon hiển thị/ẩn mật khẩu cho LoginFrame.");
                eyeOpenIcon = new ImageIcon();
                eyeClosedIcon = new ImageIcon();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải icon hiển thị/ẩn mật khẩu cho LoginFrame: " + e.getMessage());
            e.printStackTrace();
            eyeOpenIcon = new ImageIcon();
            eyeClosedIcon = new ImageIcon();
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; // Tăng gridwidth
        gbc.weightx = 1.0;
        mainPanel.add(lblTitle, gbc);
        gbc.gridwidth = 1; // Reset

        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        mainPanel.add(lblUsername, gbc);
        txtUsername = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; // Kéo dài qua 2 cột
        mainPanel.add(txtUsername, gbc);
        gbc.gridwidth = 1; // Reset

        JLabel lblPassword = new JLabel("Mật khẩu:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        mainPanel.add(lblPassword, gbc);
        txtPassword = new JPasswordField();
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; // Cho JPasswordField chiếm không gian
        mainPanel.add(txtPassword, gbc);

        lblTogglePasswordVisibility = new JLabel(eyeClosedIcon);
        lblTogglePasswordVisibility.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblTogglePasswordVisibility.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(lblTogglePasswordVisibility, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnLogin = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký");
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3; // Kéo dài qua 3 cột
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void addEventListeners() {
        lblTogglePasswordVisibility.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    txtPassword.setEchoChar((char) 0);
                    lblTogglePasswordVisibility.setIcon(eyeOpenIcon);
                } else {
                    txtPassword.setEchoChar('•');
                    lblTogglePasswordVisibility.setIcon(eyeClosedIcon);
                }
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim(); // trim() để loại bỏ khoảng trắng thừa
                String password = new String(txtPassword.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Vui lòng nhập tên đăng nhập và mật khẩu.",
                            "Thông tin còn thiếu", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                AuthService authService = new AuthService();
                User loggedInUser = authService.login(username, password);
                if (loggedInUser != null) {
                    dispose();
                    if ("admin".equals(loggedInUser.getVaiTro())) {
                        new MainAdminFrame(loggedInUser).setVisible(true);
                    } else {
                        new MainUserFrame(loggedInUser).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Tên đăng nhập hoặc mật khẩu không đúng!",
                            "Lỗi Đăng nhập", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame registerFrame = new RegisterFrame(LoginFrame.this);
                registerFrame.setVisible(true);
                
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
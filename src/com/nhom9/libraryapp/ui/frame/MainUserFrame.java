package com.nhom9.libraryapp.ui.frame;



import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.nhom9.libraryapp.model.User;
// Import các panel
import com.nhom9.libraryapp.ui.panel.BookSearchPanel;
import com.nhom9.libraryapp.ui.panel.BorrowHistoryPanel;
import com.nhom9.libraryapp.ui.panel.BorrowedBooksPanel;

/**
 * Khung giao diện chính cho người dùng thông thường.
 */
@SuppressWarnings("serial")
public class MainUserFrame extends JFrame {

    private User currentUser; // Lưu thông tin người dùng đang đăng nhập
    private JTabbedPane tabbedPane;
    private JButton btnLogout;

    // Constructor nhận thông tin User đã đăng nhập
    public MainUserFrame(User user) {
        this.currentUser = user;

        setTitle("Hệ thống Quản lý Thư viện - Xin chào, " + currentUser.getHoTen());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Giữa màn hình

        initComponents();
        addEventListeners();
    }

    private void initComponents() {
        // Panel chính chứa TabbedPane và nút Logout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Tabbed Pane để chứa các chức năng
        tabbedPane = new JTabbedPane();

        // Tạo các Panel chức năng (lấy từ package panel)
        // !!! Lưu ý: Các Panel này cần được tạo đầy đủ trong package .ui.panel
        // Hiện tại chỉ là placeholder
        JPanel searchBorrowPanel = new BookSearchPanel(currentUser); // Ví dụ panel tìm kiếm
        JPanel borrowedPanel = new BorrowedBooksPanel(currentUser); // Ví dụ panel sách đang mượn
        JPanel historyPanel = new BorrowHistoryPanel(currentUser);   // Ví dụ panel lịch sử

        // Thêm các tab
        tabbedPane.addTab("Tìm & Mượn Sách", null, searchBorrowPanel, "Tìm kiếm và mượn sách mới");
        tabbedPane.addTab("Sách Đang Mượn", null, borrowedPanel, "Xem và trả sách đang mượn");
        tabbedPane.addTab("Lịch Sử Mượn", null, historyPanel, "Xem lịch sử mượn trả sách");

        mainPanel.add(tabbedPane, BorderLayout.CENTER); // Thêm TabbedPane vào giữa

        // Panel dưới cùng chứa nút Logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLogout = new JButton("Đăng xuất");
        bottomPanel.add(btnLogout);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH); // Thêm panel nút vào dưới cùng

        add(mainPanel);
    }

     private void addEventListeners() {
         btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        MainUserFrame.this,
                        "Bạn có chắc chắn muốn đăng xuất?",
                        "Xác nhận Đăng xuất",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    dispose(); // Đóng cửa sổ hiện tại
                    // Mở lại cửa sổ đăng nhập
                    SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
                }
            }
        });
     }

    // // Phương thức main để chạy thử nghiệm Frame này
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(new Runnable() {
    //         public void run() {
    //              // Tạo một User giả để test
    //              User testUser = new User();
    //              testUser.setId(1);
    //              testUser.setHoTen("Người Dùng Test");
    //              testUser.setVaiTro("user");
    //
    //              new MainUserFrame(testUser).setVisible(true);
    //         }
    //     });
    // }
}

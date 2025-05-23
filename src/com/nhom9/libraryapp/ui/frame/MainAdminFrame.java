package com.nhom9.libraryapp.ui.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.ui.panel.BookManagementPanel;
import com.nhom9.libraryapp.ui.panel.BookSearchPanel;
import com.nhom9.libraryapp.ui.panel.BorrowHistoryPanel;
import com.nhom9.libraryapp.ui.panel.BorrowedBooksPanel;
import com.nhom9.libraryapp.ui.panel.StatisticsPanel;
import com.nhom9.libraryapp.ui.panel.UserManagementPanel;
import com.nhom9.libraryapp.util.UIUtil;

@SuppressWarnings("serial")
public class MainAdminFrame extends JFrame {

    private User currentAdmin;
    private JTabbedPane tabbedPane;
    private JButton btnLogout;

    private BookManagementPanel bookManagementPanel;
    private UserManagementPanel userManagementPanel;
    private BookSearchPanel searchBorrowPanel;
    private BorrowedBooksPanel borrowedPanel;
    private BorrowHistoryPanel historyPanel;
    private StatisticsPanel statisticsPanel;

    public MainAdminFrame(User adminUser) {
        if (!"admin".equalsIgnoreCase(adminUser.getVaiTro())) {
            throw new IllegalArgumentException("User is not an admin.");
        }
        this.currentAdmin = adminUser;

        setTitle("Hệ thống Quản lý Thư viện [ADMIN] - Xin chào, " + currentAdmin.getHoTen());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.setFrameIcon(this, "/icons/book.png", MainAdminFrame.class); // Sử dụng MainAdminFrame.class

        initComponents();
        addEventListeners();
        addTabChangeListener();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // Khởi tạo các Panel
        bookManagementPanel = new BookManagementPanel(); // Admin không cần truyền User vào đây
        userManagementPanel = new UserManagementPanel(); // Admin không cần truyền User (trừ khi UserManagementPanel cần biết currentAdmin)
        searchBorrowPanel = new BookSearchPanel(currentAdmin); // Admin mượn sách cho chính mình
        borrowedPanel = new BorrowedBooksPanel(currentAdmin);   // Admin xem sách mình đang mượn
        historyPanel = new BorrowHistoryPanel(currentAdmin);    // Admin xem lịch sử mượn của mình
        statisticsPanel = new StatisticsPanel();              // Panel thống kê

        // Thêm các tab
        tabbedPane.addTab("Quản Lý Sách", null, bookManagementPanel, "Thêm, sửa, xóa sách");
        tabbedPane.addTab("Quản Lý Người Dùng", null, userManagementPanel, "Quản lý tài khoản người dùng");
        tabbedPane.addTab("Tìm & Mượn Sách", null, searchBorrowPanel, "Tìm kiếm và mượn sách");
        tabbedPane.addTab("Sách Đang Mượn", null, borrowedPanel, "Xem và trả sách đang mượn");
        tabbedPane.addTab("Lịch Sử Mượn", null, historyPanel, "Xem lịch sử mượn trả sách");
        tabbedPane.addTab("Thống Kê", null, statisticsPanel, "Xem thống kê thư viện");

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLogout = new JButton("Đăng xuất");
        bottomPanel.add(btnLogout);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void addEventListeners() {
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainAdminFrame.this, "Bạn có chắc chắn muốn đăng xuất?",
                        "Xác nhận Đăng xuất", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
                }
            }
        });
    }

    private void addTabChangeListener() {
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Component selectedComponent = tabbedPane.getSelectedComponent();

                if (selectedComponent == borrowedPanel && borrowedPanel != null) {
                    System.out.println("Admin Tab: Sách Đang Mượn selected, reloading data...");
                    borrowedPanel.loadBorrowedBooks();
                } else if (selectedComponent == historyPanel && historyPanel != null) {
                    System.out.println("Admin Tab: Lịch Sử Mượn selected, reloading data...");
                    historyPanel.loadHistory();
                } else if (selectedComponent == searchBorrowPanel && searchBorrowPanel != null) {
                    System.out.println("Admin Tab: Tìm & Mượn Sách selected, reloading data...");
                    searchBorrowPanel.refreshBookListView(); // Gọi phương thức public
                } else if (selectedComponent == bookManagementPanel && bookManagementPanel != null) {
                    System.out.println("Admin Tab: Quản Lý Sách selected, reloading data...");
                    bookManagementPanel.loadAllBooks(); // Gọi phương thức public
                } else if (selectedComponent == userManagementPanel && userManagementPanel != null) {
                    System.out.println("Admin Tab: Quản Lý Người Dùng selected, reloading data...");
                    userManagementPanel.loadAllUsers(); // Đảm bảo phương thức này public
                } else if (selectedComponent == statisticsPanel && statisticsPanel != null) {
                    System.out.println("Admin Tab: Thống Kê selected, reloading data...");
                    
                }
            }
        });
    }

    public void refreshBookSearchPanelData() {
        if (searchBorrowPanel != null) {
            System.out.println("MainAdminFrame: Requesting refresh for BookSearchPanel...");
            searchBorrowPanel.refreshBookListView();
        }
    }

    public void refreshBookManagementPanelData() {
        if (bookManagementPanel != null) {
            System.out.println("MainAdminFrame: Requesting refresh for BookManagementPanel...");
            bookManagementPanel.loadAllBooks();
        }
    }

    public void refreshBorrowedBooksPanelData() {
        if (borrowedPanel != null) {
             System.out.println("MainAdminFrame: Requesting refresh for BorrowedBooksPanel...");
            borrowedPanel.loadBorrowedBooks();
        }
    }

     public void refreshBorrowHistoryPanelData() {
        if (historyPanel != null) {
             System.out.println("MainAdminFrame: Requesting refresh for BorrowHistoryPanel...");
            historyPanel.loadHistory();
        }
    }
}
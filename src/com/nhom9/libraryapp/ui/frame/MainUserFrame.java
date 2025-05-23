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
import com.nhom9.libraryapp.ui.panel.BookSearchPanel;
import com.nhom9.libraryapp.ui.panel.BorrowHistoryPanel;
import com.nhom9.libraryapp.ui.panel.BorrowedBooksPanel;
import com.nhom9.libraryapp.util.UIUtil;

@SuppressWarnings("serial")
public class MainUserFrame extends JFrame {

    private User currentUser;
    private JTabbedPane tabbedPane;
    private JButton btnLogout;

    private BookSearchPanel searchBorrowPanel;
    private BorrowedBooksPanel borrowedPanel;
    private BorrowHistoryPanel historyPanel;

    public MainUserFrame(User user) {
        this.currentUser = user;

        setTitle("Hệ thống Quản lý Thư viện - Xin chào, " + currentUser.getHoTen());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        UIUtil.setFrameIcon(this, "/icons/book.png", MainUserFrame.class); 

        initComponents();
        addEventListeners();
        addTabChangeListener();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        searchBorrowPanel = new BookSearchPanel(currentUser);
        borrowedPanel = new BorrowedBooksPanel(currentUser);
        historyPanel = new BorrowHistoryPanel(currentUser);

        tabbedPane.addTab("Tìm & Mượn Sách", null, searchBorrowPanel, "Tìm kiếm và mượn sách mới");
        tabbedPane.addTab("Sách Đang Mượn", null, borrowedPanel, "Xem và trả sách đang mượn");
        tabbedPane.addTab("Lịch Sử Mượn", null, historyPanel, "Xem lịch sử mượn trả sách");

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
                int confirm = JOptionPane.showConfirmDialog(
                        MainUserFrame.this,
                        "Bạn có chắc chắn muốn đăng xuất?",
                        "Xác nhận Đăng xuất",
                        JOptionPane.YES_NO_OPTION);
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
                    System.out.println("User Tab: Sách Đang Mượn selected, reloading data...");
                    borrowedPanel.loadBorrowedBooks();
                } else if (selectedComponent == historyPanel && historyPanel != null) {
                    System.out.println("User Tab: Lịch Sử Mượn selected, reloading data...");
                    historyPanel.loadHistory();
                } else if (selectedComponent == searchBorrowPanel && searchBorrowPanel != null) {
                    System.out.println("User Tab: Tìm & Mượn Sách selected, reloading data...");
                    searchBorrowPanel.refreshBookListView(); 
                }
            }
        });
    }

    public void refreshBookSearchPanelData() {
        if (searchBorrowPanel != null) {
            System.out.println("MainUserFrame: Requesting refresh for BookSearchPanel...");
            searchBorrowPanel.refreshBookListView();
        }
    }

    public void refreshBorrowedBooksPanelData() {
        if (borrowedPanel != null) {
            System.out.println("MainUserFrame: Requesting refresh for BorrowedBooksPanel...");
            borrowedPanel.loadBorrowedBooks();
        }
    }

    public void refreshBorrowHistoryPanelData() {
        if (historyPanel != null) {
            System.out.println("MainUserFrame: Requesting refresh for BorrowHistoryPanel...");
            historyPanel.loadHistory();
        }
    }
}
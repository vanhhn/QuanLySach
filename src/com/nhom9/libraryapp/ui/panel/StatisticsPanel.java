package com.nhom9.libraryapp.ui.panel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.nhom9.libraryapp.service.ReportService;

@SuppressWarnings("serial")
public class StatisticsPanel extends JPanel {

    private JLabel lblTotalBookTitles, lblTotalBookCopies, lblAvailableBooks, lblBorrowedBooks;
    private JLabel lblTotalUsers, lblAdminUsers, lblRegularUsers;
    private ReportService reportService;
    private JButton btnRefresh; // Khai báo nút refresh

    public StatisticsPanel() {
        this.reportService = new ReportService();
        setLayout(new GridBagLayout()); // Sử dụng GridBagLayout cho toàn bộ panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; // Tất cả component trong cột 0

        initComponents(gbc); // Truyền gbc vào
        loadStatistics(); // Tải dữ liệu ban đầu
        // addEventListeners(); // Không cần nếu chỉ có nút refresh đã có listener
    }

    private void initComponents(GridBagConstraints gbc) {
        Font titleFont = new Font("Arial", Font.BOLD, 18);
        Font dataFont = new Font("Arial", Font.PLAIN, 16);
        Dimension labelSize = new Dimension(350, 30);

        // --- Thống kê Sách ---
        JLabel lblBookStatsTitle = new JLabel("Thống kê Sách");
        lblBookStatsTitle.setFont(titleFont);
        gbc.gridy = 0; gbc.gridwidth = 2; // Span 2 cột cho tiêu đề
        gbc.anchor = GridBagConstraints.CENTER; // Căn giữa tiêu đề
        add(lblBookStatsTitle, gbc);
        gbc.gridwidth = 1; // Reset span
        gbc.anchor = GridBagConstraints.WEST; // Reset anchor

        lblTotalBookTitles = createDataLabel("Tổng số đầu sách:", dataFont, labelSize);
        gbc.gridy = 1; add(lblTotalBookTitles, gbc);

        lblTotalBookCopies = createDataLabel("Tổng số bản sách:", dataFont, labelSize);
        gbc.gridy = 2; add(lblTotalBookCopies, gbc);

        lblAvailableBooks = createDataLabel("Tổng sách còn trong kho:", dataFont, labelSize);
        gbc.gridy = 3; add(lblAvailableBooks, gbc);

        lblBorrowedBooks = createDataLabel("Tổng sách đang được mượn:", dataFont, labelSize);
        gbc.gridy = 4; add(lblBorrowedBooks, gbc);

        // --- Thống kê Người dùng ---
        JSeparator separator = new JSeparator();
        gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 20, 0);
        add(separator, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 15, 10, 15);

        JLabel lblUserStatsTitle = new JLabel("Thống kê Người dùng");
        lblUserStatsTitle.setFont(titleFont);
        gbc.gridy = 6; gbc.gridwidth = 2; // Span 2 cột
        gbc.anchor = GridBagConstraints.CENTER; // Căn giữa tiêu đề
        add(lblUserStatsTitle, gbc);
        gbc.gridwidth = 1; // Reset span
        gbc.anchor = GridBagConstraints.WEST; // Reset anchor

        lblTotalUsers = createDataLabel("Tổng số người dùng:", dataFont, labelSize);
        gbc.gridy = 7; add(lblTotalUsers, gbc);

        lblAdminUsers = createDataLabel("Số lượng Quản trị viên:", dataFont, labelSize);
        gbc.gridy = 8; add(lblAdminUsers, gbc);

        lblRegularUsers = createDataLabel("Số lượng người dùng thường:", dataFont, labelSize);
        gbc.gridy = 9; add(lblRegularUsers, gbc);

        // Nút làm mới
        btnRefresh = new JButton("Làm mới thống kê");
        btnRefresh.addActionListener(e -> loadStatistics());
        gbc.gridy = 10; gbc.gridwidth = 2; // Span 2 cột
        gbc.anchor = GridBagConstraints.CENTER; // Căn giữa nút
        gbc.fill = GridBagConstraints.NONE; // Không cho nút co giãn
        gbc.insets = new Insets(25, 15, 10, 15);
        add(btnRefresh, gbc);
    }

    private JLabel createDataLabel(String title, Font font, Dimension size) {
        JLabel label = new JLabel(title + " Đang tải...");
        label.setFont(font);
        label.setPreferredSize(size);
        label.setMinimumSize(size);
        return label;
    }

    public void loadStatistics() { // Đặt là public
        System.out.println("StatisticsPanel: Loading statistics...");
        setLabelsLoading();
        try {
            Map<String, Integer> stats = reportService.getBasicStats();
            updateLabels(
                stats.getOrDefault("totalBookTitles", -1),
                stats.getOrDefault("totalBookCopies", -1),
                stats.getOrDefault("availableBookCopies", -1),
                stats.getOrDefault("borrowedBookCopies", -1),
                stats.getOrDefault("totalUsers", -1),
                stats.getOrDefault("adminUsers", -1),
                stats.getOrDefault("regularUsers", -1)
            );
        } catch (Exception e) {
           System.err.println("Lỗi khi tải dữ liệu thống kê: " + e.getMessage());
           e.printStackTrace();
           JOptionPane.showMessageDialog(this, "Không thể tải dữ liệu thống kê.\nLỗi: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
           setLabelsError();
        }
    }

    private void setLabelsLoading() {
        lblTotalBookTitles.setText("Tổng số đầu sách: Đang tải...");
        lblTotalBookCopies.setText("Tổng số bản sách: Đang tải...");
        lblAvailableBooks.setText("Tổng sách còn trong kho: Đang tải...");
        lblBorrowedBooks.setText("Tổng sách đang được mượn: Đang tải...");
        lblTotalUsers.setText("Tổng số người dùng: Đang tải...");
        lblAdminUsers.setText("Số lượng Quản trị viên: Đang tải...");
        lblRegularUsers.setText("Số lượng người dùng thường: Đang tải...");
    }

    private void setLabelsError() {
        String errorText = " Lỗi!";
        lblTotalBookTitles.setText("Tổng số đầu sách:" + errorText);
        lblTotalBookCopies.setText("Tổng số bản sách:" + errorText);
        lblAvailableBooks.setText("Tổng sách còn trong kho:" + errorText);
        lblBorrowedBooks.setText("Tổng sách đang được mượn:" + errorText);
        lblTotalUsers.setText("Tổng số người dùng:" + errorText);
        lblAdminUsers.setText("Số lượng Quản trị viên:" + errorText);
        lblRegularUsers.setText("Số lượng người dùng thường:" + errorText);
    }

    private void updateLabels(int totalTitles, int totalCopies, int availableCopies, int borrowedCopies,
                              int totalUsers, int adminUsers, int regularUsers) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        String errorVal = " Lỗi";

        lblTotalBookTitles.setText("Tổng số đầu sách: " + (totalTitles == -1 ? errorVal : numberFormat.format(totalTitles)));
        lblTotalBookCopies.setText("Tổng số bản sách: " + (totalCopies == -1 ? errorVal : numberFormat.format(totalCopies)));
        lblAvailableBooks.setText("Tổng sách còn trong kho: " + (availableCopies == -1 ? errorVal : numberFormat.format(availableCopies)));
        lblBorrowedBooks.setText("Tổng sách đang được mượn: " + (borrowedCopies == -1 ? errorVal : numberFormat.format(borrowedCopies)));
        lblTotalUsers.setText("Tổng số người dùng: " + (totalUsers == -1 ? errorVal : numberFormat.format(totalUsers)));
        lblAdminUsers.setText("Số lượng Quản trị viên: " + (adminUsers == -1 ? errorVal : numberFormat.format(adminUsers)));
        lblRegularUsers.setText("Số lượng người dùng thường: " + (regularUsers == -1 ? errorVal : numberFormat.format(regularUsers)));
    }
}
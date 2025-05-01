package com.nhom9.libraryapp.ui.panel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Map; // Import Map

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.nhom9.libraryapp.service.ReportService;

/**
 * Panel hiển thị các thông tin thống kê cơ bản.
 */
@SuppressWarnings("serial")
public class StatisticsPanel extends JPanel {

    private JLabel lblTotalBookTitles, lblTotalBookCopies, lblAvailableBooks, lblBorrowedBooks;
    private JLabel lblTotalUsers, lblAdminUsers, lblRegularUsers;
    private ReportService reportService; // Service layer

    public StatisticsPanel() {
        this.reportService = new ReportService(); // Khởi tạo service
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;

        initComponents(gbc);
        loadStatistics();
    }

     private void initComponents(GridBagConstraints gbc) {
        Font titleFont = new Font("Arial", Font.BOLD, 18);
        Font dataFont = new Font("Arial", Font.PLAIN, 16);
        // Tăng kích thước chiều rộng để chứa nội dung dài hơn
        Dimension labelSize = new Dimension(350, 30);

        // --- Thống kê Sách ---
        JLabel lblBookStatsTitle = new JLabel("Thống kê Sách");
        lblBookStatsTitle.setFont(titleFont);
        gbc.gridy = 0; gbc.gridwidth = 2; // Span 2 cột cho tiêu đề
        add(lblBookStatsTitle, gbc);
        gbc.gridwidth = 1; // Reset span

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
        add(lblUserStatsTitle, gbc);
        gbc.gridwidth = 1; // Reset span


        lblTotalUsers = createDataLabel("Tổng số người dùng:", dataFont, labelSize);
        gbc.gridy = 7; add(lblTotalUsers, gbc);

        lblAdminUsers = createDataLabel("Số lượng Quản trị viên:", dataFont, labelSize);
        gbc.gridy = 8; add(lblAdminUsers, gbc);

        lblRegularUsers = createDataLabel("Số lượng người dùng thường:", dataFont, labelSize);
        gbc.gridy = 9; add(lblRegularUsers, gbc);

        // Nút làm mới
        JButton btnRefresh = new JButton("Làm mới thống kê");
        btnRefresh.addActionListener(e -> loadStatistics());
        gbc.gridy = 10; gbc.gridwidth = 2; // Span 2 cột
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 15, 10, 15);
        add(btnRefresh, gbc);
    }

    // Helper method để tạo label hiển thị dữ liệu
    private JLabel createDataLabel(String title, Font font, Dimension size) {
        JLabel label = new JLabel(title + " Đang tải...");
        label.setFont(font);
        label.setPreferredSize(size);
        label.setMinimumSize(size);
        return label;
    }

    // Tải dữ liệu thống kê
    private void loadStatistics() {
         System.out.println("Loading statistics from database...");
         setLabelsLoading(); // Hiển thị trạng thái đang tải
         try {
             // Gọi Service để lấy Map thống kê
             Map<String, Integer> stats = reportService.getBasicStats();

             // Lấy giá trị từ Map, dùng getOrDefault để tránh NullPointerException nếu key thiếu
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
            // Hiển thị lỗi trên label
            setLabelsError();
         }
    }

    // Hiển thị trạng thái đang tải
    private void setLabelsLoading() {
        lblTotalBookTitles.setText("Tổng số đầu sách: Đang tải...");
        lblTotalBookCopies.setText("Tổng số bản sách: Đang tải...");
        lblAvailableBooks.setText("Tổng sách còn trong kho: Đang tải...");
        lblBorrowedBooks.setText("Tổng sách đang được mượn: Đang tải...");
        lblTotalUsers.setText("Tổng số người dùng: Đang tải...");
        lblAdminUsers.setText("Số lượng Quản trị viên: Đang tải...");
        lblRegularUsers.setText("Số lượng người dùng thường: Đang tải...");
    }

     // Hiển thị trạng thái lỗi
     private void setLabelsError() {
         String errorText = " Lỗi!";
         lblTotalBookTitles.setText(lblTotalBookTitles.getText().replace("Đang tải...", errorText));
         lblTotalBookCopies.setText(lblTotalBookCopies.getText().replace("Đang tải...", errorText));
         lblAvailableBooks.setText(lblAvailableBooks.getText().replace("Đang tải...", errorText));
         lblBorrowedBooks.setText(lblBorrowedBooks.getText().replace("Đang tải...", errorText));
         lblTotalUsers.setText(lblTotalUsers.getText().replace("Đang tải...", errorText));
         lblAdminUsers.setText(lblAdminUsers.getText().replace("Đang tải...", errorText));
         lblRegularUsers.setText(lblRegularUsers.getText().replace("Đang tải...", errorText));
     }


    // Cập nhật các label hiển thị
    private void updateLabels(int totalTitles, int totalCopies, int availableCopies, int borrowedCopies,
                              int totalUsers, int adminUsers, int regularUsers) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        String errorVal = " Lỗi"; // Chuỗi hiển thị nếu giá trị là -1 (lỗi)

        lblTotalBookTitles.setText("Tổng số đầu sách: " + (totalTitles == -1 ? errorVal : numberFormat.format(totalTitles)));
        lblTotalBookCopies.setText("Tổng số bản sách: " + (totalCopies == -1 ? errorVal : numberFormat.format(totalCopies)));
        lblAvailableBooks.setText("Tổng sách còn trong kho: " + (availableCopies == -1 ? errorVal : numberFormat.format(availableCopies)));
        lblBorrowedBooks.setText("Tổng sách đang được mượn: " + (borrowedCopies == -1 ? errorVal : numberFormat.format(borrowedCopies)));

        lblTotalUsers.setText("Tổng số người dùng: " + (totalUsers == -1 ? errorVal : numberFormat.format(totalUsers)));
        lblAdminUsers.setText("Số lượng Quản trị viên: " + (adminUsers == -1 ? errorVal : numberFormat.format(adminUsers)));
        lblRegularUsers.setText("Số lượng người dùng thường: " + (regularUsers == -1 ? errorVal : numberFormat.format(regularUsers)));
    }
}
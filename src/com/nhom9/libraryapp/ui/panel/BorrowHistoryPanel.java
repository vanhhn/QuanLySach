package com.nhom9.libraryapp.ui.panel; // Sử dụng package của bạn

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.nhom9.libraryapp.model.LoanDetail; // Import lớp mới
import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.service.LibraryService; // Import Service

/**
 * Panel hiển thị lịch sử mượn/trả sách của người dùng.
 */
@SuppressWarnings("serial")
public class BorrowHistoryPanel extends JPanel {

    private User currentUser;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private LibraryService libraryService; // Service layer

    public BorrowHistoryPanel(User user) {
        this.currentUser = user;
        this.libraryService = new LibraryService();
        setLayout(new BorderLayout(10, 10));
        initComponents();
        loadHistory();
    }

     private void initComponents() {
        JLabel lblTitle = new JLabel("Lịch Sử Mượn/Trả Sách", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle, BorderLayout.NORTH);

        // Đổi tên cột và đảm bảo thứ tự đúng
        String[] columnNames = {"ID Mượn", "ID Sách", "Tên sách", "Ngày mượn", "Ngày trả dự kiến", "Ngày trả thực tế", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
             @Override public Class<?> getColumnClass(int columnIndex) {
                 if (columnIndex == 0 || columnIndex == 1) return Integer.class;
                 if (columnIndex == 3 || columnIndex == 4 || columnIndex == 5) return Date.class;
                 return String.class;
             }
        };
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setAutoCreateRowSorter(true);

         historyTable.getColumnModel().getColumn(0).setPreferredWidth(60);
         historyTable.getColumnModel().getColumn(1).setPreferredWidth(60);
         historyTable.getColumnModel().getColumn(2).setPreferredWidth(200);
         historyTable.getColumnModel().getColumn(3).setPreferredWidth(100);
         historyTable.getColumnModel().getColumn(4).setPreferredWidth(110);
         historyTable.getColumnModel().getColumn(5).setPreferredWidth(110);
         historyTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadHistory());
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadHistory() {
        System.out.println("Loading borrow history for User ID: " + currentUser.getId());
        try {
            // Gọi Service lấy LoanDetail
            List<LoanDetail> history = libraryService.getLoanHistoryByUser(currentUser.getId());
            updateTable(history); // Gọi updateTable đã sửa
        } catch (Exception e) {
            handleDataLoadError("tải lịch sử mượn", e);
        }
    }

    // Sửa updateTable để nhận List<LoanDetail> và thêm đủ cột
    private void updateTable(List<LoanDetail> loans) {
        tableModel.setRowCount(0);
        if (loans != null) {
            for (LoanDetail loan : loans) {
                Vector<Object> row = new Vector<>();
                row.add(loan.getId());             // Cột 0: ID Mượn
                row.add(loan.getBookId());          // Cột 1: ID Sách
                row.add(loan.getBookTitle());       // Cột 2: Tên sách
                row.add(loan.getNgayMuon());        // Cột 3: Ngày mượn
                row.add(loan.getNgayTraDuKien());   // Cột 4: Ngày trả DK
                row.add(loan.getNgayTraThucTe());   // Cột 5: Ngày trả TT
                row.add(loan.getTrangThai());       // Cột 6: Trạng thái
                tableModel.addRow(row);
            }
        }
         if (historyTable.getRowCount() > 0) {
           historyTable.clearSelection();
        }
    }

     // handleDataLoadError giữ nguyên
     private void handleDataLoadError(String actionDescription, Exception e) {
         System.err.println("Lỗi khi " + actionDescription + ": " + e.getMessage());
         e.printStackTrace();
         JOptionPane.showMessageDialog(this, "Không thể " + actionDescription + ".\nLỗi: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
         updateTable(Collections.emptyList());
     }
}
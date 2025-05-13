package com.nhom9.libraryapp.ui.panel;

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

import com.nhom9.libraryapp.model.LoanDetail;
import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.service.LibraryService;

@SuppressWarnings("serial")
public class BorrowHistoryPanel extends JPanel {

    private User currentUser;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private LibraryService libraryService;

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

    public void loadHistory() {
        System.out.println("Loading borrow history for User ID: " + currentUser.getId());
        try {
            List<LoanDetail> history = libraryService.getLoanHistoryByUser(currentUser.getId());
            updateTable(history);
        } catch (Exception e) {
            handleDataLoadError("tải lịch sử mượn", e);
        }
    }

    private void updateTable(List<LoanDetail> loans) {
        tableModel.setRowCount(0);
        if (loans != null) {
            for (LoanDetail loan : loans) {
                Vector<Object> row = new Vector<>();
                row.add(loan.getId());
                row.add(loan.getBookId());
                row.add(loan.getBookTitle());
                row.add(loan.getNgayMuon());
                row.add(loan.getNgayTraDuKien());
                row.add(loan.getNgayTraThucTe());
                row.add(loan.getTrangThai());
                tableModel.addRow(row);
            }
        }
         if (historyTable.getRowCount() > 0) {
           historyTable.clearSelection();
        }
    }

     private void handleDataLoadError(String actionDescription, Exception e) {
         System.err.println("Lỗi khi " + actionDescription + ": " + e.getMessage());
         e.printStackTrace();
         JOptionPane.showMessageDialog(this, "Không thể " + actionDescription + ".\nLỗi: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
         updateTable(Collections.emptyList());
     }
}
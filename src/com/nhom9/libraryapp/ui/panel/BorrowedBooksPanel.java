package com.nhom9.libraryapp.ui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.nhom9.libraryapp.model.LoanDetail;
import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.service.LibraryService;
import com.nhom9.libraryapp.ui.frame.MainAdminFrame;
import com.nhom9.libraryapp.ui.frame.MainUserFrame;

@SuppressWarnings("serial")
public class BorrowedBooksPanel extends JPanel {

    private User currentUser;
    private JTable borrowedTable;
    private DefaultTableModel tableModel;
    private JButton btnReturn;
    private LibraryService libraryService;

    public BorrowedBooksPanel(User user) {
        this.currentUser = user;
        this.libraryService = new LibraryService();
        setLayout(new BorderLayout(10, 10));
        initComponents();
        addEventListeners();
        loadBorrowedBooks();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Sách Bạn Đang Mượn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle, BorderLayout.NORTH);

        String[] columnNames = {"ID Mượn", "ID Sách", "Tên sách", "Ngày mượn", "Ngày trả dự kiến"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1) return Integer.class;
                if (columnIndex == 3 || columnIndex == 4) return Date.class;
                return String.class;
            }
        };
        borrowedTable = new JTable(tableModel);
        borrowedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        borrowedTable.setAutoCreateRowSorter(true);

        borrowedTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        borrowedTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        borrowedTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        borrowedTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        borrowedTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(borrowedTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnReturn = new JButton("Trả sách");
        btnReturn.setEnabled(false);
        actionPanel.add(btnReturn);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        btnReturn.addActionListener(e -> returnSelectedBook());
        borrowedTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnReturn.setEnabled(borrowedTable.getSelectedRow() != -1);
            }
        });
    }

    public void loadBorrowedBooks() {
        System.out.println("Loading borrowed books for User ID: " + currentUser.getId());
        try {
            List<LoanDetail> loans = libraryService.getActiveLoansByUser(currentUser.getId());
            updateTable(loans);
        } catch (Exception e) {
            handleDataLoadError("tải sách đang mượn", e);
        }
    }

    private void returnSelectedBook() {
        int selectedRow = borrowedTable.getSelectedRow();
        if (selectedRow == -1) return;

        int modelRow = borrowedTable.convertRowIndexToModel(selectedRow);
        int loanId = (int) tableModel.getValueAt(modelRow, 0);
        String bookTitle = tableModel.getValueAt(modelRow, 2) != null ?
                           tableModel.getValueAt(modelRow, 2).toString() :
                           "ID Sách: " + tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn trả sách:\n" + bookTitle + "?",
                "Xác nhận Trả sách", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Attempting to return loan with ID: " + loanId);
            try {
                boolean success = libraryService.returnBook(loanId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Trả sách '" + bookTitle + "' thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadBorrowedBooks(); 

                    
                    Window parentWindow = SwingUtilities.getWindowAncestor(this);
                    if (parentWindow instanceof MainAdminFrame) {
                        MainAdminFrame mainAdminFrame = (MainAdminFrame) parentWindow;
                        mainAdminFrame.refreshBookSearchPanelData();
                        mainAdminFrame.refreshBookManagementPanelData();
                        mainAdminFrame.refreshBorrowHistoryPanelData(); // Làm mới lịch sử ngay
                    } else if (parentWindow instanceof MainUserFrame) {
                        MainUserFrame mainUserFrame = (MainUserFrame) parentWindow;
                        mainUserFrame.refreshBookSearchPanelData();
                        mainUserFrame.refreshBorrowHistoryPanelData(); // Làm mới lịch sử ngay
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Trả sách thất bại!", "Lỗi Trả Sách", JOptionPane.ERROR_MESSAGE);
                    loadBorrowedBooks(); // Tải lại để đảm bảo UI đúng
                }
            } catch (Exception e) {
                System.err.println("Lỗi hệ thống khi trả sách: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống khi trả sách:\n" + e.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            }
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
                tableModel.addRow(row);
            }
        }
        if (borrowedTable.getRowCount() > 0) {
            borrowedTable.clearSelection();
        }
        btnReturn.setEnabled(false);
    }

    private void handleDataLoadError(String actionDescription, Exception e) {
        System.err.println("Lỗi khi " + actionDescription + ": " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Không thể " + actionDescription + ".\nLỗi: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        updateTable(Collections.emptyList());
    }
}
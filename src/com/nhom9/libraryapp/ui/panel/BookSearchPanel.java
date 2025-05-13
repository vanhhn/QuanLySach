package com.nhom9.libraryapp.ui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.nhom9.libraryapp.model.Book;
import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.service.LibraryService;
import com.nhom9.libraryapp.ui.frame.MainAdminFrame;
import com.nhom9.libraryapp.ui.frame.MainUserFrame;

@SuppressWarnings("serial")
public class BookSearchPanel extends JPanel {

    private User currentUser;
    private JTextField txtSearchTitle, txtSearchAuthor, txtSearchGenre;
    private JButton btnSearch, btnClearSearch;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton btnBorrow;
    private LibraryService libraryService;

    public BookSearchPanel(User user) {
        this.currentUser = user;
        this.libraryService = new LibraryService();
        setLayout(new BorderLayout(10, 10));
        initComponents();
        addEventListeners();
        loadInitialData();
    }

    private void initComponents() {
        JPanel searchCriteriaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchCriteriaPanel.add(new JLabel("Tên sách:"));
        txtSearchTitle = new JTextField(15);
        searchCriteriaPanel.add(txtSearchTitle);
        searchCriteriaPanel.add(new JLabel("Tác giả:"));
        txtSearchAuthor = new JTextField(15);
        searchCriteriaPanel.add(txtSearchAuthor);
        searchCriteriaPanel.add(new JLabel("Thể loại:"));
        txtSearchGenre = new JTextField(10);
        searchCriteriaPanel.add(txtSearchGenre);
        btnSearch = new JButton("Tìm kiếm");
        searchCriteriaPanel.add(btnSearch);
        btnClearSearch = new JButton("Xóa tìm");
        searchCriteriaPanel.add(btnClearSearch);
        add(searchCriteriaPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Tên sách", "Tác giả", "Thể loại", "SL Còn lại"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4 || columnIndex == 0) return Integer.class;
                return String.class;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setAutoCreateRowSorter(true);
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBorrow = new JButton("Mượn sách");
        btnBorrow.setEnabled(false);
        actionPanel.add(btnBorrow);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        btnSearch.addActionListener(e -> searchBooks());
        btnClearSearch.addActionListener(e -> {
            txtSearchTitle.setText("");
            txtSearchAuthor.setText("");
            txtSearchGenre.setText("");
            loadInitialData();
        });
        btnBorrow.addActionListener(e -> borrowSelectedBook());
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateBorrowButtonState();
            }
        });
    }

    private void updateBorrowButtonState() {
        int selectedRow = bookTable.getSelectedRow();
        boolean borrowEnabled = false;
        if (selectedRow != -1) {
            int modelRow = bookTable.convertRowIndexToModel(selectedRow);
            int quantityColumnIndex = 4;
            try {
                Object quantityValue = tableModel.getValueAt(modelRow, quantityColumnIndex);
                 if (quantityValue instanceof Integer) {
                    borrowEnabled = ((Integer) quantityValue) > 0;
                 }
            } catch (ArrayIndexOutOfBoundsException ex) {
                 System.err.println("Lỗi lấy giá trị số lượng: " + ex.getMessage());
                 borrowEnabled = false;
             }
        }
        btnBorrow.setEnabled(borrowEnabled);
     }

    public void loadInitialData() {
        System.out.println("Loading initial book data from database...");
        try {
            List<Book> books = libraryService.searchBooks("", "", "");
            updateTable(books);
        } catch (Exception e) {
            handleDataLoadError("tải danh sách sách ban đầu", e);
        }
    }

    private void searchBooks() {
        String title = txtSearchTitle.getText().trim();
        String author = txtSearchAuthor.getText().trim();
        String genre = txtSearchGenre.getText().trim();
        System.out.println("Searching books in database with criteria: Title=" + title + ", Author=" + author + ", Genre=" + genre);
        try {
            List<Book> books = libraryService.searchBooks(title, author, genre);
            updateTable(books);
            if (books.isEmpty() && (!title.isEmpty() || !author.isEmpty() || !genre.isEmpty())) {
                 JOptionPane.showMessageDialog(this, "Không tìm thấy sách nào phù hợp.", "Thông báo Tìm Kiếm", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            handleDataLoadError("tìm kiếm sách", e);
        }
    }

    private void borrowSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) { return; }
        int modelRow = bookTable.convertRowIndexToModel(selectedRow);
        int bookId = (int) tableModel.getValueAt(modelRow, 0);
        String bookTitle = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn mượn cuốn sách:\n" + bookTitle + "?",
                "Xác nhận Mượn sách", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Attempting to borrow book ID: " + bookId + " for User ID: " + currentUser.getId());
            try {
                boolean success = libraryService.borrowBook(currentUser.getId(), bookId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Mượn sách '" + bookTitle + "' thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadCurrentView(); // Làm mới panel hiện tại

                    // THÔNG BÁO CHO MAINFRAME CẬP NHẬT CÁC TAB KHÁC
                    Window parentWindow = SwingUtilities.getWindowAncestor(this);
                    if (parentWindow instanceof MainAdminFrame) {
                        MainAdminFrame mainAdminFrame = (MainAdminFrame) parentWindow;
                        mainAdminFrame.refreshBorrowedBooksPanelData();
                        mainAdminFrame.refreshBorrowHistoryPanelData();
                        // Admin cũng có thể cần làm mới Quản lý sách nếu số lượng tổng cũng thay đổi (ít xảy ra khi mượn)
                        // mainAdminFrame.refreshBookManagementPanelData();
                    } else if (parentWindow instanceof MainUserFrame) {
                        MainUserFrame mainUserFrame = (MainUserFrame) parentWindow;
                        mainUserFrame.refreshBorrowedBooksPanelData();
                        mainUserFrame.refreshBorrowHistoryPanelData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Mượn sách thất bại! Sách có thể đã hết hoặc có lỗi xảy ra.", "Lỗi Mượn Sách", JOptionPane.ERROR_MESSAGE);
                    loadCurrentView(); // Tải lại để đảm bảo UI nhất quán
                }
            } catch (Exception e) {
                System.err.println("Lỗi hệ thống khi mượn sách: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống khi mượn sách:\n" + e.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadCurrentView() {
         if (!txtSearchTitle.getText().trim().isEmpty() ||
             !txtSearchAuthor.getText().trim().isEmpty() ||
             !txtSearchGenre.getText().trim().isEmpty()) {
             searchBooks();
         } else {
             loadInitialData();
         }
     }

    public void refreshBookListView() {
        System.out.println("BookSearchPanel: Refreshing book list view...");
        loadCurrentView();
    }

    private void updateTable(List<Book> books) {
        tableModel.setRowCount(0);
        if (books != null) {
            for (Book book : books) {
                Vector<Object> row = new Vector<>();
                row.add(book.getId());
                row.add(book.getTenSach());
                row.add(book.getTacGia());
                row.add(book.getTheLoai());
                row.add(book.getSoLuongConLai());
                tableModel.addRow(row);
            }
        }
        if (bookTable.getRowCount() > 0) {
           bookTable.clearSelection();
        }
         updateBorrowButtonState();
    }

    private void handleDataLoadError(String actionDescription, Exception e) {
         System.err.println("Lỗi khi " + actionDescription + ": " + e.getMessage());
         e.printStackTrace();
         JOptionPane.showMessageDialog(this, "Không thể " + actionDescription + ".\nLỗi: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
         updateTable(Collections.emptyList());
     }
}
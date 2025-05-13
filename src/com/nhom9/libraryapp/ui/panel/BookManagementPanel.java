package com.nhom9.libraryapp.ui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import com.nhom9.libraryapp.service.LibraryService;
import com.nhom9.libraryapp.ui.dialog.AddEditBookDialog;
import com.nhom9.libraryapp.ui.frame.MainAdminFrame;

@SuppressWarnings("serial")
public class BookManagementPanel extends JPanel {

    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private JTextField txtSearch;
    private LibraryService libraryService;

    public BookManagementPanel() {
        this.libraryService = new LibraryService();
        setLayout(new BorderLayout(10, 10));
        initComponents();
        addEventListeners();
        loadAllBooks();
    }

    private void initComponents() {
        JPanel controlPanel = new JPanel(new BorderLayout(10, 5));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm sách (Tên/Tác giả/Thể loại):"));
        txtSearch = new JTextField(25);
        searchPanel.add(txtSearch);
        JButton btnPerformSearch = new JButton("Tìm");
        btnPerformSearch.addActionListener(e -> searchBooks());
        searchPanel.add(btnPerformSearch);
        controlPanel.add(searchPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Thêm sách mới");
        btnEdit = new JButton("Sửa sách");
        btnDelete = new JButton("Xóa sách");
        btnRefresh = new JButton("Làm mới");
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Tên sách", "Tác giả", "Thể loại", "Tổng SL", "SL Còn lại", "Ngày nhập"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4 || columnIndex == 5) return Integer.class;
                if (columnIndex == 6) return Timestamp.class;
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
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(130);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addEventListeners() {
        btnRefresh.addActionListener(e -> loadAllBooks());
        btnAdd.addActionListener(e -> openAddBookDialog());
        btnEdit.addActionListener(e -> openEditBookDialog());
        btnDelete.addActionListener(e -> deleteSelectedBook());
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = bookTable.getSelectedRow() != -1;
                btnEdit.setEnabled(rowSelected);
                btnDelete.setEnabled(rowSelected);
            }
        });
    }

    public void loadAllBooks() {
        System.out.println("BookManagementPanel: Loading all books...");
        txtSearch.setText("");
        try {
            List<Book> books = libraryService.getAllBooks();
            updateTable(books);
        } catch (Exception e) {
            handleDataLoadError("tải tất cả sách", e);
        }
    }

    private void searchBooks() {
        String searchTerm = txtSearch.getText().trim();
        System.out.println("BookManagementPanel: Admin searching for books: " + searchTerm);
        try {
            List<Book> books = libraryService.searchBooks(searchTerm, searchTerm, searchTerm);
            updateTable(books);
            if (books.isEmpty() && !searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách nào khớp với '" + searchTerm + "'", "Thông báo Tìm Kiếm", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            handleDataLoadError("tìm kiếm sách", e);
        }
    }

    private void openAddBookDialog() {
        System.out.println("Opening Add Book Dialog...");
        AddEditBookDialog dialog = new AddEditBookDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isDataChanged()) {
            loadAllBooks(); // Làm mới panel hiện tại
            // Thông báo cho MainAdminFrame làm mới các panel sách khác
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow instanceof MainAdminFrame) {
                ((MainAdminFrame) parentWindow).refreshBookSearchPanelData();
            }
        }
    }

    private void openEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) return;
        int modelRow = bookTable.convertRowIndexToModel(selectedRow);
        int bookId = (int) tableModel.getValueAt(modelRow, 0);
        System.out.println("Opening Edit Book Dialog for Book ID: " + bookId);
        try {
            Book bookToEdit = libraryService.getBookById(bookId);
            if (bookToEdit == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin sách với ID: " + bookId, "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            AddEditBookDialog dialog = new AddEditBookDialog((Frame) SwingUtilities.getWindowAncestor(this), bookToEdit);
            dialog.setVisible(true);
            if (dialog.isDataChanged()) {
                loadAllBooks(); // Làm mới panel hiện tại
                // Thông báo cho MainAdminFrame làm mới các panel sách khác
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                if (parentWindow instanceof MainAdminFrame) {
                    ((MainAdminFrame) parentWindow).refreshBookSearchPanelData();
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy thông tin sách để sửa (ID: " + bookId + "): " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin sách để sửa.\nLỗi: " + e.getMessage(), "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) return;
        int modelRow = bookTable.convertRowIndexToModel(selectedRow);
        int bookId = (int) tableModel.getValueAt(modelRow, 0);
        String bookTitle = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa cuốn sách:\n" + bookTitle + " (ID: " + bookId + ")?\n" +
                "Hành động này không thể hoàn tác!",
                "Xác nhận Xóa sách",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Attempting to delete book with ID: " + bookId);
            try {
                boolean success = libraryService.deleteBook(bookId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa sách thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadAllBooks(); // Làm mới panel hiện tại
                    // Thông báo cho MainAdminFrame làm mới các panel sách khác
                    Window parentWindow = SwingUtilities.getWindowAncestor(this);
                    if (parentWindow instanceof MainAdminFrame) {
                        ((MainAdminFrame) parentWindow).refreshBookSearchPanelData();
                    }
                }
            } catch (SQLException ex) {
                if (ex.getMessage() != null && ex.getMessage().contains("đang có người mượn")) {
                    JOptionPane.showMessageDialog(this, "Không thể xóa sách này vì đang có người mượn!", "Lỗi Ràng Buộc", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi CSDL khi xóa sách: " + ex.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống khi xóa sách: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
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
                row.add(book.getSoLuongTong());
                row.add(book.getSoLuongConLai());
                row.add(book.getNgayNhap());
                tableModel.addRow(row);
            }
        }
        if (bookTable.getRowCount() > 0) {
            bookTable.clearSelection();
        }
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void handleDataLoadError(String actionDescription, Exception e) {
        System.err.println("Lỗi khi " + actionDescription + ": " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Không thể " + actionDescription + ".\nLỗi: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        updateTable(Collections.emptyList());
    }
}
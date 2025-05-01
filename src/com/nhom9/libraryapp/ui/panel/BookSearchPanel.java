package com.nhom9.libraryapp.ui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Collections; // Import Collections
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
import javax.swing.table.DefaultTableModel;

import com.nhom9.libraryapp.model.Book;
import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.service.LibraryService;

/**
 * Panel cho chức năng tìm kiếm và mượn sách.
 */
@SuppressWarnings("serial")
public class BookSearchPanel extends JPanel {

    private User currentUser;
    private JTextField txtSearchTitle, txtSearchAuthor, txtSearchGenre;
    private JButton btnSearch, btnClearSearch; // Thêm nút Clear
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton btnBorrow;
    private LibraryService libraryService; // Service layer

    public BookSearchPanel(User user) {
        this.currentUser = user;
        this.libraryService = new LibraryService(); // Khởi tạo Service
        setLayout(new BorderLayout(10, 10));
        initComponents();
        addEventListeners();
        loadInitialData(); // Tải dữ liệu ban đầu khi panel được tạo
    }

    private void initComponents() {
        // --- Khu vực tìm kiếm (Phía trên) ---
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

        btnClearSearch = new JButton("Xóa tìm"); // Nút xóa điều kiện tìm kiếm
        searchCriteriaPanel.add(btnClearSearch);

        add(searchCriteriaPanel, BorderLayout.NORTH);

        // --- Bảng hiển thị kết quả (Ở giữa) ---
        String[] columnNames = {"ID", "Tên sách", "Tác giả", "Thể loại", "SL Còn lại"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
             @Override
             public Class<?> getColumnClass(int columnIndex) {
                 // Để JTable biết cột số lượng là số -> sắp xếp đúng
                 if (columnIndex == 4 || columnIndex == 0) { // ID và SL còn lại
                     return Integer.class;
                 }
                 return String.class;
             }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setAutoCreateRowSorter(true); // Cho phép sắp xếp cột

        // Đặt độ rộng cột gợi ý
         bookTable.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
         bookTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên sách
         bookTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Tác giả
         bookTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Thể loại
         bookTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // SL Còn lại

        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Khu vực nút chức năng (Phía dưới) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBorrow = new JButton("Mượn sách");
        btnBorrow.setEnabled(false); // Ban đầu vô hiệu hóa
        actionPanel.add(btnBorrow);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        // Sự kiện nút Tìm kiếm
        btnSearch.addActionListener(e -> searchBooks());

         // Sự kiện nút Xóa tìm
         btnClearSearch.addActionListener(e -> {
             txtSearchTitle.setText("");
             txtSearchAuthor.setText("");
             txtSearchGenre.setText("");
             loadInitialData(); // Quay lại danh sách ban đầu
         });

        // Sự kiện nút Mượn sách
        btnBorrow.addActionListener(e -> borrowSelectedBook());

        // Sự kiện khi chọn một hàng trong bảng
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Chỉ xử lý khi việc chọn đã ổn định
                 updateBorrowButtonState();
            }
        });
    }

    // Cập nhật trạng thái nút Mượn dựa trên hàng được chọn
     private void updateBorrowButtonState() {
        int selectedRow = bookTable.getSelectedRow();
        boolean borrowEnabled = false;
        if (selectedRow != -1) {
            // Chuyển đổi viewRow sang modelRow nếu có sắp xếp/lọc
            int modelRow = bookTable.convertRowIndexToModel(selectedRow);
            // Kiểm tra xem sách có còn hàng không (lấy từ cột "SL Còn lại")
            int quantityColumnIndex = 4; // Index của cột "SL Còn lại" trong model
            try {
                Object quantityValue = tableModel.getValueAt(modelRow, quantityColumnIndex);
                 if (quantityValue instanceof Integer) {
                    borrowEnabled = ((Integer) quantityValue) > 0;
                 }
            } catch (ArrayIndexOutOfBoundsException ex) {
                 System.err.println("Lỗi lấy giá trị số lượng: " + ex.getMessage());
                 borrowEnabled = false; // Lỗi thì không cho mượn
             }
        }
        btnBorrow.setEnabled(borrowEnabled);
     }


    // Tải dữ liệu ban đầu (ví dụ: tất cả sách)
    private void loadInitialData() {
        System.out.println("Loading initial book data from database...");
        try {
            // Lấy tất cả sách hoặc sách còn hàng tùy logic
            List<Book> books = libraryService.searchBooks("", "", ""); // Lấy tất cả
            // Hoặc: List<Book> books = libraryService.getAllAvailableBooks(); // Nếu có phương thức này
            updateTable(books);
        } catch (Exception e) {
            handleDataLoadError("tải danh sách sách ban đầu", e);
        }
    }

    // Thực hiện tìm kiếm sách
    private void searchBooks() {
        String title = txtSearchTitle.getText().trim();
        String author = txtSearchAuthor.getText().trim();
        String genre = txtSearchGenre.getText().trim();

        System.out.println("Searching books in database with criteria: Title=" + title + ", Author=" + author + ", Genre=" + genre);
        try {
            List<Book> books = libraryService.searchBooks(title, author, genre);
            updateTable(books);
            if (books.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Không tìm thấy sách nào phù hợp.", "Thông báo Tìm Kiếm", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            handleDataLoadError("tìm kiếm sách", e);
        }
    }

    // Xử lý mượn sách được chọn
    private void borrowSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            // Nút đã được kiểm soát nhưng kiểm tra lại cho chắc
            return;
        }
        // Lấy model row index trong trường hợp bảng đang được sắp xếp
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
                    // Tải lại dữ liệu bảng để cập nhật số lượng
                    loadCurrentView();
                } else {
                    JOptionPane.showMessageDialog(this, "Mượn sách thất bại! Sách có thể đã hết hoặc có lỗi xảy ra.", "Lỗi Mượn Sách", JOptionPane.ERROR_MESSAGE);
                     // Tải lại để đảm bảo số lượng trên UI là đúng
                     loadCurrentView();
                }
            } catch (Exception e) { // Bắt lỗi từ Service/DAO
                System.err.println("Lỗi hệ thống khi mượn sách: " + e.getMessage());
                e.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi hệ thống khi mượn sách:\n" + e.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

     // Helper method để tải lại dữ liệu đang hiển thị (kết quả tìm kiếm hoặc view ban đầu)
     private void loadCurrentView() {
         if (!txtSearchTitle.getText().trim().isEmpty() ||
             !txtSearchAuthor.getText().trim().isEmpty() ||
             !txtSearchGenre.getText().trim().isEmpty()) {
             searchBooks();
         } else {
             loadInitialData();
         }
     }

    // Cập nhật dữ liệu cho bảng
    private void updateTable(List<Book> books) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
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
        // Bỏ chọn hàng và cập nhật trạng thái nút sau khi tải lại
        if (bookTable.getRowCount() > 0) {
           bookTable.clearSelection();
        }
         updateBorrowButtonState(); // Cập nhật lại trạng thái nút Mượn
    }

    // Xử lý lỗi tải dữ liệu chung
    private void handleDataLoadError(String actionDescription, Exception e) {
         System.err.println("Lỗi khi " + actionDescription + ": " + e.getMessage());
         e.printStackTrace();
         JOptionPane.showMessageDialog(this, "Không thể " + actionDescription + ".\nLỗi: " + e.getMessage(), "Lỗi Tải Dữ Liệu", JOptionPane.ERROR_MESSAGE);
         updateTable(Collections.emptyList()); // Hiển thị bảng rỗng khi lỗi
     }
}
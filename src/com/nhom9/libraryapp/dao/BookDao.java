package com.nhom9.libraryapp.dao;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nhom9.libraryapp.model.Book;

/**
 * DAO class for managing Book data in the Sach table.
 */
public class BookDao {

     /**
     * Lấy sách theo ID.
     */
    public Book getBookById(int bookId) throws SQLException {
        String sql = "SELECT * FROM Sach WHERE id_sach = ?";
        Book book = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    book = mapResultSetToBook(rs);
                }
            }
        }
        return book;
    }

     /**
     * Lấy tất cả sách trong thư viện.
     */
    public List<Book> getAllBooks() throws SQLException {
        String sql = "SELECT * FROM Sach ORDER BY ten_sach";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    /**
     * Tìm kiếm sách dựa trên tiêu chí (sử dụng LIKE cho tìm kiếm gần đúng).
     */
    public List<Book> searchBooks(String title, String author, String genre) throws SQLException {
        // Xây dựng câu lệnh SQL động một cách an toàn
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM Sach WHERE 1=1"); // Bắt đầu với điều kiện luôn đúng
        List<Object> params = new ArrayList<>();

        if (title != null && !title.trim().isEmpty()) {
            sqlBuilder.append(" AND ten_sach LIKE ?");
            params.add("%" + title.trim() + "%");
        }
        if (author != null && !author.trim().isEmpty()) {
            sqlBuilder.append(" AND tac_gia LIKE ?");
            params.add("%" + author.trim() + "%");
        }
        if (genre != null && !genre.trim().isEmpty()) {
            sqlBuilder.append(" AND the_loai LIKE ?");
            params.add("%" + genre.trim() + "%");
        }
        sqlBuilder.append(" ORDER BY ten_sach");

        String sql = sqlBuilder.toString();
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set các tham số
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }
        return books;
    }

    /**
     * Thêm sách mới.
     */
    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO Sach (ten_sach, tac_gia, the_loai, so_luong_tong, so_luong_con_lai) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTenSach());
            pstmt.setString(2, book.getTacGia());
            pstmt.setString(3, book.getTheLoai());
            pstmt.setInt(4, book.getSoLuongTong());
            pstmt.setInt(5, book.getSoLuongConLai()); // Thường bằng SL Tổng khi mới thêm
            rowsAffected = pstmt.executeUpdate();
        }
        return rowsAffected > 0;
    }

    /**
     * Cập nhật thông tin sách.
     */
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE Sach SET ten_sach = ?, tac_gia = ?, the_loai = ?, so_luong_tong = ?, so_luong_con_lai = ? WHERE id_sach = ?";
        int rowsAffected = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTenSach());
            pstmt.setString(2, book.getTacGia());
            pstmt.setString(3, book.getTheLoai());
            pstmt.setInt(4, book.getSoLuongTong());
            pstmt.setInt(5, book.getSoLuongConLai());
            pstmt.setInt(6, book.getId()); // ID sách cần cập nhật
            rowsAffected = pstmt.executeUpdate();
        }
        return rowsAffected > 0;
    }

     /**
     * Cập nhật chỉ số lượng còn lại của sách (dùng khi mượn/trả).
     * @param bookId ID sách.
     * @param change Số lượng thay đổi (+1 khi trả, -1 khi mượn).
     * @return true nếu cập nhật thành công, false nếu thất bại (ví dụ: SL âm).
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public boolean updateBookQuantity(int bookId, int change) throws SQLException {
         // Thêm điều kiện kiểm tra số lượng không âm ngay trong câu UPDATE
        String sql = "UPDATE Sach SET so_luong_con_lai = so_luong_con_lai + ? WHERE id_sach = ? AND so_luong_con_lai + ? >= 0";
        int rowsAffected = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, change);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, change); // Kiểm tra điều kiện >= 0 sau khi thay đổi
            rowsAffected = pstmt.executeUpdate();
        }
        // Nếu rowsAffected = 0, có thể là do ID sách không đúng HOẶC do việc thay đổi sẽ làm số lượng âm.
        // Cần kiểm tra kỹ hơn ở tầng Service nếu muốn phân biệt rõ.
        return rowsAffected > 0;
    }


    /**
     * Xóa sách.
     */
    public boolean deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM Sach WHERE id_sach = ?";
        int rowsAffected = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            rowsAffected = pstmt.executeUpdate();
        }
         // SQLException sẽ được ném lên nếu có lỗi ràng buộc (ví dụ FK với PhieuMuon đặt là RESTRICT)
        return rowsAffected > 0;
    }

    // --- Các phương thức đếm/tính tổng cho thống kê ---

    public int getTotalBookCount() throws SQLException { // Đếm số đầu sách
        String sql = "SELECT COUNT(*) FROM Sach";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

     public int getTotalBookCopies() throws SQLException { // Đếm tổng số bản sách
        String sql = "SELECT SUM(so_luong_tong) FROM Sach";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

      public int getTotalAvailableCopies() throws SQLException { // Đếm tổng số sách còn lại
        String sql = "SELECT SUM(so_luong_con_lai) FROM Sach";
         try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }


     // --- Phương thức tiện ích để map ResultSet sang Book ---
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id_sach"));
        book.setTenSach(rs.getString("ten_sach"));
        book.setTacGia(rs.getString("tac_gia"));
        book.setTheLoai(rs.getString("the_loai"));
        book.setSoLuongTong(rs.getInt("so_luong_tong"));
        book.setSoLuongConLai(rs.getInt("so_luong_con_lai"));
        book.setNgayNhap(rs.getTimestamp("ngay_nhap"));
        return book;
    }
}

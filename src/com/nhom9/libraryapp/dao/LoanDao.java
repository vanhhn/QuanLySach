package com.nhom9.libraryapp.dao; // Sử dụng package của bạn

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.nhom9.libraryapp.model.Loan;
import com.nhom9.libraryapp.model.LoanDetail; // Import lớp mới

/**
 * DAO class for managing Loan data in the PhieuMuon table.
 */
public class LoanDao {

    /**
     * Lấy thông tin phiếu mượn theo ID (Không cần join, trả về Loan cơ bản).
     */
     public Loan getLoanById(int loanId) throws SQLException {
        String sql = "SELECT * FROM PhieuMuon WHERE id_phieu_muon = ?";
        Loan loan = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loanId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loan = mapResultSetToLoan(rs); // Dùng map cơ bản
                }
            }
        }
        return loan;
    }

    /**
     * Thêm một phiếu mượn mới.
     */
    public boolean addLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO PhieuMuon (id_nguoidung, id_sach, ngay_muon, ngay_tra_du_kien, trang_thai) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loan.getUserId());
            pstmt.setInt(2, loan.getBookId());
            pstmt.setDate(3, loan.getNgayMuon()); // java.sql.Date
            pstmt.setDate(4, loan.getNgayTraDuKien()); // java.sql.Date
            pstmt.setString(5, loan.getTrangThai());
            rowsAffected = pstmt.executeUpdate();
        }
        return rowsAffected > 0;
    }

    /**
     * Cập nhật trạng thái và ngày trả thực tế của phiếu mượn (dùng khi trả sách).
     */
    public boolean updateLoanStatus(int loanId, String status, Date returnDate) throws SQLException {
        String sql = "UPDATE PhieuMuon SET trang_thai = ?, ngay_tra_thuc_te = ? WHERE id_phieu_muon = ?";
        int rowsAffected = 0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            // Cho phép set Date là null nếu cần (ví dụ rollback)
            if (returnDate != null) {
                 pstmt.setDate(2, returnDate);
             } else {
                 pstmt.setNull(2, Types.DATE);
             }
            pstmt.setInt(3, loanId);
            rowsAffected = pstmt.executeUpdate();
        }
        return rowsAffected > 0;
    }

    /**
     * Lấy danh sách các phiếu mượn đang hoạt động của một người dùng, bao gồm tên sách.
     * @param userId ID của người dùng.
     * @return List các đối tượng LoanDetail.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public List<LoanDetail> getActiveLoansByUser(int userId) throws SQLException {
        // Thêm JOIN với bảng Sach để lấy ten_sach
        String sql = "SELECT pm.id_phieu_muon, pm.id_nguoidung, pm.id_sach, pm.ngay_muon, " +
                     "pm.ngay_tra_du_kien, pm.ngay_tra_thuc_te, pm.trang_thai, s.ten_sach " +
                     "FROM PhieuMuon pm " +
                     "JOIN Sach s ON pm.id_sach = s.id_sach " +
                     "WHERE pm.id_nguoidung = ? AND pm.trang_thai = 'đang mượn' " +
                     "ORDER BY pm.ngay_muon";
        List<LoanDetail> loans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Sử dụng phương thức map mới để tạo LoanDetail
                    loans.add(mapResultSetToLoanDetail(rs));
                }
            }
        }
        return loans;
    }

     /**
     * Lấy toàn bộ lịch sử mượn/trả của một người dùng, bao gồm tên sách.
      * @param userId ID của người dùng.
      * @return List các đối tượng LoanDetail.
      * @throws SQLException Nếu có lỗi CSDL.
     */
    public List<LoanDetail> getLoanHistoryByUser(int userId) throws SQLException {
         // Thêm JOIN với bảng Sach để lấy ten_sach
        String sql = "SELECT pm.id_phieu_muon, pm.id_nguoidung, pm.id_sach, pm.ngay_muon, " +
                     "pm.ngay_tra_du_kien, pm.ngay_tra_thuc_te, pm.trang_thai, s.ten_sach " +
                     "FROM PhieuMuon pm " +
                     "JOIN Sach s ON pm.id_sach = s.id_sach " +
                     "WHERE pm.id_nguoidung = ? " +
                     "ORDER BY pm.ngay_muon DESC";
        List<LoanDetail> loans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                     // Sử dụng phương thức map mới để tạo LoanDetail
                    loans.add(mapResultSetToLoanDetail(rs));
                }
            }
        }
        return loans;
    }

    // --- Phương thức map mới cho LoanDetail ---
    private LoanDetail mapResultSetToLoanDetail(ResultSet rs) throws SQLException {
        // Tạo đối tượng LoanDetail và điền thông tin từ ResultSet
        return new LoanDetail(
                rs.getInt("id_phieu_muon"),
                rs.getInt("id_nguoidung"),
                rs.getInt("id_sach"),
                rs.getDate("ngay_muon"),
                rs.getDate("ngay_tra_du_kien"),
                rs.getDate("ngay_tra_thuc_te"), // Lấy cả ngày trả thực tế
                rs.getString("trang_thai"),
                rs.getString("ten_sach") // Lấy tên sách từ kết quả JOIN
        );
    }

    // --- Phương thức map cũ cho Loan (vẫn hữu ích cho getLoanById) ---
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id_phieu_muon"));
        loan.setUserId(rs.getInt("id_nguoidung"));
        loan.setBookId(rs.getInt("id_sach"));
        loan.setNgayMuon(rs.getDate("ngay_muon"));
        loan.setNgayTraDuKien(rs.getDate("ngay_tra_du_kien"));
        loan.setNgayTraThucTe(rs.getDate("ngay_tra_thuc_te"));
        loan.setTrangThai(rs.getString("trang_thai"));
        return loan;
    }


    // --- Các phương thức đếm cho thống kê ---

    /**
     * Đếm số lượng sách đang được mượn trong toàn hệ thống.
     */
    public int countActiveLoans() throws SQLException {
        String sql = "SELECT COUNT(*) FROM PhieuMuon WHERE trang_thai = 'đang mượn'";
         try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

     /**
     * Đếm số lượng người đang mượn một cuốn sách cụ thể.
     * Dùng để kiểm tra trước khi xóa sách.
     */
     public int countActiveLoansByBookId(int bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PhieuMuon WHERE id_sach = ? AND trang_thai = 'đang mượn'";
         try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setInt(1, bookId);
             try(ResultSet rs = pstmt.executeQuery()) {
                 if (rs.next()) {
                     return rs.getInt(1);
                 }
             }
        }
        return 0;
    }
}
package com.nhom9.libraryapp.dao;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nhom9.libraryapp.model.User;

/**
 * DAO class for managing User data in the NguoiDung table.
 */
public class UserDao {

    /**
     * Lấy thông tin user dựa trên tên đăng nhập.
     * @param username Tên đăng nhập.
     * @return Đối tượng User hoặc null nếu không tìm thấy.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id_nguoidung, ho_ten, email, ten_dang_nhap, mat_khau, vai_tro, ngay_tao FROM NguoiDung WHERE ten_dang_nhap = ?";
        User user = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs, true); // Lấy cả mật khẩu hash
                }
            }
        }
        return user;
    }

     /**
     * Lấy thông tin user dựa trên email.
     * @param email Email.
     * @return Đối tượng User hoặc null nếu không tìm thấy.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT id_nguoidung, ho_ten, email, ten_dang_nhap, mat_khau, vai_tro, ngay_tao FROM NguoiDung WHERE email = ?";
        User user = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs, false); // Không cần lấy mật khẩu khi check email
                }
            }
        }
        return user;
    }

      /**
     * Lấy thông tin user dựa trên ID.
     * @param userId ID người dùng.
     * @return Đối tượng User hoặc null nếu không tìm thấy.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT id_nguoidung, ho_ten, email, ten_dang_nhap, mat_khau, vai_tro, ngay_tao FROM NguoiDung WHERE id_nguoidung = ?";
        User user = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs, false); // Không cần lấy mật khẩu
                }
            }
        }
        return user;
    }

    /**
     * Thêm một người dùng mới vào cơ sở dữ liệu.
     * @param user Đối tượng User chứa thông tin cần thêm (mật khẩu đã được hash).
     * @return true nếu thêm thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public boolean addUser(User user) throws SQLException {
        // Thêm cả ngày tạo nếu cột không tự động cập nhật (hoặc bỏ qua nếu có DEFAULT CURRENT_TIMESTAMP)
        String sql = "INSERT INTO NguoiDung (ho_ten, email, ten_dang_nhap, mat_khau, vai_tro) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getHoTen());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getTenDangNhap());
            pstmt.setString(4, user.getMatKhau()); // Mật khẩu đã hash từ Service
            pstmt.setString(5, user.getVaiTro());

            rowsAffected = pstmt.executeUpdate();
        }
        return rowsAffected > 0;
    }

    /**
     * Lấy danh sách tất cả người dùng.
     * @return List các đối tượng User.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT id_nguoidung, ho_ten, email, ten_dang_nhap, mat_khau, vai_tro, ngay_tao FROM NguoiDung ORDER BY ho_ten";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs, false)); // Không lấy mật khẩu cho danh sách
            }
        }
        return users;
    }

     /**
     * Xóa người dùng khỏi CSDL.
     * @param userId ID của người dùng cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi CSDL (có thể bao gồm lỗi ràng buộc nếu user đang mượn sách và FK không có ON DELETE CASCADE).
     */
    public boolean deleteUser(int userId) throws SQLException {
        // Lưu ý: Nếu bảng PhieuMuon có FK đến NguoiDung với ON DELETE CASCADE,
        // việc xóa user sẽ tự động xóa các phiếu mượn liên quan.
        // Nếu là ON DELETE RESTRICT hoặc không có, sẽ gây lỗi nếu user còn phiếu mượn.
        String sql = "DELETE FROM NguoiDung WHERE id_nguoidung = ?";
        int rowsAffected = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            rowsAffected = pstmt.executeUpdate();
        }
        return rowsAffected > 0;
    }


    // --- Các phương thức đếm cho thống kê ---

    public int countTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguoiDung";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countUsersByRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguoiDung WHERE vai_tro = ?";
         try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, role);
             try(ResultSet rs = pstmt.executeQuery()) {
                 if (rs.next()) {
                     return rs.getInt(1);
                 }
             }
        }
        return 0;
    }

     // --- Phương thức tiện ích để map ResultSet sang User ---
    private User mapResultSetToUser(ResultSet rs, boolean includePassword) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id_nguoidung"));
        user.setHoTen(rs.getString("ho_ten"));
        user.setEmail(rs.getString("email"));
        user.setTenDangNhap(rs.getString("ten_dang_nhap"));
        if (includePassword) {
            user.setMatKhau(rs.getString("mat_khau")); // Lấy mật khẩu đã hash
        }
        user.setVaiTro(rs.getString("vai_tro"));
        user.setNgayTao(rs.getTimestamp("ngay_tao"));
        return user;
    }

     // (Tùy chọn) Thêm phương thức updateUser nếu cần
     // public boolean updateUser(User user) throws SQLException { ... }
}

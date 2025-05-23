package com.nhom9.libraryapp.dao;

import com.nhom9.libraryapp.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private User mapResultSetToUser(ResultSet rs, boolean includePassword) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id_nguoidung"));
        user.setHoTen(rs.getString("ho_ten"));
        user.setEmail(rs.getString("email"));
        user.setTenDangNhap(rs.getString("ten_dang_nhap"));
        if (includePassword) {
            user.setMatKhau(rs.getString("mat_khau"));
        }
        user.setVaiTro(rs.getString("vai_tro"));
        user.setNgayTao(rs.getTimestamp("ngay_tao"));
        return user;
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM NguoiDung WHERE ten_dang_nhap = ?";
        User user = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs, true); 
                }
            }
        }
        return user;
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM NguoiDung WHERE email = ?";
        User user = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs, false); 
                }
            }
        }
        return user;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM NguoiDung WHERE id_nguoidung = ?";
        User user = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs, true); 
                }
            }
        }
        return user;
    }

    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO NguoiDung (ho_ten, email, ten_dang_nhap, mat_khau, vai_tro) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getHoTen());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getTenDangNhap());
            pstmt.setString(4, user.getMatKhau()); 
            pstmt.setString(5, user.getVaiTro());
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM NguoiDung ORDER BY ho_ten";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs, false)); 
            }
        }
        return users;
    }

    /**
     * Cập nhật thông tin người dùng (Họ tên, Email, Vai trò).
     * Tên đăng nhập và mật khẩu không được thay đổi qua phương thức này.
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE NguoiDung SET ho_ten = ?, email = ?, vai_tro = ? WHERE id_nguoidung = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getHoTen());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getVaiTro());
           
            pstmt.setInt(4, user.getId()); 
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM NguoiDung WHERE id_nguoidung = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public int countUsersByRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguoiDung WHERE vai_tro = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public int countTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguoiDung";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Tìm kiếm người dùng dựa trên một từ khóa (tìm trong họ tên, email, tên đăng nhập).
     */
    public List<User> searchUsers(String searchTerm) throws SQLException {
        String sql = "SELECT id_nguoidung, ho_ten, email, ten_dang_nhap, vai_tro, ngay_tao " +
                     "FROM NguoiDung " +
                     "WHERE ho_ten LIKE ? OR email LIKE ? OR ten_dang_nhap LIKE ? " +
                     "ORDER BY ho_ten";
        List<User> users = new ArrayList<>();
        String queryParam = "%" + (searchTerm != null ? searchTerm : "") + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, queryParam);
            pstmt.setString(2, queryParam);
            pstmt.setString(3, queryParam);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs, false));
                }
            }
        }
        return users;
    }
}

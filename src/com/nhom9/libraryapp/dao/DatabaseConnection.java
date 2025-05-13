package com.nhom9.libraryapp.dao; // Hoặc com.yourcompany.libraryapp.db_config

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp tiện ích để quản lý kết nối đến cơ sở dữ liệu MySQL.
 */
public class DatabaseConnection {

    // --- THAY ĐỔI CÁC THÔNG TIN NÀY CHO PHÙ HỢP ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_thu_vien?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"; // Thay đổi host, port, tên db nếu cần
    private static final String DB_USER = "root"; // Thay bằng username của bạn
    private static final String DB_PASSWORD = "15082003Aa"; // Thay bằng mật khẩu của bạn
    // --- KẾT THÚC PHẦN THAY ĐỔI ---

    // Tải driver MySQL một lần khi lớp được nạp
    static {
        try {
            // Đảm bảo driver đã được đăng ký (không bắt buộc với JDBC 4.0+)
             Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy MySQL JDBC Driver!");
            e.printStackTrace();
            // Ném một lỗi nghiêm trọng hơn để dừng ứng dụng nếu không có driver
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * Lấy một kết nối mới đến cơ sở dữ liệu.
     * Người gọi có trách nhiệm đóng kết nối này sau khi sử dụng (nên dùng try-with-resources).
     * @return Đối tượng Connection.
     * @throws SQLException Nếu có lỗi khi kết nối.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // (Không bắt buộc) Có thể thêm phương thức đóng kết nối an toàn nếu không dùng try-with-resources
    // public static void closeConnection(Connection conn) {
    //     if (conn != null) {
    //         try {
    //             conn.close();
    //         } catch (SQLException e) {
    //             System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
    //             e.printStackTrace();
    //         }
    //     }
    // }
    // public static void closeStatement(Statement stmt) { ... }
    // public static void closeResultSet(ResultSet rs) { ... }

     // Test connection (Optional main method for testing)
     public static void main(String[] args) {
         try (Connection conn = getConnection()) {
             if (conn != null) {
                 System.out.println("Kết nối đến cơ sở dữ liệu thành công!");
             } else {
                 System.out.println("Kết nối thất bại!");
             }
         } catch (SQLException e) {
             System.err.println("Lỗi kết nối: " + e.getMessage());
             e.printStackTrace();
         }
     }
}
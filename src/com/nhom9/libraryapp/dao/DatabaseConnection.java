package com.nhom9.libraryapp.dao; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_thu_vien?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"; 
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "15082003Aa"; 
    static {
        try {
            
             Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy MySQL JDBC Driver!");
            e.printStackTrace();
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
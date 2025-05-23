package com.nhom9.libraryapp.service;



import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.nhom9.libraryapp.dao.BookDao;
import com.nhom9.libraryapp.dao.LoanDao;
import com.nhom9.libraryapp.dao.UserDao;


public class ReportService {

    private BookDao bookDao;
    private LoanDao loanDao;
    private UserDao userDao;

    public ReportService() {
        this.bookDao = new BookDao();
        this.loanDao = new LoanDao();
        this.userDao = new UserDao();
    }

    /**
     * Lấy tất cả các số liệu thống kê cơ bản.
     * @return Một Map chứa các cặp key-value là tên thống kê và giá trị. Trả về Map rỗng nếu có lỗi.
     */
    public Map<String, Integer> getBasicStats() {
        Map<String, Integer> stats = new HashMap<>();
        try {
            // --- Lấy dữ liệu từ các DAO ---
            stats.put("totalBookTitles", bookDao.getTotalBookCount()); // Tổng số dòng sách (đầu sách)
            stats.put("totalBookCopies", bookDao.getTotalBookCopies()); // Tổng số bản sách (SUM so_luong_tong)
            stats.put("availableBookCopies", bookDao.getTotalAvailableCopies()); // Tổng số sách còn lại (SUM so_luong_con_lai)
            stats.put("borrowedBookCopies", loanDao.countActiveLoans()); // Số sách đang mượn
            stats.put("totalUsers", userDao.countTotalUsers());
            stats.put("adminUsers", userDao.countUsersByRole("admin"));
            stats.put("regularUsers", userDao.countUsersByRole("user"));
            // Thêm các thống kê khác nếu cần
            // stats.put("overdueLoans", loanDao.countOverdueLoans());
            // -----------------------------

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu thống kê: " + e.getMessage());
            e.printStackTrace();
            // Có thể trả về map rỗng hoặc một map chứa giá trị lỗi (-1)
            return new HashMap<>(); // Trả về rỗng khi lỗi
        }
        return stats;
    }


}
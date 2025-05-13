package com.nhom9.libraryapp.service;

import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.dao.UserDao;
import com.nhom9.libraryapp.util.PasswordUtil;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthService {

    private UserDao userDao;

    // Regex cho Email
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Helper method kiểm tra mật khẩu phía service
    private String getPasswordValidationError(String password) {
        if (password == null) return "Mật khẩu không được để trống.";
        if (password.length() < 8) return "- Phải có ít nhất 8 ký tự.";
        if (password.length() > 20) return "- Không được vượt quá 20 ký tự.";
        if (!Pattern.compile(".*[0-9].*").matcher(password).matches()) return "- Phải chứa ít nhất một chữ số.";
        if (!Pattern.compile(".*[a-z].*").matcher(password).matches()) return "- Phải chứa ít nhất một chữ cái thường.";
        if (!Pattern.compile(".*[A-Z].*").matcher(password).matches()) return "- Phải chứa ít nhất một chữ cái hoa.";
        if (!Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~].*").matcher(password).matches()) return "- Phải chứa ít nhất một ký tự đặc biệt.";
        return null; // Không có lỗi
    }

    public AuthService() {
        this.userDao = new UserDao();
    }

    public User login(String username, String plainPassword) {
        try {
            User user = userDao.getUserByUsername(username);
            if (user != null) {
                String hashedPasswordFromDb = user.getMatKhau(); // UserDao cần trả về mật khẩu hash
                if (hashedPasswordFromDb == null) { // Trường hợp user từ DB không có mật khẩu (dữ liệu lỗi)
                    System.err.println("Lỗi: Người dùng " + username + " không có mật khẩu trong CSDL.");
                    return null;
                }
                boolean passwordMatch = PasswordUtil.checkPassword(plainPassword, hashedPasswordFromDb);
                if (passwordMatch) {
                    user.setMatKhau(null); // Không trả về mật khẩu hash cho UI
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(User newUser) {
        // --- KIỂM TRA DỮ LIỆU ĐẦU VÀO PHÍA SERVICE ---
        if (newUser == null ||
            newUser.getTenDangNhap() == null || newUser.getTenDangNhap().trim().isEmpty() ||
            newUser.getEmail() == null || newUser.getEmail().trim().isEmpty() ||
            newUser.getMatKhau() == null || newUser.getMatKhau().isEmpty()) { // Mật khẩu gốc không được rỗng
            System.err.println("Service Error: Dữ liệu đăng ký không hợp lệ (thiếu thông tin bắt buộc).");
            return false;
        }

        // Kiểm tra Email
        Matcher emailMatcher = EMAIL_PATTERN.matcher(newUser.getEmail().trim());
        if (!emailMatcher.matches()) {
            System.err.println("Service Error: Định dạng email không hợp lệ: " + newUser.getEmail());
            return false;
        }

        // Kiểm tra Mật khẩu
        String passwordError = getPasswordValidationError(newUser.getMatKhau()); // Kiểm tra mật khẩu gốc
        if (passwordError != null) {
            System.err.println("Service Error: Mật khẩu không hợp lệ. " + passwordError);
            return false;
        }
        // ---------------------------------------------

        try {
            if (userDao.getUserByUsername(newUser.getTenDangNhap().trim()) != null) {
                System.err.println("Service Error: Tên đăng nhập '" + newUser.getTenDangNhap() + "' đã tồn tại.");
                return false;
            }
            if (userDao.getUserByEmail(newUser.getEmail().trim()) != null) {
                 System.err.println("Service Error: Email '" + newUser.getEmail() + "' đã tồn tại.");
                 return false;
            }

            String plainPassword = newUser.getMatKhau();
            String hashedPassword = PasswordUtil.hashPassword(plainPassword);
            newUser.setMatKhau(hashedPassword); // Cập nhật mật khẩu đã hash

            // Đảm bảo các trường khác không null nếu DB yêu cầu NOT NULL
            if(newUser.getHoTen() == null) newUser.setHoTen(""); // Hoặc xử lý theo yêu cầu


            return userDao.addUser(newUser);

        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi đăng ký: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) { // Bắt các lỗi khác như từ PasswordUtil
             System.err.println("Lỗi không xác định khi đăng ký: " + e.getMessage());
             e.printStackTrace();
             return false;
        }
    }
}
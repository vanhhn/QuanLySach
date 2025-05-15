package com.nhom9.libraryapp.service;

import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.dao.UserDao;
import com.nhom9.libraryapp.util.PasswordUtil;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthService { // Giữ tên AuthService nhưng giờ bao gồm cả quản lý User

    private UserDao userDao;
    private String lastErrorMessage; // Để lưu thông báo lỗi từ service

    // Regex cho Email
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Helper method kiểm tra mật khẩu
    private String getPasswordValidationError(String password) {
        if (password == null || password.isEmpty()) return "Mật khẩu không được để trống.";
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

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private void setLastErrorMessage(String message) {
        this.lastErrorMessage = message;
    }

    // --- Chức năng Xác thực ---
    public User login(String username, String plainPassword) {
        setLastErrorMessage(null);
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            setLastErrorMessage("Tên đăng nhập hoặc mật khẩu không được để trống.");
            System.err.println(getLastErrorMessage());
            return null;
        }
        try {
            User user = userDao.getUserByUsername(username.trim());
            if (user != null) {
                String hashedPasswordFromDb = user.getMatKhau();
                if (hashedPasswordFromDb == null) {
                    setLastErrorMessage("Lỗi: Người dùng " + username + " không có mật khẩu trong CSDL.");
                    System.err.println(getLastErrorMessage());
                    return null;
                }
                boolean passwordMatch = PasswordUtil.checkPassword(plainPassword, hashedPasswordFromDb);
                if (passwordMatch) {
                    user.setMatKhau(null);
                    return user;
                } else {
                    setLastErrorMessage("Tên đăng nhập hoặc mật khẩu không đúng!");
                }
            } else {
                 setLastErrorMessage("Tên đăng nhập hoặc mật khẩu không đúng!");
            }
        } catch (SQLException e) {
            setLastErrorMessage("Lỗi CSDL khi đăng nhập: " + e.getMessage());
            System.err.println(getLastErrorMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(User newUser) {
        setLastErrorMessage(null);
        if (newUser == null ||
            newUser.getTenDangNhap() == null || newUser.getTenDangNhap().trim().isEmpty() ||
            newUser.getEmail() == null || newUser.getEmail().trim().isEmpty() ||
            newUser.getMatKhau() == null || newUser.getMatKhau().isEmpty()) {
            setLastErrorMessage("Dữ liệu đăng ký không hợp lệ (thiếu thông tin bắt buộc).");
            System.err.println(getLastErrorMessage());
            return false;
        }

        String emailToValidate = newUser.getEmail().trim();
        Matcher emailMatcher = EMAIL_PATTERN.matcher(emailToValidate);
        if (!emailMatcher.matches()) {
            setLastErrorMessage("Định dạng email không hợp lệ: " + emailToValidate);
            System.err.println(getLastErrorMessage());
            return false;
        }
        newUser.setEmail(emailToValidate);

        String passwordError = getPasswordValidationError(newUser.getMatKhau());
        if (passwordError != null) {
            setLastErrorMessage("Mật khẩu không hợp lệ. " + passwordError);
            System.err.println(getLastErrorMessage());
            return false;
        }

        try {
            String trimmedUsername = newUser.getTenDangNhap().trim();
            newUser.setTenDangNhap(trimmedUsername);

            if (userDao.getUserByUsername(trimmedUsername) != null) {
                setLastErrorMessage("Tên đăng nhập '" + trimmedUsername + "' đã tồn tại.");
                System.err.println(getLastErrorMessage());
                return false;
            }
            if (userDao.getUserByEmail(emailToValidate) != null) {
                 setLastErrorMessage("Email '" + emailToValidate + "' đã tồn tại.");
                 System.err.println(getLastErrorMessage());
                 return false;
            }

            String plainPassword = newUser.getMatKhau();
            String hashedPassword = PasswordUtil.hashPassword(plainPassword);
            newUser.setMatKhau(hashedPassword);

            if(newUser.getHoTen() == null) newUser.setHoTen("");
            if(newUser.getVaiTro() == null) newUser.setVaiTro("user");


            return userDao.addUser(newUser);

        } catch (SQLException e) {
            setLastErrorMessage("Lỗi CSDL khi đăng ký: " + e.getMessage());
            System.err.println(getLastErrorMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
             setLastErrorMessage("Lỗi không xác định khi đăng ký: " + e.getMessage());
             System.err.println(getLastErrorMessage());
             e.printStackTrace();
             return false;
        }
    }

    // --- Chức năng Quản lý User (từ UserService cũ) ---
    public List<User> getAllUsers() {
        setLastErrorMessage(null);
        try {
            return userDao.getAllUsers();
        } catch (SQLException e) {
            setLastErrorMessage("Lỗi khi lấy danh sách người dùng: " + e.getMessage());
            System.err.println(getLastErrorMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public User getUserById(int userId) {
        setLastErrorMessage(null);
        try {
            return userDao.getUserById(userId);
        } catch (SQLException e) {
            setLastErrorMessage("Lỗi khi lấy người dùng theo ID " + userId + ": " + e.getMessage());
            System.err.println(getLastErrorMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateUserByAdmin(User userToUpdate) throws SQLException {
        setLastErrorMessage(null);
        if (userToUpdate == null || userToUpdate.getEmail() == null || userToUpdate.getEmail().trim().isEmpty()) {
            setLastErrorMessage("Dữ liệu cập nhật không hợp lệ (thiếu email).");
            System.err.println(getLastErrorMessage());
            return false;
        }
        String emailToValidate = userToUpdate.getEmail().trim();
        Matcher emailMatcher = EMAIL_PATTERN.matcher(emailToValidate);
        if (!emailMatcher.matches()) {
            setLastErrorMessage("Định dạng email không hợp lệ: " + emailToValidate);
            System.err.println(getLastErrorMessage());
            return false;
        }
        userToUpdate.setEmail(emailToValidate);

        User existingUserWithEmail = userDao.getUserByEmail(emailToValidate);
        if (existingUserWithEmail != null && existingUserWithEmail.getId() != userToUpdate.getId()) {
            setLastErrorMessage("Email '" + emailToValidate + "' đã được sử dụng bởi người dùng khác.");
            System.err.println(getLastErrorMessage());
            return false;
        }

        if ("user".equalsIgnoreCase(userToUpdate.getVaiTro())) {
            User currentUserInDb = userDao.getUserById(userToUpdate.getId());
            if (currentUserInDb != null && "admin".equalsIgnoreCase(currentUserInDb.getVaiTro())) {
                int adminCount = userDao.countUsersByRole("admin");
                if (adminCount <= 1) {
                    setLastErrorMessage("Không thể thay đổi vai trò của quản trị viên cuối cùng thành 'user'.");
                    System.err.println(getLastErrorMessage());
                    return false;
                }
            }
        }
        
        User existingUserForPassword = userDao.getUserById(userToUpdate.getId());
        if (existingUserForPassword != null) {
            userToUpdate.setMatKhau(existingUserForPassword.getMatKhau()); // Giữ nguyên mật khẩu đã hash
        } else {
            setLastErrorMessage("Không tìm thấy người dùng để cập nhật.");
            System.err.println(getLastErrorMessage());
            return false;
        }

        return userDao.updateUser(userToUpdate);
    }

    public boolean deleteUser(int userId /*, User currentAdmin */) throws SQLException {
        setLastErrorMessage(null);
        User userToDelete = userDao.getUserById(userId);
        if (userToDelete == null) {
            setLastErrorMessage("Không tìm thấy người dùng để xóa với ID: " + userId);
            System.err.println(getLastErrorMessage());
            return false;
        }

        if ("admin".equalsIgnoreCase(userToDelete.getVaiTro())) {
            int adminCount = userDao.countUsersByRole("admin");
            if (adminCount <= 1) {
                 setLastErrorMessage("Không thể xóa quản trị viên cuối cùng.");
                 System.err.println(getLastErrorMessage());
                 return false;
            }
        }
        // if (currentAdmin != null && currentAdmin.getId() == userId) {
        //     setLastErrorMessage("Bạn không thể tự xóa tài khoản của mình.");
        //     System.err.println(getLastErrorMessage());
        //     return false;
        // }
        return userDao.deleteUser(userId);
    }
    
    public List<User> searchUsers(String searchTerm) {
        setLastErrorMessage(null);
        try {
            if (searchTerm == null) searchTerm = "";
            return userDao.searchUsers(searchTerm.trim());
        } catch (SQLException e) {
            setLastErrorMessage("Lỗi khi tìm kiếm người dùng: " + e.getMessage());
            System.err.println(getLastErrorMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Chức năng này có thể trùng với register, nhưng giữ lại nếu logic khác biệt
    public boolean addUserByAdmin(User newUser) {
        setLastErrorMessage(null);
        if (newUser == null || newUser.getTenDangNhap() == null || newUser.getTenDangNhap().trim().isEmpty() ||
            newUser.getEmail() == null || newUser.getEmail().trim().isEmpty() ||
            newUser.getMatKhau() == null || newUser.getMatKhau().isEmpty() ||
            newUser.getVaiTro() == null || newUser.getVaiTro().trim().isEmpty() ) {
            setLastErrorMessage("Dữ liệu thêm người dùng bởi admin không hợp lệ (thiếu thông tin).");
            System.err.println(getLastErrorMessage());
            return false;
        }
        
        String emailToValidate = newUser.getEmail().trim();
        Matcher emailMatcher = EMAIL_PATTERN.matcher(emailToValidate);
        if (!emailMatcher.matches()) {
            setLastErrorMessage("Định dạng email không hợp lệ: " + emailToValidate);
            System.err.println(getLastErrorMessage());
            return false;
        }
        newUser.setEmail(emailToValidate);

        String passwordError = getPasswordValidationError(newUser.getMatKhau());
        if (passwordError != null) {
            setLastErrorMessage("Mật khẩu không hợp lệ: " + passwordError);
            System.err.println(getLastErrorMessage());
            return false;
        }
        
        try {
            String trimmedUsername = newUser.getTenDangNhap().trim();
            newUser.setTenDangNhap(trimmedUsername);

            if (userDao.getUserByUsername(trimmedUsername) != null) {
                setLastErrorMessage("Tên đăng nhập '" + trimmedUsername + "' đã tồn tại.");
                 System.err.println(getLastErrorMessage());
                return false;
            }
            if (userDao.getUserByEmail(emailToValidate) != null) {
                setLastErrorMessage("Email '" + emailToValidate + "' đã tồn tại.");
                System.err.println(getLastErrorMessage());
                return false;
            }

            newUser.setMatKhau(PasswordUtil.hashPassword(newUser.getMatKhau()));
            if(newUser.getHoTen() == null) newUser.setHoTen("");

            return userDao.addUser(newUser);
        } catch (SQLException e) {
            setLastErrorMessage("Lỗi CSDL khi admin thêm người dùng: " + e.getMessage());
            System.err.println(getLastErrorMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            setLastErrorMessage("Lỗi không xác định khi admin thêm người dùng: " + e.getMessage());
            System.err.println(getLastErrorMessage());
            e.printStackTrace();
            return false;
        }
    }
}

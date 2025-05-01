package com.nhom9.libraryapp.service;



import java.sql.SQLException; // Cần import SQLException nếu DAO có thể ném ra

import com.nhom9.libraryapp.dao.UserDao; // Import UserDao
import com.nhom9.libraryapp.model.User;
import com.nhom9.libraryapp.util.PasswordUtil; // Import PasswordUtil

/**
 * Lớp Service xử lý logic liên quan đến xác thực người dùng (Đăng nhập, Đăng ký).
 */
public class AuthService {

    private UserDao userDao;
    // PasswordUtil có thể là static hoặc được inject/tạo instance
    // private PasswordUtil passwordUtil;

    public AuthService() {
        // Khởi tạo DAO trực tiếp (cách đơn giản)
        this.userDao = new UserDao();
        // this.passwordUtil = new PasswordUtil(); // Nếu PasswordUtil không phải static
    }

    /**
     * Xử lý logic đăng nhập người dùng.
     * @param username Tên đăng nhập do người dùng nhập.
     * @param plainPassword Mật khẩu gốc do người dùng nhập.
     * @return Đối tượng User nếu đăng nhập thành công, null nếu thất bại.
     */
    public User login(String username, String plainPassword) {
        try {
            // --- Gọi DAO để lấy user theo username ---
            User user = userDao.getUserByUsername(username);
            // --------------------------------------

            if (user != null) {
                // --- Kiểm tra mật khẩu đã hash ---
                String hashedPasswordFromDb = user.getMatKhau(); // Lấy mật khẩu đã hash từ DB (UserDao cần trả về)
                // boolean passwordMatch = passwordUtil.checkPassword(plainPassword, hashedPasswordFromDb);
                boolean passwordMatch = PasswordUtil.checkPassword(plainPassword, hashedPasswordFromDb); // Giả sử dùng phương thức static

                if (passwordMatch) {
                    // Đăng nhập thành công, không trả về mật khẩu hash trong đối tượng User cho UI
                    user.setMatKhau(null);
                    return user;
                }
                // -----------------------------------
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đăng nhập: " + e.getMessage());
            // Xử lý lỗi CSDL (ví dụ: log lỗi, có thể trả về null hoặc ném Exception tùy chỉnh)
            e.printStackTrace(); // In lỗi ra console để debug
        }
        return null; // Đăng nhập thất bại (sai username, sai password, hoặc lỗi DB)
    }

    /**
     * Xử lý logic đăng ký người dùng mới.
     * @param newUser Đối tượng User chứa thông tin người dùng mới (mật khẩu là plain text).
     * @return true nếu đăng ký thành công, false nếu thất bại (ví dụ: username/email đã tồn tại, lỗi DB).
     */
    public boolean register(User newUser) {
        try {
            // --- Kiểm tra xem username hoặc email đã tồn tại chưa ---
            if (userDao.getUserByUsername(newUser.getTenDangNhap()) != null) {
                System.err.println("Lỗi đăng ký: Tên đăng nhập '" + newUser.getTenDangNhap() + "' đã tồn tại.");
                return false; // Username đã tồn tại
            }
            if (newUser.getEmail() != null && !newUser.getEmail().isEmpty() && userDao.getUserByEmail(newUser.getEmail()) != null) {
                 System.err.println("Lỗi đăng ký: Email '" + newUser.getEmail() + "' đã tồn tại.");
                 return false; // Email đã tồn tại
            }
            // ----------------------------------------------------

            // --- Mã hóa mật khẩu trước khi lưu ---
            String plainPassword = newUser.getMatKhau();
            // String hashedPassword = passwordUtil.hashPassword(plainPassword);
            String hashedPassword = PasswordUtil.hashPassword(plainPassword); // Giả sử dùng static
            newUser.setMatKhau(hashedPassword); // Cập nhật mật khẩu đã hash vào đối tượng User
            // -----------------------------------

            // --- Gọi DAO để thêm user mới vào DB ---
            return userDao.addUser(newUser);
            // -------------------------------------

        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi đăng ký: " + e.getMessage());
            e.printStackTrace();
            return false; // Đăng ký thất bại do lỗi DB
        } catch (Exception e) {
            // Bắt các lỗi khác, ví dụ lỗi từ PasswordUtil
             System.err.println("Lỗi không xác định khi đăng ký: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
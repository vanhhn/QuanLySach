package com.nhom9.libraryapp.util;

import org.mindrot.jbcrypt.BCrypt;

//import at.favre.lib.crypto.bcrypt.BCrypt;

/**
* Lớp tiện ích cung cấp các hàm để mã hóa và kiểm tra mật khẩu sử dụng thuật toán BCrypt.
*/
public class PasswordUtil {

 // Số vòng lặp (log rounds) cho BCrypt. Giá trị càng cao càng an toàn nhưng càng chậm.
 // 10-12 là giá trị phổ biến và cân bằng giữa an toàn và hiệu năng.
 private static final int BCRYPT_WORKLOAD = 12;

 /**
  * Mã hóa mật khẩu gốc sử dụng BCrypt.
  * Phương thức này tự động tạo salt mới cho mỗi lần gọi.
  *
  * @param plainPassword Mật khẩu gốc cần mã hóa.
  * @return Chuỗi hash của mật khẩu (bao gồm cả salt).
  * @throws IllegalArgumentException nếu mật khẩu đầu vào là null hoặc rỗng.
  */
 public static String hashPassword(String plainPassword) {
     if (plainPassword == null || plainPassword.isEmpty()) {
         throw new IllegalArgumentException("Mật khẩu không được để trống.");
     }
     // BCrypt.gensalt() tạo salt với workload đã định nghĩa
     String salt = BCrypt.gensalt(BCRYPT_WORKLOAD);
     // BCrypt.hashpw() thực hiện mã hóa
     return BCrypt.hashpw(plainPassword, salt);
 }

 /**
  * Kiểm tra xem mật khẩu gốc có khớp với mật khẩu đã được mã hóa (hash) bằng BCrypt hay không.
  *
  * @param plainPassword Mật khẩu gốc người dùng nhập vào.
  * @param hashedPassword Chuỗi hash mật khẩu lấy từ cơ sở dữ liệu (chuỗi này đã chứa salt).
  * @return true nếu mật khẩu khớp, false nếu không khớp hoặc có lỗi xảy ra.
  */
 public static boolean checkPassword(String plainPassword, String hashedPassword) {
     if (plainPassword == null || plainPassword.isEmpty() || hashedPassword == null || hashedPassword.isEmpty()) {
         // Không thể so sánh nếu một trong hai là rỗng
         return false;
     }

     try {
         // BCrypt.checkpw() tự động trích xuất salt từ hashedPassword để so sánh
         return BCrypt.checkpw(plainPassword, hashedPassword);
     } catch (IllegalArgumentException e) {
         // Có thể xảy ra nếu hashedPassword không đúng định dạng BCrypt
         System.err.println("Lỗi khi kiểm tra mật khẩu: Định dạng hash không hợp lệ. " + e.getMessage());
         return false;
     } catch (Exception e) {
         // Bắt các lỗi không mong muốn khác
          System.err.println("Lỗi không xác định khi kiểm tra mật khẩu: " + e.getMessage());
          e.printStackTrace();
          return false;
     }
 }

 // // Phương thức main để chạy thử nghiệm (có thể xóa hoặc comment lại sau)
 // public static void main(String[] args) {
 //     String originalPassword = "mysecretpassword";

 //     // Mã hóa mật khẩu
 //     String hashedPassword = hashPassword(originalPassword);
 //     System.out.println("Original Password: " + originalPassword);
 //     System.out.println("Hashed Password:   " + hashedPassword);
 //     System.out.println("Hash length:       " + hashedPassword.length()); // Thường là 60 ký tự

 //     // Kiểm tra mật khẩu đúng
 //     boolean isPasswordCorrect = checkPassword(originalPassword, hashedPassword);
 //     System.out.println("Checking correct password ('" + originalPassword + "'): " + isPasswordCorrect); // Expected: true

 //     // Kiểm tra mật khẩu sai
 //     String wrongPassword = "wrongpassword";
 //     boolean isPasswordWrong = checkPassword(wrongPassword, hashedPassword);
 //     System.out.println("Checking wrong password ('" + wrongPassword + "'):   " + isPasswordWrong);   // Expected: false

 //     // Kiểm tra với hash không hợp lệ
 //     String invalidHash = "thisisnotavalidbcrypthash";
 //     boolean isHashInvalid = checkPassword(originalPassword, invalidHash);
 //     System.out.println("Checking invalid hash ('" + invalidHash + "'): " + isHashInvalid); // Expected: false
 // }
}
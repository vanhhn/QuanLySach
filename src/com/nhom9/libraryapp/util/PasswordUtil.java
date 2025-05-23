package com.nhom9.libraryapp.util;

import org.mindrot.jbcrypt.BCrypt;




public class PasswordUtil {

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
         return false;
     }

     try {
         
         return BCrypt.checkpw(plainPassword, hashedPassword);
     } catch (IllegalArgumentException e) {
         System.err.println("Lỗi khi kiểm tra mật khẩu: Định dạng hash không hợp lệ. " + e.getMessage());
         return false;
     } catch (Exception e) {
         
          System.err.println("Lỗi không xác định khi kiểm tra mật khẩu: " + e.getMessage());
          e.printStackTrace();
          return false;
     }
 }

 
}
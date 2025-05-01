package com.nhom9.libraryapp.main;



import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.nhom9.libraryapp.ui.frame.LoginFrame; // Import lớp LoginFrame

/**
 * Lớp chính chứa phương thức main để khởi chạy ứng dụng Quản lý Thư viện.
 */
public class MainApp {

    /**
     * Phương thức chính của ứng dụng.
     * @param args Tham số dòng lệnh (không sử dụng trong ứng dụng này).
     */
    public static void main(String[] args) {
        // --- Thiết lập Look and Feel (Giao diện) ---
        // Cố gắng sử dụng Nimbus Look and Feel để giao diện trông hiện đại hơn.
        // Nếu không được, nó sẽ sử dụng Look and Feel mặc định của hệ thống hoặc cross-platform.
        try {
            // Tìm Look and Feel Nimbus
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break; // Tìm thấy Nimbus, thoát vòng lặp
                }
            }
            // Nếu không tìm thấy Nimbus, có thể thử dùng LookAndFeel của hệ thống
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Nếu có lỗi khi cài đặt Look and Feel (ví dụ: Nimbus không có sẵn),
            // Swing sẽ tự động sử dụng Look and Feel mặc định (Metal).
             System.err.println("Không thể cài đặt Nimbus Look and Feel, sử dụng giao diện mặc định.");
             // e.printStackTrace(); // Bỏ comment nếu muốn xem chi tiết lỗi
        }

        // --- Khởi chạy giao diện trên Event Dispatch Thread (EDT) ---
        // Điều này rất quan trọng để đảm bảo an toàn luồng (thread-safety) cho các thành phần Swing.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Tạo và hiển thị cửa sổ đăng nhập ban đầu
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
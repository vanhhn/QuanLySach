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
        
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break; 
                }
            }
          
        } catch (Exception e) {
           
             System.err.println("Không thể cài đặt Nimbus Look and Feel, sử dụng giao diện mặc định.");
             
        }

        
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
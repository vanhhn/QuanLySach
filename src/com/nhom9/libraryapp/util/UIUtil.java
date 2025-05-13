package com.nhom9.libraryapp.util;



import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class UIUtil {

    /**
     * Đặt icon cho một JFrame từ một đường dẫn resource trong classpath.
     *
     * @param frame      JFrame cần đặt icon.
     * @param iconPath   Đường dẫn đến file icon trong classpath (ví dụ: "/icons/app_icon.png").
     * @param callingClass Class được sử dụng để lấy resource (thường là class của frame).
     */
    public static void setFrameIcon(JFrame frame, String iconPath, Class<?> callingClass) {
        if (frame == null || iconPath == null || iconPath.trim().isEmpty() || callingClass == null) {
            System.err.println("Tham số không hợp lệ để đặt icon.");
            return;
        }
        try {
            URL iconURL = callingClass.getResource(iconPath);
            if (iconURL != null) {
                Image icon = Toolkit.getDefaultToolkit().getImage(iconURL);
                frame.setIconImage(icon);
            } else {
                System.err.println("Không tìm thấy file icon tại: " + iconPath + " (tương đối với classpath của " + callingClass.getName() + ")");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đặt icon cho frame '" + frame.getTitle() + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
}

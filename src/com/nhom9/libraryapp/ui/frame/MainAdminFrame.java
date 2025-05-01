package com.nhom9.libraryapp.ui.frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.nhom9.libraryapp.model.User;
// Import các panel
import com.nhom9.libraryapp.ui.panel.BookManagementPanel;
import com.nhom9.libraryapp.ui.panel.BookSearchPanel;
import com.nhom9.libraryapp.ui.panel.BorrowHistoryPanel;
import com.nhom9.libraryapp.ui.panel.BorrowedBooksPanel;
import com.nhom9.libraryapp.ui.panel.StatisticsPanel;
import com.nhom9.libraryapp.ui.panel.UserManagementPanel;

/**
 * Khung giao diện chính cho Quản trị viên (Admin).
 */
@SuppressWarnings("serial")
public class MainAdminFrame extends JFrame {

	private User currentAdmin; // Lưu thông tin admin đang đăng nhập
	private JTabbedPane tabbedPane;
	private JButton btnLogout;

	public MainAdminFrame(User adminUser) {
		if (!"admin".equalsIgnoreCase(adminUser.getVaiTro())) {
			// Biện pháp phòng ngừa nếu user thường cố tình vào frame admin
			throw new IllegalArgumentException("User is not an admin.");
		}
		this.currentAdmin = adminUser;

		setTitle("Hệ thống Quản lý Thư viện [ADMIN] - Xin chào, " + currentAdmin.getHoTen());
		setSize(900, 700); // Có thể lớn hơn frame user
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		initComponents();
		addEventListeners();
	}

	private void initComponents() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		tabbedPane = new JTabbedPane();

		// Tạo các Panel chức năng (cần được implement đầy đủ)
		// Placeholder Panels:
		JPanel bookManagementPanel = new BookManagementPanel(); // Ví dụ panel quản lý sách
		JPanel userManagementPanel = new UserManagementPanel(); // Ví dụ panel quản lý người dùng
		JPanel searchBorrowPanel = new BookSearchPanel(currentAdmin); // Admin cũng có thể tìm/mượn
		JPanel borrowedPanel = new BorrowedBooksPanel(currentAdmin); // Admin cũng có thể xem sách đang mượn (của mình)
		JPanel historyPanel = new BorrowHistoryPanel(currentAdmin); // Admin cũng có thể xem lịch sử (của mình)
		JPanel statisticsPanel = new StatisticsPanel(); // Ví dụ panel thống kê

		// Thêm các tab cho Admin
		tabbedPane.addTab("Quản Lý Sách", null, bookManagementPanel, "Thêm, sửa, xóa sách");
		tabbedPane.addTab("Quản Lý Người Dùng", null, userManagementPanel, "Quản lý tài khoản người dùng");
		tabbedPane.addTab("Tìm & Mượn Sách", null, searchBorrowPanel, "Tìm kiếm và mượn sách");
		tabbedPane.addTab("Sách Đang Mượn", null, borrowedPanel, "Xem và trả sách đang mượn");
		tabbedPane.addTab("Lịch Sử Mượn", null, historyPanel, "Xem lịch sử mượn trả sách");
		tabbedPane.addTab("Thống Kê", null, statisticsPanel, "Xem thống kê thư viện");

		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		// Panel dưới cùng chứa nút Logout
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnLogout = new JButton("Đăng xuất");
		bottomPanel.add(btnLogout);

		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		add(mainPanel);
	}

	private void addEventListeners() {
		btnLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(MainAdminFrame.this, "Bạn có chắc chắn muốn đăng xuất?",
						"Xác nhận Đăng xuất", JOptionPane.YES_NO_OPTION);

				if (confirm == JOptionPane.YES_OPTION) {
					dispose(); // Đóng cửa sổ hiện tại
					// Mở lại cửa sổ đăng nhập
					SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
				}
			}
		});
	}

	// // Phương thức main để chạy thử nghiệm Frame này
	// public static void main(String[] args) {
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// // Tạo một User admin giả để test
	// User testAdmin = new User();
	// testAdmin.setId(99);
	// testAdmin.setHoTen("Quản Trị Viên");
	// testAdmin.setVaiTro("admin");
	//
	// new MainAdminFrame(testAdmin).setVisible(true);
	// }
	// });
	// }
}

package com.nhom9.libraryapp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.nhom9.libraryapp.model.Book;
import com.nhom9.libraryapp.service.LibraryService;

/**
 * Dialog để thêm hoặc sửa thông tin sách.
 */
@SuppressWarnings("serial")
public class AddEditBookDialog extends JDialog {

    private Book bookToEdit;
    private boolean isEditMode;
    private LibraryService libraryService; // Service layer

    private JTextField txtTenSach;
    private JTextField txtTacGia;
    private JTextField txtTheLoai;
    private JSpinner spinnerSoLuongTong;
    private JButton btnSave;
    private JButton btnCancel;

    private boolean dataChanged = false;

    public AddEditBookDialog(Frame owner, Book bookToEdit) {
        super(owner, true);
        this.bookToEdit = bookToEdit;
        this.isEditMode = (bookToEdit != null);
        this.libraryService = new LibraryService(); // Khởi tạo Service

        setTitle(isEditMode ? "Chỉnh sửa thông tin sách" : "Thêm sách mới");
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initComponents();
        addEventListeners();

        if (isEditMode) {
            populateFields();
        }

        pack();
    }

    // initComponents() giữ nguyên như trước
     private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Tên sách ---
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên sách (*):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtTenSach = new JTextField(25);
        formPanel.add(txtTenSach, gbc);

        // --- Tác giả ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Tác giả:"), gbc);
        gbc.gridx = 1;
        txtTacGia = new JTextField();
        formPanel.add(txtTacGia, gbc);

        // --- Thể loại ---
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Thể loại:"), gbc);
        gbc.gridx = 1;
        txtTheLoai = new JTextField();
        formPanel.add(txtTheLoai, gbc);

        // --- Số lượng tổng ---
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Số lượng tổng (*):"), gbc);
        gbc.gridx = 1;
        SpinnerNumberModel numberModel = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
        spinnerSoLuongTong = new JSpinner(numberModel);
        spinnerSoLuongTong.setPreferredSize(new Dimension(80, spinnerSoLuongTong.getPreferredSize().height));
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(spinnerSoLuongTong, gbc);

        // --- Panel chứa nút ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnSave = new JButton(isEditMode ? "Cập nhật" : "Thêm mới");
        btnCancel = new JButton("Hủy bỏ");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        // --- Thêm các panel vào Dialog ---
        setLayout(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    // populateFields() giữ nguyên như trước
     private void populateFields() {
        if (bookToEdit != null) {
            txtTenSach.setText(bookToEdit.getTenSach());
            txtTacGia.setText(bookToEdit.getTacGia());
            txtTheLoai.setText(bookToEdit.getTheLoai());
            spinnerSoLuongTong.setValue(bookToEdit.getSoLuongTong());
        }
    }

    // addEventListeners() giữ nguyên như trước
    private void addEventListeners() {
        btnSave.addActionListener(e -> saveBook());
        btnCancel.addActionListener(e -> dispose());
    }

    // Xử lý lưu thông tin sách - Cập nhật để gọi Service
    private void saveBook() {
        // --- Validate dữ liệu ---
        String tenSach = txtTenSach.getText().trim();
        int soLuongTong = (Integer) spinnerSoLuongTong.getValue();

        if (tenSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sách không được để trống!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtTenSach.requestFocus();
            return;
        }
        if (soLuongTong < 0) {
            JOptionPane.showMessageDialog(this, "Số lượng tổng không được là số âm!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            spinnerSoLuongTong.requestFocus();
            return;
        }

        // --- Tạo đối tượng Book từ dữ liệu nhập ---
        Book bookData = new Book();
        bookData.setTenSach(tenSach);
        bookData.setTacGia(txtTacGia.getText().trim());
        bookData.setTheLoai(txtTheLoai.getText().trim());
        bookData.setSoLuongTong(soLuongTong);
        // Số lượng còn lại sẽ được xử lý trong Service

        // Xử lý logic lưu
        boolean success = false;
        try {
            if (isEditMode) {
                // --- Cập nhật sách ---
                bookData.setId(bookToEdit.getId()); // Đặt ID cho sách cần cập nhật
                System.out.println("Attempting to update book: ID=" + bookData.getId());
                success = libraryService.updateBook(bookData); // Gọi service cập nhật
            } else {
                // --- Thêm sách mới ---
                System.out.println("Attempting to add new book: " + bookData.getTenSach());
                success = libraryService.addBook(bookData); // Gọi service thêm mới
            }

            if (success) {
                dataChanged = true; // Đánh dấu thành công
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Cập nhật thông tin sách thành công!" : "Thêm sách mới thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Đóng dialog
            } else {
                // Service trả về false có thể do lỗi logic (ví dụ: không thể giảm SL tổng < SL đang mượn)
                 JOptionPane.showMessageDialog(this,
                        (isEditMode ? "Cập nhật sách thất bại!" : "Thêm sách mới thất bại!") + "\nKiểm tra lại thông tin hoặc số lượng sách.",
                        "Lỗi Lưu Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) { // Bắt các lỗi khác
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // isDataChanged() giữ nguyên như trước
     public boolean isDataChanged() {
        return dataChanged;
    }
}
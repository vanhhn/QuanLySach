package com.nhom9.libraryapp.model; // Hoặc package model của bạn

import java.sql.Date;

/**
 * Lớp chứa thông tin chi tiết của một phiếu mượn, bao gồm cả tên sách.
 * Có thể kế thừa Loan hoặc chứa các thuộc tính riêng.
 */
public class LoanDetail extends Loan { // Kế thừa Loan để có sẵn các trường của Loan

    private String bookTitle; // Thêm trường tên sách

    // Constructors
    public LoanDetail() {
        super(); // Gọi constructor của lớp cha (Loan)
    }

    // Constructor để dễ dàng tạo đối tượng từ dữ liệu lấy được (bao gồm cả tên sách)
    public LoanDetail(int id, int userId, int bookId, Date ngayMuon, Date ngayTraDuKien, Date ngayTraThucTe, String trangThai, String bookTitle) {
        super(id, userId, bookId, ngayMuon, ngayTraDuKien, ngayTraThucTe, trangThai); // Gọi constructor đầy đủ của Loan
        this.bookTitle = bookTitle;
    }

    // Getter and Setter cho bookTitle
    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Override
    public String toString() {
        // Ghi đè toString để bao gồm cả tên sách
        return "LoanDetail{" +
               "loanInfo=" + super.toString() + // Lấy thông tin từ Loan.toString()
               ", bookTitle='" + bookTitle + '\'' +
               '}';
    }
}
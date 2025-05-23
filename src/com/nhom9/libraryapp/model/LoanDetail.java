package com.nhom9.libraryapp.model;
import java.sql.Date;


public class LoanDetail extends Loan { 

    private String bookTitle; 

    // Constructors
    public LoanDetail() {
        super();
    }

   
    public LoanDetail(int id, int userId, int bookId, Date ngayMuon, Date ngayTraDuKien, Date ngayTraThucTe, String trangThai, String bookTitle) {
        super(id, userId, bookId, ngayMuon, ngayTraDuKien, ngayTraThucTe, trangThai); 
        this.bookTitle = bookTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Override
    public String toString() {
        return "LoanDetail{" +
               "loanInfo=" + super.toString() + 
               ", bookTitle='" + bookTitle + '\'' +
               '}';
    }
}
package com.nhom9.libraryapp.model;

import java.sql.Date;

public class Loan {
	private int id;
	private int userId; 
	private int bookId; 
	private Date ngayMuon;
	private Date ngayTraDuKien;
	private Date ngayTraThucTe; 
	private String trangThai; 

	public Loan() {
	}

	public Loan(int userId, int bookId, Date ngayMuon, Date ngayTraDuKien, String trangThai) {
		this.userId = userId;
		this.bookId = bookId;
		this.ngayMuon = ngayMuon;
		this.ngayTraDuKien = ngayTraDuKien;
		this.trangThai = trangThai;
		this.ngayTraThucTe = null; 
	}

	public Loan(int id, int userId, int bookId, Date ngayMuon, Date ngayTraDuKien, Date ngayTraThucTe,
			String trangThai) {
		this.id = id;
		this.userId = userId;
		this.bookId = bookId;
		this.ngayMuon = ngayMuon;
		this.ngayTraDuKien = ngayTraDuKien;
		this.ngayTraThucTe = ngayTraThucTe;
		this.trangThai = trangThai;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public Date getNgayMuon() {
		return ngayMuon;
	}

	public void setNgayMuon(Date ngayMuon) {
		this.ngayMuon = ngayMuon;
	}

	public Date getNgayTraDuKien() {
		return ngayTraDuKien;
	}

	public void setNgayTraDuKien(Date ngayTraDuKien) {
		this.ngayTraDuKien = ngayTraDuKien;
	}

	public Date getNgayTraThucTe() {
		return ngayTraThucTe;
	}

	public void setNgayTraThucTe(Date ngayTraThucTe) {
		this.ngayTraThucTe = ngayTraThucTe;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
}

package com.nhom9.libraryapp.model;

import java.sql.Timestamp;

public class Book {
	private int id;
	private String tenSach;
	private String tacGia;
	private String theLoai;
	private int soLuongTong;
	private int soLuongConLai;
	private Timestamp ngayNhap;

	public Book() {
	}

	public Book(String tenSach, String tacGia, String theLoai, int soLuongTong, int soLuongConLai) {
		this.tenSach = tenSach;
		this.tacGia = tacGia;
		this.theLoai = theLoai;
		this.soLuongTong = soLuongTong;
		this.soLuongConLai = soLuongConLai;
	}

	public Book(int id, String tenSach, String tacGia, String theLoai, int soLuongTong, int soLuongConLai,
			Timestamp ngayNhap) {
		this.id = id;
		this.tenSach = tenSach;
		this.tacGia = tacGia;
		this.theLoai = theLoai;
		this.soLuongTong = soLuongTong;
		this.soLuongConLai = soLuongConLai;
		this.ngayNhap = ngayNhap;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTenSach() {
		return tenSach;
	}

	public void setTenSach(String tenSach) {
		this.tenSach = tenSach;
	}

	public String getTacGia() {
		return tacGia;
	}

	public void setTacGia(String tacGia) {
		this.tacGia = tacGia;
	}

	public String getTheLoai() {
		return theLoai;
	}

	public void setTheLoai(String theLoai) {
		this.theLoai = theLoai;
	}

	public int getSoLuongTong() {
		return soLuongTong;
	}

	public void setSoLuongTong(int soLuongTong) {
		this.soLuongTong = soLuongTong;
	}

	public int getSoLuongConLai() {
		return soLuongConLai;
	}

	public void setSoLuongConLai(int soLuongConLai) {
		this.soLuongConLai = soLuongConLai;
	}

	public Timestamp getNgayNhap() {
		return ngayNhap;
	}

	public void setNgayNhap(Timestamp ngayNhap) {
		this.ngayNhap = ngayNhap;
	}

}

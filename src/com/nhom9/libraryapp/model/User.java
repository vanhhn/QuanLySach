package com.nhom9.libraryapp.model;

import java.sql.Timestamp;

public class User {
	private int id;
	private String hoTen;
	private String email;
	private String tenDangNhap;
	private String matKhau;
	private String vaiTro;
	private Timestamp ngayTao;

	public User() {
	}

	public User(String hoTen, String email, String tenDangNhap, String matKhau, String vaiTro) {
		this.hoTen = hoTen;
		this.email = email;
		this.tenDangNhap = tenDangNhap;
		this.matKhau = matKhau; 
		this.vaiTro = vaiTro;
	}

	public User(int id, String hoTen, String email, String tenDangNhap, String vaiTro, Timestamp ngayTao) {
		this.id = id;
		this.hoTen = hoTen;
		this.email = email;
		this.tenDangNhap = tenDangNhap;
		this.matKhau = null;
		this.vaiTro = vaiTro;
		this.ngayTao = ngayTao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHoTen() {
		return hoTen;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTenDangNhap() {
		return tenDangNhap;
	}

	public void setTenDangNhap(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}

	public String getMatKhau() {
		return matKhau;
	}

	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}

	public String getVaiTro() {
		return vaiTro;
	}

	public void setVaiTro(String vaiTro) {
		this.vaiTro = vaiTro;
	}

	public Timestamp getNgayTao() {
		return ngayTao;
	}

	public void setNgayTao(Timestamp ngayTao) {
		this.ngayTao = ngayTao;
	}

}

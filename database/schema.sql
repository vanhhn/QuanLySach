CREATE DATABASE IF NOT EXISTS db_thu_vien;
USE db_thu_vien;

CREATE TABLE NguoiDung (
    id_nguoidung INT AUTO_INCREMENT PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL,
    mat_khau VARCHAR(255) NOT NULL, -- Store hashed password
    vai_tro ENUM('admin', 'user') NOT NULL DEFAULT 'user',
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Sach (
    id_sach INT AUTO_INCREMENT PRIMARY KEY,
    ten_sach VARCHAR(255) NOT NULL,
    tac_gia VARCHAR(150),
    the_loai VARCHAR(100),
    so_luong_tong INT NOT NULL DEFAULT 0,
    so_luong_con_lai INT NOT NULL DEFAULT 0,
    ngay_nhap TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ten_sach (ten_sach),
    INDEX idx_tac_gia (tac_gia),
    INDEX idx_the_loai (the_loai),
    CONSTRAINT chk_so_luong CHECK (so_luong_con_lai >= 0 AND so_luong_con_lai <= so_luong_tong)
);

CREATE TABLE PhieuMuon (
    id_phieu_muon INT AUTO_INCREMENT PRIMARY KEY,
    id_nguoidung INT NOT NULL,
    id_sach INT NOT NULL,
    ngay_muon DATE NOT NULL,
    ngay_tra_du_kien DATE,
    ngay_tra_thuc_te DATE NULL,
    trang_thai ENUM('đang mượn', 'đã trả', 'quá hạn') NOT NULL DEFAULT 'đang mượn',
    FOREIGN KEY (id_nguoidung) REFERENCES NguoiDung(id_nguoidung) ON DELETE CASCADE, -- Or ON DELETE RESTRICT
    FOREIGN KEY (id_sach) REFERENCES Sach(id_sach) ON DELETE RESTRICT, -- Prevent deleting book if borrowed
    INDEX idx_ngay_muon (ngay_muon),
    INDEX idx_trang_thai (trang_thai)
);

-- You might want to create an initial admin user
-- Remember to use a strong HASHED password in practice
-- INSERT INTO NguoiDung (ho_ten, email, ten_dang_nhap, mat_khau, vai_tro)
-- VALUES ('Adminstrator', 'admin@example.com', 'admin', 'hashed_password_here', 'admin');
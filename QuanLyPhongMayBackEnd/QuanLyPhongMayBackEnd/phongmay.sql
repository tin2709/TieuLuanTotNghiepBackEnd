SELECT * FROM phongmay.quyen;
USE phongmay;
CREATE database phongmay
DROP database phongmay
-- Insert data into `quyen`
INSERT INTO quyen (ten_quyen) VALUES
('Admin'),
('Teacher'),
('Employee');

-- Insert data into `chuc_vu`
INSERT INTO chuc_vu (ten_cv) VALUES
('Giảng viên'),
('Nhân viên'),
('Quản lý');

-- Insert data into `tai_khoan`
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ma_quyen, email, image) VALUES
('admin', 'admin123', 1, 'admin@example.com', 'admin.jpg'),
('user', 'user123', 2, 'user@example.com', 'user.jpg');

-- Insert data into `khoa`
INSERT INTO khoa (ten_khoa) VALUES
('Khoa Công Nghệ Thông Tin'),
('Khoa Công nghệ Hóa học');

-- Insert data into `toa_nha`
INSERT INTO toa_nha (ten_toanha) VALUES
('Tòa nhà A'),
('Tòa nhà B');

-- Insert data into `tang`
INSERT INTO tang (ten_tang, ma_toanha) VALUES
('Tầng 1', 1),
('Tầng 2', 1);

-- Insert data into `phong_may`
INSERT INTO phong_may (ten_phong, so_may,so_thiet_bi, mo_ta, trang_thai, ma_tang) 
VALUES 
('Phòng P01', 41, 7, 'Phòng máy tính cho môn CNTT', 'Trống', 1),
('Phòng P02', 41, 7, 'Phòng máy tính cho môn CNTT', 'Đang có tiết', 1),
('Phòng P03', 41, 7, 'Phòng máy tính cho môn CNTT', 'Không thể dùng', 1),
('Phòng P04', 41, 7, 'Phòng máy tính cho môn CNTT', 'Trống', 1),
('Phòng P05', 41, 7, 'Phòng máy tính cho môn CNTT', 'Đang có tiết', 1),
('Phòng P06', 41, 7, 'Phòng máy tính cho môn CNTT', 'Không thể dùng', 1);




-- Insert data into `mon_hoc`
INSERT INTO mon_hoc (ten_mon, ngay_bat_dau, ngay_ket_thuc, so_buoi) 
VALUES ('Chuyên đề web', '2025-02-24', '2025-04-28', 10),
       ('Máy học', '2025-02-27', '2025-05-01', 10);

-- Insert data into `giao_vien`
INSERT INTO giao_vien (ho_ten, so_dien_thoai, email, hoc_vi, ma_tk, ma_khoa)
VALUES 
('Nguyen Thi Lan', '0987654321', 'nguyenlan@example.com', 'Tiến sĩ', 1, 1),
('Tran Minh Quang', '0912345678', 'tranminhquang@example.com', 'Thạc sĩ', 2, 2)



-- Insert data into `ca_thuc_hanh`
INSERT INTO ca_thuc_hanh (ten_ca, ngay_thuc_hanh, tiet_bat_dau, tiet_ket_thuc, buoi_so, ma_giao_vien, ma_phong, ma_mon) 
VALUES 
('Ca 2', '2024-03-31', 4, 6, 7, 1, 6, 1),
('Ca 2', '2024-04-07', 4, 6, 8, 1, 6, 1),
('Ca 4', '2024-03-27', 10, 12, 6, 3, 6, 2),
('Ca 4', '2024-04-03', 10, 12, 7, 3, 6, 2),
('Ca 4', '2024-04-10', 10, 12, 8, 3, 6, 2),
SELECT * FROM ca_thuc_hanh where ten_ca like 'Ca sáng'

-- Insert data into `may_tinh`
INSERT INTO may_tinh (trang_thai, mo_ta, ngay_lap_dat, ma_phong) 
VALUES 
('Đang hoạt động', 'Máy tính cấu hình cao', '2025-03-01', 20),
('Đang hoạt động', 'Máy tính cấu hình trung bình', '2025-03-02',20),
('Đang hoạt động', 'Máy tính cấu hình thấp', '2025-03-03', 20);

-- Insert data into `ghi_chu_may_tinh`
INSERT INTO ghi_chu_may_tinh (noi_dung, ma_may, ngay_bao_loi, ngay_sua, matk_bao_loi, matk_sua_loi) 
VALUES 
('Máy bị lỗi màn hình', 5, '2025-03-01', '2025-03-02', 'admin', 'user');

-- 15. Insert vào bảng ghi_chu_phong_may
INSERT INTO ghi_chu_phong_may (noi_dung, ma_phong, ngay_bao_loi, ngay_sua, matk_bao_loi, matk_sua_loi) 
VALUES 
('Phòng bị mất điện', 12, '2025-03-01', '2025-03-02', 'admin', 'user');

-- Insert data into `nhan_vien`
INSERT INTO nhan_vien (ten_nv, email, sdt, ma_cv, ma_tk) 
VALUES 
('Lê Văn C', 'lvanc@example.com', '0123456789', 1, 1),
('Phan Thị D', 'pthid@example.com', '0987654321', 2, 2);

-- 13. Insert vào bảng lich_truc
INSERT INTO lich_truc (ngay_truc, thoi_gian_bat_dau, thoi_gian_ket_thuc, ma_nv, ma_tang) 
VALUES 
('2025-03-05', '08:00', '12:00', 1, 1),
('2025-03-06', '13:00', '17:00', 2, 2);
SELECT * FROM phongmay.quyen;
CREATE TABLE GiaoVien (
    maGiaoVien BIGINT PRIMARY KEY AUTO_INCREMENT,
    hoTen VARCHAR(255) NOT NULL
);
CREATE TABLE CaThucHanh (
    maCaThucHanh BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenCa VARCHAR(255) NOT NULL,
    ngayThucHanh DATE NOT NULL,
    tietBatDau INT NOT NULL,
    tietKetThuc INT NOT NULL,
    buoiSo INT NOT NULL,
    maGiaoVien BIGINT,
    maPhong BIGINT,
    maMon BIGINT,
    FOREIGN KEY (maGiaoVien) REFERENCES GiaoVien(maGiaoVien),
    FOREIGN KEY (maPhong) REFERENCES PhongMay(maPhong),
    FOREIGN KEY (maMon) REFERENCES MonHoc(maMon)
);
CREATE TABLE ghi_chu_may_tinh (
    ma_ghichuMT BIGINT PRIMARY KEY AUTO_INCREMENT,
    noi_dung NVARCHAR(2500),
    ma_may BIGINT,
    ngay_bao_loi DATE,
    ngay_sua DATE,
    matk_bao_loi VARCHAR(255),
    matk_sua_loi VARCHAR(255),
    FOREIGN KEY (ma_may) REFERENCES may_tinh(ma_may) -- Giả sử bảng `may_tinh` đã tồn tại
);
CREATE TABLE ghi_chu_phong_may (
    ma_ghichu BIGINT PRIMARY KEY AUTO_INCREMENT,
    noi_dung NVARCHAR(2500),
    ma_phong BIGINT,
    ngay_bao_loi DATE,
    ngay_sua DATE,
    matk_bao_loi VARCHAR(255),
    matk_sua_loi VARCHAR(255),
    FOREIGN KEY (ma_phong) REFERENCES phong_may(ma_phong) -- Giả sử bảng `phong_may` đã tồn tại
);
CREATE TABLE khoa (
    ma_khoa BIGINT PRIMARY KEY AUTO_INCREMENT,
    ten_khoa NVARCHAR(255)
);
CREATE TABLE chuc_vu (
    ma_cv BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_cv NVARCHAR(50) NOT NULL
);


CREATE TABLE lich_truc (
    ma_lich BIGINT AUTO_INCREMENT PRIMARY KEY,
    ngay_truc DATE,
    thoi_gian_bat_dau NVARCHAR(255),
    thoi_gian_ket_thuc NVARCHAR(255),
    ma_nv BIGINT,
    ma_tang BIGINT,
    FOREIGN KEY (ma_nv) REFERENCES nhan_vien(ma_nv),
    FOREIGN KEY (ma_tang) REFERENCES tang(ma_tang)
);
CREATE TABLE nhan_vien (
    ma_nv BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_nv NVARCHAR(100) NOT NULL,
    email NVARCHAR(100),
    sdt VARCHAR(10),
    ma_cv BIGINT NOT NULL,
    ma_tk BIGINT NOT NULL,
    FOREIGN KEY (ma_cv) REFERENCES chuc_vu(ma_cv),
    FOREIGN KEY (ma_tk) REFERENCES tai_khoan(ma_tk)
);

CREATE TABLE quyen (
    ma_quyen BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_quyen NVARCHAR(50) NOT NULL
);

CREATE TABLE tai_khoan (
    ma_tk BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_dang_nhap NVARCHAR(255) UNIQUE NOT NULL,
    mat_khau NVARCHAR(255) NOT NULL,
    ma_quyen BIGINT NOT NULL,
    email NVARCHAR(255) UNIQUE NOT NULL,
    image NVARCHAR(255),
    FOREIGN KEY (ma_quyen) REFERENCES quyen(ma_quyen)
);

CREATE TABLE phong_may (
    ma_phong BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_phong NVARCHAR(255) NOT NULL,
    ma_khoa BIGINT,
    FOREIGN KEY (ma_khoa) REFERENCES khoa(ma_khoa)
);
CREATE TABLE toa_nha (
    ma_toanha BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_toanha NVARCHAR(50) NOT NULL
);

CREATE TABLE tang (
    ma_tang BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_tang NVARCHAR(50) NOT NULL,
    ma_toanha BIGINT,
    FOREIGN KEY (ma_toanha) REFERENCES toa_nha(ma_toanha)
);
CREATE TABLE mon_hoc (
    ma_mon BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_mon NVARCHAR(100) NOT NULL,
    ngay_bat_dau DATE NOT NULL,
    ngay_ket_thuc DATE,
    so_buoi INT NOT NULL
);
CREATE TABLE MayTinh (
    ma_may BIGINT AUTO_INCREMENT PRIMARY KEY,
    trang_thai NVARCHAR(50) DEFAULT N'Đang hoạt động' CHECK (trang_thai IN (N'Đã hỏng', N'Đang hoạt động')),
    mo_ta NVARCHAR(255),
    ngay_lap_dat DATE,
    ma_phong BIGINT,
    FOREIGN KEY (ma_phong) REFERENCES PhongMay(ma_phong)
);
CREATE TABLE PhongMay (
    ma_phong BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_phong NVARCHAR(255) NOT NULL,
    so_may INT CHECK (so_may >= 0) NOT NULL,
    mo_ta NVARCHAR(255),
    trang_thai NVARCHAR(50) DEFAULT N'Trống' CHECK (trang_thai IN (N'Trống', N'Đang có tiết', N'Không thể dùng')),
    ma_tang BIGINT NOT NULL,
    FOREIGN KEY (ma_tang) REFERENCES Tang(ma_tang)
);
SELECT CONSTRAINT_NAME
FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_NAME = 'may_tinh' AND CONSTRAINT_TYPE = 'CHECK';

ALTER TABLE may_tinh
    DROP CONSTRAINT IF EXISTS trang_thai_check,
    ADD CONSTRAINT trang_thai_check CHECK (trang_thai IN ('Đã hỏng', 'Đang hoạt động', 'Không hoạt động'));

SELECT *
FROM phong_may
WHERE trang_thai = N'Trống';
INSERT INTO may_tinh (ten_may, trang_thai, mo_ta, ma_phong) VALUES
(N'Máy 1', N'Không hoạt động', N'gv', 1),
(N'Máy 2', N'Đang hoạt động', N'hs', 1),
(N'Máy 3', N'Đang hoạt động', N'hs', 1),
(N'Máy 4', N'Đang hoạt động', N'hs', 1),
(N'Máy 5', N'Đang hoạt động', N'hs', 1),
(N'Máy 6', N'Đang hoạt động', N'hs', 1),
(N'Máy 7', N'Đang hoạt động', N'hs', 1),
(N'Máy 8', N'Đang hoạt động', N'hs', 1),
(N'Máy 9', N'Đang hoạt động', N'hs', 1),
(N'Máy 10', N'Đang hoạt động', N'hs', 1),
(N'Máy 11', N'Đang hoạt động', N'hs', 1),
(N'Máy 12', N'Đang hoạt động', N'hs', 1),
(N'Máy 13', N'Đang hoạt động', N'hs', 1),
(N'Máy 14', N'Đang hoạt động', N'hs', 1),
(N'Máy 15', N'Đang hoạt động', N'hs', 1),
(N'Máy 16', N'Đang hoạt động', N'hs', 1),
(N'Máy 17', N'Đang hoạt động', N'hs', 1),
(N'Máy 18', N'Đang hoạt động', N'hs', 1),
(N'Máy 19', N'Đang hoạt động', N'hs', 1),
(N'Máy 20', N'Đang hoạt động', N'hs', 1),
(N'Máy 21', N'Đang hoạt động', N'hs', 1),
(N'Máy 22', N'Đang hoạt động', N'hs', 1),
(N'Máy 23', N'Đang hoạt động', N'hs', 1),
(N'Máy 24', N'Đang hoạt động', N'hs', 1),
(N'Máy 25', N'Đang hoạt động', N'hs', 1),
(N'Máy 26', N'Đang hoạt động', N'hs', 1),
(N'Máy 27', N'Đang hoạt động', N'hs', 1),
(N'Máy 28', N'Đang hoạt động', N'hs', 1),
(N'Máy 29', N'Đang hoạt động', N'hs', 1),
(N'Máy 30', N'Đang hoạt động', N'hs', 1),
(N'Máy 31', N'Đang hoạt động', N'hs', 1),
(N'Máy 32', N'Đang hoạt động', N'hs', 1),
(N'Máy 33', N'Đang hoạt động', N'hs', 1),
(N'Máy 34', N'Đang hoạt động', N'hs', 1),
(N'Máy 35', N'Đang hoạt động', N'hs', 1),
(N'Máy 36', N'Đang hoạt động', N'hs', 1),
(N'Máy 37', N'Đang hoạt động', N'hs', 1),
(N'Máy 38', N'Đang hoạt động', N'hs', 1),
(N'Máy 39', N'Đang hoạt động', N'hs', 1),
(N'Máy 40', N'Đang hoạt động', N'hs', 1),
(N'Máy 41', N'Đang hoạt động', N'hs', 1);
INSERT INTO may_tinh (ten_may, trang_thai, mo_ta, ma_phong) VALUES
(N'Máy 1', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'gv', 3),
(N'Máy 2', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 3', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 4', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 5', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 6', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 7', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 8', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 9', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 10', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 11', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 12', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 13', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 14', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 15', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 16', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 17', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 18', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 19', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 20', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 21', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 22', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 23', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 24', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 25', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 26', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 27', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 28', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 29', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 30', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 31', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 32', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 33', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 34', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 35', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 36', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 37', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 38', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 39', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 40', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3),
(N'Máy 41', ELT(FLOOR(RAND() * 3) + 1, N'Đã hỏng', N'Đang hoạt động', N'Không hoạt động'), N'hs', 3);
UPDATE may_tinh
SET ngay_lap_dat = NOW()
WHERE ma_phong = 1 AND ten_may LIKE 'Máy %';

package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.LichTruc;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.entity.ToaNha;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TangRepository;
import com.example.QuanLyPhongMayBackEnd.repository.ToaNhaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReader;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TangService {

    @Autowired
    private PhongMayRepository phongMayRepository;

    @Autowired
    private TangRepository tangRepository;
    @Autowired
    private  LichTrucService lichTrucService;


    @Autowired
    private PhongMayService phongMayService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private ToaNhaRepository toaNhaRepository;
    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public Tang layTangTheoMa(Long maTang, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        Tang tang = null;
        Optional<Tang> kq = tangRepository.findById(maTang);
        try {
            tang = kq.get();
            return tang;
        } catch (Exception e) {
            return tang;
        }
    }

    public List<PhongMay> layDSPhongMayTheoTang(Long maTang, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return phongMayService.layPhongMayTheoMaTang(maTang, token);
    }



    public List<Tang> layDSTang(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return tangRepository.findAll();
    }

    @Transactional
    public void xoa(Long maTang, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }
        List<PhongMay> danhSachPhongMay = phongMayRepository.findByTang_MaTang(maTang);

        for (PhongMay phongMay : danhSachPhongMay) {
            List<LichTruc> dsLichTruc = lichTrucService.layLichTrucTheoMaTang(maTang, token);

            // Xoá từng lịch trực liên quan
            for (LichTruc lichTruc : dsLichTruc) {
                lichTrucService.xoa(lichTruc.getMaLich(), token);
            }

            // Xoá phòng máy
            phongMayService.xoa(phongMay.getMaPhong(),token);
        }

        // Sau khi xoá tất cả phòng máy và lịch trực, tiến hành xoá tầng
        tangRepository.deleteById(maTang);
    }

    public Tang luu(Tang tang, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return tangRepository.save(tang);
    }

    public List<Tang> layTangTheoToaNha(Long maToaNha, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return tangRepository.findByToaNha_MaToaNha(maToaNha);
    }

    public Long tinhSoLuongTangTheoMaToaNha(Long maToaNha,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return tangRepository.countByToaNha_MaToaNha(maToaNha);
    }
    @Transactional
    public void importCSVFile(MultipartFile file) throws Exception {
        // Kiểm tra nếu file không rỗng
        if (file.isEmpty()) {
            throw new Exception("File rỗng");
        }

        // Đọc file CSV sử dụng OpenCSV
        List<Tang> tangList = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), "UTF-8")) {
            CSVReader csvReader = new CSVReader(reader);
            String[] nextLine;

            // Bỏ qua dòng tiêu đề
            boolean isHeader = true;
            while ((nextLine = csvReader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false; // Bỏ qua dòng tiêu đề
                    continue;
                }

                // Lấy tên tầng từ cột đầu tiên
                String tenTang = nextLine[0].trim(); // Tên Tầng

                // Lấy mã tòa nhà từ cột thứ hai
                String maToaNhaStr = nextLine[1].trim(); // Mã Tòa Nhà

                // Kiểm tra mã tòa nhà
                if (maToaNhaStr.isEmpty() || !maToaNhaStr.matches("\\d+")) { // Kiểm tra nếu mã là số hợp lệ
                    System.out.println("Mã tòa nhà không hợp lệ: " + maToaNhaStr);
                    continue;
                }

                // Chuyển mã tòa nhà thành kiểu Long
                Long maToaNha = Long.parseLong(maToaNhaStr);

                // Tìm tòa nhà tương ứng
                Optional<ToaNha> toaNhaOptional = toaNhaRepository.findById(maToaNha);
                if (toaNhaOptional.isPresent()) {
                    ToaNha toaNha = toaNhaOptional.get();

                    // Tạo đối tượng Tang và thêm vào danh sách
                    Tang tang = new Tang();
                    tang.setTenTang(tenTang);
                    tang.setToaNha(toaNha);

                    tangList.add(tang);
                } else {
                    // Nếu không tìm thấy tòa nhà
                    System.out.println("Không tìm thấy tòa nhà với mã: " + maToaNha);
                }
            }
        } catch (IOException e) {
            throw new Exception("Lỗi khi đọc file CSV: " + e.getMessage());
        }

        // Lưu tất cả các tầng vào cơ sở dữ liệu
        if (!tangList.isEmpty()) {
            tangRepository.saveAll(tangList);
        }
    }


}
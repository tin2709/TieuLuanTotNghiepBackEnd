package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.LichTruc;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.entity.ToaNha;
import com.example.QuanLyPhongMayBackEnd.repository.ToaNhaRepository;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.CSVReader;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class ToaNhaService {

    @Autowired
    private ToaNhaRepository toaNhaRepository;

    @Autowired
    private TangService tangService;

    @Autowired
    private LichTrucService lichTrucService;
    @Autowired
    private TaiKhoanService taiKhoanService;

    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    public ToaNha layToaNhaTheoMa(Long maToaNha, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        ToaNha toaNha = null;
        Optional<ToaNha> kq = toaNhaRepository.findById(maToaNha);
        try {
            toaNha = kq.get();
            return toaNha;
        } catch (Exception e) {
            return toaNha;
        }
    }

    public List<ToaNha> layDSToaNha(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return toaNhaRepository.findAll();
    }

    @Transactional
    public void xoa(Long maToaNha, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }
        List<Tang> dsTang = tangService.layTangTheoToaNha(maToaNha, token);
        for (Tang tang : dsTang) {
            List<LichTruc> dsLichTruc = lichTrucService.layLichTrucTheoMaTang(tang.getMaTang(), token);
            for (LichTruc lichTruc : dsLichTruc) {
                lichTrucService.xoa(lichTruc.getMaLich(), token);
            }
            tangService.xoa(tang.getMaTang(), token);
        }
        toaNhaRepository.deleteById(maToaNha);
    }

    public ToaNha luu(ToaNha toaNha, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return toaNhaRepository.save(toaNha);
    }

    @Transactional
    public void importCSVFile(MultipartFile file) throws Exception {

        // Kiểm tra nếu file không rỗng
        if (file.isEmpty()) {
            throw new Exception("File rỗng");
        }

        // Đọc file CSV sử dụng OpenCSV
        List<ToaNha> toaNhaList = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), "UTF-8")) {
            CSVReader csvReader = new CSVReader(reader);
            String[] nextLine;

            // Bỏ qua dòng tiêu đề
            boolean isHeader = true; // Flag để bỏ qua dòng tiêu đề
            while ((nextLine = csvReader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false; // Bỏ qua dòng tiêu đề
                    continue;
                }

                // Kiểm tra nếu dòng không rỗng và đảm bảo đúng định dạng
                if (nextLine.length > 0) { // Check nếu có dữ liệu trong dòng
                    StringBuilder tenToaNha = new StringBuilder();

                    // Duyệt qua các cột và kết hợp chúng thành một chuỗi đầy đủ
                    for (String value : nextLine) {
                        if (tenToaNha.length() > 0) {
                            tenToaNha.append(" "); // Thêm dấu phẩy nếu có nhiều cột
                        }
                        tenToaNha.append(value); // Thêm dữ liệu từ cột vào tên tòa nhà
                    }

                    // Tạo đối tượng ToaNha và thêm vào danh sách
                    ToaNha toaNha = new ToaNha();
                    toaNha.setTenToaNha(tenToaNha.toString().trim()); // Đảm bảo tên không có dấu cách thừa
                    toaNhaList.add(toaNha);
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new Exception("Lỗi khi đọc file CSV: " + e.getMessage());
        }

        // Lưu tất cả dữ liệu vào cơ sở dữ liệu
        if (!toaNhaList.isEmpty()) {
            toaNhaRepository.saveAll(toaNhaList);
        }
    }

}

package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.LichTruc;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.entity.ToaNha;
import com.example.QuanLyPhongMayBackEnd.repository.TangRepository;
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
    private TangRepository tangRepository;

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

                // Lấy tên tầng (các cột giữa dấu phẩy), thay dấu phẩy bằng khoảng trắng
                String tenTang = nextLine[1].replaceAll(",", " ").trim(); // Tầng, loại bỏ dấu phẩy và thay bằng khoảng trắng

                // Lấy mã tòa nhà từ cột cuối cùng, loại bỏ dấu phẩy nếu có
                String maToaNhaStr = nextLine[nextLine.length - 1].replaceAll(",", "").trim(); // Mã tòa nhà, loại bỏ dấu phẩy

                // Kiểm tra nếu mã tòa nhà rỗng hoặc không hợp lệ
                if (maToaNhaStr.isEmpty() || !maToaNhaStr.matches("\\d+")) {  // Kiểm tra chuỗi không rỗng và là số hợp lệ
                    System.out.println("Mã tòa nhà không hợp lệ: " + maToaNhaStr);
                    continue; // Bỏ qua dòng này nếu mã tòa nhà không hợp lệ
                }

                // Chuyển mã tòa nhà thành kiểu Long
                Long maToaNha = Long.parseLong(maToaNhaStr);

                // Tìm tòa nhà tương ứng
                Optional<ToaNha> toaNhaOptional = toaNhaRepository.findById(maToaNha);
                if (toaNhaOptional.isPresent()) {
                    ToaNha toaNha = toaNhaOptional.get();

                    // Tạo đối tượng Tang và lưu vào danh sách
                    Tang tang = new Tang();
                    tang.setTenTang(tenTang);
                    tang.setToaNha(toaNha);

                    tangList.add(tang);
                } else {
                    // Nếu không tìm thấy tòa nhà, có thể bỏ qua hoặc xử lý lỗi
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

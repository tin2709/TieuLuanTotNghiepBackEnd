package com.example.QuanLyPhongMayBackEnd.entity; // Đảm bảo đúng package

import jakarta.persistence.*;
// Bỏ các import của Lombok: import lombok.Getter; import lombok.Setter; import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_permissions", // Tên bảng trong database
        uniqueConstraints = {
                // Định nghĩa ràng buộc UNIQUE trên sự kết hợp của 3 cột DB
                @UniqueConstraint(columnNames = {"user_id", "resource", "action"}, name = "uk_user_resource_action")
        })
// Bỏ các annotation Lombok
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Mapping tới cột 'id'
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private TaiKhoan taiKhoan;

    @Column(name = "resource", nullable = false, length = 100)
    private String resource;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    // --- CONSTRUCTOR KHÔNG THAM SỐ (Bắt buộc cho JPA/Hibernate) ---
    public UserPermission() {
        // Để trống hoặc khởi tạo giá trị mặc định nếu cần
    }
    // --- KẾT THÚC CONSTRUCTOR KHÔNG THAM SỐ ---

    // --- CONSTRUCTOR CÓ THAM SỐ (Để tiện tạo đối tượng mới) ---
    public UserPermission(TaiKhoan taiKhoan, String resource, String action) {
        this.taiKhoan = taiKhoan;
        this.resource = resource;
        this.action = action;
    }
    // --- KẾT THÚC CONSTRUCTOR CÓ THAM SỐ ---


    // --- GETTERS VÀ SETTERS (Viết tay) ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    // --- KẾT THÚC GETTERS VÀ SETTERS ---


    // --- equals() và hashCode() (Giữ nguyên) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPermission that = (UserPermission) o;
        return java.util.Objects.equals(taiKhoan != null ? taiKhoan.getMaTK() : null, that.taiKhoan != null ? that.taiKhoan.getMaTK() : null) &&
                java.util.Objects.equals(resource, that.resource) &&
                java.util.Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(taiKhoan != null ? taiKhoan.getMaTK() : null, resource, action);
    }
    // --- KẾT THÚC equals() và hashCode() ---


    // --- toString() (Giữ nguyên) ---
    @Override
    public String toString() {
        return "UserPermission{" +
                "id=" + id +
                ", taiKhoanId=" + (taiKhoan != null ? taiKhoan.getMaTK() : "null") + // In ID để an toàn
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
    // --- KẾT THÚC toString() ---
}
package com.example.homestay.web.form;

import com.example.homestay.domain.RoomStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;

public class RoomForm {

    private Long id;

    @NotNull(message = "请选择所属门店")
    private Long homestayId;

    @NotBlank(message = "请填写房型名称")
    @Size(max = 80, message = "房型名称不能超过80个字符")
    private String name;

    @NotBlank(message = "请填写房型分类")
    private String roomType;

    @Min(value = 1, message = "可住人数至少为1人")
    @Max(value = 20, message = "可住人数不能超过20人")
    private int capacity = 2;

    @NotNull(message = "请填写每晚价格")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @NumberFormat(pattern = "#,##0.00")
    private BigDecimal price;

    @Min(value = 1, message = "房量至少为1间")
    @Max(value = 999, message = "房量不能超过999间")
    private int totalRooms = 1;

    @NotNull(message = "请选择房态")
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Size(max = 240, message = "设施标签不能超过240个字符")
    private String amenities;

    private String coverImage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHomestayId() {
        return homestayId;
    }

    public void setHomestayId(Long homestayId) {
        this.homestayId = homestayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}

package com.example.homestay.web.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class BookingForm {

    @NotNull(message = "请选择房型")
    private Long roomId;

    @NotBlank(message = "请填写入住人姓名")
    @Size(max = 40, message = "姓名不能超过40个字符")
    private String guestName;

    @NotBlank(message = "请填写联系电话")
    @Pattern(regexp = "^1\\d{10}$", message = "请输入11位手机号码")
    private String guestPhone;

    @NotNull(message = "请选择入住日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkIn;

    @NotNull(message = "请选择离店日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOut;

    @Min(value = 1, message = "至少预订1间")
    @Max(value = 10, message = "单次最多预订10间")
    private int roomCount = 1;

    @Min(value = 1, message = "入住人数至少为1人")
    @Max(value = 30, message = "入住人数不能超过30人")
    private int guests = 1;

    @Size(max = 500, message = "备注不能超过500个字符")
    private String note;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

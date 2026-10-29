package com.example.homestay.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HomestayForm {

    private Long id;

    @NotBlank(message = "请填写门店名称")
    @Size(max = 80, message = "门店名称不能超过80个字符")
    private String name;

    @NotBlank(message = "请填写所在城市")
    @Size(max = 40, message = "城市名称不能超过40个字符")
    private String city;

    @NotBlank(message = "请填写详细地址")
    @Size(max = 160, message = "地址不能超过160个字符")
    private String address;

    @NotBlank(message = "请填写一句话介绍")
    @Size(max = 160, message = "介绍不能超过160个字符")
    private String summary;

    @Size(max = 1200, message = "门店详情不能超过1200个字符")
    private String description;

    @NotBlank(message = "请填写封面图片地址")
    private String coverImage;

    @Size(max = 200, message = "特色标签不能超过200个字符")
    private String tags;

    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

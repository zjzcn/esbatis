package com.github.esbatis.test;

import java.util.List;

public class Demo {

    private Long id;
    private String dataDt;
    private Integer checkType;
    private Long checkId;
    private String checkValueJson;
    private String createdAt;
    private String updatedAt;
    private List<Long> age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataDt() {
        return dataDt;
    }

    public void setDataDt(String dataDt) {
        this.dataDt = dataDt;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
    }

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public String getCheckValueJson() {
        return checkValueJson;
    }

    public void setCheckValueJson(String checkValueJson) {
        this.checkValueJson = checkValueJson;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Long> getAge() {
        return age;
    }

    public void setAge(List<Long> age) {
        this.age = age;
    }
}

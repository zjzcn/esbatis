package com.github.esbatis.test;

import java.util.Date;
import java.util.List;

public class Demo {

    private Long id;
    private Date date;
    private List<Long> age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Long> getAge() {
        return age;
    }

    public void setAge(List<Long> age) {
        this.age = age;
    }
}

package com.jsb.classspider;

public class Product {
    //uu id
//    private String id;

    //产品名称
    private String name;
    // 代码
    private String code;
    //父代码
    private String pcode;
    //说明
    private String description;
    //网址
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Product(String name, String code, String pcode, String description, String url) {
        this.name = name;
        this.code = code;
        this.pcode = pcode;
        this.description = description;
        this.url = url;
    }
}

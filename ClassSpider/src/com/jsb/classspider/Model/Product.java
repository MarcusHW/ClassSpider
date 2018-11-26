package com.jsb.classspider.Model;


public class Product {

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
    //级别
    private String webLevel;

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

    public String getWebLevel() {
        return webLevel;
    }

    public void setWebLevel(String webLevel) {
        this.webLevel = webLevel;
    }

    public Product(String name, String code, String pcode, String description, String url, String webLevel) {
        this.name = name;
        this.code = code;
        this.pcode = pcode;
        this.description = description;
        this.url = url;
        this.webLevel = webLevel;
    }
}

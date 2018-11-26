package com.jsb.classspider.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * 延时对象
 */
public class ProductTimeOut {

    private List<Product> productList = new ArrayList<>();
    //延时级别
    private String level;

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public ProductTimeOut(List<Product> productList, String level) {
        this.productList = productList;
        this.level = level;
    }
}

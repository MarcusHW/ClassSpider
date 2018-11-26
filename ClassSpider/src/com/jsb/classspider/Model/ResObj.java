package com.jsb.classspider.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回对象，包含5级数组和延时数组
 */
public class ResObj {
    private List<Product> save5List = new ArrayList<>();

    private ProductTimeOut productTimeOut;

    public List<Product> getSave5List() {
        return save5List;
    }

    public void setSave5List(List<Product> save5List) {
        this.save5List = save5List;
    }

    public ProductTimeOut getProductTimeOut() {
        return productTimeOut;
    }

    public void setProductTimeOut(ProductTimeOut productTimeOut) {
        this.productTimeOut = productTimeOut;
    }

    public ResObj(List<Product> save5List, ProductTimeOut productTimeOut) {
        this.save5List = save5List;
        this.productTimeOut = productTimeOut;
    }
}

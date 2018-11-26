package com.jsb.classspider;

import com.jsb.classspider.Model.Product;
import com.jsb.classspider.Model.ProductTimeOut;
import com.jsb.classspider.Model.ResObj;

import java.io.IOException;
import java.util.List;

public interface Process {

    /**
     * 检查是否存在，不存在添加
     *
     * @param list 数据数组
     */
    void saveProductList(List<Product> list);

    /**
     * 得到指定层级的product List
     *
     * @param Level 层级
     * @return Product List
     */
    List<Product> getProductList(int Level);

    List<Product> getLastLevelList();

    void getLevel1() throws IOException;


    ResObj getNormalLevel(List<Product> list, String level) throws IOException;

    ProductTimeOut getEndLevel(List<Product> list) throws IOException, InterruptedException;
}

package com.jsb.classspider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface Process {
    public List<Product> redownList = new ArrayList<>();


    /**
     * 检查是否存在，不存在添加
     *
     * @param list 数据数组
     */
    public void saveProductList(List<Product> list);

    /**
     * 得到指定层级的product List
     *
     * @param level 层级
     * @return Product List
     */
    public List<Product> getProductList(int Level);

    public List<Product> getLastLevelList();

    public void getLevell() throws IOException;


    public ResObj getNormalLevel(List<Product> list, String level) throws IOException;

    public ProductTimeOut getEndLevel(List<Product> list) throws IOException, InterruptedException;
}

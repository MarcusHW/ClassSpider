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

    public void getLevel2();

    public void getLevel3();

    public void getLevel4();

    public void getLevel5();

    public void save5(List<Product> proList, int level);

    public boolean isRightLevelCode(String code, int level);

    public void reDown();

    public List<Product> getReDownList(int level);

    public void removeReDown(Product product);
}

package com.jsb.classspider;

import java.io.IOException;
import java.util.List;

public interface Process {
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

    public List<Product> getLevell() throws IOException;

    public List<Product> getLevel2();

    public List<Product> getLevel3();

    public List<Product> getLevel4();

    public List<Product> getLevel5();

    public void save5(List<Product> proList, int level);

    public boolean isRightLevelCode(String code, int level);
}

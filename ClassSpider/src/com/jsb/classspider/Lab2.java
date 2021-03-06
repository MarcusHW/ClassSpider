package com.jsb.classspider;

import java.io.IOException;
import java.util.List;

public class Lab2 {
    public static void main(String[] args) throws IOException {
        ProcessImpl process = new ProcessImpl();
        //存1
        process.getLevell();
        //得1存2
        List<Product> list1 = process.getProductList(1);
        ResObj resObj2 = process.getNormalLevel(list1, "2");
        if (resObj2.getProductTimeOut().getProductList().size() > 0) {
            ResObj obj2 = process.getNormalLevel(resObj2.getProductTimeOut().getProductList(), "2");
            if (obj2.getProductTimeOut().getProductList().size() > 0) {
                process.getNormalLevel(obj2.getProductTimeOut().getProductList(), "2");
            }
        }

        //得到三级
        List<Product> list2 = process.getProductList(2);
        ResObj resObj3 = process.getNormalLevel(list2, "3");
        process.saveProductList(resObj3.getSave5List());
        if (resObj3.getProductTimeOut().getProductList().size() > 0) {
            ResObj obj3 = process.getNormalLevel(resObj3.getProductTimeOut().getProductList(), "3");
            if (obj3.getProductTimeOut().getProductList().size() > 0) {
                process.getNormalLevel(obj3.getProductTimeOut().getProductList(), "3");
            }
        }

        //得到四级
        List<Product> list3 = process.getProductList(3);
        ResObj resObj4 = process.getNormalLevel(list3, "4");
        process.saveProductList(resObj4.getSave5List());
        ResObj obj4 = process.getNormalLevel(resObj4.getProductTimeOut().getProductList(), "4");
        if (obj4.getProductTimeOut().getProductList().size() > 0) {
            process.getNormalLevel(obj4.getProductTimeOut().getProductList(), "4");
        }

        //得到五级
        List<Product> list4 = process.getLastLevelList();
        //todo
        ProductTimeOut timeOut5 = process.getEndLevel(list4);
        if (timeOut5.getProductList().size() > 0) {
            ProductTimeOut resObj5 = process.getEndLevel(timeOut5.getProductList());
            if (resObj5.getProductList().size() > 0) {
                ProductTimeOut obj5 = process.getEndLevel(resObj5.getProductList());
            }
        }
    }

}

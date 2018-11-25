package com.jsb.classspider;

import java.io.IOException;
import java.util.List;

public class Lab {
    public static void main(String[] args) throws IOException {
//        List<Product> list1 = Process.getLevel1();
//        Process.saveProductList(list1);
//        List<Product> list2 = Process.getLevel2();
//        Process.saveProductList(list2);
        List<Product> list3 = Process.getLevel3();
        Process.saveProductList(list3);
        List<Product> list4 = Process.getLevel4();
        Process.saveProductList(list4);
        List<Product> list5 = Process.getLevel5();
        Process.saveProductList(list5);

    }
}

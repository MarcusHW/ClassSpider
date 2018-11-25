package com.jsb.classspider;

import java.io.IOException;
import java.util.List;

public class Lab {
    public static void main(String[] args) throws IOException {
        ProcessImpl process = new ProcessImpl();
        List<Product> list1 = process.getLevell();
        process.saveProductList(list1);
        List<Product> list2 = process.getLevel2();
        process.saveProductList(list2);
        List<Product> list3 = process.getLevel3();
        process.saveProductList(list3);
        List<Product> list4 = process.getLevel4();
        process.saveProductList(list4);
        List<Product> list5 = process.getLevel5();
        process.saveProductList(list5);

    }
}

package com.jsb.classspider;

import java.io.IOException;
import java.util.List;

public class Lab {
    public static void main(String[] args) throws IOException {
        ProcessImpl process = new ProcessImpl();
        process.getLevell();
        process.getLevel2();
        process.getLevel3();
        process.getLevel4();
        process.getLevel5();
        //todo
        //重试下载
        process.saveProductList(process.redownList);

    }
}

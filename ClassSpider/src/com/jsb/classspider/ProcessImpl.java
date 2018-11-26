package com.jsb.classspider;

import com.dataofbank.ryze.util.RegexUtil;
import com.dataofbank.ryze.util.StringUtil;
import com.jsb.classspider.Dao.MongoDao;
import com.jsb.classspider.Model.Product;
import com.jsb.classspider.Model.ProductTimeOut;
import com.jsb.classspider.Model.ResObj;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessImpl implements Process {
    private static String BASE_URL = "http://www.stats.gov.cn/tjsj/tjbz/tjypflml/2010/";


    @Override
    public void
    saveProductList(List<Product> list) {
        MongoCollection<Document> collection = MongoDao.getCollection("PRODUCT");
        List<org.bson.Document> docList = new ArrayList<>();
        for (Product product : list) {
            org.bson.Document document = new org.bson.Document();
            document.append("产品名称", product.getName());
            document.append("代码", product.getCode());
            document.append("父代码", product.getPcode());
            document.append("说明", StringUtil.removeAllBlank(product.getDescription()));
            document.append("网址", product.getUrl());
            document.append("级别", product.getWebLevel());
            if (collection.count(document) == 0) {
                docList.add(document);
            }
        }
        if (docList.size() > 0) {
            collection.insertMany(docList);
        }
    }

    @Override
    public List<Product> getProductList(int Level) {
        List<Product> list = new ArrayList<>();
        MongoCollection<Document> collection = MongoDao.getCollection("PRODUCT");
        Document query = new Document();
        query.append("级别", String.valueOf(Level));
        FindIterable<Document> findIterable = collection.find(query);
        for (Document next : findIterable) {
            String webLevel = next.get("级别", String.class);
            String code = next.get("代码", String.class);
            if (webLevel.equals(String.valueOf(Level))) {
                Product product = new Product(next.get("产品名称", String.class), next.get("代码", String.class),
                        next.get("父代码", String.class), next.get("说明", String.class), next.get("网址", String.class), next.get("级别", String.class));
                list.add(product);
            }
        }
        return list;
    }

    @Override
    public List<Product> getLastLevelList() {
        List<Product> list = new ArrayList<>();
        MongoCollection<Document> collection = MongoDao.getCollection("PRODUCT");
        FindIterable<Document> findIterable = collection.find();
        for (Document next : findIterable) {
            String webLevel = next.get("级别", String.class);
            String code = next.get("代码", String.class);
            if (code.length() == 8 || code.length() == 10 || webLevel.equals("4")) {
                Product product = new Product(next.get("产品名称", String.class), next.get("代码", String.class),
                        next.get("父代码", String.class), next.get("说明", String.class), next.get("网址", String.class), next.get("级别", String.class));
                list.add(product);
            }
        }
        return list;
    }

    @Override
    public void getLevel1() throws IOException {
        org.jsoup.nodes.Document document = Jsoup.connect(BASE_URL).timeout(5000).get();
        Elements elements = document.select("tr.provincetr> td> a:nth-child(1)");
        List<Product> list = new ArrayList<>();
        for (Element element : elements) {
            String href = element.attr("href");
            String code = RegexUtil.findFirst("\\d+.html", href).replace(".html", "");
            String name = element.text().split("-")[1];
            Product product = new Product(name, code, "", "", BASE_URL + href, "1");
            list.add(product);
        }
        saveProductList(list);
    }

    @Override
    public ResObj getNormalLevel(List<Product> list, String level) {
        List<Product> timeOutList = new ArrayList<>();
        List<Product> save5List = new ArrayList<>();
        for (Product product : list) {
            List<Product> realList = new ArrayList<>();
            String Pcode = product.getCode();
            String url = product.getUrl();
            try {
                Thread.sleep(2000);
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(10000).get();
                Elements villagetr = document.getElementsByClass("villagetr");
                if (villagetr.size() > 0) {
                    product.setWebLevel("4");
                    save5List.add(product);
                    continue;
                }
                List<String> classList = new ArrayList<>();
                classList.add("citytr");
                classList.add("countytr");
                classList.add("towntr");
                for (String s : classList) {
                    Elements elements = document.getElementsByClass(s);
                    if (elements.size() == 0) {
                        continue;
                    }
                    for (Element element : elements) {
                        String code = element.select("td:nth-child(1) > a:nth-child(1)").text();
                        String name = element.select("td:nth-child(2) > a:nth-child(1)").text();
                        String href = element.select("td:nth-child(2) > a:nth-child(1)").attr("href");
                        String jumpUrl = "";
                        if (s.equals("citytr")) {
                            jumpUrl = BASE_URL + href;
                        } else if (s.equals("countytr")) {
                            jumpUrl = BASE_URL + code.substring(0, 2) + "/" + href;
                        } else {
                            jumpUrl = BASE_URL + code.substring(0, 2) + "/" + code.substring(2, 4) + "/" + href;
                        }
                        Product pro = new Product(name, code, Pcode, "", jumpUrl, level);
                        if (code.length() == 8 || code.length() == 10) {
                            pro.setWebLevel("4");
                            save5List.add(pro);
                        } else {
                            realList.add(pro);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("延时:" + url);
                timeOutList.add(product);
            }
            saveProductList(realList);
        }

        return new ResObj(save5List, new ProductTimeOut(timeOutList, level));
    }

    @Override
    public ProductTimeOut getEndLevel(List<Product> list) throws InterruptedException {
        List<Product> timeOutList = new ArrayList<>();
        List<Product> saveList = new ArrayList<>();
        for (Product product : list) {
            Thread.sleep(2000);
            String pCode = product.getCode();
            String url = product.getUrl();
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(10000).get();
                Elements elements = document.select("tr.villagetr");
                for (Element element : elements) {
                    String code5 = element.select("td:nth-child(1)").text();
                    String description = elements.select("td:nth-child(3)").text();
                    Product product4 = new Product(elements.select("td:nth-child(2)").text(), code5, pCode, description, "", "5");
                    saveList.add(product4);
                }
            } catch (Exception e) {
                timeOutList.add(product);
            }
        }
        saveProductList(saveList);
        return new ProductTimeOut(timeOutList, "4");
    }
}

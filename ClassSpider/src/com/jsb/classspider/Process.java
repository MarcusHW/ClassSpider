package com.jsb.classspider;

import com.dataofbank.ryze.util.RegexUtil;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬取数据并持久化
 */
class Process {
    private static String BASE_URL = "http://www.stats.gov.cn/tjsj/tjbz/tjypflml/2010/";

    /**
     * 检查是否存在，不存在添加
     *
     * @param list 数据数组
     */
    static void saveProductList(List<Product> list) {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ryze");
        MongoCollection<org.bson.Document> collection = mongoDatabase.getCollection("PRODUCT");
        List<org.bson.Document> docList = new ArrayList<>();
        for (Product product : list) {
            org.bson.Document document = new org.bson.Document();
            document.append("产品名称", product.getName());
            document.append("代码", product.getCode());
            document.append("父代码", product.getPcode());
            document.append("说明", product.getDescription());
            if (collection.count(document) == 0) {
                docList.add(document);
            }
        }
        if (docList.size() > 0) {
            collection.insertMany(docList);
        }

    }

    /**
     * 得到指定层级的product List
     *
     * @param level 层级
     * @return Product List
     */
    private static List<Product> getProductList(int level) {
        List<Product> list = new ArrayList<>();
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ryze");
        MongoCollection<org.bson.Document> collection = mongoDatabase.getCollection("PRODUCT");
        FindIterable<org.bson.Document> findIterable = collection.find();
        MongoCursor<org.bson.Document> iterator = findIterable.iterator();
        while (iterator.hasNext()) {
            org.bson.Document next = iterator.next();
            String code = next.get("代码", String.class);
            if (isRightLevelCode(code, level)) {
                Product product = new Product(next.get("产品名称", String.class), next.get("代码", String.class),
                        next.get("父代码", String.class), next.get("说明", String.class));
                list.add(product);
            }
        }
        return list;
    }

    private static boolean isRightLevelCode(String code, int level) {
        return code.length() == level * 2;
    }

    static List<Product> getLevel1() throws IOException {
        Document document = Jsoup.connect(BASE_URL).get();
        Elements elements = document.select("tr.provincetr> td> a:nth-child(1)");
        List<Product> list = new ArrayList<>();
        for (Element element : elements) {
            String href = element.attr("href");
            String code = RegexUtil.findFirst("\\d+.html", href).replace(".html", "");
            String name = element.text().split("-")[1];
            Product product = new Product(name, code, "", "");
            list.add(product);
        }
        return list;
    }


    static List<Product> getLevel2() throws IOException {
        List<Product> productList = getProductList(1);
        List<Product> list = new ArrayList<>();
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = BASE_URL + pcode + ".html";
            Document document = Jsoup.connect(url).timeout(5000).get();
            Elements elements = document.select("tr.citytr> td:nth-child(2) > a:nth-child(1)");
            for (Element element : elements) {
                String href = element.attr("href");
                String code2 = RegexUtil.findFirst("\\d+.html", href).replace(".html", "");
                String name = element.text();
                Product product2 = new Product(name, code2, pcode, "");
                list.add(product2);
            }
        }
        return list;
    }

    static List<Product> getLevel3() throws IOException {
        List<Product> save5List = new ArrayList<>();
        List<Product> list = new ArrayList<>();
        List<Product> productList = getProductList(2);
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = BASE_URL + pcode.substring(0, 2) + "/" + pcode + ".html";
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("tr.countytr");
            for (Element element : elements) {
                String name;
                String code3;
                if (element.getElementsByTag("a") == null) {
                    code3 = element.select("td:nth-child(1)").text();
                    name = element.select("td:nth-child(2)").text();
                } else {
                    String href = element.select("td:nth-child(2) > a:nth-child(1)").attr("href");
                    name = element.select("td:nth-child(2) > a:nth-child(1)").text();
                    code3 = RegexUtil.findFirst("\\d+.html", href).replace(".html", "");
                }
                code3 = code3.length() == 6 ? code3 + "0000" : code3;
                Product product3 = new Product(name, code3, pcode, "");
                if (code3.length() == 10) {
                    save5List.add(product3);
                } else {
                    list.add(product3);
                }
            }
        }
        save5(save5List);
        return list;
    }

    static List<Product> getLevel4() throws IOException {
        List<Product> save5List = new ArrayList<>();
        List<Product> list = new ArrayList<>();
        List<Product> productList = getProductList(3);
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = BASE_URL + pcode.substring(0, 2) + "/" + pcode.substring(2, 4) + "/" + pcode + ".html";
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("tr.countytr");
            for (Element element : elements) {
                String code4;
                String name;
                if (element.getElementsByTag("a") == null) {
                    code4 = element.select("td:nth-child(1)").text();
                    name = element.select("td:nth-child(2)").text();
                } else {
                    String href = element.select("td:nth-child(2) > a:nth-child(1)").attr("href");
                    code4 = RegexUtil.findFirst("\\d+.html", href).replace(".html", "");
                    name = element.select("td:nth-child(2) > a:nth-child(1)").text();
                }
                code4 = code4.length() == 8 ? code4 + "00" : code4;
                Product product4 = new Product(name, code4, pcode, "");
                if (code4.length() == 10) {
                    save5List.add(product4);
                } else {
                    list.add(product4);
                }
            }
        }
        save5(save5List);
        return list;
    }

    static List<Product> getLevel5() throws IOException {
        List<Product> list = new ArrayList<>();
        List<Product> productList = getProductList(4);
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = BASE_URL + pcode.substring(0, 2) + "/" + pcode.substring(2, 4) + "/" + pcode.substring(4, 6) + "/" + pcode + ".html";
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("tr.villagetr");
            for (Element element : elements) {
                String code5 = element.select("td:nth-child(1)").text();
                String name = elements.select("td:nth-child(2)").text();
                String description = elements.select("td:nth-child(3)").text();
                Product product4 = new Product(name, code5, pcode, description);
                list.add(product4);
            }
        }
        return list;
    }

    private static void save5(List<Product> proList) throws IOException {
        List<Product> list = new ArrayList<>();
        for (Product product : proList) {
            String url = "";
            String s = product.getCode();
            if (s.substring(5, 6).equals("0")) {
                url = BASE_URL + s.substring(0, 2) + "/" + s.substring(2, 4) + "/" + s.substring(0, 6) + ".html";
            } else {
                url = BASE_URL + s.substring(0, 2) + "/" + s.substring(2, 4) + "/" + s.substring(4, 6) + "/" + s.substring(0, 8) + ".html";
            }
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("tr.villagetr");
            for (Element element : elements) {
                String code5 = element.select("td:nth-child(1)").text();
                String description = elements.select("td:nth-child(3)").text();
                Product product4 = new Product(elements.select("td:nth-child(2)").text(), code5, s, description);
                list.add(product4);
            }
        }
        saveProductList(list);
    }
}
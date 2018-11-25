package com.jsb.classspider;

import com.dataofbank.ryze.util.RegexUtil;
import com.dataofbank.ryze.util.StringUtil;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
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
    public void saveProductList(List<Product> list) {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ryze");
        MongoCollection<Document> collection = mongoDatabase.getCollection("PRODUCT");
        List<org.bson.Document> docList = new ArrayList<>();
        for (Product product : list) {
            org.bson.Document document = new org.bson.Document();
            document.append("产品名称", product.getName());
            document.append("代码", product.getCode());
            document.append("父代码", product.getPcode());
            document.append("说明", StringUtil.removeAllBlank(product.getDescription()));
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
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ryze");
        MongoCollection<org.bson.Document> collection = mongoDatabase.getCollection("PRODUCT");
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> iterator = findIterable.iterator();
        while (iterator.hasNext()) {
            org.bson.Document next = iterator.next();
            String code = next.get("代码", String.class);
            if (isRightLevelCode(code, Level)) {
                Product product = new Product(next.get("产品名称", String.class), next.get("代码", String.class),
                        next.get("父代码", String.class), next.get("说明", String.class), next.get("网站", String.class));
                list.add(product);
            }
        }
        return list;
    }

    @Override
    public List<Product> getLevell() throws IOException {
        org.jsoup.nodes.Document document = Jsoup.connect(BASE_URL).timeout(5000).get();
        Elements elements = document.select("tr.provincetr> td> a:nth-child(1)");
        List<Product> list = new ArrayList<>();
        for (Element element : elements) {
            String href = element.attr("href");
            String code = RegexUtil.findFirst("\\d+.html", href).replace(".html", "");
            String name = element.text().split("-")[1];
            Product product = new Product(name, code, "", "", "");
            list.add(product);
        }
        return list;
    }

    @Override
    public List<Product> getLevel2() {
        List<Product> productList = getProductList(1);
        List<Product> list = new ArrayList<>();
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = BASE_URL + pcode + ".html";
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(5000).get();
                Elements elements = document.select("tr.citytr> td:nth-child(2) > a:nth-child(1)");
                for (Element element : elements) {
                    String href = element.attr("href");
                    String code2 = RegexUtil.findFirst("\\d+.html", href).replace(".html", "");
                    String name = element.text();
                    Product product2 = new Product(name, code2, pcode, "", "");
                    list.add(product2);
                }
            } catch (Exception e) {
                System.out.println("2级超时:" + url);
            }
        }
        return list;
    }

    @Override
    public List<Product> getLevel3() {
        List<Product> save5List = new ArrayList<>();
        List<Product> list = new ArrayList<>();
        List<Product> productList = getProductList(2);
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = BASE_URL + pcode.substring(0, 2) + "/" + pcode + ".html";
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(15000).get();
                Elements elements = document.select("tr.countytr");
                for (Element element : elements) {
                    String code3 = element.select("td:nth-child(1) > a:nth-child(1)").text();
                    String name = element.select("td:nth-child(2) > a:nth-child(1)").text();
                    Product product3 = new Product(name, code3, pcode, "", "");
                    if (code3.length() != 10) {
                        list.add(product3);
                    } else {
                        save5List.add(product3);
                    }
                }
            } catch (Exception e) {
                System.out.println("3级超时:" + url);
            }
        }
        save5(save5List, 3);
        return list;
    }

    @Override
    public List<Product> getLevel4() {
        List<Product> save5List = new ArrayList<>();
        List<Product> list = new ArrayList<>();
        List<Product> productList = getProductList(3);
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = BASE_URL + pcode.substring(0, 2) + "/" + pcode.substring(2, 4) + "/" + pcode + ".html";
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(5000).get();
                Elements elements = document.select("tr.towntr");
                for (Element element : elements) {
                    String code4 = element.select("td:nth-child(1) > a:nth-child(1)").text();
                    String name = element.select("td:nth-child(2) > a:nth-child(1)").text();
                    Product product4 = new Product(name, code4, pcode, "", "");
                    if (code4.length() == 10) {
                        save5List.add(product4);
                    } else {
                        list.add(product4);
                    }
                }
            } catch (Exception e) {
                System.out.println("4级超时:" + url);
            }
        }
        save5(save5List, 4);
        return list;
    }

    @Override
    public List<Product> getLevel5() {
        List<Product> list = new ArrayList<>();
        List<Product> productList = getProductList(4);
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = BASE_URL + pcode.substring(0, 2) + "/" + pcode.substring(2, 4) + "/" + pcode.substring(4, 6) + "/" + pcode + ".html";
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(5000).get();
                Elements elements = document.select("tr.villagetr");
                for (Element element : elements) {
                    String code5 = element.select("td:nth-child(1)").text();
                    String name = elements.select("td:nth-child(2)").text();
                    String description = elements.select("td:nth-child(3)").text();
                    Product product4 = new Product(name, code5, pcode, description, "");
                    list.add(product4);
                }
            } catch (Exception e) {
                System.out.println("5级超时:" + url);
            }
        }
        return list;
    }

    @Override
    public void save5(List<Product> proList, int level) {
        List<Product> list = new ArrayList<>();
        for (Product product : proList) {
            String url = "";
            String pcode = product.getCode();
            if (level == 3) {
                url = BASE_URL + pcode.substring(0, 2) + "/" + pcode.substring(2, 4) + "/" + pcode.substring(0, 4) + ".html";
            } else {
                url = BASE_URL + pcode.substring(0, 2) + "/" + pcode.substring(2, 4) + "/" + pcode.substring(4, 6) + "/" + pcode.substring(0, 6) + ".html";
            }
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(10000).get();
                Elements elements = document.select("tr.villagetr");
                for (Element element : elements) {
                    String code5 = element.select("td:nth-child(1)").text();
                    String description = elements.select("td:nth-child(3)").text();
                    Product product4 = new Product(elements.select("td:nth-child(2)").text(), code5, pcode, description, "");
                    list.add(product4);
                }
            } catch (Exception e) {
                System.out.println("存5超时:" + url);
            }
        }
        saveProductList(list);

    }

    @Override
    public boolean isRightLevelCode(String code, int level) {
        return code.length() == level * 2;
    }


}

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
    public void
    saveProductList(List<Product> list) {
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
            document.append("网址", product.getUrl());
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
                        next.get("父代码", String.class), next.get("说明", String.class), next.get("网址", String.class));
                list.add(product);
            }
        }
        return list;
    }

    @Override
    public List<Product> getLastLevelList() {
        List<Product> list = new ArrayList<>();
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ryze");
        MongoCollection<org.bson.Document> collection = mongoDatabase.getCollection("PRODUCT");
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> iterator = findIterable.iterator();
        while (iterator.hasNext()) {
            org.bson.Document next = iterator.next();
            String code = next.get("代码", String.class);
            String url = next.get("网址", String.class);
            if (!url.contains("完")) {
                if (code.length() == 8 || code.length() == 10) {
                    Product product = new Product(next.get("产品名称", String.class), next.get("代码", String.class),
                            next.get("父代码", String.class), next.get("说明", String.class), next.get("网址", String.class));
                    list.add(product);
                }
            }
        }
        return list;
    }

    @Override
    public void getLevell() throws IOException {
        org.jsoup.nodes.Document document = Jsoup.connect(BASE_URL).timeout(5000).get();
        Elements elements = document.select("tr.provincetr> td> a:nth-child(1)");
        List<Product> list = new ArrayList<>();
        for (Element element : elements) {
            String href = element.attr("href");
            String code = RegexUtil.findFirst("\\d+.html", href).replace(".html", "");
            String name = element.text().split("-")[1];
            Product product = new Product(name, code, "", "", BASE_URL + href);
            list.add(product);
        }
        saveProductList(list);
    }

    @Override
    public void getLevel2() {
        List<Product> productList = getProductList(1);
        List<Product> list = new ArrayList<>();
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = product.getUrl();
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(5000).get();
                Elements elements = document.select("tr.citytr");
                for (Element element : elements) {
                    String code2 = element.select("td:nth-child(1) > a:nth-child(1)").text();
                    String href = element.select("td:nth-child(2) > a:nth-child(1)").attr("href");
                    String name = element.select("td:nth-child(2) > a:nth-child(1)").text();
                    Product product2 = new Product(name, code2, pcode, "", BASE_URL + href);
                    list.add(product2);
                }
            } catch (Exception e) {
                System.out.println("2级超时:" + url);
                product.setDescription("2");
                redownList.add(product);
            }
        }
        saveProductList(list);
    }

    @Override
    public void getLevel3() {
        List<Product> save5List = new ArrayList<>();
        List<Product> list = new ArrayList<>();
        List<Product> productList = getProductList(2);
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = product.getUrl();
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(15000).get();
                Elements elements = document.select("tr.countytr");
                for (Element element : elements) {
                    String code3 = element.select("td:nth-child(1) > a:nth-child(1)").text();
                    String name = element.select("td:nth-child(2) > a:nth-child(1)").text();
                    String href = element.select("td:nth-child(2) > a:nth-child(1)").attr("href");
                    Product product3 = new Product(name, code3, pcode, "", BASE_URL + code3.substring(0, 2) + "/" + href);
                    if (code3.length() != 10) {
                        list.add(product3);
                    } else {
                        save5List.add(product3);
                    }
                }
//                removeReDown(product);
            } catch (Exception e) {
                System.out.println("3级超时:" + url);
                product.setDescription("3");
                redownList.add(product);
            }
        }
        save5(save5List, 3);
        saveProductList(list);
    }

    @Override
    public void getLevel4() {
        List<Product> save5List = new ArrayList<>();
        List<Product> list = new ArrayList<>();
        List<Product> productList = getProductList(3);
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = product.getUrl();
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(5000).get();
                Elements elements = document.select("tr.towntr");
                for (Element element : elements) {
                    String code4 = element.select("td:nth-child(1) > a:nth-child(1)").text();
                    String name = element.select("td:nth-child(2) > a:nth-child(1)").text();
                    String href = element.select("td:nth-child(2) > a:nth-child(1)").attr("href");
                    Product product4 = new Product(name, code4, pcode, "", BASE_URL + code4.substring(0, 2) + "/" + code4.substring(2, 4) + "/" + href);
                    if (code4.length() == 10) {
                        save5List.add(product4);
                    } else {
                        list.add(product4);
                    }
                }
//                removeReDown(product);
            } catch (Exception e) {
                System.out.println("4级超时:" + url);
                product.setDescription("4");
                redownList.add(product);
            }
        }
        save5(save5List, 4);
        saveProductList(list);
    }

    @Override
    public void getLevel5() {
        List<Product> list = new ArrayList<>();
        List<Product> productList = getLastLevelList();
        for (Product product : productList) {
            String pcode = product.getCode();
            String url = product.getUrl();
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(5000).get();
                Elements elements = document.select("tr.villagetr");
                for (Element element : elements) {
                    String code5 = element.select("td:nth-child(1)").text();
                    String name = elements.select("td:nth-child(2)").text();
                    String description = elements.select("td:nth-child(3)").text();
                    Product product4 = new Product(name, code5, pcode, description, "完");
                    list.add(product4);
                }
//                removeReDown(product);
            } catch (Exception e) {
                System.out.println("5级超时:" + url);
                product.setDescription("5");
                redownList.add(product);

            }
        }
        saveProductList(list);
    }

    @Override
    public void save5(List<Product> proList, int level) {
        List<Product> list = new ArrayList<>();
        for (Product product : proList) {
            String url = product.getUrl();
            String pcode = product.getCode();
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(10000).get();
                Elements elements = document.select("tr.villagetr");
                for (Element element : elements) {
                    String code5 = element.select("td:nth-child(1)").text();
                    String description = elements.select("td:nth-child(3)").text();
                    Product product4 = new Product(elements.select("td:nth-child(2)").text(), code5, pcode, description, "完");
                    list.add(product4);
                }
//                removeReDown(product);
            } catch (Exception e) {
                System.out.println("存5超时:" + url);
                product.setDescription(String.valueOf(level));
                redownList.add(product);
            }
        }
        saveProductList(list);
    }

    @Override
    public boolean isRightLevelCode(String code, int level) {
        return code.length() == level * 2;
    }

    @Override
    public List<Product> getReDownList(int level) {
        List<Product> list = new ArrayList<>();
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ryze");
        MongoCollection<Document> collection = mongoDatabase.getCollection("REDOWN");
        org.bson.Document document = new org.bson.Document();
        document.append("说明", level);
        FindIterable<Document> iterable = collection.find(document);
        MongoCursor<Document> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            org.bson.Document next = iterator.next();
            String code = next.get("代码", String.class);
            Product product = new Product(next.get("产品名称", String.class), next.get("代码", String.class),
                    next.get("父代码", String.class), next.get("说明", String.class), next.get("网址", String.class));
            list.add(product);
        }
        return list;
    }

    @Override
    public void removeReDown(Product product) {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ryze");
        MongoCollection<Document> collection = mongoDatabase.getCollection("REDOWN");
        org.bson.Document document = new org.bson.Document();
        document.append("产品名称", product.getName());
        document.append("代码", product.getCode());
        document.append("父代码", product.getPcode());
        document.append("说明", StringUtil.removeAllBlank(product.getDescription()));
        document.append("网址", product.getUrl());
        if (collection.count(document) > 0) {
            collection.findOneAndDelete(document);
        }
    }

    @Override
    public ResObj getNormalLevel(List<Product> list, String level) throws IOException {
        List<Product> timeOutList = new ArrayList<>();
        List<Product> save5List = new ArrayList<>();
        List<Product> realList = new ArrayList<>();
        for (Product product : list) {
            String Pcode = product.getCode();
            String url = product.getUrl();
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(7000).get();
                Elements villagetr = document.getElementsByClass("villagetr");
                if (villagetr.size() > 0) {
                    save5List.add(product);
                    continue;
                }
                List<String> classList = new ArrayList<>();
                classList.add("citytr");
                classList.add("countytr");
                classList.add("towntr");
                for (String s : classList) {
                    Elements elements = document.getElementsByClass(s);
                    if (elements == null) {
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
                        Product pro = new Product(name, code, Pcode, "", jumpUrl);
                        if (code.length() == 8 || code.length() == 10) {
                            save5List.add(pro);
                        } else {
                            realList.add(pro);
                            //todo
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("延时:" + url);
                timeOutList.add(product);
            }
        }
        saveProductList(realList);
        return new ResObj(save5List, new ProductTimeOut(timeOutList, level));
    }

    @Override
    public ProductTimeOut getEndLevel(List<Product> list) throws IOException {
        List<Product> timeOutList = new ArrayList<>();
        List<Product> saveList = new ArrayList<>();
        for (Product product : list) {
            String pCode = product.getCode();
            String url = product.getUrl();
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).timeout(7000).get();
                Elements elements = document.select("tr.villagetr");
                for (Element element : elements) {
                    String code5 = element.select("td:nth-child(1)").text();
                    String description = elements.select("td:nth-child(3)").text();
                    Product product4 = new Product(elements.select("td:nth-child(2)").text(), code5, pCode, description, "完");
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

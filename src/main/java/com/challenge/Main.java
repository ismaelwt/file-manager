package com.challenge;

import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.model.ShoppingCart;
import com.google.gson.Gson;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final String FILE_INPUT = "files";

    public static void main(String[] args) throws URISyntaxException, IOException {

        Main app = new Main();
        List<File> result = app.getAllFilesFromResource(FILE_INPUT);

        for (File file : result) {
            InputStream is = app.getFileFromResourceAsStream(FILE_INPUT + "/" + file.getName());
            createFile(doProcess(is), file.getName());
        }
    }

    private static String doProcess(InputStream is) {
        List<ShoppingCart> shopping = new ArrayList<>();
        List<Order> orders = new ArrayList<>();

        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {

                var substringAux = line.substring(10, line.length() - 1);
                var orderId = getOrderId(substringAux);
                var productValue = getSubstring(line);
                var productId = getProductId(substringAux);

                var userId = getUserId(line);
                var date = getDate(line);
                var username = getUsername(substringAux);

                ShoppingCart cart = new ShoppingCart();
                cart.setName(username);
                cart.setUserId(userId);

                Order o = new Order(orderId, 0.0, date, List.of(new Product(productId, Double.parseDouble(productValue.replaceAll(" ", "")))));
                handleOrder(orders, orderId, productValue, productId, o);
                handleShopping(shopping, userId, cart, o);
            }

            return new Gson().toJson(shopping);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void handleShopping(List<ShoppingCart> shopping, int userId, ShoppingCart cart, Order o) {
        if (shopping.isEmpty()) {
            cart.setOrders(List.of(o));
            shopping.add(cart);
        } else {

            var cartUser = shopping.stream().filter(c -> c.getUserId().equals(userId)).findFirst();

            if (cartUser.isPresent()) {

                var orderList = cartUser.get().getOrders();
                List<Order> arr = new ArrayList<>();
                arr.addAll(orderList);
                arr.add(o);
            } else {
                cart.setOrders(List.of(o));
                shopping.add(cart);
            }
        }
    }

    private static void handleOrder(List<Order> orders, int orderId, String productValue, int productId, Order o) {
        if (orders.size() == 0) {
            orders.add(o);
        } else {

            var order = orders.stream().filter(e -> e.getOrderId().equals(orderId)).findFirst();

            if (order.isPresent()) {

                var productList = order.get().getProducts();
                List<Product> arr = new ArrayList<>();
                arr.addAll(productList);
                arr.add(new Product(productId, Double.parseDouble(productValue.replaceAll(" ", ""))));
                order.get().setTotal(arr.stream().mapToDouble(e -> e.getValue()).sum());
                order.get().setProducts(arr);
            } else {
                orders.add(o);
            }
        }
    }

    private static void createFile(String json, String fileName) throws IOException {
        FileWriter myWriter = new FileWriter(fileName);
        myWriter.write(json);
        myWriter.close();
    }

    private List<File> getAllFilesFromResource(String folder)
            throws URISyntaxException, IOException {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(folder);

        List<File> collect = Files.walk(Paths.get(resource.toURI()))
                .filter(Files::isRegularFile)
                .map(x -> x.toFile())
                .collect(Collectors.toList());

        return collect;
    }

    private InputStream getFileFromResourceAsStream(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    private static String getUsername(String substringAux) {
        return substringAux.substring(0, substringAux.indexOf("0")).replaceAll("\\s+", " ");
    }

    private static int getDate(String line) {
        return Integer.parseInt(line.substring(line.length() - 8));
    }

    private static int getUserId(String line) {
        return Integer.parseInt(line.substring(0, 10));
    }

    private static int getProductId(String substringAux) {
        return Integer.parseInt(substringAux.substring(substringAux.indexOf("0") + 10, substringAux.indexOf("0") + 20));
    }

    private static String getSubstring(String line) {
        return line.substring((line.length()) - 16, line.length() - 8);
    }

    private static int getOrderId(String substringAux) {
        return Integer.parseInt(substringAux.substring(substringAux.indexOf("0"), substringAux.indexOf("0") + 10));
    }
}
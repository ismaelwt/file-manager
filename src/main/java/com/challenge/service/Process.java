package com.challenge.service;

import com.challenge.Main;
import com.challenge.model.Order;
import com.challenge.model.Product;
import com.challenge.model.ShoppingCart;
import com.google.gson.Gson;
import jakarta.inject.Inject;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Process {

    @Inject
    private ShoppingService shoppingService;

    public Process(ShoppingService shoppingService) {
        this.shoppingService = shoppingService;
    }

    public String doProcess(InputStream is) {
        List<ShoppingCart> shopping = new ArrayList<>();
        List<Order> orders = new ArrayList<>();

        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {

                var substringAux = line.substring(10, line.length() - 1);
                var orderId = this.shoppingService.getOrderId(substringAux);
                var productValue = this.shoppingService.getSubstring(line);
                var productId = this.shoppingService.getProductId(substringAux);

                var userId = this.shoppingService.getUserId(line);
                var date = this.shoppingService.getDate(line);
                var username = this.shoppingService.getUsername(substringAux);

                ShoppingCart cart = new ShoppingCart();
                cart.setName(username);
                cart.setUserId(Integer.parseInt(userId));

                Order o = new Order(Integer.parseInt(orderId), 0.0, Integer.parseInt(date), List.of(new Product(Integer.parseInt(productId), Double.parseDouble(productValue.replaceAll(" ", "")))));
                handleOrder(orders, Integer.parseInt(orderId), productValue, Integer.parseInt(productId), o);
                handleShopping(shopping, Integer.parseInt(userId), cart, o);
            }

            return new Gson().toJson(shopping);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleShopping(List<ShoppingCart> shopping, int userId, ShoppingCart cart, Order o) {
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

    private void handleOrder(List<Order> orders, int orderId, String productValue, int productId, Order o) {
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

    public void createFile(String json, String fileName) throws IOException {
        System.out.println("##### -------- FIM DO PROCESSO " + fileName + " --------#######");
        var finalName = fileName.replaceAll("txt", "json");
        FileWriter myWriter = new FileWriter(new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "out", finalName));
        myWriter.write(json);
        myWriter.close();
        System.out.println("##### -------- " + finalName + " CRIADO COM SUCESSO --------#######");
    }

    public List<File> getAllFilesFromResource(String folder)
            throws URISyntaxException, IOException {

        ClassLoader classLoader = Main.class.getClassLoader();

        URL resource = classLoader.getResource(folder);

        List<File> collect = Files.walk(Paths.get(resource.toURI()))
                .filter(Files::isRegularFile)
                .map(x -> x.toFile())
                .collect(Collectors.toList());

        return collect;
    }

    public List<Path> getPathsFromResourceJAR(String folder)
            throws URISyntaxException, IOException {

        List<Path> paths = new ArrayList<>();

        URI uri = getClass().getClassLoader().getResource(folder).toURI();
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            myPath = fileSystem.getPath(folder);
        } else {
            myPath = Paths.get(uri);
        }
        Stream<Path> walk = Files.walk(myPath, 1);

        for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
            Path next = it.next();
            if (next.toString().contains("txt"))
                paths.add(next);
        }

        return paths;

    }

    public InputStream getFileFromResourceAsStream(String fileName) {

        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }
}

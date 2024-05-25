package com.challenge.service;

public class ShoppingService {

    public String getUsername(String substringAux) {
        return substringAux.substring(0, substringAux.indexOf("0")).replaceAll("\\s+", " ");
    }

    public String getDate(String line) {
        return line.substring(line.length() - 8);
    }

    public String getUserId(String line) {
        return line.substring(0, 10);
    }

    public String getProductId(String substringAux) {
        return substringAux.substring(substringAux.indexOf("0") + 10, substringAux.indexOf("0") + 20);
    }

    public String getSubstring(String line) {
        return line.substring((line.length()) - 16, line.length() - 8);
    }

    public String getOrderId(String substringAux) {
        return substringAux.substring(substringAux.indexOf("0"), substringAux.indexOf("0") + 10);
    }
}

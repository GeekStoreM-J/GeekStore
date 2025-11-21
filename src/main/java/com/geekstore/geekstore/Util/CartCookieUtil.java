package com.geekstore.geekstore.Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CartCookieUtil {

    private static final String CART_COOKIE_NAME = "CART";

    public static class CartItem {
        public Long productId;
        public int quantity;

        public CartItem() {
        }

        public CartItem(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }

    public static List<CartItem> getCart(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (CART_COOKIE_NAME.equals(cookie.getName())) {
                        ObjectMapper mapper = new ObjectMapper();
                        String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        return mapper.readValue(decoded, new TypeReference<List<CartItem>>() {
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void saveCart(HttpServletResponse response, List<CartItem> cart) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(cart);
            // 🔹 Codifica o JSON para evitar caracteres inválidos no cookie
            String encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8);
            Cookie cookie = new Cookie(CART_COOKIE_NAME, encodedJson);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7 dias
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
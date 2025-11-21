package com.geekstore.geekstore.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekstore.geekstore.Model.Order;
import com.geekstore.geekstore.Model.OrderItem;
import com.geekstore.geekstore.Model.Product;
import com.geekstore.geekstore.Model.User;
import com.geekstore.geekstore.Service.OrderService;
import com.geekstore.geekstore.Service.ProductService;
import com.geekstore.geekstore.Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public CartController(ProductService productService,
            OrderService orderService,
            UserService userService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    // ===================== CLASSES AUXILIARES =====================
    public static class CartItem {
        private Long productId;
        private int quantity;

        public CartItem() {
        }

        public CartItem(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    public static class CartProduct {
        private final Product product;
        private final int quantity;

        public CartProduct(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    // ===================== MÉTODOS AUXILIARES =====================
    @SuppressWarnings("unchecked")
    private List<CartItem> getCartFromSession(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private void saveCartToSession(HttpSession session, List<CartItem> cart) {
        session.setAttribute("cart", cart);
    }

    private void saveCartToCookie(HttpServletResponse response, List<CartItem> cart) {
        try {
            String json = objectMapper.writeValueAsString(cart);
            String encoded = URLEncoder.encode(json, StandardCharsets.UTF_8);
            Cookie cookie = new Cookie("cart", encoded);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 dias
            response.addCookie(cookie);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private List<CartItem> getCartFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("cart".equals(cookie.getName())) {
                    try {
                        String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        return objectMapper.readValue(decoded, new TypeReference<List<CartItem>>() {
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private void syncSessionAndCookie(HttpSession session, HttpServletResponse response, List<CartItem> cart) {
        saveCartToSession(session, cart);
        saveCartToCookie(response, cart);
    }

    // ===================== ADICIONAR AO CARRINHO =====================
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            HttpServletRequest request,
            HttpServletResponse response) {

        HttpSession session = request.getSession();
        List<CartItem> cart = getCartFromSession(session);

        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            cart.add(new CartItem(productId, quantity));
        }

        syncSessionAndCookie(session, response, cart);
        return "redirect:/cart";
    }

    // ===================== REMOVER DO CARRINHO =====================
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("productId") Long productId,
            HttpServletRequest request,
            HttpServletResponse response) {

        HttpSession session = request.getSession();
        List<CartItem> cart = getCartFromSession(session);

        cart.removeIf(item -> item.getProductId().equals(productId));

        syncSessionAndCookie(session, response, cart);
        return "redirect:/cart";
    }

    // ===================== EXIBIR CARRINHO =====================
    @GetMapping
    public String viewCart(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        List<CartItem> cart = getCartFromSession(session);

        // Se a sessão estiver vazia, tenta carregar do cookie
        if (cart.isEmpty()) {
            cart = getCartFromCookie(request);
            saveCartToSession(session, cart);
        }

        List<CartProduct> cartProducts = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cart) {
            Optional<Product> productOpt = productService.findById(item.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(total);
                cartProducts.add(new CartProduct(product, item.getQuantity()));
            }
        }

        model.addAttribute("cartItems", cartProducts);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", subtotal); // frete grátis por enquanto
        return "product/cart";
    }

    // ===================== FINALIZAR COMPRA =====================
    @GetMapping("/checkout")
    public String checkout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(auth.getName());
        HttpSession session = request.getSession();
        List<CartItem> cart = getCartFromSession(session);

        if (cart.isEmpty()) {
            // Tenta carregar do cookie
            cart = getCartFromCookie(request);
            if (cart.isEmpty()) {
                return "redirect:/cart";
            }
        }

        // Criar pedido
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cart) {
            Product product = productService.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + item.getProductId()));

            OrderItem orderItem = new OrderItem(order, product, item.getQuantity(), product.getPrice());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Salvar pedido no banco
        orderService.save(order);

        // Limpar carrinho
        cart.clear();
        syncSessionAndCookie(session, response, cart);

        // Redirecionar para confirmação com o ID do pedido
        return "redirect:/orders/confirmation/" + order.getId();
    }
}
package com.geekstore.geekstore.Controller;

import com.geekstore.geekstore.Model.Product;
import com.geekstore.geekstore.Service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Redireciona a raiz "/" para a lista de produtos.
     */
    @GetMapping("/")
    public String redirectToProducts() {
        return "redirect:/products";
    }

    /**
     * Lista todos os produtos.
     * URL: /products
     */
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/index"; // view: templates/product/index.html
    }

    /**
     * Mostra detalhes de um produto específico.
     * URL: /products/{id}
     */
    @GetMapping("/products/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));
        model.addAttribute("product", product);
        return "product/show"; // view: templates/product/show.html
    }

    /**
     * Exibe o carrinho de compras.
     * URL: /cart
     */
    @GetMapping("/cart")
    public String cart() {
        return "product/cart"; // templates/product/cart.html
    }
}
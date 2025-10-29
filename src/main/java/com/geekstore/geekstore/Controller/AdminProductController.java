package com.geekstore.geekstore.Controller;

import com.geekstore.geekstore.Model.Product;
import com.geekstore.geekstore.Service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    // ===== LISTAGEM DE PRODUTOS =====
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "admin/product/index"; // view: templates/admin/product/index.html
    }

    // ===== FORMULÁRIO DE CRIAÇÃO =====
    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product/form"; // view: templates/admin/product/form.html
    }

    // ===== SALVAR PRODUTO =====
    @PostMapping
    public String saveProduct(@ModelAttribute("product") Product product) {
        productService.save(product);
        return "redirect:/admin/products";
    }

    // ===== FORMULÁRIO DE EDIÇÃO =====
    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));
        model.addAttribute("product", product);
        return "admin/product/form"; // mesmo formulário para criar/editar
    }

    // ===== DELETAR PRODUTO =====
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/admin/products";
    }

    // ===== DETALHES DO PRODUTO (OPCIONAL) =====
    @GetMapping("/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));
        model.addAttribute("product", product);
        return "admin/product/show"; // view: templates/admin/product/show.html
    }
}

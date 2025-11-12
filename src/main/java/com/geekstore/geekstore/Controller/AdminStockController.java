package com.geekstore.geekstore.Controller;

import com.geekstore.geekstore.Model.Stock;
import com.geekstore.geekstore.Model.Product;
import com.geekstore.geekstore.Service.StockService;
import com.geekstore.geekstore.Service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/stocks")
public class AdminStockController {

    private final StockService stockService;
    private final ProductService productService;

    public AdminStockController(StockService stockService, ProductService productService) {
        this.stockService = stockService;
        this.productService = productService;
    }

    // ===== LISTAR ESTOQUES =====
    @GetMapping
    public String listStocks(Model model) {
        model.addAttribute("stocks", stockService.findAll());
        model.addAttribute("products", productService.findAll()); // necessário para o select no modal
        return "admin/stock/index"; // Template: src/main/resources/templates/admin/stock/index.html
    }

    // ===== CRIAR NOVO ESTOQUE =====
    @PostMapping
    public String saveStock(@ModelAttribute("stock") Stock stock, Model model) {
        try {
            // Garante que o produto existe
            Product product = productService.findById(stock.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto inválido."));

            stock.setProduct(product);
            stockService.save(stock);

            return "redirect:/admin/stocks";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Erro ao salvar estoque: " + e.getMessage());
            return "redirect:/admin/stocks";
        }
    }

    // ===== ATUALIZAR ESTOQUE =====
    @PostMapping("/edit/{id}")
    public String updateStock(
            @PathVariable Long id,
            @ModelAttribute("stock") Stock updatedStock,
            Model model) {
        try {
            Stock existingStock = stockService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Estoque inválido: " + id));

            Product product = productService.findById(updatedStock.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto inválido."));

            // Atualiza os campos
            existingStock.setProduct(product);
            existingStock.setQuantity(updatedStock.getQuantity());

            stockService.save(existingStock);
            return "redirect:/admin/stocks";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Erro ao atualizar estoque: " + e.getMessage());
            return "redirect:/admin/stocks";
        }
    }

    // ===== DELETAR ESTOQUE =====
    @GetMapping("/delete/{id}")
    public String deleteStock(@PathVariable Long id) {
        stockService.deleteById(id);
        return "redirect:/admin/stocks";
    }
}

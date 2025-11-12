package com.geekstore.geekstore.Controller;

import com.geekstore.geekstore.Model.Product;
import com.geekstore.geekstore.Service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    return "admin/product/index";
  }

  // ===== CRIAR PRODUTO =====
  @PostMapping
  public String saveProduct(
      @ModelAttribute("product") Product product,
      @RequestParam(value = "image", required = false) MultipartFile fileImage,
      Model model) {
    try {
      String imageUrl = productService.saveImageUrl(fileImage);
      if (imageUrl != null) {
        product.setImageUrl(imageUrl);
      }

      productService.save(product);
      return "redirect:/admin/products";

    } catch (IOException | SecurityException e) {
      e.printStackTrace();
      model.addAttribute("errorMessage", "Erro ao salvar produto/imagem: " + e.getMessage());
      return "redirect:/admin/products";
    }
  }

  // ===== ATUALIZAR PRODUTO (EDIÇÃO) =====
  @PostMapping("/edit/{id}")
  public String updateProduct(
      @PathVariable Long id,
      @ModelAttribute("product") Product updatedProduct,
      @RequestParam(value = "image", required = false) MultipartFile fileImage,
      Model model) {
    try {
      // 1. Busca o produto existente
      Product existingProduct = productService.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));

      // 2. Atualiza os campos editáveis
      existingProduct.setName(updatedProduct.getName());
      existingProduct.setPrice(updatedProduct.getPrice());
      existingProduct.setCategory(updatedProduct.getCategory());

      // 3. Atualiza imagem se houver nova
      if (fileImage != null && !fileImage.isEmpty()) {
        String imageUrl = productService.saveImageUrl(fileImage);
        if (imageUrl != null) {
          existingProduct.setImageUrl(imageUrl);
        }
      }

      // 4. Salva novamente
      productService.save(existingProduct);
      return "redirect:/admin/products";

    } catch (IOException | SecurityException e) {
      e.printStackTrace();
      model.addAttribute("errorMessage", "Erro ao atualizar produto/imagem: " + e.getMessage());
      return "redirect:/admin/products";
    }
  }

  // ===== DELETAR PRODUTO =====
  @GetMapping("/delete/{id}")
  public String deleteProduct(@PathVariable Long id) {
    productService.deleteById(id);
    return "redirect:/admin/products";
  }
}
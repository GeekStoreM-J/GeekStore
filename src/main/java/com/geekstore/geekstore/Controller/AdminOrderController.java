package com.geekstore.geekstore.Controller;

import com.geekstore.geekstore.Model.Order;
import com.geekstore.geekstore.Service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ===== LISTAGEM DE PEDIDOS =====
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "admin/order/index"; // Template: src/main/resources/templates/admin/orders/index.html
    }

    // ===== VISUALIZAR DETALHES DE UM PEDIDO =====
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        model.addAttribute("order", order);
        model.addAttribute("items", order.getItems());
        return "admin/order/view"; // (página futura de detalhes)
    }

    // ===== ATUALIZAR STATUS DO PEDIDO =====
    @PostMapping("/update-status")
    public String updateOrderStatus(
            @RequestParam("id") Long id,
            @RequestParam("status") String status,
            Model model
    ) {
        try {
            Order order = orderService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido inválido: " + id));

            order.setStatus(status);
            orderService.save(order);

            return "redirect:/admin/orders";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Erro ao atualizar status: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    // ===== DELETAR PEDIDO (OPCIONAL) =====
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return "redirect:/admin/orders";
    }
}

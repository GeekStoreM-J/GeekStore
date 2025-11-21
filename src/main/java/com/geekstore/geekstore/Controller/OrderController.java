package com.geekstore.geekstore.Controller;

import com.geekstore.geekstore.Model.Order;
import com.geekstore.geekstore.Service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Página de confirmação de pedido (após checkout)
    @GetMapping("/confirmation/{id}")
    public String orderConfirmation(@PathVariable Long id, Model model) {
        Optional<Order> orderOpt = orderService.findById(id);

        if (orderOpt.isEmpty()) {
            // redireciona se o pedido não existir
            return "redirect:/cart";
        }

        Order order = orderOpt.get();
        model.addAttribute("order", order);
        return "order/confirmation";
    }
}

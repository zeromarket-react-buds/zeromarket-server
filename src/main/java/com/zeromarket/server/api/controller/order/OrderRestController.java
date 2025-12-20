package com.zeromarket.server.api.controller.order;

import com.zeromarket.server.api.dto.order.CreateOrderRequest;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.order.OrderServiceImpl;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//POST   /api/orders
//POST   /api/orders/{orderId}/delivery-ready
//POST   /api/orders/{orderId}/ship
//POST   /api/orders/{orderId}/complete
//POST   /api/orders/{orderId}/cancel

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ResponseEntity<Map> create(
        @RequestBody CreateOrderRequest req,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long orderId = orderService.createOrder(req, userDetails.getMemberId());
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }

    @GetMapping("/{orderId}/complete")
    public ResponseEntity<?> getOrderComplete(
        @PathVariable Long orderId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
            orderService.selectOrderComplete(orderId, user.getMemberId())
        );
    }


//    @PostMapping("/{orderId}/delivery-ready")
//    public void deliveryReady(@PathVariable Long orderId) {
//        orderService.markDeliveryReady(orderId);
//    }

//    @PostMapping("/{orderId}/ship")
//    public void ship(@PathVariable Long orderId) {
//        orderService.ship(orderId);
//    }
//
//    @PostMapping("/{orderId}/complete")
//    public void complete(@PathVariable Long orderId) {
//        orderService.complete(orderId);
//    }
//
//    @PostMapping("/{orderId}/cancel")
//    public void cancel(@PathVariable Long orderId) {
//        orderService.cancel(orderId);
//    }
}


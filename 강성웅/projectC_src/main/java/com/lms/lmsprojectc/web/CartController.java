package com.lms.lmsprojectc.web;


import com.lms.lmsprojectc.entity.Cart;
import com.lms.lmsprojectc.service.CartService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController @RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;


    @GetMapping
    public List<Cart> list(@RequestParam Long userId) { return cartService.findByUser(userId); }


    @PostMapping
    public Cart add(@RequestBody AddCartRequest req) {
        return cartService.add(req.userId(), req.courseId());
    }


    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> remove(@PathVariable Long cartId) {
        cartService.remove(cartId);
        return ResponseEntity.noContent().build();
    }


    public record AddCartRequest(Long userId, Long courseId) {}
}

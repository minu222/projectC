package com.example.projectc.entity;


import com.example.projectc.domain.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;


@Entity @Table(name="orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Column(precision = 10, scale = 2)
    private BigDecimal total_amount;


    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.pending;


    private String payment_method; // e.g. card
    private LocalDateTime created_at;
    private LocalDateTime refunded_at;
    @Lob private String refund_reason;
}

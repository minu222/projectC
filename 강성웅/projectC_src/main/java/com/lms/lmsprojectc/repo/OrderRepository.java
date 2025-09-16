package com.lms.lmsprojectc.repo;

import com.lms.lmsprojectc.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}

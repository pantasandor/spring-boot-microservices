package hu.pantasandor.orderservice.repository;

import hu.pantasandor.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

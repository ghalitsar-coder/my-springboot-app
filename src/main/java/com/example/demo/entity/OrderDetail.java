package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orderdetails", schema = "public")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;      @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference("order-orderDetails")
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference("product-orderDetails")
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;      @OneToMany(mappedBy = "orderDetail")
    @JsonManagedReference("orderDetail-customizations")
    private List<OrderCustomization> orderCustomizations;
    
    // Constructors
    public OrderDetail() {}
    
    public OrderDetail(Order order, Product product, Integer quantity, BigDecimal unitPrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    // Getters and Setters
    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    
    public List<OrderCustomization> getOrderCustomizations() { return orderCustomizations; }
    public void setOrderCustomizations(List<OrderCustomization> orderCustomizations) { this.orderCustomizations = orderCustomizations; }
}

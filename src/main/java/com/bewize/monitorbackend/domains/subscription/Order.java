package com.bewize.monitorbackend.domains.subscription;

import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.OrderType;
import com.bewize.monitorbackend.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private String code;

    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    @CreatedDate
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    private Discount discount;

    private Float amount;
 
    private String transactionId;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Subscription subscription;

    public static Float calculateAmount(Float initialPrice, Discount discount){
        if(Objects.isNull(discount)){
            return initialPrice;
        }
        return initialPrice - (initialPrice * discount.getPercentage()/100) ;
    }
    public static Order create(Discount discount, Student student, OrderType type, PlanType planType, OrderStatus status, float price){
        return Order.builder()
                .discount(discount)
                .student(student)
                .type(type)
                .status(status)
                .date(LocalDateTime.now())
                .amount(price)
                .planType(planType)
                .transactionId(UUID.randomUUID().toString())
                .build();
    }


}

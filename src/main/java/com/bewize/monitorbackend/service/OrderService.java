package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.discount.DiscountDto;
import com.bewize.monitorbackend.dto.order.OrderListDto;
import com.bewize.monitorbackend.dto.student.StudentDto;
import com.bewize.monitorbackend.dto.subscription.SubscriptionDto;
import com.bewize.monitorbackend.mappers.OrderMapper;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.projection.OrderListProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public PageResponse<OrderListDto> getOrders(Pageable pageable) {
        Page<OrderListProjection> page = orderRepository.findAllProjectedBy(pageable);

        List<OrderListDto> data = page.getContent().stream().map(p -> {
            OrderListDto dto = new OrderListDto();

            // base order fields (OrderDto fields)
            dto.setId(p.getId());
            dto.setCode(p.getCode());
            dto.setType(p.getType());
            dto.setStatus(p.getStatus());
            dto.setPlanType(p.getPlanType());
            dto.setDate(p.getDate());
            dto.setAmount(p.getAmount());

            // subscription (if present)
            if (p.getSubscription() != null) {
                SubscriptionDto sub = new SubscriptionDto();
                sub.setId(p.getSubscription().getId());
                sub.setStartDate(p.getSubscription().getStartDate());
                sub.setEndDate(p.getSubscription().getEndDate());
                dto.setSubscription(sub);
            }

            // nested student (OrderListDto extends OrderDto and adds student)
            if (p.getStudent() != null) {
                StudentDto student = new StudentDto();
                student.setId(p.getStudent().getId());
                student.setFirstName(p.getStudent().getFirstName());
                student.setLastName(p.getStudent().getLastName());
                student.setEmail(p.getStudent().getEmail());
                dto.setStudent(student);
            }

            // nested discount
            if (p.getDiscount() != null) {
                DiscountDto discount = new DiscountDto();
                discount.setId(p.getDiscount().getId());
                discount.setPercentage(p.getDiscount().getPercentage());
                dto.setDiscount(discount);
            }

            return dto;
        }).toList();

        PageResponse.Meta meta = new PageResponse.Meta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return new PageResponse<>(data, meta);
    }
}

package com.bewize.monitorbackend.service;

import java.util.List;
import java.util.stream.Collectors;

import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.repository.projection.SubscriptionListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bewize.monitorbackend.domains.subscription.Subscription;
import com.bewize.monitorbackend.dto.subscription.SubscriptionCreateRequest;
import com.bewize.monitorbackend.dto.subscription.SubscriptionListDto;
import com.bewize.monitorbackend.dto.subscription.SubscriptionUpdateRequest;
import com.bewize.monitorbackend.mappers.SubscriptionMapper;
import com.bewize.monitorbackend.repository.SubscriptionRepository;
import com.bewize.monitorbackend.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final OrderRepository orderRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional(readOnly = true)
    public PageResponse<SubscriptionListDto> getSubscriptions(Pageable pageable) {

        Page<SubscriptionListProjection> page = subscriptionRepository.findAllProjectedBy(pageable);

        List<SubscriptionListDto> data = page.getContent().stream().map(p -> {
            SubscriptionListDto dto = new SubscriptionListDto();
            dto.setId(p.getId());
            dto.setStartDate(p.getStartDate());
            dto.setEndDate(p.getEndDate());

            if (p.getOrder() != null) {
                dto.setOrderId(p.getOrder().getId());
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

    @Transactional
    public SubscriptionListDto createSubscription(SubscriptionCreateRequest req) {
        Subscription entity = subscriptionMapper.toEntity(req);
        if (req.getOrderId() != null && !req.getOrderId().isBlank()) {
            entity.setOrder(orderRepository.getReferenceById(req.getOrderId()));
        }
        Subscription saved = subscriptionRepository.save(entity);
        log.info("created subscription {}", saved.getId());
        return subscriptionMapper.toListDto(saved);
    }

    @Transactional
    public SubscriptionListDto updateSubscription(String id, SubscriptionUpdateRequest req) {
        Subscription existing = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscriptionMapper.updateFromUpdateRequest(req, existing);

        if (req.getOrderId() != null) {
            if (req.getOrderId().isBlank()) {
                existing.setOrder(null);
            } else {
                existing.setOrder(orderRepository.getReferenceById(req.getOrderId()));
            }
        }

        Subscription saved = subscriptionRepository.save(existing);
        log.info("updated subscription {}", saved.getId());
        return subscriptionMapper.toListDto(saved);
    }

    @Transactional
    public void deleteSubscription(String id) {
        subscriptionRepository.deleteById(id);
        log.info("deleted subscription {}", id);
    }
}

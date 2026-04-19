package com.bewize.monitorbackend.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.subscription.ManualSubscriptionCreateRequest;
import com.bewize.monitorbackend.dto.subscription.ManualSubscriptionResponse;
import com.bewize.monitorbackend.dto.subscription.ManualSubscriptionStudentOptionDto;
import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.OrderType;
import com.bewize.monitorbackend.enums.PlanType;
import com.bewize.monitorbackend.repository.projection.SubscriptionListProjection;
import com.bewize.monitorbackend.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final StudentRepository studentRepository;
    private final SubscriptionMapper subscriptionMapper;

    private static final Set<PlanType> ALLOWED_MANUAL_PLAN_TYPES = Set.of(
            PlanType.YEAR,
            PlanType.SEMESTER,
            PlanType.TRIMESTER,
            PlanType.MONTH);

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
                page.getTotalPages());

        return new PageResponse<>(data, meta);
    }

    @Transactional(readOnly = true)
    public SubscriptionListDto getSubscriptionById(String id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Subscription not found"));
        return subscriptionMapper.toListDto(subscription);
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

    @Transactional(readOnly = true)
    public List<ManualSubscriptionStudentOptionDto> getManualSubscriptionStudents() {
        List<Student> students = studentRepository.findAll(Sort.by(Sort.Direction.ASC, "firstName", "lastName"));

        return students.stream()
                .map(student -> {
                    String firstName = student.getFirstName() == null ? "" : student.getFirstName().trim();
                    String lastName = student.getLastName() == null ? "" : student.getLastName().trim();
                    String fullName = (firstName + " " + lastName).trim();
                    String displayName = fullName.isBlank() ? "Etudiant" : fullName;
                    return new ManualSubscriptionStudentOptionDto(student.getId(), displayName);
                })
                .toList();
    }

    @Transactional
    public ManualSubscriptionResponse createManualSubscription(ManualSubscriptionCreateRequest req) {
        if (req.getStudentId() == null || req.getStudentId().isBlank()) {
            throw new RuntimeException("Student is required");
        }
        if (req.getStartDate() == null) {
            throw new RuntimeException("Start date is required");
        }
        if (req.getPlanType() == null || !ALLOWED_MANUAL_PLAN_TYPES.contains(req.getPlanType())) {
            throw new RuntimeException("Invalid plan duration");
        }

        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Subscription> activeSubscriptions = subscriptionRepository
                .findActiveSubscriptionsByStudentId(student.getId(), java.time.LocalDateTime.now());

        for (Subscription activeSubscription : activeSubscriptions) {
            activeSubscription.setEndDate(req.getStartDate().minusSeconds(1));
            subscriptionRepository.save(activeSubscription);
        }

        Order order = new Order();
        order.setCode("MANUAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setType(OrderType.CASH_PLUS);
        order.setStatus(OrderStatus.PAID);
        order.setPlanType(req.getPlanType());
        order.setDate(java.time.LocalDateTime.now());
        order.setStudent(student);
        order.setAmount(0f);
        order.setTransactionId(UUID.randomUUID().toString());

        Order savedOrder = orderRepository.save(order);

        Subscription subscription = new Subscription();
        subscription.setOrder(savedOrder);
        subscription.setStartDate(req.getStartDate());
        subscription.setEndDate(calculateEndDate(req.getStartDate(), req.getPlanType()));

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return new ManualSubscriptionResponse(
                savedSubscription.getId(),
                savedSubscription.getStartDate(),
                savedSubscription.getEndDate(),
                savedOrder.getId());
    }

    private java.time.LocalDateTime calculateEndDate(java.time.LocalDateTime startDate, PlanType planType) {
        return switch (planType) {
            case YEAR -> startDate.plusYears(1);
            case SEMESTER -> startDate.plusMonths(6);
            case TRIMESTER -> startDate.plusMonths(3);
            case MONTH -> startDate.plusMonths(1);
            default -> throw new RuntimeException("Unsupported plan duration");
        };
    }
}

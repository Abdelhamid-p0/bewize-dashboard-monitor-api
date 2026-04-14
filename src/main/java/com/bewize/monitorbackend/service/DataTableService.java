package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Discount;
import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.domains.subscription.Subscription;
import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.enums.Cycle;
import com.bewize.monitorbackend.enums.Gender;
import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.PlanType;
import com.bewize.monitorbackend.repository.DiscountRepository;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import com.bewize.monitorbackend.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DataTableService {

    private static final Set<String> STUDENT_ALLOWED_FIELDS = Set.of(
            "id", "name", "phone", "subscriptionType", "planType", "deviceSystem", "gender", "signupDate", "level");
    private static final Set<String> ORDER_ALLOWED_FIELDS = Set.of(
            "id", "student", "plan", "type", "paymentMethod", "status", "date");
    private static final Set<String> SUBSCRIPTION_ALLOWED_FIELDS = Set.of(
            "id", "cne", "startDate", "endDate", "planType", "subscriptionType", "status");
    private static final Set<String> DISCOUNT_ALLOWED_FIELDS = Set.of(
            "id", "code", "startDate", "endDate", "percentage", "status");

    private final StudentRepository studentRepository;
    private final OrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DiscountRepository discountRepository;

    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getStudentRows(
            Pageable pageable,
            List<String> fields,
            String search,
            String gender,
            String cycle,
            String level,
            String type,
            String planType) {
        Set<String> selectedFields = resolveFields(fields, STUDENT_ALLOWED_FIELDS);

        Specification<Student> spec = Specification.where(null);

        if (hasText(search)) {
            String like = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("firstName")), like),
                    cb.like(cb.lower(root.get("lastName")), like),
                    cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("phone")), like),
                    cb.like(cb.lower(root.get("cne")), like)));
        }

        if (hasText(gender)) {
            try {
                Gender g = Gender.valueOf(gender.trim().toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("gender"), g));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid enum value to keep endpoint tolerant.
            }
        }

        if (hasText(cycle)) {
            try {
                Cycle cycleValue = Cycle.valueOf(cycle.trim().toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.join("level").get("cycle"), cycleValue));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid enum value to keep endpoint tolerant.
            }
        }

        if (hasText(level)) {
            String normalized = level.trim().toLowerCase();
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.join("level").get("levelName")),
                    "%" + normalized + "%"));
        }

        if (hasText(planType) || hasText(type)) {
            String planFilter = hasText(planType) ? planType : type;
            Optional<PlanType> plan = parsePlanType(planFilter);
            if (plan.isPresent()) {
                PlanType planTypeValue = plan.get();
                spec = spec.and((root, query, cb) -> {
                    var latestOrderDateSubquery = query.subquery(LocalDateTime.class);
                    var latestOrderDateRoot = latestOrderDateSubquery.from(Order.class);
                    latestOrderDateSubquery.select(cb.greatest(latestOrderDateRoot.get("date")));
                    latestOrderDateSubquery.where(cb.equal(latestOrderDateRoot.get("student"), root));

                    var latestOrderPlanSubquery = query.subquery(Integer.class);
                    var latestOrderPlanRoot = latestOrderPlanSubquery.from(Order.class);
                    latestOrderPlanSubquery.select(cb.literal(1));
                    latestOrderPlanSubquery.where(
                            cb.equal(latestOrderPlanRoot.get("student"), root),
                            cb.equal(latestOrderPlanRoot.get("date"), latestOrderDateSubquery),
                            cb.equal(latestOrderPlanRoot.get("planType"), planTypeValue));

                    return cb.exists(latestOrderPlanSubquery);
                });
            } else {
                // Strict filtering: invalid plan value must return no rows.
                spec = spec.and((root, query, cb) -> cb.disjunction());
            }
        }

        // Exclude placeholder students where all core identity fields are empty.
        // Keep this predicate subquery-free to avoid slowing down every paginated
        // request.
        spec = spec.and((root, query, cb) -> cb.or(
                cb.isNotNull(root.get("firstName")),
                cb.isNotNull(root.get("lastName")),
                cb.isNotNull(root.get("phone")),
                cb.isNotNull(root.get("gender")),
                cb.isNotNull(root.get("singupDate")),
                cb.isNotNull(root.get("level")),
                cb.isNotNull(root.get("email")),
                cb.isNotNull(root.get("cne"))));

        Page<Student> page = studentRepository.findAll(spec, pageable);

        List<Map<String, Object>> data = page.getContent().stream().map(student -> {
            Map<String, Object> row = new HashMap<>();
            String latestPlanType = orderRepository.findLatestPlanTypeByStudentId(student.getId())
                    .map(Enum::name)
                    .orElse(null);

            put(row, selectedFields, "id", student.getId());
            put(row, selectedFields, "name", buildFullName(student.getFirstName(), student.getLastName()));
            put(row, selectedFields, "phone", student.getPhone());
            put(row, selectedFields, "subscriptionType", latestPlanType);
            put(row, selectedFields, "planType", latestPlanType);
            put(row, selectedFields, "deviceSystem", null);
            put(row, selectedFields, "gender", student.getGender() != null ? student.getGender().name() : null);
            put(row, selectedFields, "signupDate", student.getSingupDate());

            String levelLabel = null;
            if (student.getLevel() != null) {
                String levelName = student.getLevel().getLevelName();
                String cycleName = student.getLevel().getCycle() != null ? student.getLevel().getCycle().name() : null;
                levelLabel = cycleName != null ? levelName + " (" + cycleName + ")" : levelName;
            }
            put(row, selectedFields, "level", levelLabel);

            return row;
        }).toList();

        return new PageResponse<>(data, toMeta(page));
    }

    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getOrderRows(
            Pageable pageable,
            List<String> fields,
            String status,
            String planType,
            String search) {
        Set<String> selectedFields = resolveFields(fields, ORDER_ALLOWED_FIELDS);

        Specification<Order> spec = Specification.where(null);

        if (hasText(status)) {
            try {
                OrderStatus statusValue = OrderStatus.valueOf(status.trim().toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusValue));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid enum value to keep endpoint tolerant.
            }
        }

        if (hasText(planType)) {
            Optional<PlanType> parsedPlanType = parsePlanType(planType);
            if (parsedPlanType.isPresent()) {
                PlanType value = parsedPlanType.get();
                spec = spec.and((root, query, cb) -> cb.equal(root.get("planType"), value));
            } else {
                // Strict filtering: invalid plan value must return no rows.
                spec = spec.and((root, query, cb) -> cb.disjunction());
            }
        }

        if (hasText(search)) {
            String like = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("code")), like),
                    cb.like(cb.lower(root.join("student").get("firstName")), like),
                    cb.like(cb.lower(root.join("student").get("lastName")), like),
                    cb.like(cb.lower(root.join("student").get("email")), like)));
        }

        Page<Order> page = orderRepository.findAll(spec, pageable);

        List<Map<String, Object>> data = page.getContent().stream().map(order -> {
            Map<String, Object> row = new HashMap<>();

            put(row, selectedFields, "id", order.getId());

            String studentName = order.getStudent() != null
                    ? buildFullName(order.getStudent().getFirstName(), order.getStudent().getLastName())
                    : null;
            put(row, selectedFields, "student", studentName);
            put(row, selectedFields, "plan", order.getPlanType() != null ? order.getPlanType().name() : null);
            put(row, selectedFields, "type",
                    order.getAmount() != null && order.getAmount() == 0 ? "Gratuit" : "Payant");
            put(row, selectedFields, "paymentMethod", order.getType() != null ? order.getType().name() : null);
            put(row, selectedFields, "status", order.getStatus() != null ? order.getStatus().name() : null);
            put(row, selectedFields, "date", order.getDate());

            return row;
        }).toList();

        return new PageResponse<>(data, toMeta(page));
    }

    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getSubscriptionRows(
            Pageable pageable,
            List<String> fields,
            String orderId,
            Boolean active,
            String status,
            String type,
            String planType,
            String search,
            LocalDate startDate,
            LocalDate endDate) {
        Set<String> selectedFields = resolveFields(fields, SUBSCRIPTION_ALLOWED_FIELDS);

        Specification<Subscription> spec = Specification.where(null);

        if (hasText(orderId)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.join("order").get("id"), orderId.trim()));
        }

        if (active != null) {
            spec = spec.and((root, query, cb) -> active
                    ? cb.greaterThanOrEqualTo(root.get("endDate"), LocalDateTime.now())
                    : cb.lessThan(root.get("endDate"), LocalDateTime.now()));
        }

        if (hasText(status)) {
            String normalized = status.trim().toUpperCase();
            if (Objects.equals(normalized, "ACTIVE")) {
                spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("endDate"), LocalDateTime.now()));
            } else if (Objects.equals(normalized, "INACTIVE")) {
                spec = spec.and((root, query, cb) -> cb.lessThan(root.get("endDate"), LocalDateTime.now()));
            }
        }

        String effectivePlanType = hasText(planType) ? planType : type;
        if (hasText(effectivePlanType)) {
            Optional<PlanType> parsedPlanType = parsePlanType(effectivePlanType);
            if (parsedPlanType.isPresent()) {
                PlanType value = parsedPlanType.get();
                spec = spec.and((root, query, cb) -> cb.equal(root.join("order").get("planType"), value));
            } else {
                // Strict filtering: invalid plan value must return no rows.
                spec = spec.and((root, query, cb) -> cb.disjunction());
            }
        }

        if (hasText(search)) {
            String like = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                var orderJoin = root.join("order");
                var studentJoin = orderJoin.join("student");
                return cb.or(
                        cb.like(cb.lower(studentJoin.get("cne")), like),
                        cb.like(cb.lower(studentJoin.get("firstName")), like),
                        cb.like(cb.lower(studentJoin.get("lastName")), like),
                        cb.like(cb.lower(studentJoin.get("email")), like));
            });
        }

        if (startDate != null) {
            LocalDateTime startOfDay = startDate.atStartOfDay();
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), startOfDay));
        }

        if (endDate != null) {
            LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), endOfDay));
        }

        Page<Subscription> page = subscriptionRepository.findAll(spec, pageable);

        List<Map<String, Object>> data = page.getContent().stream().map(subscription -> {
            Map<String, Object> row = new HashMap<>();

            put(row, selectedFields, "id", subscription.getId());

            String cne = null;
            String plan = null;
            if (subscription.getOrder() != null) {
                if (subscription.getOrder().getStudent() != null) {
                    cne = subscription.getOrder().getStudent().getCne();
                }
                if (subscription.getOrder().getPlanType() != null) {
                    plan = subscription.getOrder().getPlanType().name();
                }
            }

            put(row, selectedFields, "cne", cne);
            put(row, selectedFields, "startDate", subscription.getStartDate());
            put(row, selectedFields, "endDate", subscription.getEndDate());
            put(row, selectedFields, "planType", plan);
            put(row, selectedFields, "subscriptionType", plan);
            put(row, selectedFields, "status", isActive(subscription.getEndDate()) ? "active" : "inactive");

            return row;
        }).toList();

        return new PageResponse<>(data, toMeta(page));
    }

    @Transactional(readOnly = true)
    public PageResponse<Map<String, Object>> getDiscountRows(
            Pageable pageable,
            List<String> fields,
            Boolean active,
            String code,
            String percentage,
            String search,
            LocalDate startDate,
            LocalDate endDate) {
        Set<String> selectedFields = resolveFields(fields, DISCOUNT_ALLOWED_FIELDS);

        Specification<Discount> spec = Specification.where(null);

        if (active != null) {
            spec = spec.and((root, query, cb) -> active
                    ? cb.greaterThanOrEqualTo(root.get("endDate"), LocalDateTime.now())
                    : cb.lessThan(root.get("endDate"), LocalDateTime.now()));
        }

        if (hasText(code)) {
            String like = "%" + code.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("code")), like));
        }

        if (hasText(percentage)) {
            try {
                Integer p = Integer.parseInt(percentage.trim());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("percentage"), p));
            } catch (NumberFormatException ignored) {
                // Ignore invalid number value.
            }
        }

        if (hasText(search)) {
            String like = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("code")), like),
                    cb.like(cb.lower(root.get("percentage").as(String.class)), like)));
        }

        if (startDate != null) {
            LocalDateTime startOfDay = startDate.atStartOfDay();
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), startOfDay));
        }

        if (endDate != null) {
            LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), endOfDay));
        }

        Page<Discount> page = discountRepository.findAll(spec, pageable);

        List<Map<String, Object>> data = page.getContent().stream().map(discount -> {
            Map<String, Object> row = new HashMap<>();

            put(row, selectedFields, "id", discount.getId());
            put(row, selectedFields, "code", discount.getCode());
            put(row, selectedFields, "startDate", discount.getStartDate());
            put(row, selectedFields, "endDate", discount.getEndDate());
            put(row, selectedFields, "percentage", discount.getPercentage());
            put(row, selectedFields, "status", isActive(discount.getEndDate()) ? "active" : "expired");

            return row;
        }).toList();

        return new PageResponse<>(data, toMeta(page));
    }

    private Set<String> resolveFields(List<String> requestedFields, Set<String> allowedFields) {
        if (requestedFields == null || requestedFields.isEmpty()) {
            return allowedFields;
        }

        Set<String> selected = new LinkedHashSet<>();

        for (String field : requestedFields) {
            if (!hasText(field)) {
                continue;
            }

            String trimmed = field.trim();
            if (Objects.equals(trimmed, "actions")) {
                continue;
            }

            if (allowedFields.contains(trimmed)) {
                selected.add(trimmed);
            }
        }

        return selected.isEmpty() ? allowedFields : selected;
    }

    private void put(Map<String, Object> row, Set<String> selectedFields, String key, Object value) {
        if (selectedFields.contains(key)) {
            row.put(key, value);
        }
    }

    private String buildFullName(String firstName, String lastName) {
        String first = firstName != null ? firstName.trim() : "";
        String last = lastName != null ? lastName.trim() : "";
        String fullName = (first + " " + last).trim();
        return fullName.isEmpty() ? null : fullName;
    }

    private Optional<PlanType> parsePlanType(String value) {
        if (!hasText(value)) {
            return Optional.empty();
        }
        try {
            return Optional.of(PlanType.valueOf(value.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isActive(LocalDateTime endDate) {
        return endDate != null && !endDate.isBefore(LocalDateTime.now());
    }

    private PageResponse.Meta toMeta(Page<?> page) {
        return new PageResponse.Meta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}

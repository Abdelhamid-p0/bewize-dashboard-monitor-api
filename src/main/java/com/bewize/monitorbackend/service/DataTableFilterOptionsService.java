package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.dto.datatable.FilterOptionDto;
import com.bewize.monitorbackend.enums.Cycle;
import com.bewize.monitorbackend.enums.Gender;
import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.PlanType;
import com.bewize.monitorbackend.repository.DiscountRepository;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import com.bewize.monitorbackend.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataTableFilterOptionsService {

    private final StudentRepository studentRepository;
    private final OrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DiscountRepository discountRepository;

    public Map<String, List<FilterOptionDto>> getStudentFilterOptions() {
        List<FilterOptionDto> planTypeOptions = mapPlanTypeOptions(orderRepository.findDistinctPlanTypes());

        return orderedMap(
                "gender", mapGenderOptions(studentRepository.findDistinctGenders()),
                "cycle", mapCycleOptions(studentRepository.findDistinctCycles()),
                "level", studentRepository.findDistinctLevelNames().stream()
                        .map(level -> new FilterOptionDto(level, level))
                        .toList(),
                "type", planTypeOptions,
                "planType", planTypeOptions);
    }

    public Map<String, List<FilterOptionDto>> getOrderFilterOptions() {
        return orderedMap(
                "status", mapOrderStatusOptions(orderRepository.findDistinctStatuses()),
                "planType", mapPlanTypeOptions(orderRepository.findDistinctPlanTypes()));
    }

    public Map<String, List<FilterOptionDto>> getSubscriptionFilterOptions() {
        List<FilterOptionDto> planTypeOptions = mapPlanTypeOptions(subscriptionRepository.findDistinctOrderPlanTypes());

        return orderedMap(
                "status", subscriptionStatusOptions(),
                "type", planTypeOptions,
                "planType", planTypeOptions);
    }

    public Map<String, List<FilterOptionDto>> getDiscountFilterOptions() {
        return orderedMap(
                "active", discountActiveOptions(),
                "code", discountRepository.findDistinctCodes().stream()
                        .map(code -> new FilterOptionDto(code, code))
                        .toList(),
                "percentage", discountRepository.findDistinctPercentages().stream()
                        .map(percentage -> new FilterOptionDto(percentage + "%", String.valueOf(percentage)))
                        .toList());
    }

    private List<FilterOptionDto> mapPlanTypeOptions(List<PlanType> values) {
        return values.stream()
                .map(value -> new FilterOptionDto(planTypeLabel(value), value.name()))
                .toList();
    }

    private String planTypeLabel(PlanType value) {
        return switch (value) {
            case FREEMIUM -> "Freemium";
            case PREMIUM -> "Premium";
            case YEAR -> "Annuel";
            case SEMESTER -> "Semestriel";
            case TRIMESTER -> "Trimestriel";
            case MONTH -> "Mensuel";
            case SCHOOL -> "École";
        };
    }

    private List<FilterOptionDto> mapGenderOptions(List<Gender> values) {
        return values.stream()
                .map(value -> new FilterOptionDto(humanizeEnumValue(value.name()), value.name()))
                .toList();
    }

    private List<FilterOptionDto> mapCycleOptions(List<Cycle> values) {
        return values.stream()
                .map(value -> new FilterOptionDto(humanizeEnumValue(value.name()), value.name()))
                .toList();
    }

    private List<FilterOptionDto> mapOrderStatusOptions(List<OrderStatus> values) {
        return values.stream()
                .map(value -> new FilterOptionDto(humanizeEnumValue(value.name()), value.name()))
                .toList();
    }

    private List<FilterOptionDto> subscriptionStatusOptions() {
        boolean hasActive = subscriptionRepository.existsByEndDateGreaterThanEqual(java.time.LocalDateTime.now());
        boolean hasInactive = subscriptionRepository.existsByEndDateLessThan(java.time.LocalDateTime.now());

        return List.of(
                hasActive ? new FilterOptionDto("Actif", "ACTIVE") : null,
                hasInactive ? new FilterOptionDto("Inactif", "INACTIVE") : null)
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    private List<FilterOptionDto> discountActiveOptions() {
        boolean hasActive = discountRepository.existsByEndDateGreaterThanEqual(java.time.LocalDateTime.now());
        boolean hasExpired = discountRepository.existsByEndDateLessThan(java.time.LocalDateTime.now());

        return List.of(
                hasActive ? new FilterOptionDto("Actif", "true") : null,
                hasExpired ? new FilterOptionDto("Expiré", "false") : null)
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    private String humanizeEnumValue(String value) {
        return Arrays.stream(value.toLowerCase().split("_"))
                .map(part -> part.isBlank() ? part : Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .collect(Collectors.joining(" "));
    }

    @SafeVarargs
    private final Map<String, List<FilterOptionDto>> orderedMap(Object... keyValues) {
        Map<String, List<FilterOptionDto>> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put((String) keyValues[i], (List<FilterOptionDto>) keyValues[i + 1]);
        }
        return map;
    }
}

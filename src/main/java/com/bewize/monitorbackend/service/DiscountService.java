package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Discount;
import com.bewize.monitorbackend.dto.discount.DiscountCreateRequest;
import com.bewize.monitorbackend.dto.discount.DiscountListDto;
import com.bewize.monitorbackend.dto.discount.DiscountUpdateRequest;
import com.bewize.monitorbackend.mappers.DiscountMapper;
import com.bewize.monitorbackend.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    @Transactional(readOnly = true)
    public List<DiscountListDto> getDiscounts() {
        List<Discount> discounts = discountRepository.findAll();
        log.info("found discounts {}", discounts.size());
        return discounts.stream().map(discountMapper::toListDto).collect(Collectors.toList());
    }

    @Transactional
    public DiscountListDto createDiscount(DiscountCreateRequest req) {
        Discount entity = discountMapper.toEntity(req);
        Discount saved = discountRepository.save(entity);
        log.info("created discount {}", saved.getId());
        return discountMapper.toListDto(saved);
    }

    @Transactional
    public DiscountListDto updateDiscount(String id, DiscountUpdateRequest req) {
        Discount existing = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        discountMapper.updateFromUpdateRequest(req, existing);
        Discount saved = discountRepository.save(existing);
        log.info("updated discount {}", saved.getId());
        return discountMapper.toListDto(saved);
    }

    @Transactional
    public void deleteDiscount(String id) {
        if (!discountRepository.existsById(id)) {
            throw new RuntimeException("Discount not found");
        }
        discountRepository.deleteById(id);
        log.info("deleted discount {}", id);
    }
}
package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.discount.DiscountCreateRequest;
import com.bewize.monitorbackend.dto.discount.DiscountListDto;
import com.bewize.monitorbackend.dto.discount.DiscountUpdateRequest;
import com.bewize.monitorbackend.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.bewize.monitorbackend.constants.ResourcesPaths.DISCOUNTS;

@Tag(name = "Discount monitor admin Resource")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DISCOUNTS)
public class DiscountController {

    private final DiscountService discountService;

    @Operation(summary = "List all discounts")
    @GetMapping
    public List<DiscountListDto> getDiscounts() {
        log.info("request to get discounts");
        List<DiscountListDto> discounts = discountService.getDiscounts();
        log.info("discounts list loaded: {}", discounts.size());
        return discounts;
    }

    @Operation(summary = "Create discount")
    @PostMapping
    public ResponseEntity<DiscountListDto> create(@RequestBody DiscountCreateRequest req) {
        log.info("request to create discount");
        DiscountListDto created = discountService.createDiscount(req);
        log.info("discount created: {}", created.getId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update discount")
    @PutMapping("/{id}")
    public ResponseEntity<DiscountListDto> update(@PathVariable String id, @RequestBody DiscountUpdateRequest req) {
        log.info("request to update discount {}", id);
        DiscountListDto updated = discountService.updateDiscount(id, req);
        log.info("discount updated: {}", id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete discount")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("request to delete discount {}", id);
        discountService.deleteDiscount(id);
        log.info("discount deleted: {}", id);
        return ResponseEntity.noContent().build();
    }
}
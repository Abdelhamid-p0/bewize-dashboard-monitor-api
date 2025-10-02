package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.subscription.SubscriptionCreateRequest;
import com.bewize.monitorbackend.dto.subscription.SubscriptionListDto;
import com.bewize.monitorbackend.dto.subscription.SubscriptionUpdateRequest;
import com.bewize.monitorbackend.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.bewize.monitorbackend.constants.ResourcesPaths.SUBSCRIPTIONS;

@Tag(name = "Subscription monitor admin Resource")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(SUBSCRIPTIONS)
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "List all subscriptions with pagination")
    @GetMapping
    public ResponseEntity<PageResponse<SubscriptionListDto>> getSubscriptions(
            @ParameterObject Pageable pageable) {

        PageResponse<SubscriptionListDto> page = subscriptionService.getSubscriptions(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Create subscription")
    @PostMapping
    public ResponseEntity<SubscriptionListDto> create(@RequestBody SubscriptionCreateRequest req) {
        log.info("request to create subscription");
        SubscriptionListDto created = subscriptionService.createSubscription(req);
        log.info("subscription created: {}", created.getId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update subscription")
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionListDto> update(@PathVariable String id, @RequestBody SubscriptionUpdateRequest req) {
        log.info("request to update subscription {}", id);
        SubscriptionListDto updated = subscriptionService.updateSubscription(id, req);
        log.info("subscription updated: {}", id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete subscription")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("request to delete subscription {}", id);
        subscriptionService.deleteSubscription(id);
        log.info("subscription deleted: {}", id);
        return ResponseEntity.noContent().build();
    }
}

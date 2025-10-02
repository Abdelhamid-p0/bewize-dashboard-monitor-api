package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.domains.subscription.Subscription;
import com.bewize.monitorbackend.dto.subscription.SubscriptionCreateRequest;
import com.bewize.monitorbackend.dto.subscription.SubscriptionListDto;
import com.bewize.monitorbackend.dto.subscription.SubscriptionUpdateRequest;
import com.bewize.monitorbackend.mappers.SubscriptionMapper;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private SubscriptionMapper subscriptionMapper;

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository, orderRepository, subscriptionMapper);
    }

    // helper dynamic mapper stubs
    private void stubToListDto() {
        when(subscriptionMapper.toListDto(any(Subscription.class))).thenAnswer(inv -> {
            Subscription src = inv.getArgument(0);
            SubscriptionListDto dto = new SubscriptionListDto();
            dto.setId(src.getId());
            dto.setStartDate(src.getStartDate());
            dto.setEndDate(src.getEndDate());
            dto.setOrderId(src.getOrder() != null ? src.getOrder().getId() : null);
            return dto;
        });
    }

    @Test
    @DisplayName("getSubscriptions returns mapped list")
    void getSubscriptions_ok() {
        Subscription s1 = Subscription.builder().id("sub-1").startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(5)).build();
        Subscription s2 = Subscription.builder().id("sub-2").startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(10)).build();
        when(subscriptionRepository.findAll()).thenReturn(List.of(s1, s2));
        stubToListDto();

        List<SubscriptionListDto> result = subscriptionService.getSubscriptions();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(SubscriptionListDto::getId).containsExactlyInAnyOrder("sub-1", "sub-2");
        verify(subscriptionRepository).findAll();
        verify(subscriptionMapper, times(2)).toListDto(any(Subscription.class));
    }

    @Test
    @DisplayName("createSubscription persists entity without order when orderId null")
    void createSubscription_noOrder() {
        SubscriptionCreateRequest req = new SubscriptionCreateRequest();
        req.setStartDate(LocalDateTime.parse("2024-01-01T00:00:00"));
        req.setEndDate(LocalDateTime.parse("2024-02-01T00:00:00"));
        req.setOrderId(null);

        Subscription toSave = Subscription.builder().startDate(req.getStartDate()).endDate(req.getEndDate()).build();
        when(subscriptionMapper.toEntity(req)).thenReturn(toSave);
        Subscription saved = Subscription.builder().id("gen-1").startDate(req.getStartDate()).endDate(req.getEndDate()).build();
        when(subscriptionRepository.save(toSave)).thenReturn(saved);
        stubToListDto();

        SubscriptionListDto dto = subscriptionService.createSubscription(req);

        assertThat(dto.getId()).isEqualTo("gen-1");
        assertThat(dto.getOrderId()).isNull();
        verify(orderRepository, never()).getReferenceById(anyString());
    }

    @Test
    @DisplayName("createSubscription sets order when orderId provided")
    void createSubscription_withOrder() {
        SubscriptionCreateRequest req = new SubscriptionCreateRequest();
        req.setStartDate(LocalDateTime.parse("2024-01-01T00:00:00"));
        req.setEndDate(LocalDateTime.parse("2024-02-01T00:00:00"));
        req.setOrderId("ord-9");

        Subscription toSave = Subscription.builder().startDate(req.getStartDate()).endDate(req.getEndDate()).build();
        when(subscriptionMapper.toEntity(req)).thenReturn(toSave);
        Order refOrder = new Order(); refOrder.setId("ord-9");
        when(orderRepository.getReferenceById("ord-9")).thenReturn(refOrder);
        Subscription saved = Subscription.builder().id("sub-x").startDate(req.getStartDate()).endDate(req.getEndDate()).order(refOrder).build();
        when(subscriptionRepository.save(toSave)).thenReturn(saved);
        stubToListDto();

        SubscriptionListDto dto = subscriptionService.createSubscription(req);

        assertThat(dto.getId()).isEqualTo("sub-x");
        assertThat(dto.getOrderId()).isEqualTo("ord-9");
        verify(orderRepository).getReferenceById("ord-9");
    }

    @Test
    @DisplayName("updateSubscription updates fields and sets order")
    void updateSubscription_setOrder() {
        Subscription existing = Subscription.builder().id("sub-1").startDate(LocalDateTime.parse("2024-01-01T00:00:00")).endDate(LocalDateTime.parse("2024-02-01T00:00:00")).build();
        when(subscriptionRepository.findById("sub-1")).thenReturn(Optional.of(existing));

        SubscriptionUpdateRequest req = new SubscriptionUpdateRequest();
        req.setEndDate(LocalDateTime.parse("2024-03-01T00:00:00"));
        req.setOrderId("ord-2");

        // void method -> use doAnswer
        doAnswer(inv -> { existing.setEndDate(req.getEndDate()); return null; })
                .when(subscriptionMapper).updateFromUpdateRequest(eq(req), eq(existing));

        Order refOrder = new Order(); refOrder.setId("ord-2");
        when(orderRepository.getReferenceById("ord-2")).thenReturn(refOrder);

        Subscription saved = Subscription.builder().id("sub-1").startDate(existing.getStartDate()).endDate(req.getEndDate()).order(refOrder).build();
        when(subscriptionRepository.save(existing)).thenReturn(saved);
        stubToListDto();

        SubscriptionListDto dto = subscriptionService.updateSubscription("sub-1", req);

        assertThat(dto.getId()).isEqualTo("sub-1");
        assertThat(dto.getEndDate()).isEqualTo(LocalDateTime.parse("2024-03-01T00:00:00"));
        assertThat(dto.getOrderId()).isEqualTo("ord-2");
        verify(orderRepository).getReferenceById("ord-2");
    }

    @Test
    @DisplayName("updateSubscription clears order when orderId blank")
    void updateSubscription_clearOrder() {
        Order prev = new Order(); prev.setId("ord-old");
        Subscription existing = Subscription.builder().id("sub-clear").order(prev).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).build();
        when(subscriptionRepository.findById("sub-clear")).thenReturn(Optional.of(existing));

        SubscriptionUpdateRequest req = new SubscriptionUpdateRequest();
        req.setOrderId(""); // request to clear

        // void method -> use doAnswer
        doAnswer(inv -> null).when(subscriptionMapper).updateFromUpdateRequest(eq(req), eq(existing));

        Subscription saved = Subscription.builder().id("sub-clear").startDate(existing.getStartDate()).endDate(existing.getEndDate()).order(null).build();
        when(subscriptionRepository.save(existing)).thenReturn(saved);
        stubToListDto();

        SubscriptionListDto dto = subscriptionService.updateSubscription("sub-clear", req);
        assertThat(dto.getOrderId()).isNull();
        verify(orderRepository, never()).getReferenceById(anyString());
    }

    @Test
    @DisplayName("updateSubscription throws when not found")
    void updateSubscription_notFound() {
        when(subscriptionRepository.findById("missing")).thenReturn(Optional.empty());
        SubscriptionUpdateRequest req = new SubscriptionUpdateRequest();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> subscriptionService.updateSubscription("missing", req));
        assertThat(ex.getMessage()).isEqualTo("Subscription not found");
        verify(subscriptionRepository).findById("missing");
        verifyNoInteractions(orderRepository, subscriptionMapper);
    }

    @Test
    @DisplayName("deleteSubscription delegates to repository")
    void deleteSubscription_ok() {
        subscriptionService.deleteSubscription("sub-del");
        verify(subscriptionRepository).deleteById("sub-del");
    }
}

package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.domains.subscription.Subscription;
import com.bewize.monitorbackend.dto.PageResponse;
import com.bewize.monitorbackend.dto.subscription.SubscriptionCreateRequest;
import com.bewize.monitorbackend.dto.subscription.SubscriptionListDto;
import com.bewize.monitorbackend.dto.subscription.SubscriptionUpdateRequest;
import com.bewize.monitorbackend.mappers.SubscriptionMapper;
import com.bewize.monitorbackend.repository.OrderRepository;
import com.bewize.monitorbackend.repository.StudentRepository;
import com.bewize.monitorbackend.repository.SubscriptionRepository;
import com.bewize.monitorbackend.repository.projection.SubscriptionListProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private StudentRepository studentRepository;
    @Mock
    private SubscriptionMapper subscriptionMapper;

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(subscriptionRepository, orderRepository, studentRepository,
                subscriptionMapper);
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
    @DisplayName("getSubscriptions returns mapped page response")
    void getSubscriptions_paged_ok() {
        Pageable pageable = PageRequest.of(0, 2);

        SubscriptionListProjection p1 = new SubscriptionListProjection() {
            public String getId() {
                return "sub-1";
            }

            public java.time.LocalDateTime getStartDate() {
                return java.time.LocalDateTime.parse("2024-01-01T00:00:00");
            }

            public java.time.LocalDateTime getEndDate() {
                return java.time.LocalDateTime.parse("2024-02-01T00:00:00");
            }

            public OrderProjection getOrder() {
                return null;
            }
        };
        SubscriptionListProjection p2 = new SubscriptionListProjection() {
            public String getId() {
                return "sub-2";
            }

            public java.time.LocalDateTime getStartDate() {
                return java.time.LocalDateTime.parse("2024-03-01T00:00:00");
            }

            public java.time.LocalDateTime getEndDate() {
                return java.time.LocalDateTime.parse("2024-04-01T00:00:00");
            }

            public OrderProjection getOrder() {
                return new OrderProjection() {
                    public String getId() {
                        return "ord-9";
                    }
                };
            }
        };
        Page<SubscriptionListProjection> page = new PageImpl<>(java.util.List.of(p1, p2), pageable, 2);
        when(subscriptionRepository.findAllProjectedBy(pageable)).thenReturn(page);

        PageResponse<SubscriptionListDto> resp = subscriptionService.getSubscriptions(pageable);

        assertThat(resp.getData()).hasSize(2);
        assertThat(resp.getData()).extracting(SubscriptionListDto::getId).containsExactlyInAnyOrder("sub-1", "sub-2");
        assertThat(resp.getMeta().getPage()).isEqualTo(0);
        assertThat(resp.getMeta().getSize()).isEqualTo(2);
        assertThat(resp.getMeta().getTotalElements()).isEqualTo(2);
        assertThat(resp.getMeta().getTotalPages()).isEqualTo(1);

        // order id only on second
        SubscriptionListDto second = resp.getData().stream().filter(d -> d.getId().equals("sub-2")).findFirst()
                .orElseThrow();
        assertThat(second.getOrderId()).isEqualTo("ord-9");

        verify(subscriptionRepository).findAllProjectedBy(pageable);
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
        Subscription saved = Subscription.builder().id("gen-1").startDate(req.getStartDate()).endDate(req.getEndDate())
                .build();
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
        Order refOrder = new Order();
        refOrder.setId("ord-9");
        when(orderRepository.getReferenceById("ord-9")).thenReturn(refOrder);
        Subscription saved = Subscription.builder().id("sub-x").startDate(req.getStartDate()).endDate(req.getEndDate())
                .order(refOrder).build();
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
        Subscription existing = Subscription.builder().id("sub-1").startDate(LocalDateTime.parse("2024-01-01T00:00:00"))
                .endDate(LocalDateTime.parse("2024-02-01T00:00:00")).build();
        when(subscriptionRepository.findById("sub-1")).thenReturn(Optional.of(existing));

        SubscriptionUpdateRequest req = new SubscriptionUpdateRequest();
        req.setEndDate(LocalDateTime.parse("2024-03-01T00:00:00"));
        req.setOrderId("ord-2");

        // void method -> use doAnswer
        doAnswer(inv -> {
            existing.setEndDate(req.getEndDate());
            return null;
        })
                .when(subscriptionMapper).updateFromUpdateRequest(eq(req), eq(existing));

        Order refOrder = new Order();
        refOrder.setId("ord-2");
        when(orderRepository.getReferenceById("ord-2")).thenReturn(refOrder);

        Subscription saved = Subscription.builder().id("sub-1").startDate(existing.getStartDate())
                .endDate(req.getEndDate()).order(refOrder).build();
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
        Order prev = new Order();
        prev.setId("ord-old");
        Subscription existing = Subscription.builder().id("sub-clear").order(prev).startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1)).build();
        when(subscriptionRepository.findById("sub-clear")).thenReturn(Optional.of(existing));

        SubscriptionUpdateRequest req = new SubscriptionUpdateRequest();
        req.setOrderId(""); // request to clear

        // void method -> use doAnswer
        doAnswer(inv -> null).when(subscriptionMapper).updateFromUpdateRequest(eq(req), eq(existing));

        Subscription saved = Subscription.builder().id("sub-clear").startDate(existing.getStartDate())
                .endDate(existing.getEndDate()).order(null).build();
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
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> subscriptionService.updateSubscription("missing", req));
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

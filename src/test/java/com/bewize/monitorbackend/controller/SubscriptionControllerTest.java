package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.subscription.SubscriptionCreateRequest;
import com.bewize.monitorbackend.dto.subscription.SubscriptionListDto;
import com.bewize.monitorbackend.dto.subscription.SubscriptionUpdateRequest;
import com.bewize.monitorbackend.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SubscriptionController controller = new SubscriptionController(subscriptionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private SubscriptionListDto subDto(String id, String orderId) {
        LocalDateTime now = LocalDateTime.parse("2024-01-01T00:00:00");
        return new SubscriptionListDto() {
            public String getId() { return id; }
            public String getOrderId() { return orderId; }
            public LocalDateTime getStartDate() { return now; }
            public LocalDateTime getEndDate() { return now.plusDays(30); }
        };
    }

    @Test
    @DisplayName("GET /subscriptions returns list")
    void getSubscriptions_ok() throws Exception {
        List<SubscriptionListDto> payload = List.of(
                subDto("sub-1", "ord-1"),
                subDto("sub-2", null)
        );
        when(subscriptionService.getSubscriptions()).thenReturn(payload);

        mockMvc.perform(get("/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("sub-1")))
                .andExpect(jsonPath("$[0].orderId", is("ord-1")))
                .andExpect(jsonPath("$[1].id", is("sub-2")));
    }

    @Test
    @DisplayName("POST /subscriptions returns 201 with Location and body")
    void createSubscription_created() throws Exception {
        SubscriptionListDto created = subDto("sub-123", "ord-123");
        when(subscriptionService.createSubscription(any(SubscriptionCreateRequest.class))).thenReturn(created);

        String body = """
                {
                  "startDate": "2024-01-01T00:00:00",
                  "endDate": "2024-02-01T00:00:00",
                  "orderId": "ord-123"
                }
                """;

        mockMvc.perform(post("/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/subscriptions/sub-123")))
                .andExpect(jsonPath("$.id", is("sub-123")))
                .andExpect(jsonPath("$.orderId", is("ord-123")));
    }

    @Test
    @DisplayName("PUT /subscriptions/{id} returns 200 with updated body")
    void updateSubscription_ok() throws Exception {
        SubscriptionListDto updated = subDto("sub-999", null);
        when(subscriptionService.updateSubscription(eq("sub-999"), any(SubscriptionUpdateRequest.class)))
                .thenReturn(updated);

        String body = """
                { "endDate": "2024-03-01T00:00:00" }
                """;

        mockMvc.perform(put("/subscriptions/{id}", "sub-999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("sub-999")));
    }

    @Test
    @DisplayName("DELETE /subscriptions/{id} returns 204")
    void deleteSubscription_noContent() throws Exception {
        doNothing().when(subscriptionService).deleteSubscription("sub-555");

        mockMvc.perform(delete("/subscriptions/{id}", "sub-555"))
                .andExpect(status().isNoContent());
    }
}

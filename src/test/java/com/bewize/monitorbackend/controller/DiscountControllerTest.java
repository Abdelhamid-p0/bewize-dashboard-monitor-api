package com.bewize.monitorbackend.controller;

import com.bewize.monitorbackend.dto.discount.DiscountCreateRequest;
import com.bewize.monitorbackend.dto.discount.DiscountListDto;
import com.bewize.monitorbackend.dto.discount.DiscountUpdateRequest;
import com.bewize.monitorbackend.service.DiscountService;
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

class DiscountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DiscountService discountService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        DiscountController controller = new DiscountController(discountService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private DiscountListDto discountDto(String id, String code, Integer percentage) {
        LocalDateTime start = LocalDateTime.parse("2024-01-01T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2024-12-31T23:59:59");
        return new DiscountListDto() {
            public String getId() { return id; }
            public String getCode() { return code; }
            public Integer getPercentage() { return percentage; }
            public LocalDateTime getStartDate() { return start; }
            public LocalDateTime getEndDate() { return end; }
        };
    }

    @Test
    @DisplayName("GET /discounts returns list")
    void getDiscounts_ok() throws Exception {
        List<DiscountListDto> payload = List.of(
                discountDto("d1", "WELCOME", 10),
                discountDto("d2", "SUMMER", 15)
        );
        when(discountService.getDiscounts()).thenReturn(payload);

        mockMvc.perform(get("/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("d1")))
                .andExpect(jsonPath("$[0].code", is("WELCOME")))
                .andExpect(jsonPath("$[1].percentage", is(15)));
    }

    @Test
    @DisplayName("POST /discounts returns 201 with Location and body")
    void createDiscount_created() throws Exception {
        DiscountListDto created = discountDto("disc-999", "WELCOME", 10);
        when(discountService.createDiscount(any(DiscountCreateRequest.class))).thenReturn(created);

        String body = """
                {
                  "code": "WELCOME",
                  "percentage": 10,
                  "startDate": "2024-01-01T00:00:00",
                  "endDate": "2024-12-31T23:59:59"
                }
                """;

        mockMvc.perform(post("/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/discounts/disc-999")))
            .andExpect(jsonPath("$.id", is("disc-999")))
            .andExpect(jsonPath("$.code", is("WELCOME")))
            .andExpect(jsonPath("$.percentage", is(10)));
    }

    @Test
    @DisplayName("PUT /discounts/{id} returns 200 with updated body")
    void updateDiscount_ok() throws Exception {
        DiscountListDto updated = discountDto("disc-999", "WELCOME", 20);
        when(discountService.updateDiscount(eq("disc-999"), any(DiscountUpdateRequest.class)))
                .thenReturn(updated);

        String body = """
                { "percentage": 20 }
                """;

        mockMvc.perform(put("/discounts/{id}", "disc-999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("disc-999")))
                .andExpect(jsonPath("$.percentage", is(20)));
    }

    @Test
    @DisplayName("DELETE /discounts/{id} returns 204")
    void deleteDiscount_noContent() throws Exception {
        doNothing().when(discountService).deleteDiscount("disc-555");

        mockMvc.perform(delete("/discounts/{id}", "disc-555"))
                .andExpect(status().isNoContent());
    }
}

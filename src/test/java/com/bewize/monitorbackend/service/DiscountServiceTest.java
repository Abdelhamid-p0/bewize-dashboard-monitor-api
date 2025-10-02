package com.bewize.monitorbackend.service;

import com.bewize.monitorbackend.domains.subscription.Discount;
import com.bewize.monitorbackend.dto.discount.DiscountCreateRequest;
import com.bewize.monitorbackend.dto.discount.DiscountListDto;
import com.bewize.monitorbackend.dto.discount.DiscountUpdateRequest;
import com.bewize.monitorbackend.mappers.DiscountMapper;
import com.bewize.monitorbackend.repository.DiscountRepository;
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
public class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;
    @Mock
    private DiscountMapper discountMapper;

    private DiscountService discountService;

    @BeforeEach
    void setUp() {
        discountService = new DiscountService(discountRepository, discountMapper);
    }

    private void stubToListDto() {
        when(discountMapper.toListDto(any(Discount.class))).thenAnswer(inv -> {
            Discount d = inv.getArgument(0);
            DiscountListDto dto = new DiscountListDto();
            dto.setId(d.getId());
            dto.setCode(d.getCode());
            dto.setPercentage(d.getPercentage());
            dto.setStartDate(d.getStartDate());
            dto.setEndDate(d.getEndDate());
            return dto;
        });
    }

    @Test
    @DisplayName("getDiscounts returns mapped list")
    void getDiscounts_ok() {
        Discount d1 = Discount.builder().id("d1").code("WELCOME").percentage(10).startDate(LocalDateTime.now()).build();
        Discount d2 = Discount.builder().id("d2").code("SUMMER").percentage(15).build();
        when(discountRepository.findAll()).thenReturn(List.of(d1, d2));
        stubToListDto();

        List<DiscountListDto> result = discountService.getDiscounts();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DiscountListDto::getId).containsExactlyInAnyOrder("d1", "d2");
        verify(discountRepository).findAll();
        verify(discountMapper, times(2)).toListDto(any(Discount.class));
    }

    @Test
    @DisplayName("createDiscount maps, saves and returns list dto")
    void createDiscount_ok() {
        DiscountCreateRequest req = new DiscountCreateRequest();
        req.setCode("WELCOME");
        req.setPercentage(10);
        Discount entity = Discount.builder().code("WELCOME").percentage(10).build();
        when(discountMapper.toEntity(req)).thenReturn(entity);
        Discount saved = Discount.builder().id("gen-1").code("WELCOME").percentage(10).build();
        when(discountRepository.save(entity)).thenReturn(saved);
        DiscountListDto dto = new DiscountListDto(); dto.setId("gen-1"); dto.setCode("WELCOME"); dto.setPercentage(10);
        when(discountMapper.toListDto(saved)).thenReturn(dto);

        DiscountListDto out = discountService.createDiscount(req);

        assertThat(out.getId()).isEqualTo("gen-1");
        verify(discountMapper).toEntity(req);
        verify(discountRepository).save(entity);
        verify(discountMapper).toListDto(saved);
    }

    @Test
    @DisplayName("updateDiscount applies mapper update and returns dto")
    void updateDiscount_ok() {
        Discount existing = Discount.builder().id("d5").code("OLD").percentage(5).build();
        when(discountRepository.findById("d5")).thenReturn(Optional.of(existing));

        DiscountUpdateRequest req = new DiscountUpdateRequest();
        req.setPercentage(25);

        doAnswer(inv -> { existing.setPercentage(req.getPercentage()); return null; })
                .when(discountMapper).updateFromUpdateRequest(eq(req), eq(existing));

        Discount saved = Discount.builder().id("d5").code("OLD").percentage(25).build();
        when(discountRepository.save(existing)).thenReturn(saved);
        DiscountListDto dto = new DiscountListDto(); dto.setId("d5"); dto.setCode("OLD"); dto.setPercentage(25);
        when(discountMapper.toListDto(saved)).thenReturn(dto);

        DiscountListDto out = discountService.updateDiscount("d5", req);

        assertThat(out.getPercentage()).isEqualTo(25);
        verify(discountRepository).findById("d5");
        verify(discountMapper).updateFromUpdateRequest(req, existing);
        verify(discountRepository).save(existing);
        verify(discountMapper).toListDto(saved);
    }

    @Test
    @DisplayName("updateDiscount throws when not found")
    void updateDiscount_notFound() {
        when(discountRepository.findById("missing")).thenReturn(Optional.empty());
        DiscountUpdateRequest req = new DiscountUpdateRequest();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> discountService.updateDiscount("missing", req));
        assertThat(ex.getMessage()).isEqualTo("Discount not found");
        verify(discountRepository).findById("missing");
        verifyNoInteractions(discountMapper);
    }

    @Test
    @DisplayName("deleteDiscount deletes when exists")
    void deleteDiscount_ok() {
        when(discountRepository.existsById("d7")).thenReturn(true);

        discountService.deleteDiscount("d7");

        verify(discountRepository).existsById("d7");
        verify(discountRepository).deleteById("d7");
    }

    @Test
    @DisplayName("deleteDiscount throws when not found")
    void deleteDiscount_notFound() {
        when(discountRepository.existsById("absent")).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> discountService.deleteDiscount("absent"));
        assertThat(ex.getMessage()).isEqualTo("Discount not found");
        verify(discountRepository).existsById("absent");
        verify(discountRepository, never()).deleteById(anyString());
    }
}

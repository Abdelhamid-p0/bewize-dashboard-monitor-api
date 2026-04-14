package com.bewize.monitorbackend.repository;

import com.bewize.monitorbackend.domains.subscription.Discount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String>, JpaSpecificationExecutor<Discount> {

    @Query("select distinct d.code from Discount d where d.code is not null order by d.code")
    List<String> findDistinctCodes();

    @Query("select distinct d.percentage from Discount d where d.percentage is not null order by d.percentage")
    List<Integer> findDistinctPercentages();

    boolean existsByEndDateGreaterThanEqual(LocalDateTime referenceDate);

    boolean existsByEndDateLessThan(LocalDateTime referenceDate);
}
package com.bewize.monitorbackend.repository;

import com.bewize.monitorbackend.domains.subscription.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String>, JpaSpecificationExecutor<Discount> {
}
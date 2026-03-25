package com.bewize.monitorbackend.repository;

import com.bewize.monitorbackend.domains.subscription.Subscription;
import com.bewize.monitorbackend.dto.subscription.SubscriptionListDto;
import com.bewize.monitorbackend.repository.projection.DashboardDayCountProjection;
import com.bewize.monitorbackend.repository.projection.DashboardMonthCountProjection;
import com.bewize.monitorbackend.repository.projection.SubscriptionListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubscriptionRepository
    extends JpaRepository<Subscription, String>, JpaSpecificationExecutor<Subscription> {

  @Query("""
          select s
          from Subscription s
          left join fetch s.order o
      """)
  Page<SubscriptionListProjection> findAllProjectedBy(Pageable pageable);

  @Override
  @EntityGraph(attributePaths = { "order", "order.student" })
  Page<Subscription> findAll(Specification<Subscription> spec, Pageable pageable);

  @Query("SELECT COUNT(s) FROM Subscription s WHERE YEAR(s.startDate) = :year AND MONTH(s.startDate) = :month")
  long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

  @Query(value = """
      SELECT to_char(date_trunc('month', s.start_date), 'YYYY-MM') as label, count(*) as count
      FROM subscription s
      WHERE extract(year from s.start_date) = :year
      GROUP BY 1
      ORDER BY 1
      """, nativeQuery = true)
  List<DashboardMonthCountProjection> findSubscriptionCountsByMonth(@Param("year") int year);

  @Query(value = """
      SELECT to_char(d::date, 'YYYY-MM-DD') as label, coalesce(agg.count, 0) as count
      FROM (
        SELECT generate_series(
          date_trunc('month', make_date(:year, :month, 1))::date,
          (date_trunc('month', make_date(:year, :month, 1)) + interval '1 month - 1 day')::date,
          '1 day'
        ) as d
      ) days
      LEFT JOIN (
        SELECT date_trunc('day', s.start_date)::date as day, count(*) as count
        FROM subscription s
        WHERE extract(year from s.start_date) = :year AND extract(month from s.start_date) = :month
        GROUP BY day
      ) agg ON agg.day = days.d
      ORDER BY days.d
      """, nativeQuery = true)
  List<DashboardDayCountProjection> findSubscriptionCountsByDay(
      @Param("year") int year,
      @Param("month") int month);
}

package com.bewize.monitorbackend.repository;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.repository.projection.DashboardDayCountProjection;
import com.bewize.monitorbackend.repository.projection.DashboardMonthCountProjection;
import com.bewize.monitorbackend.repository.projection.OrderListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("""
        select o
        from Order o
        left join fetch o.student s
        left join fetch o.discount d
        left join fetch o.subscription sub
    """)
    Page<OrderListProjection> findAllProjectedBy(Pageable pageable);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.subscription WHERE o.student.id = :studentId")
    List<Order> findOrdersWithSubscriptionByStudentId(@Param("studentId") String studentId);

    @Query("SELECT COUNT(o) FROM Order o WHERE YEAR(o.date) = :year AND MONTH(o.date) = :month")
    long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query(value = """
        SELECT to_char(date_trunc('month', o.date), 'YYYY-MM') as label, count(*) as count
        FROM orders o
        WHERE extract(year from o.date) = :year
        GROUP BY 1
        ORDER BY 1
        """, nativeQuery = true)
    List<DashboardMonthCountProjection> findOrderCountsByMonth(@Param("year") int year);

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
          SELECT date_trunc('day', o.date)::date as day, count(*) as count
          FROM orders o
          WHERE extract(year from o.date) = :year AND extract(month from o.date) = :month
          GROUP BY day
        ) agg ON agg.day = days.d
        ORDER BY days.d
        """, nativeQuery = true)
    List<DashboardDayCountProjection> findOrderCountsByDay(
            @Param("year") int year,
            @Param("month") int month
    );
}

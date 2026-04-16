package com.bewize.monitorbackend.repository;

import com.bewize.monitorbackend.domains.subscription.Order;
import com.bewize.monitorbackend.enums.OrderStatus;
import com.bewize.monitorbackend.enums.PlanType;
import com.bewize.monitorbackend.repository.projection.DashboardDayCountProjection;
import com.bewize.monitorbackend.repository.projection.DashboardMonthCountProjection;
import com.bewize.monitorbackend.repository.projection.OrderListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

  @Query("""
          select o
          from Order o
          left join fetch o.student s
          left join fetch o.discount d
          left join fetch o.subscription sub
      """)
  Page<OrderListProjection> findAllProjectedBy(Pageable pageable);

  @Override
  @EntityGraph(attributePaths = { "student", "discount", "subscription" })
  Page<Order> findAll(Specification<Order> spec, Pageable pageable);

  @Query("select o.planType from Order o where o.student.id = :studentId order by o.date desc")
  List<PlanType> findPlanTypesByStudentIdOrderByDateDesc(@Param("studentId") String studentId, Pageable pageable);

  @Query("select distinct o.status from Order o where o.status is not null order by o.status")
  List<OrderStatus> findDistinctStatuses();

  @Query("select distinct o.planType from Order o where o.planType is not null order by o.planType")
  List<PlanType> findDistinctPlanTypes();

  @Query(value = """
      SELECT DISTINCT ON (o.student_id) o.student_id, o.plan_type
      FROM orders o
      WHERE o.student_id IN (:studentIds)
      ORDER BY o.student_id, o.date DESC, o.id DESC
      """, nativeQuery = true)
  List<Object[]> findLatestPlanTypesByStudentIds(@Param("studentIds") List<String> studentIds);

  default Optional<PlanType> findLatestPlanTypeByStudentId(String studentId) {
    List<PlanType> values = findPlanTypesByStudentIdOrderByDateDesc(studentId, Pageable.ofSize(1));
    if (values == null || values.isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(values.get(0));
  }

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
      @Param("month") int month);
}

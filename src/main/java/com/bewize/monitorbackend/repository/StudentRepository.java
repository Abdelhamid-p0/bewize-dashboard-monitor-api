package com.bewize.monitorbackend.repository;

import com.bewize.monitorbackend.domains.user.Student;
import com.bewize.monitorbackend.dto.student.StudentListDto;
import com.bewize.monitorbackend.enums.Cycle;
import com.bewize.monitorbackend.enums.Gender;
import com.bewize.monitorbackend.repository.projection.DashboardDayCountProjection;
import com.bewize.monitorbackend.repository.projection.DashboardMonthCountProjection;
import com.bewize.monitorbackend.repository.projection.StudentListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String>, JpaSpecificationExecutor<Student> {

  @Query("""
          select s
          from Student s
          left join fetch s.level l
      """)
  Page<StudentListProjection> findAllProjectedBy(Pageable pageable);

  @Override
  @EntityGraph(attributePaths = { "level" })
  Page<Student> findAll(Specification<Student> spec, Pageable pageable);

  @Query("select distinct l.levelName from Student s join s.level l where l.levelName is not null order by l.levelName")
  List<String> findDistinctLevelNames();

  @Query("select distinct s.gender from Student s where s.gender is not null order by s.gender")
  List<Gender> findDistinctGenders();

  @Query("select distinct l.cycle from Student s join s.level l where l.cycle is not null order by l.cycle")
  List<Cycle> findDistinctCycles();

  @Query(value = """
      SELECT to_char(date_trunc('month', signup_date), 'YYYY-MM') as label, count(*) as count
      FROM student s
      WHERE extract(year from signup_date) = :year
      GROUP BY 1
      ORDER BY 1
      """, nativeQuery = true)
  List<DashboardMonthCountProjection> findStudentCountsByMonth(@Param("year") int year);

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
                                                  SELECT date_trunc('day', signup_date)::date as day, count(*) as count
        FROM student
                                                  WHERE extract(year from signup_date) = :year AND extract(month from signup_date) = :month
        GROUP BY day
      ) agg ON agg.day = days.d
      ORDER BY days.d
      """, nativeQuery = true)
  List<DashboardDayCountProjection> findStudentCountsByDay(
      @Param("year") int year,
      @Param("month") int month);

  @Query("SELECT COUNT(s) FROM Student s WHERE YEAR(s.singupDate) = :year AND MONTH(s.singupDate) = :month")
  long countByYearAndMonth(@Param("year") int year, @Param("month") int month);
}

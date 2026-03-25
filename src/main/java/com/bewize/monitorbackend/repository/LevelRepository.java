package com.bewize.monitorbackend.repository;

import com.bewize.monitorbackend.domains.school.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
} 

package com.climbup.repository;

import com.climbup.model.ProductivityScore;
import com.climbup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductivityScoreRepository extends JpaRepository<ProductivityScore, Long> {

    Optional<ProductivityScore> findByUserAndDate(User user, LocalDate date);

    List<ProductivityScore> findByUserAndDateBetweenOrderByDateAsc(User user, LocalDate start, LocalDate end);
}


package com.unitrack.grade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    boolean existsByStudent_Id(Long studentId);
}

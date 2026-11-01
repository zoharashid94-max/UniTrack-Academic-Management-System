package com.unitrack.semester;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    
    boolean existsBySemesterNameIgnoreCase(String semesterName);
}

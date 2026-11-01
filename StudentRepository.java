package com.unitrack.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("""
        SELECT s FROM Student s
        WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(s.department) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(s.semester) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<Student> searchStudents(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Page<Student> findByDepartmentIgnoreCase(
            String department,
            Pageable pageable
    );
}

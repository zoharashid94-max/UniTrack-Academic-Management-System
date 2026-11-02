package com.unitrack.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("""
        SELECT c FROM Course c
        WHERE (
            :keyword = ''
            OR LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.department) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.semester) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (
            :department = ''
            OR LOWER(c.department) = LOWER(:department)
        )
        """)
    Page<Course> searchCourses(
            @Param("keyword") String keyword,
            @Param("department") String department,
            Pageable pageable
    );
}

package com.unitrack;

import com.unitrack.student.StudentRepository;
import com.unitrack.course.CourseRepository;
import com.unitrack.faculty.FacultyRepository;
import com.unitrack.enrollment.EnrollmentRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnalyticsController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AnalyticsController(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            FacultyRepository facultyRepository,
            EnrollmentRepository enrollmentRepository) {

        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping("/analytics")
    public String showAnalytics(Model model) {

        model.addAttribute(
                "totalStudents",
                studentRepository.count()
        );

        model.addAttribute(
                "totalCourses",
                courseRepository.count()
        );

        model.addAttribute(
                "totalFaculty",
                facultyRepository.count()
        );

        model.addAttribute(
                "totalEnrollments",
                enrollmentRepository.count()
        );

        model.addAttribute(
                "enrollments",
                enrollmentRepository.findAll()
        );

        return "analytics";
    }
}

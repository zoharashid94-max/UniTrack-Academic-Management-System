
package com.unitrack.enrollment;

import com.unitrack.student.Student;
import com.unitrack.student.StudentRepository;
import com.unitrack.course.Course;
import com.unitrack.course.CourseRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentController(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/enrollments")
    public String listEnrollments(Model model) {

        model.addAttribute(
                "enrollments",
                enrollmentRepository.findAll()
        );

        model.addAttribute(
                "students",
                studentRepository.findAll()
        );

        model.addAttribute(
                "courses",
                courseRepository.findAll()
        );

        model.addAttribute("enrollment", new Enrollment());

        return "enrollments";
    }

    @PostMapping("/enrollments/add")
    public String addEnrollment(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "Active") String status,
            RedirectAttributes redirectAttributes) {

        if (studentId == null || courseId == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please select both a student and a course."
            );

            return "redirect:/enrollments";
        }

        Student student = studentRepository.findById(studentId)
                .orElse(null);

        Course course = courseRepository.findById(courseId)
                .orElse(null);

        if (student == null || course == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Selected student or course was not found."
            );

            return "redirect:/enrollments";
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus(status);

        enrollmentRepository.save(enrollment);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Enrollment added successfully!"
        );

        return "redirect:/enrollments";
    }

    @PostMapping("/enrollments/delete/{id}")
    public String deleteEnrollment(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Enrollment deleted successfully!"
            );
        }

        return "redirect:/enrollments";
    }
}

package com.unitrack.grade;

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
public class GradeController {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public GradeController(
            GradeRepository gradeRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/grades")
    public String showGrades(Model model) {
        model.addAttribute("grades", gradeRepository.findAll());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());

        return "grades";
    }

    @PostMapping("/grades/add")
    public String addGrade(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Double marksObtained,
            @RequestParam(required = false) Double totalMarks,
            RedirectAttributes redirectAttributes) {

        if (studentId == null || courseId == null
                || marksObtained == null || totalMarks == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please complete all fields.");
            return "redirect:/grades";
        }

        if (totalMarks <= 0 || marksObtained < 0
                || marksObtained > totalMarks) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Marks must be between zero and the total marks.");
            return "redirect:/grades";
        }

        Student student = studentRepository.findById(studentId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);

        if (student == null || course == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Selected student or course was not found.");
            return "redirect:/grades";
        }

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setCourse(course);
        grade.setMarksObtained(marksObtained);
        grade.setTotalMarks(totalMarks);

        double percentage = (marksObtained / totalMarks) * 100.0;
        percentage = Math.round(percentage * 100.0) / 100.0;

        grade.setPercentage(percentage);
        grade.setLetterGrade(calculateLetterGrade(percentage));
        grade.setResultStatus(percentage >= 50.0 ? "Pass" : "Fail");
        grade.setResultDate(LocalDate.now());

        gradeRepository.save(grade);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Result added successfully!");

        return "redirect:/grades";
    }

    @PostMapping("/grades/delete/{id}")
    public String deleteGrade(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (gradeRepository.existsById(id)) {
            gradeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Result deleted successfully!");
        }

        return "redirect:/grades";
    }

    private String calculateLetterGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        return "F";
    }
}

package com.unitrack.student;

import com.unitrack.activity.Activity;
import com.unitrack.activity.ActivityRepository;
import com.unitrack.enrollment.EnrollmentRepository;
import com.unitrack.grade.GradeRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    public StudentController(
            StudentRepository studentRepository,
            ActivityRepository activityRepository,
            EnrollmentRepository enrollmentRepository,
            GradeRepository gradeRepository) {

        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
    }

    @GetMapping("/students")
    public String showStudents(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String department,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int pageSize = 10;
        int safePage = Math.max(page, 0);

        Pageable pageable = PageRequest.of(
                safePage,
                pageSize,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Student> studentPage;

        if (!keyword.trim().isEmpty()) {
            studentPage = studentRepository.searchStudents(
                    keyword.trim(), pageable
            );
        } else if (!department.trim().isEmpty()) {
            studentPage = studentRepository.findByDepartmentIgnoreCase(
                    department.trim(), pageable
            );
        } else {
            studentPage = studentRepository.findAll(pageable);
        }

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalStudents", studentPage.getTotalElements());

        return "students";
    }

    @PostMapping("/students/add")
    public String addStudent(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String department,
            @RequestParam String semester,
            RedirectAttributes redirectAttributes) {

        Student student = new Student(
                fullName.trim(),
                email.trim(),
                department,
                semester
        );

        studentRepository.save(student);

        activityRepository.save(new Activity(
                "STUDENT_ADDED",
                "A new student, " + student.getFullName()
                        + ", was successfully added."
        ));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Student added successfully!"
        );

        return "redirect:/students";
    }

    @GetMapping("/students/edit/{id}")
    public String showEditStudent(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return studentRepository.findById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    return "edit-student";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "Student record was not found."
                    );
                    return "redirect:/students";
                });
    }

    @PostMapping("/students/update/{id}")
    public String updateStudent(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String department,
            @RequestParam String semester,
            RedirectAttributes redirectAttributes) {

        return studentRepository.findById(id)
                .map(student -> {
                    student.setFullName(fullName.trim());
                    student.setEmail(email.trim());
                    student.setDepartment(department);
                    student.setSemester(semester);

                    studentRepository.save(student);

                    activityRepository.save(new Activity(
                            "STUDENT_UPDATED",
                            "The student record for "
                                    + student.getFullName()
                                    + " was successfully updated."
                    ));

                    redirectAttributes.addFlashAttribute(
                            "successMessage",
                            "Student details updated successfully!"
                    );

                    return "redirect:/students";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "Student record was not found."
                    );
                    return "redirect:/students";
                });
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Student student = studentRepository.findById(id).orElse(null);

        if (student == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Student record was not found."
            );
            return "redirect:/students";
        }

        boolean hasGrades = gradeRepository.existsByStudent_Id(id);
        boolean hasEnrollments = enrollmentRepository.existsByStudent_Id(id);

        if (hasGrades || hasEnrollments) {
            String reason;

            if (hasGrades && hasEnrollments) {
                reason = "grades and course enrollments";
            } else if (hasGrades) {
                reason = "grades";
            } else {
                reason = "course enrollments";
            }

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This student cannot be deleted because "
                            + reason
                            + " are linked to this record. "
                            + "Academic records have been kept safe."
            );

            return "redirect:/students";
        }

        String studentName = student.getFullName();

        try {
            studentRepository.delete(student);
            studentRepository.flush();

            activityRepository.save(new Activity(
                    "STUDENT_DELETED",
                    "The student record for "
                            + studentName
                            + " was successfully deleted."
            ));

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Student deleted successfully!"
            );

        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This student cannot be deleted because another "
                            + "record is linked to this student."
            );
        }

        return "redirect:/students";
    }
}

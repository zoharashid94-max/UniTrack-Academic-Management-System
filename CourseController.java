package com.unitrack.course;

import com.unitrack.activity.Activity;
import com.unitrack.activity.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CourseController {

    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;

    public CourseController(
            CourseRepository courseRepository,
            ActivityRepository activityRepository) {
        this.courseRepository = courseRepository;
        this.activityRepository = activityRepository;
    }

    @GetMapping("/courses")
    public String showCourses(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String department,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int safePage = Math.max(page, 0);
        int pageSize = 10;

        Pageable pageable = PageRequest.of(
                safePage,
                pageSize,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Course> coursePage = courseRepository.searchCourses(
                keyword.trim(),
                department.trim(),
                pageable
        );

        // If the requested page is out of range, show the last valid page.
        if (coursePage.getTotalPages() > 0
                && safePage >= coursePage.getTotalPages()) {

            safePage = coursePage.getTotalPages() - 1;

            pageable = PageRequest.of(
                    safePage,
                    pageSize,
                    Sort.by(Sort.Direction.DESC, "id")
            );

            coursePage = courseRepository.searchCourses(
                    keyword.trim(),
                    department.trim(),
                    pageable
            );
        }

        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalCourses", coursePage.getTotalElements());

        return "courses";
    }

    @PostMapping("/courses/add")
    public String addCourse(
            @RequestParam String courseName,
            @RequestParam String courseCode,
            @RequestParam String department,
            @RequestParam Integer creditHours,
            @RequestParam String semester,
            RedirectAttributes redirectAttributes) {

        Course course = new Course(
                courseName.trim(),
                courseCode.trim(),
                department.trim(),
                creditHours,
                semester.trim()
        );

        courseRepository.save(course);

        activityRepository.save(new Activity(
                "COURSE_ADDED",
                "A new course, " + course.getCourseName()
                        + " (" + course.getCourseCode()
                        + "), was successfully added."
        ));

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Course added successfully!"
        );

        return "redirect:/courses";
    }

    @GetMapping("/courses/edit/{id}")
    public String editCourse(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return courseRepository.findById(id)
                .map(course -> {
                    model.addAttribute("course", course);
                    return "edit-course";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "Course record was not found."
                    );
                    return "redirect:/courses";
                });
    }

    @PostMapping("/courses/update/{id}")
    public String updateCourse(
            @PathVariable Long id,
            @RequestParam String courseName,
            @RequestParam String courseCode,
            @RequestParam String department,
            @RequestParam Integer creditHours,
            @RequestParam String semester,
            RedirectAttributes redirectAttributes) {

        return courseRepository.findById(id)
                .map(course -> {
                    course.setCourseName(courseName.trim());
                    course.setCourseCode(courseCode.trim());
                    course.setDepartment(department.trim());
                    course.setCreditHours(creditHours);
                    course.setSemester(semester.trim());

                    courseRepository.save(course);

                    activityRepository.save(new Activity(
                            "COURSE_UPDATED",
                            "The course record for "
                                    + course.getCourseName()
                                    + " (" + course.getCourseCode()
                                    + ") was successfully updated."
                    ));

                    redirectAttributes.addFlashAttribute(
                            "successMessage",
                            "Course updated successfully!"
                    );

                    return "redirect:/courses";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "Course record was not found."
                    );
                    return "redirect:/courses";
                });
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        return courseRepository.findById(id)
                .map(course -> {
                    String courseName = course.getCourseName();
                    String courseCode = course.getCourseCode();

                    courseRepository.deleteById(id);

                    activityRepository.save(new Activity(
                            "COURSE_DELETED",
                            "The course record for "
                                    + courseName
                                    + " (" + courseCode
                                    + ") was successfully deleted."
                    ));

                    redirectAttributes.addFlashAttribute(
                            "successMessage",
                            "Course deleted successfully!"
                    );

                    return "redirect:/courses";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            "Course record was not found."
                    );
                    return "redirect:/courses";
                });
    }
}

package com.unitrack;

import com.unitrack.activity.Activity;
import com.unitrack.activity.ActivityRepository;
import com.unitrack.course.CourseRepository;
import com.unitrack.faculty.FacultyRepository;
import com.unitrack.student.Student;
import com.unitrack.student.StudentRepository;
import com.unitrack.enrollment.Enrollment;
import com.unitrack.enrollment.EnrollmentRepository;
import com.unitrack.grade.GradeRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    public DashboardController(
            StudentRepository studentRepository,
            ActivityRepository activityRepository,
            CourseRepository courseRepository,
            FacultyRepository facultyRepository,
            EnrollmentRepository enrollmentRepository,
            GradeRepository gradeRepository) {

        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute("totalStudents", studentRepository.count());
        model.addAttribute("totalCourses", courseRepository.count());
        model.addAttribute("totalFaculty", facultyRepository.count());
        model.addAttribute("totalEnrollments", enrollmentRepository.count());
        model.addAttribute("totalResults", gradeRepository.count());

        // Unread notifications count for the dashboard bell.
        model.addAttribute(
                "unreadNotificationCount",
                activityRepository.countByReadStatusFalse()
        );

        model.addAttribute("courses", courseRepository.findAll());

        List<Student> recentStudents = studentRepository.findAll(
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.DESC, "id")
                )
        ).getContent();

        List<Activity> recentActivities =
                activityRepository.findTop10ByOrderByCreatedAtDesc();

        if (recentActivities.size() > 5) {
            recentActivities = new ArrayList<>(
                    recentActivities.subList(0, 5)
            );
        }

        model.addAttribute("recentStudents", recentStudents);
        model.addAttribute("recentActivities", recentActivities);

        // Prepare real enrollment data for the latest six months.
        List<Enrollment> allEnrollments = enrollmentRepository.findAll();
        List<Map<String, Object>> chartMonths = new ArrayList<>();

        YearMonth currentMonth = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);

            int total = 0;
            int active = 0;

            for (Enrollment enrollment : allEnrollments) {
                LocalDate enrollmentDate = enrollment.getEnrollmentDate();

                if (enrollmentDate != null
                        && YearMonth.from(enrollmentDate).equals(month)) {

                    total++;

                    if ("Active".equalsIgnoreCase(enrollment.getStatus())) {
                        active++;
                    }
                }
            }

            Map<String, Object> point = new HashMap<>();

            point.put(
                    "label",
                    month.getMonth().getDisplayName(
                            TextStyle.SHORT,
                            Locale.ENGLISH
                    )
            );
            point.put("total", total);
            point.put("active", active);

            chartMonths.add(point);
        }

        model.addAttribute("chartMonths", chartMonths);
        model.addAttribute("currentTime", LocalDateTime.now());

        return "index";
    }
}

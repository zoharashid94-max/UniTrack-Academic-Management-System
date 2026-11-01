
package com.unitrack.semester;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SemesterController {

    private final SemesterRepository semesterRepository;

    public SemesterController(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    @GetMapping("/semesters")
    public String showSemesters(Model model) {
        List<Semester> semesters = semesterRepository.findAll();

        model.addAttribute("semesters", semesters);
        model.addAttribute("semester", new Semester());
        model.addAttribute("totalSemesters", semesters.size());

        return "semesters";
    }

    @PostMapping("/semesters")
    public String addSemester(
            @ModelAttribute("semester") Semester semester,
            RedirectAttributes redirectAttributes) {

        String name = semester.getSemesterName();

        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please enter a semester name."
            );
            return "redirect:/semesters";
        }

        semester.setSemesterName(name.trim());

        if (semesterRepository.existsBySemesterNameIgnoreCase(
                semester.getSemesterName())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "A semester with this name already exists."
            );
            return "redirect:/semesters";
        }

        if (semester.getStartDate() != null
                && semester.getEndDate() != null
                && semester.getEndDate().isBefore(semester.getStartDate())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "End date cannot be before the start date."
            );
            return "redirect:/semesters";
        }

        if (semester.getStatus() == null
                || semester.getStatus().trim().isEmpty()) {
            semester.setStatus("Upcoming");
        }

        semesterRepository.save(semester);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Semester added successfully."
        );

        return "redirect:/semesters";
    }

    @PostMapping("/semesters/delete/{id}")
    public String deleteSemester(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (semesterRepository.existsById(id)) {
            semesterRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Semester deleted successfully."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Semester not found."
            );
        }

        return "redirect:/semesters";
    }
}

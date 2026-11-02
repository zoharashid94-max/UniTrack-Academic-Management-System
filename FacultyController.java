package com.unitrack.faculty;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class FacultyController {

    private final FacultyRepository facultyRepository;

    public FacultyController(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @GetMapping("/faculty")
    public String listFaculty(Model model) {
        model.addAttribute("facultyMembers", facultyRepository.findAll());
        model.addAttribute("faculty", new Faculty());
        return "faculty";
    }

    @PostMapping("/faculty/add")
    public String addFaculty(@ModelAttribute Faculty faculty,
                             RedirectAttributes redirectAttributes) {
        facultyRepository.save(faculty);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Faculty member " + faculty.getFullName() + " was successfully added."
        );
        return "redirect:/faculty";
    }

    @GetMapping("/faculty/edit/{id}")
    public String editFaculty(@PathVariable Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        Optional<Faculty> facultyOptional = facultyRepository.findById(id);

        if (facultyOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Faculty record was not found."
            );
            return "redirect:/faculty";
        }

        model.addAttribute("faculty", facultyOptional.get());
        return "faculty-edit";
    }

    @PostMapping("/faculty/update/{id}")
    public String updateFaculty(@PathVariable Long id,
                                @ModelAttribute Faculty updatedFaculty,
                                RedirectAttributes redirectAttributes) {
        Optional<Faculty> facultyOptional = facultyRepository.findById(id);

        if (facultyOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Faculty record was not found."
            );
            return "redirect:/faculty";
        }

        Faculty faculty = facultyOptional.get();
        faculty.setFullName(updatedFaculty.getFullName());
        faculty.setEmail(updatedFaculty.getEmail());
        faculty.setDepartment(updatedFaculty.getDepartment());
        faculty.setDesignation(updatedFaculty.getDesignation());

        facultyRepository.save(faculty);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "The faculty record for " + faculty.getFullName()
                        + " was successfully updated."
        );

        return "redirect:/faculty";
    }

    @PostMapping("/faculty/delete/{id}")
    public String deleteFaculty(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        Optional<Faculty> facultyOptional = facultyRepository.findById(id);

        if (facultyOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Faculty record was not found."
            );
            return "redirect:/faculty";
        }

        String facultyName = facultyOptional.get().getFullName();
        facultyRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "The faculty record for " + facultyName
                        + " was successfully deleted."
        );

        return "redirect:/faculty";
    }
}

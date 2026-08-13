package com.messmanagement.controller;

import com.messmanagement.service.LogoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Lets the mess owner upload their own logo image from their computer.
 * The image is shown in the navbar on every page once uploaded.
 */
@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final LogoService logoService;

    public SettingsController(LogoService logoService) {
        this.logoService = logoService;
    }

    @GetMapping
    public String settingsPage(Model model) {
        model.addAttribute("hasLogo", logoService.hasLogo());
        model.addAttribute("logoFilename", logoService.getCurrentLogoFilename());
        return "settings";
    }

    @PostMapping("/logo")
    public String uploadLogo(@RequestParam("logoFile") MultipartFile logoFile, Model model) {
        try {
            logoService.uploadLogo(logoFile);
            model.addAttribute("successMessage", "Logo updated.");
        } catch (IllegalArgumentException | IOException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("hasLogo", logoService.hasLogo());
        model.addAttribute("logoFilename", logoService.getCurrentLogoFilename());
        return "settings";
    }
}

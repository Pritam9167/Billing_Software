package com.main.springboot.controller;

import com.main.springboot.services.StockConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/config")
public class StockConfigController {

    @Autowired
    private StockConfigService stockConfigService;

    @GetMapping("/stock-threshold")
    public String showConfigPage(Model model) {
        model.addAttribute("threshold", stockConfigService.getThreshold());
        return "config_threshold";
    }

    @PostMapping("/update-threshold")
    public String updateThreshold(@RequestParam int threshold) {
        stockConfigService.updateThreshold(threshold);
        return "redirect:/admin/config/stock-threshold";
    }
}
 
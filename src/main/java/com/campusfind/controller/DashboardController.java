package com.campusfind.controller;

import com.campusfind.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DashboardController {

    private final ItemService itemService;

    @Autowired
    public DashboardController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> stats = itemService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("recentItems", itemService.getRecentItems(10));
        model.addAttribute("categories", itemService.getAllCategories());
        return "dashboard";
    }

    @GetMapping("/api/stats")
    @ResponseBody
    public Map<String, Object> getStatsApi() {
        return itemService.getDashboardStats();
    }
}

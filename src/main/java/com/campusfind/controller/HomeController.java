package com.campusfind.controller;

import com.campusfind.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomeController {

    private final ItemService itemService;

    @Autowired
    public HomeController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    public String index(Model model) {
        Map<String, Object> stats = itemService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("recentItems", itemService.getRecentItems(6));
        model.addAttribute("categories", itemService.getAllCategories());
        return "index";
    }
}

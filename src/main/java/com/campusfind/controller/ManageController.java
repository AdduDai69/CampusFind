package com.campusfind.controller;

import com.campusfind.model.Item;
import com.campusfind.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ManageController {

    private final ItemService itemService;

    @Autowired
    public ManageController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/manage")
    public String manageReports(@RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "category", required = false) String category,
                                @RequestParam(value = "status", required = false) String status,
                                Model model) {

        List<Item> items = itemService.searchAndFilter(keyword, type, category, status);
        Map<String, Object> stats = itemService.getDashboardStats();

        model.addAttribute("items", items);
        model.addAttribute("stats", stats);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("selectedType", type != null ? type : "ALL");
        model.addAttribute("selectedCategory", category != null ? category : "ALL");
        model.addAttribute("selectedStatus", status != null ? status : "ALL");
        model.addAttribute("categories", itemService.getAllCategories());
        model.addAttribute("resultCount", items.size());

        return "manage";
    }
}

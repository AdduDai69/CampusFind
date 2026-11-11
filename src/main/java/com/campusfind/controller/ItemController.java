package com.campusfind.controller;

import com.campusfind.model.Item;
import com.campusfind.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // Browse items with multi-filter search
    @GetMapping({"/items", "/browse"})
    public String browseItems(@RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "type", required = false) String type,
                              @RequestParam(value = "category", required = false) String category,
                              @RequestParam(value = "status", required = false) String status,
                              Model model) {

        List<Item> items = itemService.searchAndFilter(keyword, type, category, status);

        model.addAttribute("items", items);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("selectedType", type != null ? type : "ALL");
        model.addAttribute("selectedCategory", category != null ? category : "ALL");
        model.addAttribute("selectedStatus", status != null ? status : "ALL");
        model.addAttribute("categories", itemService.getAllCategories());
        model.addAttribute("resultCount", items.size());

        return "browse";
    }

    // Report form (support both /report and /items/new)
    @GetMapping({"/report", "/items/new"})
    public String showReportForm(@RequestParam(value = "type", required = false, defaultValue = "LOST") String type,
                                 Model model) {
        Item item = new Item();
        item.setType("FOUND".equalsIgnoreCase(type) ? "FOUND" : "LOST");
        item.setDateReported(LocalDate.now());
        item.setStatus("PENDING");

        model.addAttribute("item", item);
        model.addAttribute("categories", itemService.getAllCategories());
        return "report";
    }

    // Process Report submission
    @PostMapping("/items")
    public String submitReport(@Valid @ModelAttribute("item") Item item,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", itemService.getAllCategories());
            return "report";
        }

        Item saved = itemService.saveItem(item);
        redirectAttributes.addFlashAttribute("successMessage",
                "Your " + saved.getType() + " item report ('" + saved.getName() + "') was registered successfully!");

        return "redirect:/items/" + saved.getId();
    }

    // View single item details
    @GetMapping("/items/{id}")
    public String viewItemDetails(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Item> itemOpt = itemService.getItemById(id);
        if (itemOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Item with ID #" + id + " was not found.");
            return "redirect:/browse";
        }

        model.addAttribute("item", itemOpt.get());
        return "details";
    }

    // Edit item form
    @GetMapping("/items/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Item> itemOpt = itemService.getItemById(id);
        if (itemOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit: Item #" + id + " not found.");
            return "redirect:/browse";
        }

        model.addAttribute("item", itemOpt.get());
        model.addAttribute("categories", itemService.getAllCategories());
        return "edit";
    }

    // Process edit update
    @PostMapping("/items/{id}/edit")
    public String updateItem(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("item") Item item,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            item.setId(id);
            model.addAttribute("categories", itemService.getAllCategories());
            return "edit";
        }

        try {
            itemService.updateItem(id, item);
            redirectAttributes.addFlashAttribute("successMessage", "Item #" + id + " details updated successfully.");
            return "redirect:/items/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/browse";
        }
    }

    // Delete item
    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable("id") Long id,
                             @RequestParam(value = "redirectSource", required = false, defaultValue = "browse") String redirectSource,
                             RedirectAttributes redirectAttributes) {
        boolean deleted = itemService.deleteItem(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Item #" + id + " has been deleted.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Item #" + id + " could not be found for deletion.");
        }

        if ("manage".equalsIgnoreCase(redirectSource)) {
            return "redirect:/manage";
        }
        return "redirect:/browse";
    }

    // Toggle or mark recovery status
    @PostMapping("/items/{id}/toggle-status")
    public String toggleStatus(@PathVariable("id") Long id,
                               @RequestParam(value = "redirectSource", required = false, defaultValue = "details") String redirectSource,
                               RedirectAttributes redirectAttributes) {
        try {
            Item updated = itemService.toggleRecoveryStatus(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Item #" + id + " status marked as " + updated.getStatus() + ".");

            if ("manage".equalsIgnoreCase(redirectSource)) {
                return "redirect:/manage";
            }
            return "redirect:/items/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/browse";
        }
    }
}

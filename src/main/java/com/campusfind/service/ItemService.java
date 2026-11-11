package com.campusfind.service;

import com.campusfind.model.Item;
import com.campusfind.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    public static final List<String> CATEGORIES = List.of(
            "Electronics",
            "Documents",
            "Accessories",
            "Books",
            "Clothing",
            "Personal belongings",
            "Other"
    );

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // Retrieve all items sorted by newest report date
    public List<Item> getAllItems() {
        return itemRepository.findAllByOrderByDateReportedDescIdDesc();
    }

    // Retrieve single item by ID
    public Optional<Item> getItemById(Long id) {
        if (id == null) return Optional.empty();
        return itemRepository.findById(id);
    }

    // Save or update an item
    public Item saveItem(Item item) {
        if (item.getType() != null) {
            item.setType(item.getType().trim().toUpperCase());
        }
        if (item.getStatus() == null || item.getStatus().isBlank()) {
            item.setStatus("PENDING");
        } else {
            item.setStatus(item.getStatus().trim().toUpperCase());
        }
        if (item.getDateReported() == null) {
            item.setDateReported(LocalDate.now());
        }
        return itemRepository.save(item);
    }

    // Update existing item details
    public Item updateItem(Long id, Item updatedData) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item with ID " + id + " not found"));

        existing.setName(updatedData.getName());
        existing.setCategory(updatedData.getCategory());
        existing.setDescription(updatedData.getDescription());
        existing.setLocation(updatedData.getLocation());
        existing.setType(updatedData.getType() != null ? updatedData.getType().trim().toUpperCase() : existing.getType());
        existing.setDateReported(updatedData.getDateReported() != null ? updatedData.getDateReported() : existing.getDateReported());
        existing.setStatus(updatedData.getStatus() != null ? updatedData.getStatus().trim().toUpperCase() : existing.getStatus());
        existing.setContact(updatedData.getContact());

        return itemRepository.save(existing);
    }

    // Delete item by ID
    public boolean deleteItem(Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Toggle or set recovery status
    public Item toggleRecoveryStatus(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item with ID " + id + " not found"));

        if ("RECOVERED".equalsIgnoreCase(item.getStatus())) {
            item.setStatus("PENDING");
        } else {
            item.setStatus("RECOVERED");
        }
        return itemRepository.save(item);
    }

    // Explicitly mark as recovered
    public Item markAsRecovered(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item with ID " + id + " not found"));
        item.setStatus("RECOVERED");
        return itemRepository.save(item);
    }

    // Recent items for landing & dashboard
    public List<Item> getRecentItems(int count) {
        if (count <= 6) {
            return itemRepository.findTop6ByOrderByDateReportedDescIdDesc();
        }
        return itemRepository.findTop10ByOrderByDateReportedDescIdDesc();
    }

    // Filter and search
    public List<Item> searchAndFilter(String keyword, String type, String category, String status) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanType = (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type.trim())) ? type.trim().toUpperCase() : null;
        String cleanCategory = (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category.trim())) ? category.trim() : null;
        String cleanStatus = (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) ? status.trim().toUpperCase() : null;

        return itemRepository.filterItems(cleanKeyword, cleanType, cleanCategory, cleanStatus);
    }

    // Compute all dashboard statistics
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long total = itemRepository.count();
        long lost = itemRepository.countByType("LOST");
        long found = itemRepository.countByType("FOUND");
        long recovered = itemRepository.countByStatus("RECOVERED");
        long pending = itemRepository.countByStatus("PENDING");

        double recoveryRate = (total > 0) ? ((double) recovered / total) * 100.0 : 0.0;
        long recoveryPercentage = Math.round(recoveryRate);

        stats.put("total", total);
        stats.put("lost", lost);
        stats.put("found", found);
        stats.put("recovered", recovered);
        stats.put("pending", pending);
        stats.put("recoveryRate", String.format("%.1f", recoveryRate));
        stats.put("recoveryPercentage", recoveryPercentage);

        // Category breakdown map
        Map<String, Long> categoryMap = new LinkedHashMap<>();
        for (String cat : CATEGORIES) {
            categoryMap.put(cat, 0L);
        }
        List<Object[]> catRows = itemRepository.countItemsGroupedByCategory();
        for (Object[] row : catRows) {
            String cat = (String) row[0];
            Long cnt = (Long) row[1];
            if (cat != null) {
                categoryMap.put(cat, cnt);
            }
        }
        stats.put("categoryStats", categoryMap);

        return stats;
    }

    public List<String> getAllCategories() {
        return CATEGORIES;
    }
}

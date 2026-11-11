package com.campusfind;

import com.campusfind.model.Item;
import com.campusfind.repository.ItemRepository;
import com.campusfind.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CampusFindApplicationTests {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void contextLoads() {
        assertNotNull(itemService);
        assertNotNull(itemRepository);
    }

    @Test
    void testSampleDataSeeded() {
        // Test that sample data was initialized
        List<Item> allItems = itemService.getAllItems();
        assertFalse(allItems.isEmpty(), "Sample items should be seeded on startup");
        assertTrue(allItems.size() >= 10, "Should have at least 10 sample items seeded");
    }

    @Test
    void testCrudOperations() {
        // 1. Create
        Item newItem = new Item(
                "Test Water Bottle",
                "Personal belongings",
                "Metal hydro flask with blue cap found under desk.",
                "Engineering Lab 101",
                "FOUND",
                LocalDate.now(),
                "PENDING",
                "test@campus.edu"
        );
        Item saved = itemService.saveItem(newItem);
        assertNotNull(saved.getId());
        assertEquals("Test Water Bottle", saved.getName());

        // 2. Read
        Optional<Item> retrieved = itemService.getItemById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("FOUND", retrieved.get().getType());

        // 3. Update
        saved.setName("Updated Water Bottle");
        Item updated = itemService.updateItem(saved.getId(), saved);
        assertEquals("Updated Water Bottle", updated.getName());

        // 4. Toggle status
        Item toggled = itemService.toggleRecoveryStatus(saved.getId());
        assertEquals("RECOVERED", toggled.getStatus());

        // 5. Delete
        boolean deleted = itemService.deleteItem(saved.getId());
        assertTrue(deleted);
        assertFalse(itemService.getItemById(saved.getId()).isPresent());
    }

    @Test
    void testFilterAndSearch() {
        // Search by keyword
        List<Item> macbookResults = itemService.searchAndFilter("MacBook", null, null, null);
        assertFalse(macbookResults.isEmpty());

        // Filter by type
        List<Item> lostItems = itemService.searchAndFilter(null, "LOST", null, null);
        for (Item item : lostItems) {
            assertEquals("LOST", item.getType());
        }

        // Filter by category
        List<Item> electronicItems = itemService.searchAndFilter(null, null, "Electronics", null);
        for (Item item : electronicItems) {
            assertEquals("Electronics", item.getCategory());
        }
    }

    @Test
    void testDashboardStats() {
        Map<String, Object> stats = itemService.getDashboardStats();
        assertNotNull(stats);
        assertTrue((Long) stats.get("total") > 0);
        assertTrue((Long) stats.get("lost") >= 0);
        assertTrue((Long) stats.get("found") >= 0);
        assertNotNull(stats.get("recoveryRate"));
        assertNotNull(stats.get("categoryStats"));
    }
}

package com.campusfind.config;

import com.campusfind.model.Item;
import com.campusfind.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Initializes realistic sample data on first launch.
 * If data already exists in the persistent H2 database, seeding is safely skipped.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ItemRepository itemRepository;

    public DataInitializer(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        long currentCount = itemRepository.count();
        if (currentCount > 0) {
            log.info("Database already contains {} records. Skipping sample data initialization.", currentCount);
            return;
        }

        log.info("First launch detected. Initializing realistic campus lost and found records...");

        LocalDate today = LocalDate.now();

        List<Item> sampleItems = List.of(
                new Item(
                        "Space Gray MacBook Air M2 (13-inch)",
                        "Electronics",
                        "Left on Desk 24, 2nd Floor Quiet Study Area. Has a matte black hard case with a GitHub sticker and university coding club sticker on the cover.",
                        "Central Library - 2nd Floor Study Area",
                        "LOST",
                        today.minusDays(1),
                        "PENDING",
                        "marcus.vance@campus.edu | Phone: (555) 019-8234"
                ),
                new Item(
                        "University Student ID Card & Key Fob",
                        "Documents",
                        "Blue university lanyard holding a student identity card (Name: Emily Chen, Dept: CS) with magnetic dormitory access fob attached.",
                        "Student Union Cafeteria - Near Booth 7",
                        "FOUND",
                        today.minusDays(1),
                        "PENDING",
                        "Turned in to Union Information Desk (Staff Desk #2)"
                ),
                new Item(
                        "Sony WH-1000XM5 Wireless Headphones",
                        "Electronics",
                        "Midnight silver over-ear noise cancelling headphones inside their official gray zipper carrying case with 3.5mm cable inside.",
                        "Engineering Hall - Lecture Room 302",
                        "FOUND",
                        today.minusDays(2),
                        "RECOVERED",
                        "Found by Prof. Davis - Claimed by owner via serial number verification"
                ),
                new Item(
                        "Silver Apple Watch SE (44mm, Nike Sport Band)",
                        "Accessories",
                        "Dark olive Nike sports loop band. Screen has a tiny scratch on the top-right corner. Watch went into low battery mode when lost.",
                        "Recreation & Sports Center - Locker Room Bench",
                        "LOST",
                        today.minusDays(3),
                        "PENDING",
                        "david.k@campus.edu | Room 412 Maple Hall"
                ),
                new Item(
                        "Calculus Early Transcendentals (9th Edition)",
                        "Books",
                        "Hardcover textbook with yellow highlighter marks throughout Chapters 3 and 4. Name 'Liam Miller' written inside front cover.",
                        "Mathematics Building - 1st Floor Study Lounge",
                        "LOST",
                        today.minusDays(4),
                        "PENDING",
                        "liam.miller@campus.edu"
                ),
                new Item(
                        "North Face Black Rain Jacket (Size M)",
                        "Clothing",
                        "Waterproof hooded shell jacket forgotten on the back of a chair. Car key ring in the left zipped pocket.",
                        "Science Quad Bench - Outside BioChem Wing",
                        "FOUND",
                        today.minusDays(4),
                        "PENDING",
                        "Held at Campus Safety & Security Office (Bldg 12)"
                ),
                new Item(
                        "TI-84 Plus CE Graphing Calculator (Rose Gold)",
                        "Electronics",
                        "Rose gold edition calculator with slide cover. Has periodic table cheat sheet taped inside the protective slide cover.",
                        "Chemistry Building - Lab Room 204",
                        "FOUND",
                        today.minusDays(5),
                        "RECOVERED",
                        "Claimed by Chemistry sophomore student"
                ),
                new Item(
                        "Brown Leather Bi-fold Wallet",
                        "Personal belongings",
                        "Contains state driver's license, transit pass, student ID, and university gym card. Urgent reward offered for return.",
                        "Campus Shuttle Bus #4 (East Route)",
                        "LOST",
                        today.minusDays(6),
                        "PENDING",
                        "Contact Campus Transit Dispatch or email: jordan.p@campus.edu"
                ),
                new Item(
                        "Ray-Ban Clubmaster Classic Sunglasses",
                        "Accessories",
                        "Tortoise shell frames with gold rims in a brown textured leather snap case. Cleaning cloth included.",
                        "Campus Green - Under Willow Tree near Fountain",
                        "FOUND",
                        today.minusDays(7),
                        "PENDING",
                        "Turned in to Visitor Center Reception"
                ),
                new Item(
                        "Stainless Steel Hydro Flask (32 oz, Cobalt Blue)",
                        "Personal belongings",
                        "Cobalt blue wide-mouth bottle with silicone boot on bottom and national parks stickers.",
                        "Humanities Auditorium - Row K Seat 12",
                        "LOST",
                        today.minusDays(8),
                        "RECOVERED",
                        "Retrieved from Auditorium lost basket"
                ),
                new Item(
                        "Set of Dormitory & Bike U-Lock Keys",
                        "Accessories",
                        "3 brass keys on a miniature red Swiss Army knife keychain with an orange fluorescent tag.",
                        "Bike Racks outside West Parking Garage",
                        "FOUND",
                        today.minusDays(9),
                        "PENDING",
                        "Handed over to Campus Parking Services Office"
                ),
                new Item(
                        "Anker 20,000mAh Power Bank & USB-C Cable",
                        "Electronics",
                        "Black matte external battery bank with high speed PD port. Left plugged into wall outlet near beanbag chairs.",
                        "Student Hub - 3rd Floor Innovation Pod",
                        "LOST",
                        today.minusDays(10),
                        "PENDING",
                        "taylor.swift-fan@campus.edu | Room 201 Oak Hall"
                )
        );

        itemRepository.saveAll(sampleItems);
        log.info("Successfully seeded {} sample campus lost & found records.", sampleItems.size());
    }
}

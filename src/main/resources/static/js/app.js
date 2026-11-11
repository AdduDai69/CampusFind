/**
 * Campus Find - Client Side Application Logic
 * Ultra-Modern Monochrome Theme & Interactions
 */

document.addEventListener("DOMContentLoaded", () => {
    // 1. Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll(".alert-dismissible");
    alerts.forEach(alert => {
        setTimeout(() => {
            try {
                const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
                bsAlert.close();
            } catch (e) {
                alert.style.display = "none";
            }
        }, 5000);
    });

    // 2. Report Form Type Selector (LOST vs FOUND button toggle)
    const typeRadios = document.querySelectorAll("input[name='type']");

    function updateReportTypeHighlight() {
        typeRadios.forEach(radio => {
            const card = document.querySelector(`label[for='${radio.id}']`);
            if (card) {
                if (radio.checked) {
                    card.classList.add("selected");
                } else {
                    card.classList.remove("selected");
                }
            }
        });
    }

    if (typeRadios.length > 0) {
        typeRadios.forEach(radio => {
            radio.addEventListener("change", updateReportTypeHighlight);
        });
        updateReportTypeHighlight();
    }

    // 3. Quick building filter buttons on Browse page
    const buildingPills = document.querySelectorAll(".quick-building-filter");
    const keywordInput = document.getElementById("searchKeyword");
    const filterForm = document.getElementById("filterForm");

    if (buildingPills.length > 0 && keywordInput && filterForm) {
        buildingPills.forEach(pill => {
            pill.addEventListener("click", (e) => {
                e.preventDefault();
                keywordInput.value = pill.getAttribute("data-building");
                filterForm.submit();
            });
        });
    }

    // 4. Delete Confirmation Dialog
    const deleteButtons = document.querySelectorAll(".btn-delete-confirm");
    deleteButtons.forEach(btn => {
        btn.addEventListener("click", (e) => {
            const itemName = btn.getAttribute("data-item-name") || "this item";
            if (!confirm(`Are you sure you want to permanently delete "${itemName}"? This action cannot be undone.`)) {
                e.preventDefault();
            }
        });
    });

    // 5. Scroll Reveal Animations (Intersection Observer)
    const observerOptions = {
        threshold: 0.1,
        rootMargin: "0px 0px -40px 0px"
    };

    const revealObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add("animate-fade-in-up");
                revealObserver.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll(".cf-card, .stat-card").forEach(el => {
        revealObserver.observe(el);
    });

    // 6. Initialize Charts if canvas is present
    initDashboardCharts();
});

function initDashboardCharts() {
    const statusCanvas = document.getElementById("statusPieChart");
    const categoryCanvas = document.getElementById("categoryBarChart");

    if (statusCanvas && typeof Chart !== "undefined") {
        const lostCount = parseInt(statusCanvas.getAttribute("data-lost") || "0", 10);
        const foundCount = parseInt(statusCanvas.getAttribute("data-found") || "0", 10);
        const recoveredCount = parseInt(statusCanvas.getAttribute("data-recovered") || "0", 10);

        new Chart(statusCanvas, {
            type: "doughnut",
            data: {
                labels: ["Lost Items", "Found Items", "Recovered"],
                datasets: [{
                    data: [lostCount, foundCount, recoveredCount],
                    backgroundColor: [
                        "#F43F5E", // Ruby Crimson
                        "#FFFFFF", // Stark White
                        "#10B981"  // Emerald
                    ],
                    borderColor: "#0E1015",
                    borderWidth: 4,
                    hoverOffset: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: "bottom",
                        labels: {
                            color: "#A1A1AA",
                            font: { family: "'Plus Jakarta Sans', sans-serif", size: 12, weight: 600 },
                            padding: 18,
                            usePointStyle: true,
                            pointStyle: "circle"
                        }
                    }
                },
                cutout: "72%"
            }
        });
    }

    if (categoryCanvas && typeof Chart !== "undefined") {
        try {
            const rawData = categoryCanvas.getAttribute("data-categories");
            if (rawData) {
                const parsed = JSON.parse(rawData);
                const labels = Object.keys(parsed);
                const values = Object.values(parsed);

                new Chart(categoryCanvas, {
                    type: "bar",
                    data: {
                        labels: labels,
                        datasets: [{
                            label: "Reported Items",
                            data: values,
                            backgroundColor: "rgba(255, 255, 255, 0.85)",
                            hoverBackgroundColor: "#FFFFFF",
                            borderRadius: 10,
                            borderSkipped: false
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            x: {
                                grid: { display: false },
                                ticks: { 
                                    color: "#A1A1AA", 
                                    font: { family: "'Plus Jakarta Sans', sans-serif", size: 11, weight: 500 } 
                                }
                            },
                            y: {
                                beginAtZero: true,
                                grid: { color: "rgba(255, 255, 255, 0.05)" },
                                ticks: {
                                    color: "#A1A1AA",
                                    stepSize: 1,
                                    precision: 0,
                                    font: { family: "'Plus Jakarta Sans', sans-serif" }
                                }
                            }
                        }
                    }
                });
            }
        } catch (e) {
            console.error("Failed to parse category chart data", e);
        }
    }
}

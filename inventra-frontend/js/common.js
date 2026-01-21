// js/common.js
async function refreshLowStockAlerts() {
    try {
        const token = localStorage.getItem("token");
        if (!token) return;

        const res = await fetch(`http://localhost:8080/alerts/low-stock?t=${Date.now()}`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (!res.ok) throw new Error("Failed to load alerts");

        const alerts = await res.json();
        const container = document.getElementById("lowStockAlerts");
        if (!container) return; // if not on dashboard page

        container.innerHTML = "";

        if (alerts.length === 0) {
            container.innerHTML = '<p class="text-center text-success">No active low stock alerts</p>';
            return;
        }

        alerts.forEach(alert => {
            const productName = alert.product?.name || "Unknown Product";
            const currentQty = alert.product?.quantity ?? "Unknown";
            const reorderLevel = alert.product?.reorderLevel ?? "Unknown";
            const triggered = alert.triggeredAt ? new Date(alert.triggeredAt).toLocaleString() : "Just now";

            const card = document.createElement("div");
            card.className = "alert-card";
            card.innerHTML = `
                <h4>Low stock alert: '${productName}'</h4>
                <p class="qty">Only ${currentQty} units left (Reorder Level: ${reorderLevel})</p>
                <div class="meta">
                    <span>Triggered: ${triggered}</span>
                </div>
            `;
            container.appendChild(card);
        });

    } catch (err) {
        console.error("Alerts error:", err);
        const container = document.getElementById("lowStockAlerts");
        if (container) {
            container.innerHTML = '<p class="text-danger">Error loading alerts</p>';
        }
    }
}
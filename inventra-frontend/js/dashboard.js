const token = localStorage.getItem("token");

if (!token) {
    window.location.href = "login.html";
}

// TOTAL PRODUCTS
fetch("http://localhost:8080/products/all", {
    headers: { 
        "Authorization": "Bearer " + token,
        "Cache-Control": "no-cache, no-store, must-revalidate"
    },
    cache: "no-store"
})
.then(res => {
    const contentType = res.headers.get("content-type") || "";
    
    if (!res.ok) {
        if (contentType.includes("text/html")) {
            return res.text().then(html => {
                console.log("HTML Preview:", html.substring(0, 300)); // Debug in console
                throw new Error(`Server returned HTML (likely 403/401) - Status: ${res.status}`);
            });
        }
        throw new Error(`Server error - Status: ${res.status}`);
    }
    
    if (!contentType.includes("application/json")) {
        return res.text().then(text => {
            console.log("Response Preview:", text.substring(0, 300)); // Debug in console
            throw new Error(`Unexpected response type: ${contentType}`);
        });
    }
    
    return res.json();
})
.then(data => {
    document.getElementById("totalProducts").innerText = data.length;
})
.catch(err => {
    console.error("Total products fetch failed:", err);
    document.getElementById("totalProducts").innerText = "Error";
    alert("Failed to load total products:\n" + err.message + 
          "\n\nCheck console for details. Possible fix: Logout/login as ADMIN.");
});

// LOW STOCK COUNT
fetch("http://localhost:8080/products/low-stock", {
    headers: { 
        "Authorization": "Bearer " + token,
        "Cache-Control": "no-cache, no-store, must-revalidate"
    },
    cache: "no-store"
})
.then(res => {
    const contentType = res.headers.get("content-type") || "";

    if (!res.ok) {
        if (contentType.includes("text/html")) {
            return res.text().then(html => {
                console.log("HTML Preview:", html.substring(0, 300));
                throw new Error(`Server returned HTML (likely 403/401) - Status: ${res.status}`);
            });
        }
        throw new Error(`Server error - Status: ${res.status}`);
    }

    if (!contentType.includes("application/json")) {
        return res.text().then(text => {
            console.log("Response Preview:", text.substring(0, 300));
            throw new Error(`Unexpected response type: ${contentType}`);
        });
    }

    return res.json();
})
.then(data => {
    document.getElementById("lowstock").innerText = data.length;
})
.catch(err => {
    console.error("Low stock fetch failed:", err);
    document.getElementById("lowstock").innerText = "Error";
});

// Load low-stock alerts on dashboard + beautiful rendering
function loadLowStockAlerts() {
    fetch("http://localhost:8080/alerts/low-stock", {
        headers: {
            "Authorization": "Bearer " + token,
            "Cache-Control": "no-cache, no-store, must-revalidate"
        },
        cache: "no-store"
    })
    .then(res => {
        if (!res.ok) throw new Error(`Failed to load alerts - Status: ${res.status}`);
        return res.json();
    })
    .then(data => {
        const alertContainer = document.getElementById("lowStockAlerts");
        alertContainer.innerHTML = "";

        if (data.length === 0) {
            alertContainer.innerHTML = `
                <div style="text-align:center; padding:2.5rem; color:#64748b; font-size:1.1rem;">
                    ✓ No active low stock alerts at the moment
                </div>`;
            return;
        }

        data.forEach(alert => {
            // Try to parse product name from message or use fallback
            let productName = "Product";
            let quantity = "—";
            let reorderLevel = "—";

            // Attempt to extract useful info from message (you can improve parsing later)
            const msg = alert.message || "";
            const qtyMatch = msg.match(/Qty:?\s*(\d+)/i) || msg.match(/only\s*(\d+)\s*units?/i);
            const reorderMatch = msg.match(/Reorder(?: Level)?:?\s*(\d+)/i);

            if (qtyMatch) quantity = qtyMatch[1];
            if (reorderMatch) reorderLevel = reorderMatch[1];

            if (msg.includes("'") && msg.includes("'")) {
                const nameMatch = msg.match(/'([^']+)'/);
                if (nameMatch) productName = nameMatch[1];
            }

            const isCritical = Number(quantity) <= Number(reorderLevel) / 2;

            const alertCard = document.createElement("div");
            alertCard.className = `alert-card ${isCritical ? 'critical' : ''}`;
            alertCard.innerHTML = `
                <span class="alert-icon">⚠</span>
                <h4>${productName}</h4>
                <div class="alert-content">
                    <p><strong>Current stock:</strong> <span class="qty">${quantity}</span> units</p>
                    <p><strong>Reorder level:</strong> ${reorderLevel}</p>
                </div>
                <div class="meta">
                    <span>Triggered: ${new Date(alert.triggeredAt).toLocaleString()}</span>
                    <span>SKU: ${alert.sku || '—'}</span>
                </div>
            `;
            alertContainer.appendChild(alertCard);
        });
    })
    .catch(err => {
        console.error("Alerts fetch error:", err);
        document.getElementById("lowStockAlerts").innerHTML = `
            <div style="text-align:center; padding:2rem; color:#ef4444;">
                Error loading alerts: ${err.message}
            </div>`;
    });
}

// Call on page load
document.addEventListener("DOMContentLoaded", loadLowStockAlerts);
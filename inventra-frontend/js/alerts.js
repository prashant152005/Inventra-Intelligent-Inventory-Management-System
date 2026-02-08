document.addEventListener("DOMContentLoaded", loadAlerts);

function loadAlerts() {
    fetch("http://localhost:8080/alerts", {
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        }
    })
        .then(res => res.json())
        .then(data => renderAlerts(data));
}

function renderAlerts(alerts) {
    const table = document.getElementById("alertsTable");
    table.innerHTML = "";

    alerts.forEach(alert => {
        let row = document.createElement("tr");

        let typeClass = "";
        if (alert.alertType === "LOW_STOCK") typeClass = "alert-low";
        if (alert.alertType === "LOW_STOCK_NEAR_EXPIRY") typeClass = "alert-combined";

        row.innerHTML = `
            <td>${alert.product.name}</td>
            <td class="${typeClass}">${alert.alertType}</td>
            <td>${alert.message}</td>
            <td>${alert.triggeredAt.replace("T", " ")}</td>
            <td>${alert.active ? "ACTIVE" : "RESOLVED"}</td>
        `;

        table.appendChild(row);
    });
}

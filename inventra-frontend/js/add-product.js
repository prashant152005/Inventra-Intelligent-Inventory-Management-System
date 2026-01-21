const token = localStorage.getItem("token");

// Immediately check token on page load
if (!token) {
    alert("Session expired or not logged in. Please login again.");
    window.location.href = "login.html";
    throw new Error("No token");
}

function addProduct() {
    const sku = document.getElementById("sku")?.value?.trim();
    const name = document.getElementById("name")?.value?.trim();
    const description = document.getElementById("description")?.value?.trim() || "";
    const quantity = Number(document.getElementById("quantity")?.value);
    const reorderLevel = Number(document.getElementById("reorderLevel")?.value);
    const unitPrice = Number(document.getElementById("unitPrice")?.value);

    // Basic validation
    if (!sku || !name || isNaN(quantity) || isNaN(reorderLevel) || isNaN(unitPrice)) {
        alert("Please fill all required fields correctly!");
        return;
    }

    const body = {
        sku,
        name,
        description,
        quantity,
        reorderLevel,
        unitPrice
    };

    fetch("http://localhost:8080/products/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify(body)
    })
    .then(res => {
        if (!res.ok) {
            return res.text().then(text => {
                throw new Error(`Server error ${res.status}: ${text || "Unknown error"}`);
            });
        }
        return res.json();
    })
    .then(data => {
        console.log("Product added:", data);
        alert("Product added successfully!");
        window.location.href = "products.html?t=" + new Date().getTime();
    })
    .catch(err => {
        console.error("Add product failed:", err);
        alert("Failed to add product!\n" + err.message);
    });
}
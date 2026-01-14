const token = localStorage.getItem("token");
const role = localStorage.getItem("role");
document.getElementById("userRole").innerText = role;

if (role !== "ADMIN") {
    document.querySelectorAll(".admin-only")
        .forEach(el => el.style.display = "none");
}

function addProduct() {
    const product = {
        sku: document.getElementById("sku").value,
        name: document.getElementById("name").value,
        description: document.getElementById("description").value,
        quantity: parseInt(document.getElementById("quantity").value),
        reorderLevel: parseInt(document.getElementById("reorderLevel").value),
        unitPrice: parseFloat(document.getElementById("unitPrice").value)
    };

    fetch("http://localhost:8080/products/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify(product)
    })
    .then(res => {
        if (!res.ok) throw new Error();
        return res.json();
    })
    .then(() => {
        document.getElementById("msg").innerText = "✅ Product added successfully!";
        document.getElementById("msg").style.color = "green";
        document.getElementById("addProductForm").reset();
    })
    .catch(() => {
        document.getElementById("msg").innerText = "❌ Failed to add product!";
        document.getElementById("msg").style.color = "red";
    });
}

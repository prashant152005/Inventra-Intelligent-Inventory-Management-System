const token = localStorage.getItem("token");

if (!token) {
    alert("Session expired. Please login again.");
    window.location.href = "login.html";
}

// LOAD CATEGORY + SUPPLIER
async function loadDropdowns() {
    const [catRes, supRes] = await Promise.all([
        fetch("http://localhost:8080/api/categories", {
            headers: { Authorization: "Bearer " + token }
        }),
        fetch("http://localhost:8080/api/suppliers", {
            headers: { Authorization: "Bearer " + token }
        })
    ]);

    const categories = await catRes.json();
    const suppliers = await supRes.json();

    const catSelect = document.getElementById("category");
    const supSelect = document.getElementById("supplier");

    catSelect.innerHTML = `<option value="">Select Category</option>`;
    supSelect.innerHTML = `<option value="">Select Supplier</option>`;

    categories.forEach(c => {
        catSelect.innerHTML += `<option value="${c.id}">${c.name}</option>`;
    });

    suppliers.forEach(s => {
        supSelect.innerHTML += `<option value="${s.id}">${s.name}</option>`;
    });
}

loadDropdowns();

// ADD PRODUCT
function addProduct() {
    const sku = document.getElementById("sku").value.trim();
    const name = document.getElementById("name").value.trim();
    const description = document.getElementById("description").value.trim();
    const quantity = Number(document.getElementById("quantity").value);
    const reorderLevel = Number(document.getElementById("reorderLevel").value);
    const unitPrice = Number(document.getElementById("unitPrice").value);
    const categoryId = document.getElementById("category").value;
    const supplierId = document.getElementById("supplier").value;
    const expiryDate = document.getElementById("expiryDate").value;

    if (!sku || !name || !categoryId || !supplierId || !expiryDate) {
        alert("Please fill all required fields");
        return;
    }

    const body = {
        sku,
        name,
        description,
        quantity,
        reorderLevel,
        unitPrice,
        categoryId,
        supplierId,
        expiryDate
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
        if (!res.ok) throw new Error("Failed to add product");
        return res.json();
    })
    .then(() => {
        alert("Product added successfully!");
        window.location.href = "products.html";
    })
    .catch(err => {
        console.error(err);
        alert("Error adding product");
    });
}

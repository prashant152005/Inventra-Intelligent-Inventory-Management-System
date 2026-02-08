const token = localStorage.getItem("token");

async function submitStockIn() {
    const params = new URLSearchParams(window.location.search);
    const productId = params.get("productId");

    const quantity = Number(document.getElementById("quantity").value);
    const expiryDate = document.getElementById("expiryDate").value;
    const supplier = document.getElementById("supplier").value;

    try {
        const res = await fetch(
            `http://localhost:8080/api/inventory/stock-in/${productId}`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({
                    quantity,
                    expiryDate,
                    supplierName: supplier
                })
            }
        );

        if (!res.ok) {
            const err = await res.text();
            throw new Error(err);
        }

        alert("Stock added successfully");
        window.location.href = "products.html";

    } catch (err) {
        console.error(err);
        alert("Error adding stock");
    }
}

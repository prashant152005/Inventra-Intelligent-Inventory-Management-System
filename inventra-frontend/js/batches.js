const token = localStorage.getItem("token");

if (!token) {
  alert("Session expired. Please login again.");
  window.location.href = "login.html";
}

// Get productId from URL
const params = new URLSearchParams(window.location.search);
const productId = params.get("productId");

if (!productId) {
  alert("Product ID missing");
}

// Fetch batches for product
fetch(`http://localhost:8080/batches/product/${productId}`, {
  headers: {
    Authorization: "Bearer " + token
  }
})
.then(res => res.json())
.then(data => {
  const table = document.getElementById("batchTable");
  table.innerHTML = "";
 if (data.length === 0) {
    table.innerHTML = `
      <tr>
        <td colspan="6" style="text-align:center;">No batches found</td>
      </tr>`;
    return;
  }
  data.forEach(batch => {
    table.innerHTML += `
      <tr class="${batch.status}">
        <td>${batch.batchNumber}</td>
        <td>${batch.expiryDate}</td>
        <td>${batch.quantity}</td>
        <td>${batch.supplierName}</td>
        <td>${batch.status}</td>
      </tr>
    `;
  });
})
.catch(err => {
  console.error(err);
  alert("Failed to load batches");
});

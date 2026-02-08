// ================= GLOBAL VARIABLES =================
const token = localStorage.getItem("token");
const role = localStorage.getItem("role") || "USER";

const editModal = document.getElementById("editModal");
const editId = document.getElementById("editId");
const editName = document.getElementById("editName");
const editReorder = document.getElementById("editReorder");
const editPrice = document.getElementById("editPrice");

// ================= AUTH CHECK =================
if (!token) {
  location.href = "login.html";
}

// ================= LOAD PRODUCTS =================
async function loadProducts() {
  const res = await fetch("http://localhost:8080/products/all", {
    headers: { Authorization: "Bearer " + token }
  });

  const data = await res.json();
  const tbody = document.getElementById("productTable");
  tbody.innerHTML = "";

  data.forEach(p => {
    let status = "in", statusText = "In Stock";
    if (p.quantity <= 0) {
      status = "out"; statusText = "Out";
    } else if (p.quantity <= p.reorderLevel) {
      status = "low"; statusText = "Low";
    }

    tbody.innerHTML += `
      <tr>
        <td>${p.name}</td>
        <td>${p.sku}</td>
        <td>${p.category?.name || "-"}</td>
        <td>${p.supplier?.name || "-"}</td>
        <td>${p.quantity}</td>
        <td>${p.reorderLevel}</td>
        <td>₹${p.unitPrice}</td>
        <td class="status ${status}">${statusText}</td>
        <td>
          <div class="action-group">
            ${role === "ADMIN" ? `
              <button class="btn-sm edit"
                onclick="openEdit(${p.productId}, '${p.name}', ${p.reorderLevel}, ${p.unitPrice})">
                Edit
              </button>

              <button class="btn-sm delete"
                onclick="deleteProduct('${p.sku}')">
                Del
              </button>
            ` : ""}

            ${(role === "ADMIN" || role === "EMPLOYEE") ? `
              <button class="btn-sm stock-in"
                onclick="openStockIn(${p.productId})">
                + In
              </button>

              <button class="btn-sm stock-out"
                onclick="stockOut(${p.productId}, '${p.name}')">
                Out
              </button>

              <button class="btn-sm batch"
                onclick="viewBatches(${p.productId})">
                Batches
              </button>
            ` : ""}
          </div>
        </td>


      </tr>
    `;
  });
}

window.stockOut = async (id, name) => {
  const qty = Number(prompt(`Remove quantity from ${name}`));
  if (!qty || qty <= 0) return;

  await fetch(`http://localhost:8080/products/${id}/stock-out?quantity=${qty}`, {
    method: "POST",
    headers: { Authorization: "Bearer " + token }
  });

  loadProducts();
};

window.deleteProduct = async sku => {
  if (!confirm("Delete product?")) return;

  await fetch(`http://localhost:8080/products/delete/${sku}`, {
    method: "DELETE",
    headers: { Authorization: "Bearer " + token }
  });

  loadProducts();
};

// ================= EDIT MODAL =================
window.openEdit = (id, name, reorder, price) => {
  editId.value = id;
  editName.value = name;
  editReorder.value = reorder;
  editPrice.value = price;
  editModal.style.display = "flex";
};

window.closeEdit = () => {
  editModal.style.display = "none";
};

window.updateProduct = async () => {
  await fetch("http://localhost:8080/products/update", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + token
    },
    body: JSON.stringify({
      productId: editId.value,
      name: editName.value,
      reorderLevel: editReorder.value,
      unitPrice: editPrice.value
    })
  });

  closeEdit();
  loadProducts();
};

// ================= INIT =================
document.addEventListener("DOMContentLoaded", loadProducts);
function viewBatches(productId) {
    // Navigate to batch view page with productId
    window.location.href = `batches.html?productId=${productId}`;
}
function openStockIn(productId) {
    window.location.href = `stock-in.html?productId=${productId}`;
}

const token = localStorage.getItem("token");

if (!token) {
  window.location.href = "login.html";
}

// TOTAL PRODUCTS
fetch("http://localhost:8080/products/all", {
  headers: { "Authorization": "Bearer " + token }
})
.then(res => res.json())
.then(data => {
  const el = document.getElementById("totalProducts");
  if (el) el.innerText = data.length;
});

// LOW STOCK
fetch("http://localhost:8080/products/low-stock", {
  headers: { "Authorization": "Bearer " + token }
})
.then(res => res.json())
.then(data => {
  const el = document.getElementById("lowstock");
  if (el) el.innerText = data.length;
});

// CATEGORIES COUNT
fetch("http://localhost:8080/categories/all", {
  headers: { "Authorization": "Bearer " + token }
})
.then(res => res.json())
.then(data => {
  const el = document.getElementById("categoriesCount");
  if (el) el.innerText = data.length;
});

// NAVIGATION
function goLowStock() {
  window.location.href = "low-stock.html";
}
function gocategories(){
    window.location.href="categories.html";
}
if (role !== "ADMIN") {
    document.querySelectorAll(".admin-only").forEach(el => {
        el.classList.add("hide-column");
    });
}

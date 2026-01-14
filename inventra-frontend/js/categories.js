const token = localStorage.getItem("token");
const role = localStorage.getItem("role");
document.getElementById("userRole").innerText = role;

// loadCategories();

// function loadCategories() {
//   fetch("http://localhost:8080/categories/all", {
//     headers: {
//       "Authorization": "Bearer " + token
//     }
//   })
//   .then(res => res.json())
//   .then(data => {
//     console.log("CATEGORIES:", data);

//     const tbody = document.getElementById("categoryTableBody");
//     const emptyMsg = document.getElementById("emptyMsg");

//     tbody.innerHTML = "";

//     if (data.length === 0) {
//       emptyMsg.style.display = "block";
//       return;
//     }

//     emptyMsg.style.display = "none";

//     data.forEach(cat => {
//       const tr = document.createElement("tr");
//       tr.innerHTML = `
//         <td>${cat.name}</td>
//         <td class="admin-only">
//           <button class="small-btn red" onclick="deleteCategory('${cat.name}')">Delete</button>
//         </td>
//       `;
//       tbody.appendChild(tr);
//     });
//   });
// }


//loadCategories();
// function loadCategories() {
//   fetch("http://localhost:8080/categories/all", {
//     headers: { "Authorization": "Bearer " + token }
//   })
//   .then(res => res.json())
//   .then(data => {
//     const select = document.getElementById("categoryId");
//     data.forEach(c => {
//       const opt = document.createElement("option");
//       opt.value = c.id;
//       opt.textContent = c.name;
//       select.appendChild(opt);
//     });
//   });
// }




loadCategories();

function loadCategories() {
    fetch("http://localhost:8080/categories/all", {
        headers: { "Authorization": "Bearer " + token }
    })
    .then(res => res.json())
    .then(data => {
        const tbody = document.getElementById("categoryTableBody");
        const emptyMsg = document.getElementById("emptyMsg");

        tbody.innerHTML = "";

        if (data.length === 0) {
            emptyMsg.style.display = "block";
            return;
        }

        emptyMsg.style.display = "none";

        data.forEach(cat => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${cat.name}</td>
                <td class="admin-only">
                    <button onclick="deleteCategory('${cat.name}')" class="small-btn danger">Delete</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    });
}


function addCategory() {
    const name = document.getElementById("categoryName").value.trim();

    if (!name) {
        alert("Category name cannot be empty");
        return;
    }

    fetch("http://localhost:8080/categories/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({ name })
    })
    .then(res => {
        if (!res.ok) throw new Error();
        location.reload();
    })
    .catch(() => alert("Failed to add category"));
}


function deleteCategory(name) {
  if (!confirm("Delete category?")) return;

  fetch(`http://localhost:8080/categories/${name}`, {
    method: "DELETE",
    headers: {
      "Authorization": "Bearer " + token
    }
  })
  .then(res => {
    if (!res.ok) throw new Error();
    loadCategories();
  })
  .catch(() => alert("Error deleting category"));
}

// hide admin-only if employee
if (role !== "ADMIN") {
  document.querySelectorAll(".admin-only")
    .forEach(el => el.style.display = "none");
}

// function saveProduct() {
//     const body = {
//         sku: document.getElementById("sku").value,
//         name: document.getElementById("name").value,
//         quantity: document.getElementById("quantity").value,
//         reorderLevel: document.getElementById("reorderLevel").value,
//         unitPrice: document.getElementById("unitPrice").value,
//         categoryId: document.getElementById("categorySelect").value
//     };

//     fetch("http://localhost:8080/products/add", {
//         method: "POST",
//         headers: {
//             "Content-Type": "application/json",
//             "Authorization": "Bearer " + token
//         },
//         body: JSON.stringify(body)
//     })
//     .then(res => {
//         if (!res.ok) throw new Error();
//         alert("Product Added!");
//         location.href = "products.html";
//     })
//     .catch(() => alert("Error saving product"));
// }


function saveProduct() {
    const body = {
        sku: document.getElementById("sku").value,
        name: document.getElementById("name").value,
        quantity: document.getElementById("quantity").value,
        reorderLevel: document.getElementById("reorderLevel").value,
        unitPrice: document.getElementById("unitPrice").value,
        description: document.getElementById("description").value,
        categoryId: document.getElementById("categoryId").value
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
        if (!res.ok) throw new Error();
        alert("Product Added!");
        location.href = "products.html";
    })
    .catch(() => alert("Error saving product"));
}


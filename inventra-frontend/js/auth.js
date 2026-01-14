const API_URL = "http://localhost:8080";

/* ================= REGISTER (ADMIN ONLY) ================= */
function registerUser() {

    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const contactNumber = document.getElementById("contactNumber").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    // Admin always creates EMPLOYEE
    const role = "EMPLOYEE";

    if (password !== confirmPassword) {
        alert("Passwords do not match");
        return;
    }

    fetch(`${API_URL}/users/add`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        body: JSON.stringify({
            username,
            email,
            contactNumber,
            password,
            role
        })
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Forbidden");
        }
        return res.json();
    })
    .then(() => {
        alert("Employee registered successfully");
    })
    .catch(() => {
        document.getElementById("error").innerText =
            "Only ADMIN can register employees";
    });
}

/* ================= LOGIN ================= */
function loginUser() {

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    })
    .then(res => res.json())
    .then(data => {

        if (data.token) {
            localStorage.setItem("token", data.token);
            localStorage.setItem("role", data.role);

            window.location.href = "dashboard.html";
        } else {
            document.getElementById("error").innerText =
                data.error || "Login failed";
        }
    })
    .catch(() => {
        document.getElementById("error").innerText = "Server error";
    });
}

/* ================= ROLE FROM TOKEN ================= */
function getRoleFromToken(token) {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.role;
}

/* ================= LOGOUT ================= */
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    window.location.href = "login.html";
}

/* ================= DARK / LIGHT MODE ================= */
function toggleTheme() {
    document.body.classList.toggle("dark");
}
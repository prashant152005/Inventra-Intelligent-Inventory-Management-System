const API_URL = "http://localhost:8080";

/* ================= REGISTER (ADMIN ONLY) ================= */
/* ================= REGISTER (ADMIN ONLY) ================= */
function registerUser() {
    const username = document.getElementById("username").value.trim();
    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const contactNumber = document.getElementById("contactNumber").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    // FIXED: Read the selected role from dropdown
    const role = document.getElementById("role").value;

    if (!username || !fullName || !email || !contactNumber || !password || !confirmPassword) {
        document.getElementById("error").innerText = "All fields are required";
        return;
    }

    if (password !== confirmPassword) {
        document.getElementById("error").innerText = "Passwords do not match";
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
            fullName,
            email,
            contactNumber,
            password,
            role  // ← Now sends the actual selected value (ADMIN or EMPLOYEE)
        })
    })
    .then(res => {
        if (!res.ok) {
            return res.text().then(text => {
                throw new Error(text || "Registration failed");
            });
        }
        return res.json();
    })
    .then(() => {
        alert("User registered successfully!");
        window.location.href = "dashboard.html";
    })
    .catch(err => {
        console.error(err);
        document.getElementById("error").innerText = err.message;
    });
}

// ... rest of your auth.js code (login, logout, etc.) remains unchanged ...

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
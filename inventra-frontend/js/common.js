document.addEventListener("DOMContentLoaded", () => {

  // Role handling
  const role = localStorage.getItem("role") || "USER";
  const roleSpan = document.getElementById("userRole");
  if (roleSpan) roleSpan.innerText = role;

  if (role !== "ADMIN") {
    document.querySelectorAll(".admin-only")
      .forEach(el => el.style.display = "none");
  }

  // Active link
  document.querySelectorAll(".nav-link").forEach(link => {
    if (link.href === window.location.href) {
      link.classList.add("active");
    }
  });

  // Theme toggle
  const root = document.documentElement;
  document.getElementById("themeToggle")?.addEventListener("click", () => {
    root.classList.toggle("dark");
  });

  // Sidebar collapse
  document.getElementById("collapseBtn")?.addEventListener("click", () => {
    document.getElementById("sidebar")
      ?.classList.toggle("collapsed");
  });
});

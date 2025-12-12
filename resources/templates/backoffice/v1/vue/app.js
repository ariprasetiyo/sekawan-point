const app = document.getElementById("mainContentSPA");

async function navigate() {
  const path = location.hash || "#/";

  let page = "pages/home.html"; // default

  if (path === "#/navigate-user-registration") page = "v-user-registration.html";
  if (path === "#/navigate-dashboard") page = "v-dashboard.html";

  const html = await fetch(page).then(res => res.text());
  app.innerHTML = html;
}

// Load first time
//navigate();

// Load every hash change
window.addEventListener("hashchange", navigate);

// Example: fetch API
async function loadApi() {
  const el = document.getElementById("api-data");

  const res = await fetch("http://localhost:8080/api/hello");
  const json = await res.json();

  el.textContent = JSON.stringify(json, null, 2);
}
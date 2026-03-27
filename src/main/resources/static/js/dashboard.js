// ===== Dashboard.js (Cleaned & Corrected) =====

// Run once on DOM load
document.addEventListener('DOMContentLoaded', () => {
    initSidebarToggle();
    initGreeting();
    initMarkTaskButtons();
    initStatsAnimation();
    createQuickAddButton();
});

// =============================================
// SIDEBAR TOGGLE
// =============================================
function initSidebarToggle() {
    const sidebar = document.getElementById("sidebar");
    const mainContent = document.getElementById("main-content");
    const toggleBtn = document.getElementById("toggleSidebarBtn");

    toggleBtn?.addEventListener("click", () => {
        sidebar.classList.toggle("collapsed");
        mainContent.classList.toggle("expanded");
    });
}

// =============================================
// GREETING
// =============================================
function initGreeting() {
    const hour = new Date().getHours();
    const greeting = hour >= 17 ? 'Evening' : hour >= 12 ? 'Afternoon' : 'Morning';
    const greetElem = document.getElementById('greeting');
    if (greetElem) greetElem.textContent = greeting;
}

// =============================================
// COUNT-UP ANIMATION
// =============================================
function animateCount(id, duration = 800) {
    const elem = document.getElementById(id);
    if (!elem) return;

    const endValue = parseInt(elem.getAttribute("data-value") || elem.textContent);
    if (isNaN(endValue) || endValue <= 0) return;

    let start = 0;
    const stepTime = Math.max(Math.floor(duration / endValue), 20);

    const timer = setInterval(() => {
        start += 1;
        elem.textContent = start;
        if (start >= endValue) clearInterval(timer);
    }, stepTime);
}

function initStatsAnimation() {
    ["productivityScore", "current-streak", "pendingCount"].forEach(id => animateCount(id));
}



// =============================================
// MARK TASK COMPLETE
// =============================================
function initMarkTaskButtons() {
    document.querySelectorAll(".mark-done-btn").forEach(btn => {
        btn.addEventListener("click", () => markTaskCompleted(btn.dataset.id, btn));
    });
}

async function markTaskCompleted(taskId, button) {
    try {
        const response = await fetch(`/tasks/complete/${taskId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        const data = await response.json();

        if (!response.ok) return alert(data.error || "Something went wrong");

        document.getElementById("productivityScore").innerText = data.score;
        document.getElementById("current-streak").innerText = data.streak;

        const row = button.closest("tr");
        if (row) {
            const badge = row.querySelector("td:nth-child(4) span");
            if (badge) {
                badge.textContent = "Done";
                badge.classList.remove("text-warning", "fw-semibold");
                badge.classList.add("text-success", "fw-semibold");
            }

            button.disabled = true;
            button.classList.remove("btn-success");
            button.classList.add("btn-secondary");
            button.textContent = "✔ Done";
        }

        showToast("✅ Task completed! Productivity +10");

        if (window.updateAchievements) window.updateAchievements();

    } catch (err) {
        console.error("Error completing task", err);
        alert("Failed to complete task");
    }
}

// =============================================
// TOAST
// =============================================
function showToast(message) {
    const toast = document.createElement("div");
    toast.innerText = message;
    Object.assign(toast.style, {
        position: "fixed",
        bottom: "20px",
        right: "20px",
        background: "#28a745",
        color: "#fff",
        padding: "10px 20px",
        borderRadius: "8px",
        boxShadow: "0 2px 8px rgba(0,0,0,0.2)",
        opacity: 0,
        transition: "opacity 0.3s ease",
        zIndex: 9999
    });
    document.body.appendChild(toast);
    setTimeout(() => toast.style.opacity = 1, 100);
    setTimeout(() => toast.style.opacity = 0, 2200);
    setTimeout(() => toast.remove(), 2500);
}

// =============================================
// FLOATING QUICK-ADD BUTTON
// =============================================
function createQuickAddButton() {
    const button = document.createElement("button");
    button.innerText = "+";
    Object.assign(button.style, {
        position: "fixed",
        bottom: "30px",
        right: "30px",
        width: "60px",
        height: "60px",
        borderRadius: "50%",
        fontSize: "28px",
        background: "#007bff",
        color: "#fff",
        border: "none",
        cursor: "pointer",
        boxShadow: "0 4px 12px rgba(0,0,0,0.3)",
        zIndex: 9999
    });
    button.addEventListener("click", () => {
        window.location.href = "/tasks/add";
    });
    document.body.appendChild(button);
}


async function renderDashboardTasks() {
  const dashboardEl = document.getElementById("dashboardTasks");
  dashboardEl.innerHTML = "";

  try {
    const response = await fetch("/tasks/today/json");
    const tasks = await response.json();

    const order = { HIGH: 1, MEDIUM: 2, LOW: 3 };
    const sorted = tasks.sort((a, b) => order[a.priority] - order[b.priority]);
    const topFive = sorted.slice(0, 5);

    topFive.forEach(task => {
      const li = document.createElement("li");
      li.className = "list-group-item";

      li.innerHTML = `
        <div>
          <input type="checkbox" ${task.completed ? "checked" : ""} disabled>
          <span class="task-title ${task.completed ? "completed" : ""}">${task.title}</span>
        </div>
        <span class="priority-badge priority-${task.priority}">${task.priority}</span>
      `;

      dashboardEl.appendChild(li);
    });
  } catch (err) {
    dashboardEl.innerHTML = `<li class="list-group-item text-danger">Failed to load tasks</li>`;
  }
}

document.addEventListener("DOMContentLoaded", renderDashboardTasks);
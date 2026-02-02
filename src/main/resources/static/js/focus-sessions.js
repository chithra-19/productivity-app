let currentPage = 0;
const pageSize = 5;

const sessionList = document.getElementById("sessionList");
const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const pageInfo = document.getElementById("pageInfo");

/* =============================
   Fetch sessions with pagination
============================= */
async function fetchSessions(page = 0) {
  try {
    const res = await fetch(
      `/api/focus-sessions?page=${page}&size=${pageSize}`
    );

    if (!res.ok) {
      throw new Error("Failed to fetch sessions");
    }

    const data = await res.json();

    renderSessions(data.content);
    updatePagination(data);
  } catch (err) {
    console.error(err);
    sessionList.innerHTML =
      `<div class="empty-state">Failed to load sessions</div>`;
  }
}

/* =============================
   Render sessions
============================= */
function renderSessions(sessions) {
  sessionList.innerHTML = "";

  if (sessions.length === 0) {
    sessionList.innerHTML =
      `<div class="empty-state">No focus sessions yet</div>`;
    return;
  }

  sessions.forEach(session => {
    const div = document.createElement("div");
    div.className = "session-item";

    const statusClass = session.successful
      ? "status-completed"
      : "status-aborted";

    const statusText = session.successful
      ? "Completed"
      : "Aborted";

    div.innerHTML = `
      <div class="session-title">
        Focus Session (${session.durationMinutes} min)
      </div>
      <div class="session-time">
        ${formatDate(session.startTime)} → ${formatDate(session.endTime)}
      </div>
      <div class="session-status ${statusClass}">
        ${statusText}
      </div>
    `;

    sessionList.appendChild(div);
  });
}

/* =============================
   Pagination controls
============================= */
function updatePagination(pageData) {
  pageInfo.textContent = `Page ${pageData.number + 1} of ${pageData.totalPages}`;

  prevBtn.disabled = pageData.first;
  nextBtn.disabled = pageData.last;

  currentPage = pageData.number;
}

prevBtn.addEventListener("click", () => {
  if (currentPage > 0) {
    fetchSessions(currentPage - 1);
  }
});

nextBtn.addEventListener("click", () => {
  fetchSessions(currentPage + 1);
});

/* =============================
   Utils
============================= */
function formatDate(dateStr) {
  if (!dateStr) return "—";

  const date = new Date(dateStr);
  return date.toLocaleString("en-IN", {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit"
  });
}

/* =============================
   Initial load
============================= */
fetchSessions();

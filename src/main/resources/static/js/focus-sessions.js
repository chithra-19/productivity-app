let currentPage = 0;
const pageSize = 10;

const tableBody = document.getElementById("sessionTableBody");
const filterDropdown = document.getElementById("statusFilter");
const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const pageInfo = document.getElementById("pageInfo");

/* =============================
   INIT
============================= */
document.addEventListener("DOMContentLoaded", () => {
  fetchSessions(0);
});

/* =============================
   FETCH SESSIONS (PAGINATION + FILTER)
============================= */
async function fetchSessions(page = 0) {
  try {
    tableBody.innerHTML = `
      <tr><td colspan="5">Loading...</td></tr>
    `;

    prevBtn.disabled = true;
    nextBtn.disabled = true;

    const status = filterDropdown.value;

    let url = `/api/focus-sessions/me?page=${page}&size=${pageSize}`;

    if (status && status !== "ALL") {
      url += `&status=${status}`;
    }

    const res = await fetch(url, {
      method: "GET",
      credentials: "include"
    });

    if (!res.ok) {
      throw new Error("Failed to fetch sessions");
    }

    const data = await res.json();

    if (!data || !data.content) {
      renderTable([]);
      pageInfo.textContent = "No data";
      return;
    }

    renderTable(data.content);
    updatePagination(data);

  } catch (err) {
    console.error("Error loading sessions:", err);

    tableBody.innerHTML = `
      <tr><td colspan="5">Error loading sessions</td></tr>
    `;

    prevBtn.disabled = false;
    nextBtn.disabled = false;
  }
}

/* =============================
   RENDER TABLE
============================= */
function renderTable(sessions) {
  tableBody.innerHTML = "";

  if (!sessions || sessions.length === 0) {
    tableBody.innerHTML = `
      <tr><td colspan="5">No sessions found</td></tr>
    `;
    return;
  }

  const statusClassMap = {
    COMPLETED: "status-completed",
    ABORTED: "status-aborted",
    ACTIVE: "status-active"
  };

  sessions.forEach(session => {
    const row = document.createElement("tr");

    const typeCell = document.createElement("td");
    typeCell.textContent = session.sessionType;

    const durationCell = document.createElement("td");
    durationCell.textContent = `${session.durationMinutes} min`;

    const startCell = document.createElement("td");
    startCell.textContent = formatDate(session.startTime);

    const endCell = document.createElement("td");
    endCell.textContent = session.endTime ? formatDate(session.endTime) : "-";

    const statusCell = document.createElement("td");
    statusCell.textContent = session.status;

    const statusClass = statusClassMap[session.status] || "status-default";
    statusCell.className = statusClass;

    row.appendChild(typeCell);
    row.appendChild(durationCell);
    row.appendChild(startCell);
    row.appendChild(endCell);
    row.appendChild(statusCell);

    tableBody.appendChild(row);
  });
}

/* =============================
   PAGINATION UI
============================= */
function updatePagination(data) {
  currentPage = data.number;

  pageInfo.textContent = `Page ${data.number + 1} of ${data.totalPages || 1}`;

  prevBtn.disabled = data.first;
  nextBtn.disabled = data.last;
}

/* =============================
   BUTTON EVENTS
============================= */
prevBtn.addEventListener("click", () => {
  if (currentPage > 0) {
    fetchSessions(currentPage - 1);
  }
});

nextBtn.addEventListener("click", () => {
  fetchSessions(currentPage + 1);
});

/* =============================
   FILTER CHANGE
============================= */
filterDropdown.addEventListener("change", () => {
  currentPage = 0;
  fetchSessions(0);
});

/* =============================
   DATE FORMAT
============================= */
function formatDate(dateStr) {
  if (!dateStr) return "-";

  const date = new Date(dateStr);

  return date.toLocaleString("en-IN", {
    timeZone: "Asia/Kolkata",
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: true
  });
}
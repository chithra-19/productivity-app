let currentPage = 0;
const pageSize = 50;

const tableBody = document.getElementById("sessionTableBody");
const filterDropdown = document.getElementById("statusFilter");
const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const pageInfo = document.getElementById("pageInfo");

/* =============================
   Fetch sessions with pagination
============================= */
async function fetchSessions(page = 0) {
  try {
    // ✅ Loading state
    tableBody.innerHTML = `
      <tr>
        <td colspan="5">Loading...</td>
      </tr>
    `;

    prevBtn.disabled = true;
    nextBtn.disabled = true;

    const status = filterDropdown.value;

    let url = `/api/focus-sessions/me?page=${page}&size=${pageSize}`;

    if (status !== "ALL") {
      url += `&status=${status}`;
    }

    const res = await fetch(url, {
      method: "GET",
      credentials: "include"
    });

    // ✅ Response check
    if (!res.ok) {
      throw new Error("Failed to fetch sessions");
    }

    const data = await res.json();

    renderTable(data.content);
    updatePagination(data);

  } catch (err) {
    console.error(err);

    tableBody.innerHTML = `
      <tr>
        <td colspan="5">Error loading sessions</td>
      </tr>
    `;
  }
}

/* =============================
   Render sessions
============================= */
function renderTable(sessions) {
  tableBody.innerHTML = "";

  if (!sessions || sessions.length === 0) {
    tableBody.innerHTML = `
      <tr>
        <td colspan="5">No sessions found</td>
      </tr>
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

    const status = session.status;
    const statusClass = statusClassMap[status] || "status-default";

    // ✅ safer DOM assignment
    const typeCell = document.createElement("td");
    typeCell.textContent = session.sessionType;

    const durationCell = document.createElement("td");
    durationCell.textContent = `${session.durationMinutes} min`;

    const startCell = document.createElement("td");
    startCell.textContent = formatDate(session.startTime);

    const endCell = document.createElement("td");
    endCell.textContent = session.endTime
      ? formatDate(session.endTime)
      : "-";

    const statusCell = document.createElement("td");
    statusCell.textContent = status;
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
   Pagination controls
============================= */
function updatePagination(pageData) {
  pageInfo.textContent =
    pageData.totalPages > 0
      ? `Page ${pageData.number + 1} of ${pageData.totalPages}`
      : "No data";

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
  if (!dateStr) return "-";

  const date = new Date(dateStr); // ✅ DO NOT add "Z"

  return date.toLocaleString("en-IN", {
    timeZone: "Asia/Kolkata", // 🔥 force correct timezone
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: true
  });
}

/* =============================
   Filter change
============================= */
filterDropdown.addEventListener("change", () => {
  currentPage = 0; // ✅ reset page
  fetchSessions(0);
});

/* =============================
   Initial load
============================= */

  fetchSessions(0);

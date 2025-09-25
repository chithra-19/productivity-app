// --- Decode JWT ---
function parseJwt(token) {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (e) {
    return null;
  }
}

// --- Sidebar toggle ---
function toggleSidebar() {
  document.getElementById('sidebar').classList.toggle('d-none');
}

// --- Greeting based on time ---
function updateGreeting() {
  const hour = new Date().getHours();
  let greeting = "Morning";
  if (hour >= 12 && hour < 17) greeting = "Afternoon";
  else if (hour >= 17) greeting = "Evening";
  document.getElementById('greeting').textContent = greeting;
}

// --- Fetch daily quote ---
async function refreshQuote() {
  const token = localStorage.getItem('token');
  if (!token) return;

  try {
    const res = await fetch('/api/quote', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (res.ok) {
      const data = await res.text();
      document.getElementById('quote').textContent = data;
    }
  } catch (e) {
    console.error("Failed to fetch quote", e);
  }
}

// --- Load heatmap ---
async function loadHeatmap() {
  const token = localStorage.getItem('token');
  if (!token) return;

  const user = parseJwt(token);
  if (!user) return;

  document.querySelectorAll('#username').forEach(el => el.textContent = user.username);

  const category = 'TASK';
  const days = 30;

  try {
    const res = await fetch(`/api/heatmap/${category}?userId=${user.id}&days=${days}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await res.json();

    const heatmapData = {};
    data.heatmapData.forEach(day => {
      const ts = new Date(day.date).getTime() / 1000;
      heatmapData[ts] = day.isStreakDay ? day.taskCount + 1 : day.taskCount;
    });

    new CalHeatMap().init({
      itemSelector: "#heatmap-container",
      domain: "month",
      subDomain: "day",
      data: heatmapData,
      start: new Date(new Date().setDate(new Date().getDate() - days + 1)),
      cellSize: 20,
      cellPadding: 5,
      domainGutter: 10,
      range: 1,
      legend: [1,2,3,4,5],
      displayLegend: true,
      tooltip: true,
      onClick: (date, count) => alert(`${date.toDateString()}: ${count} tasks`)
    });

  } catch (err) {
    console.error("Failed to load heatmap data", err);
  }
}

// --- Initialize dashboard ---
document.addEventListener('DOMContentLoaded', () => {
  updateGreeting();
  refreshQuote();
  loadHeatmap();
});

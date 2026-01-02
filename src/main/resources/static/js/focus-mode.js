
const STORAGE_KEYS = {
  DAILY_GOAL: "focus_daily_goal",
  REMAINING_GOAL: "focus_remaining_goal",
  TOTAL_MINUTES: "focus_total_minutes",
  SESSIONS: "focus_sessions_today",
  THEME: "focus_theme",
  LAST_DATE: "focus_last_date"
};

function checkNewDay() {
  const today = new Date().toDateString();
  const lastDate = localStorage.getItem(STORAGE_KEYS.LAST_DATE);

  if (lastDate !== today) {
    localStorage.setItem(STORAGE_KEYS.LAST_DATE, today);
    localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, 0);
    localStorage.setItem(STORAGE_KEYS.SESSIONS, 0);

    const goal = localStorage.getItem(STORAGE_KEYS.DAILY_GOAL) || 5;
    localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, goal);
  }
}


let timerMinutes = 25;
let timerSeconds = 0;
let interval = null;
let running = false;

// Stats
let totalFocusedMinutes = 0;
let completedSessions = 0;

// Session history & streak
let sessionHistory = [];
let currentStreak = 0;
let lastSessionDate = null;

const timerEl = document.getElementById("timer");
const startBtn = document.getElementById("startBtn");
const pauseBtn = document.getElementById("pauseBtn");
const resetBtn = document.getElementById("resetBtn");
const sessionTypes = document.querySelectorAll(".session-type");
const customInput = document.getElementById("customDuration");
const totalHoursEl = document.getElementById("totalHours");
const sessionsCompletedEl = document.getElementById("sessionsCompleted");
const historyEl = document.getElementById("historyList");
const streakEl = document.getElementById("streakCount");
const dailyGoalInput = document.getElementById("dailyGoal");

const body = document.body;
const lightBtn = document.getElementById("lightBtn");
const darkBtn = document.getElementById("darkBtn");

// Progress ring setup
const progressCircle = document.querySelector('.progress-ring__circle');
const radius = progressCircle.r.baseVal.value;
const circumference = 2 * Math.PI * radius;
progressCircle.style.strokeDasharray = `${circumference} ${circumference}`;
progressCircle.style.strokeDashoffset = circumference;


let dailyGoalHours = Number(document.getElementById('dailyGoal').value);
let remainingGoalHours = dailyGoalHours;
const remainingGoalEl = document.getElementById('remainingGoal');

// Update remaining goal whenever goal changes
document.getElementById('dailyGoal').addEventListener('change', () => {
  dailyGoalHours = Number(document.getElementById('dailyGoal').value);
  remainingGoalHours = dailyGoalHours - totalFocusedMinutes/60;
  remainingGoalEl.textContent = remainingGoalHours.toFixed(2);
});

function updateDisplay() {
  let min = String(timerMinutes).padStart(2,'0');
  let sec = String(timerSeconds).padStart(2,'0');
  timerEl.textContent = `${min}:${sec}`;
}

function updateStats() {
  totalHoursEl.textContent = (totalFocusedMinutes / 60).toFixed(2);
  sessionsCompletedEl.textContent = completedSessions;
}

// Chart setup
const ctx = document.getElementById('focusChart').getContext('2d');
let focusChart = new Chart(ctx, {
  type: 'bar',
  data: {
    labels: [], // last 7 days
    datasets: [
      {
        label: 'Hours Focused',
        data: [],
        backgroundColor: 'rgba(159,108,255,0.7)',
      },
      {
        label: 'Sessions Completed',
        data: [],
        backgroundColor: 'rgba(76,175,80,0.7)',
      }
    ]
  },
  options: {
    responsive: true,
    plugins: { legend: { position: 'top' } },
    scales: {
      y: { beginAtZero: true }
    }
  }
});


const barBtn = document.getElementById('barBtn');
const lineBtn = document.getElementById('lineBtn');

barBtn.addEventListener('click', () => {
  focusChart.config.type = 'bar';
  focusChart.update();
});

lineBtn.addEventListener('click', () => {
  focusChart.config.type = 'line';
  focusChart.update();
});

// Function to update chart
function updateChart() {
  const days = [];
  const focusData = [];
  const shortData = [];
  const longData = [];
  const customData = [];
  const today = new Date();

  for (let i = 6; i >= 0; i--) {
    const d = new Date();
    d.setDate(today.getDate() - i);
    const dayStr = d.toLocaleDateString('en-US', { weekday:'short', month:'short', day:'numeric' });
    days.push(dayStr);

    let focus = 0, shortB = 0, longB = 0, custom = 0;

    sessionHistory.forEach(s => {
      const sessionDate = new Date(s.date);
      if(sessionDate.toDateString() === d.toDateString()) {
        const type = s.type.toLowerCase();
        if(type.includes("focus")) focus += s.minutes/60;
        else if(type.includes("short")) shortB += s.minutes/60;
        else if(type.includes("long")) longB += s.minutes/60;
        else custom += s.minutes/60;
      }
    });

    focusData.push(focus.toFixed(2));
    shortData.push(shortB.toFixed(2));
    longData.push(longB.toFixed(2));
    customData.push(custom.toFixed(2));
  }

  focusChart.data.labels = days;
  focusChart.data.datasets = [
    { label: 'Focus', data: focusData, backgroundColor: '#d1c4ff' },
    { label: 'Short Break', data: shortData, backgroundColor: '#c8f7dc' },
    { label: 'Long Break', data: longData, backgroundColor: '#ffe0b2' },
    { label: 'Custom', data: customData, backgroundColor: '#f8d7da' }
  ];
  focusChart.options.scales = {
    y: { beginAtZero: true, stacked: true },
    x: { stacked: true }
  };
  focusChart.update();
}


// Call updateChart whenever a session is added
function addSessionToHistory(type, minutes) {
  const now = new Date();
  const dateStr = now.toLocaleString();

  // Update streak logic
  if(lastSessionDate) {
    const last = new Date(lastSessionDate);
    const diff = now.setHours(0,0,0,0) - last.setHours(0,0,0,0);
    if(diff === 24*60*60*1000) currentStreak++;
    else if(diff > 24*60*60*1000) currentStreak = 1;
  } else { currentStreak = 1; }
  lastSessionDate = now;

  streakEl.textContent = `${currentStreak} 🔥`;

  const sessionObj = { type, minutes, date: dateStr };
  sessionHistory.unshift(sessionObj);

  const li = document.createElement("li");
  li.className = "history-item";
  const typeLower = type.toLowerCase();
  if(typeLower.includes("focus")) li.classList.add("history-focus");
  else if(typeLower.includes("short")) li.classList.add("history-short");
  else if(typeLower.includes("long")) li.classList.add("history-long");
  else li.classList.add("history-custom");
  li.textContent = `${dateStr} — ${type} (${minutes} min)`;
  historyEl.prepend(li);

  // Update chart
  updateChart();
}


// Auto-reset daily stats at midnight
function setupDailyReset() {
  setInterval(() => {
    const now = new Date();
    const todayStr = now.toDateString();

    if (window.lastResetDate !== todayStr) {
      // Reset daily stats
      totalFocusedMinutes = 0;
      completedSessions = 0;
      updateStats();

      // Reset progress circle
      progressCircle.style.strokeDashoffset = circumference;

      // Update last reset date
      window.lastResetDate = todayStr;
    }
  }, 60 * 1000); // check every minute
}

// Call this function after initial setup
setupDailyReset();


// Timer functions
function startTimer() {
  if(running) return;
  running = true;
  interval = setInterval(() => {
    if(timerSeconds === 0) {
      if(timerMinutes === 0) {
        clearInterval(interval);
        running = false;
		showToast("Session Complete! ✅");


        const sessionMinutes = Number(document.querySelector(".session-type.active")?.dataset.minutes || customInput.value);
        totalFocusedMinutes += sessionMinutes;
        completedSessions++;
        updateStats();
        addSessionToHistory(document.querySelector(".session-type.active")?.textContent || "Custom", sessionMinutes);


		// Reduce remaining goal
		remainingGoalHours -= sessionMinutes / 60;
		if(remainingGoalHours < 0) remainingGoalHours = 0;
		remainingGoalEl.textContent = remainingGoalHours.toFixed(2);

		// Update progress bar
		const goalProgress = document.getElementById('goalProgress');
		const percent = ((dailyGoalHours - remainingGoalHours)/dailyGoalHours) * 100;
		goalProgress.style.width = percent + '%';
		
		localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, totalFocusedMinutes);
		localStorage.setItem(STORAGE_KEYS.SESSIONS, completedSessions);
		localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, remainingGoalHours);

        // fill progress circle
        progressCircle.style.strokeDashoffset = 0;
        return;
      }
      timerMinutes--;
      timerSeconds = 59;
    } else {
      timerSeconds--;
    }
    updateDisplay();

    // Update progress circle
    const sessionTotal = Number(document.querySelector(".session-type.active")?.dataset.minutes || customInput.value);
    const elapsed = sessionTotal*60 - (timerMinutes*60 + timerSeconds);
    const offset = circumference - (elapsed / (sessionTotal*60)) * circumference;
    progressCircle.style.strokeDashoffset = offset;
  },1000);
}

function pauseTimer() { running=false; clearInterval(interval); }
function resetTimer() { pauseTimer(); timerMinutes=Number(document.querySelector(".session-type.active")?.dataset.minutes || customInput.value); timerSeconds=0; updateDisplay(); progressCircle.style.strokeDashoffset = circumference; }


// Session type selection
sessionTypes.forEach(type => {
  type.addEventListener("click", () => {
    sessionTypes.forEach(t=>t.classList.remove("active"));
    type.classList.add("active");
    timerMinutes=Number(type.dataset.minutes);
    timerSeconds=0;
    updateDisplay();
    progressCircle.style.strokeDashoffset = circumference;
  });
});
customInput.addEventListener("change", () => { timerMinutes=Number(customInput.value); timerSeconds=0; sessionTypes.forEach(t=>t.classList.remove("active")); updateDisplay(); progressCircle.style.strokeDashoffset = circumference; });

startBtn.addEventListener("click", startTimer);
pauseBtn.addEventListener("click", pauseTimer);
resetBtn.addEventListener("click", resetTimer);

// Theme toggle
lightBtn.addEventListener("click", () => {
  body.classList.remove("dark"); body.classList.add("light");
  lightBtn.classList.add("active"); darkBtn.classList.remove("active");
});
darkBtn.addEventListener("click", () => {
  body.classList.remove("light"); body.classList.add("dark");
  darkBtn.classList.add("active"); lightBtn.classList.remove("active");
});

updateDisplay();
updateStats();

// 🔁 Check if a new day started and reset daily stats if needed
checkNewDay();

// ===== Restore saved values =====
const savedGoal = localStorage.getItem(STORAGE_KEYS.DAILY_GOAL);
const savedRemaining = localStorage.getItem(STORAGE_KEYS.REMAINING_GOAL);
const savedMinutes = localStorage.getItem(STORAGE_KEYS.TOTAL_MINUTES);
const savedSessions = localStorage.getItem(STORAGE_KEYS.SESSIONS);
const savedTheme = localStorage.getItem(STORAGE_KEYS.THEME);

// Daily goal
if (savedGoal) {
  dailyGoalInput.value = savedGoal;
}

// Restore stats
totalFocusedMinutes = savedMinutes ? Number(savedMinutes) : 0;
completedSessions = savedSessions ? Number(savedSessions) : 0;

// Remaining goal (safe fallback)
const goal = Number(dailyGoalInput.value) || 0;
remainingGoalHours = savedRemaining
  ? Number(savedRemaining)
  : Math.max(goal - totalFocusedMinutes / 60, 0);

// UI update
remainingGoalEl.textContent = remainingGoalHours.toFixed(2);
updateStats();

// Theme restore
if (savedTheme === "dark") {
  body.classList.add("dark");
  body.classList.remove("light");
} else {
  body.classList.add("light");
  body.classList.remove("dark");
}

// ===== Handle goal change =====
dailyGoalInput.addEventListener("change", () => {
  const newGoal = Number(dailyGoalInput.value) || 0;

  remainingGoalHours = Math.max(newGoal - totalFocusedMinutes / 60, 0);
  remainingGoalEl.textContent = remainingGoalHours.toFixed(2);

  // Update progress bar
  const goalProgress = document.getElementById('goalProgress');
  const percent = ((newGoal - remainingGoalHours)/newGoal) * 100;
  goalProgress.style.width = percent + '%';

  localStorage.setItem(STORAGE_KEYS.DAILY_GOAL, newGoal);
  localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, remainingGoalHours);

  updateStats();
});

function showToast(message) {
  const toast = document.getElementById('toast');
  toast.textContent = message;
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 2500); // hide after 2.5s
}


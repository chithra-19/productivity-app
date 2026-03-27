const STORAGE_KEYS = {
  DAILY_GOAL: "focus_daily_goal",
  REMAINING_GOAL: "focus_remaining_goal",
  TOTAL_MINUTES: "focus_total_minutes",
  SESSIONS: "focus_sessions_today",
  THEME: "focus_theme",
  LAST_DATE: "focus_last_date",

  SESSION_HISTORY: "focus_session_history",
  STREAK: "focus_streak",
  LAST_SESSION_DATE: "focus_last_session_date"
};

const TIMER_KEYS = {
  START: "focus_timer_start",
  DURATION: "focus_timer_duration",
  RUNNING: "focus_timer_running"
};


function checkNewDay() {
  const today = new Date().toDateString();
  const lastDate = localStorage.getItem(STORAGE_KEYS.LAST_DATE);

  if (lastDate !== today) {
    localStorage.setItem(STORAGE_KEYS.LAST_DATE, today);

    // Reset daily stats
    totalFocusedMinutes = 0;
    completedSessions = 0;
    updateStats();

    // Reset remaining goal
    const goal = Number(localStorage.getItem(STORAGE_KEYS.DAILY_GOAL)) || 5;
    remainingGoalHours = goal;
	const remainingMinutes = Math.max(remainingGoalHours * 60, 0);
	const remainingHours = Math.floor(remainingMinutes / 60);
	const remainingMins = remainingMinutes % 60;
	remainingGoalEl.textContent = `${remainingHours}h ${remainingMins}m`;
    updateGoalProgress();

    // Save resets in localStorage
    localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, 0);
    localStorage.setItem(STORAGE_KEYS.SESSIONS, 0);
    localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, remainingGoalHours);
  }
}


let timerMinutes = 25;
let timerSeconds = 0;
let interval = null;
let running = false;

// Stats
// Restore stats from localStorage, or start at 0 if nothing saved
let totalFocusedMinutes = Number(localStorage.getItem(STORAGE_KEYS.TOTAL_MINUTES)) || 0;
let completedSessions = Number(localStorage.getItem(STORAGE_KEYS.SESSIONS)) || 0;

// Session history & streak
sessionHistory.push({
  date: new Date().toISOString(), // ✅ ISO format
  type: sessionType,
  minutes: sessionMinutes
});
localStorage.setItem(STORAGE_KEYS.SESSION_HISTORY, JSON.stringify(sessionHistory));

document.addEventListener("DOMContentLoaded", () => { 
	syncStatsFromBackend(); 
});

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

// ✅ Display remaining goal in "xh ym" format 
function updateRemainingGoalDisplay() { 
	const remainingMinutes = Math.max(dailyGoalHours * 60 - totalFocusedMinutes, 0);
	 const remainingHours = Math.floor(remainingMinutes / 60);
	  const remainingMins = remainingMinutes % 60;
	  remainingGoalEl.textContent = ${remainingHours}h ${remainingMins}m;
   }
   // ✅ Initial display on page load
  updateRemainingGoalDisplay();
  // ✅ Update when goal changes
  dailyGoalInput.addEventListener('change', () => { 
	dailyGoalHours = Number(dailyGoalInput.value); 
	remainingGoalHours = dailyGoalHours - totalFocusedMinutes / 60;
  updateRemainingGoalDisplay(); });


// Update remaining goal whenever goal changes
document.getElementById('dailyGoal').addEventListener('change', () => {
  dailyGoalHours = Number(document.getElementById('dailyGoal').value);
  remainingGoalHours = dailyGoalHours - totalFocusedMinutes/60;
  const remainingMinutes = Math.max(remainingGoalHours * 60, 0);
  const remainingHours = Math.floor(remainingMinutes / 60);
  const remainingMins = remainingMinutes % 60;
  remainingGoalEl.textContent = `${remainingHours}h ${remainingMins}m`;
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
function updateStreak(today) {
  const lastDateStr = localStorage.getItem(STORAGE_KEYS.LAST_SESSION_DATE);
  let streak = Number(localStorage.getItem(STORAGE_KEYS.STREAK)) || 0;

  if (!lastDateStr) {
    streak = 1;
  } else {
    const lastDate = new Date(lastDateStr);
    const diffDays =
      (today.setHours(0,0,0,0) - lastDate.setHours(0,0,0,0)) /
      (1000 * 60 * 60 * 24);

    if (diffDays === 1) streak++;
    else if (diffDays > 1) streak = 1;
  }

  localStorage.setItem(STORAGE_KEYS.STREAK, streak);
  localStorage.setItem(STORAGE_KEYS.LAST_SESSION_DATE, new Date().toISOString());

  streakEl.textContent = `${streak} 🔥`;
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


function startTimer() {
  if (running) return;

  running = true;

  // If starting fresh, store start time
  if (!localStorage.getItem(TIMER_KEYS.RUNNING)) {
    const sessionTotal =
      Number(document.querySelector(".session-type.active")?.dataset.minutes || customInput.value);

    localStorage.setItem(TIMER_KEYS.START, Date.now());
    localStorage.setItem(TIMER_KEYS.DURATION, sessionTotal * 60 * 1000);
    localStorage.setItem(TIMER_KEYS.RUNNING, "true");
  }

  interval = setInterval(() => {
    const start = Number(localStorage.getItem(TIMER_KEYS.START));
    const duration = Number(localStorage.getItem(TIMER_KEYS.DURATION));
    const elapsed = Date.now() - start;
    const remaining = duration - elapsed;

    if (remaining <= 0) {
      clearInterval(interval);
      running = false;

      localStorage.removeItem(TIMER_KEYS.RUNNING);

      timerMinutes = 0;
      timerSeconds = 0;
      updateDisplay();
      progressCircle.style.strokeDashoffset = 0;

      showToast("Session Complete! ✅");

      const sessionMinutes = duration / 60000;
      totalFocusedMinutes += sessionMinutes;
      completedSessions++;

      updateStats();
      addSessionToHistory(
        document.querySelector(".session-type.active")?.textContent || "Custom",
        sessionMinutes
      );

      // goal logic
      remainingGoalHours -= sessionMinutes / 60;
      if (remainingGoalHours < 0) remainingGoalHours = 0;
	  const remainingMinutes = Math.max(remainingGoalHours * 60, 0);
	  const remainingHours = Math.floor(remainingMinutes / 60);
	  const remainingMins = remainingMinutes % 60;
	  remainingGoalEl.textContent = `${remainingHours}h ${remainingMins}m`;

	  updateDailyGoalDisplay();
      const percent =
        ((dailyGoalHours - remainingGoalHours) / dailyGoalHours) * 100;
      document.getElementById("goalProgress").style.width = percent + "%";

      localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, totalFocusedMinutes);
      localStorage.setItem(STORAGE_KEYS.SESSIONS, completedSessions);
      localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, remainingGoalHours);

      return;
    }

    const totalSeconds = Math.floor(remaining / 1000);
    timerMinutes = Math.floor(totalSeconds / 60);
    timerSeconds = totalSeconds % 60;

    updateDisplay();

    const offset =
      circumference - (elapsed / duration) * circumference;
    progressCircle.style.strokeDashoffset = Math.max(0, offset);

  }, 1000);
}

function pauseTimer() { running = false; clearInterval(interval);
// Save remaining time in ms 
const remaining = (timerMinutes * 60 + timerSeconds) * 1000; localStorage.setItem(TIMER_KEYS.REMAINING, remaining);
// Clear running flag and start time
 localStorage.removeItem(TIMER_KEYS.RUNNING); localStorage.removeItem(TIMER_KEYS.START);
  localStorage.removeItem(TIMER_KEYS.DURATION); } 
  
function continueTimer() { 
	if (running) return; running = true;
const remaining = Number(localStorage.getItem(TIMER_KEYS.REMAINING)); if (!remaining || remaining <= 0) return;
// Set new start time and duration
 localStorage.setItem(TIMER_KEYS.START, Date.now());
  localStorage.setItem(TIMER_KEYS.DURATION, remaining); 
  localStorage.setItem(TIMER_KEYS.RUNNING, "true");
  
  interval = setInterval(() => { const start = Number(localStorage.getItem(TIMER_KEYS.START)); 
	const duration = Number(localStorage.getItem(TIMER_KEYS.DURATION)); 
	const elapsed = Date.now() - start; 
	const left = duration - elapsed;

  if (left <= 0) {
    clearInterval(interval);
    running = false;
    localStorage.removeItem(TIMER_KEYS.RUNNING);
    timerMinutes = 0;
    timerSeconds = 0;
    updateDisplay();
    progressCircle.style.strokeDashoffset = 0;
    showToast("Session Complete! ✅");
    completeSessionBackend();
    return;
  }

  const totalSeconds = Math.ceil(left / 1000); // ← use ceil instead of floor
  timerMinutes = Math.floor(totalSeconds / 60);
  timerSeconds = totalSeconds % 60;
  updateDisplay();

  const offset = circumference - (elapsed / duration) * circumference;
  progressCircle.style.strokeDashoffset = Math.max(0, offset);
  }, 1000); } 



function resetTimer() {
  pauseTimer();

  const sessionTotal =
    Number(document.querySelector(".session-type.active")?.dataset.minutes || customInput.value);

  timerMinutes = sessionTotal;
  timerSeconds = 0;
  updateDisplay();

  progressCircle.style.strokeDashoffset = circumference;

  localStorage.removeItem(TIMER_KEYS.START);
  localStorage.removeItem(TIMER_KEYS.DURATION);
  localStorage.removeItem(TIMER_KEYS.RUNNING); // ✅ ADD THIS
}


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

(function restoreRunningTimer() {
  if (!localStorage.getItem(TIMER_KEYS.RUNNING)) return;

  const start = Number(localStorage.getItem(TIMER_KEYS.START));
  const duration = Number(localStorage.getItem(TIMER_KEYS.DURATION));
  const remaining = duration - (Date.now() - start);

  if (remaining <= 0) {
    localStorage.removeItem(TIMER_KEYS.RUNNING);
    return;
  }

  const totalSeconds = Math.floor(remaining / 1000);
  timerMinutes = Math.floor(totalSeconds / 60);
  timerSeconds = totalSeconds % 60;

  updateDisplay();
  startTimer(); // auto resume
})();

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
const savedHistory = localStorage.getItem(STORAGE_KEYS.SESSION_HISTORY);
if (savedHistory) {
  sessionHistory = JSON.parse(savedHistory);
  sessionHistory.forEach(s => {
    const li = document.createElement("li");
    li.textContent = `${s.date} — ${s.type} (${s.minutes} min)`;
    historyEl.appendChild(li);
  });
}


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
// Restore stats
totalFocusedMinutes = savedMinutes ? Number(savedMinutes) : 0;
completedSessions = savedSessions ? Number(savedSessions) : 0;

// ---- Add daily goal + progress sync here ----
dailyGoalHours = Number(savedGoal) || 5;
dailyGoalInput.value = dailyGoalHours;

remainingGoalHours = savedRemaining
  ? Number(savedRemaining)
  : Math.max(dailyGoalHours - totalFocusedMinutes / 60, 0);
  const remainingMinutes = Math.max(remainingGoalHours * 60, 0);
  const remainingHours = Math.floor(remainingMinutes / 60);
  const remainingMins = remainingMinutes % 60;
  remainingGoalEl.textContent = `${remainingHours}h ${remainingMins}m`;

const updateGoalProgress = () => {
  const percent = ((dailyGoalHours - remainingGoalHours) / dailyGoalHours) * 100;
  document.getElementById('goalProgress').style.width = percent + '%';
};

updateGoalProgress();
updateDailyGoalDisplay();
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
dailyGoalInput.addEventListener('change', () => {
  const newGoal = Number(dailyGoalInput.value) || 0;

  dailyGoalHours = newGoal;
  remainingGoalHours = Math.max(dailyGoalHours - totalFocusedMinutes / 60, 0);
  const remainingMinutes = Math.max(remainingGoalHours * 60, 0);
  const remainingHours = Math.floor(remainingMinutes / 60);
  const remainingMins = remainingMinutes % 60;
  remainingGoalEl.textContent = `${remainingHours}h ${remainingMins}m`;
  updateDailyGoalDisplay();
  updateGoalProgress();

  localStorage.setItem(STORAGE_KEYS.DAILY_GOAL, dailyGoalHours);
  localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, remainingGoalHours);
});

function showToast(message) {
  const toast = document.getElementById('toast');
  toast.textContent = message;
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 2500); // hide after 2.5s
}

// Update the 0h / Xh display
function updateDailyGoalDisplay() { 
	const focusedMinutes = totalFocusedMinutes;
	 const goalMinutes = dailyGoalHours * 60;
	 const focusedHours = Math.floor(focusedMinutes / 60);
	  const focusedMins = focusedMinutes % 60;
	  const goalHours = Math.floor(goalMinutes / 60);
	  document.getElementById('goalDone').textContent = ${focusedHours}h ${focusedMins}m; document.getElementById('goalTotal').textContent = ${goalHours}h; }

	  const saveBtn = document.getElementById("saveGoalBtn");

	  saveBtn.addEventListener("click", () => { 
		const newGoal = Number(dailyGoalInput.value);
		 if (!isNaN(newGoal) && newGoal > 0 && newGoal <= 24) { 
			dailyGoalHours = newGoal; 
			remainingGoalHours = Math.max(dailyGoalHours - totalFocusedMinutes / 60, 0);
			localStorage.setItem(STORAGE_KEYS.DAILY_GOAL, dailyGoalHours);
			localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, remainingGoalHours);

			updateDailyGoalDisplay(); // ✅ updates 0h / Xh
			updateGoalProgress();     // ✅ updates progress bar
			remainingGoalEl.textContent = remainingGoalHours.toFixed(2); // ✅ updates remaining
			showToast("Goal updated! 🎯"); // ✅ shows toast
			} 
		});
		
function updateDailyProgress() {
	 fetch(/api/users/${userId}/daily-progress)
	  .then(res => res.json()) .then(progress => { 
		const percent = (progress.dailyFocusMinutes / progress.dailyGoalMinutes) * 100; 
		document.getElementById("goalProgressBar").style.width = ${percent}%; 
		document.getElementById("goalText").textContent = ${Math.floor(progress.dailyFocusMinutes/60)}h ${progress.dailyFocusMinutes%60}m / ${progress.dailyGoalMinutes/60}h; 
	});
 }
 document.addEventListener("DOMContentLoaded", () => {
   updateDailyStats();
 });

function updateDailyStats() {
	 fetch(/api/focus-sessions/user/${userId}/daily-stats) 
	 .then(res => res.json()) .then(stats => { 
		const percent = (stats.dailyFocusMinutes / stats.dailyGoalMinutes) * 100; 
		document.getElementById("goalProgressBar").style.width = ${percent}%;
		document.getElementById("goalText").textContent = ${Math.floor(stats.dailyFocusMinutes/60)}h ${stats.dailyFocusMinutes%60}m / ${stats.dailyGoalMinutes/60}h;
		document.getElementById("sessionCount").textContent = stats.sessionsCompletedToday;
		document.getElementById("totalFocusHours").textContent =
        (stats.totalFocusMinutes / 60).toFixed(2);
		});
	} 
	
function syncStatsFromBackend() {
	 fetch(/api/focus-sessions/user/${userId}/daily-stats) 
	 .then(res => res.json()) .then(stats => { 
		totalFocusedMinutes = stats.dailyFocusMinutes;
		completedSessions = stats.sessionsCompletedToday;
		  updateStats(); // ✅ updates 0.83 and 2
		  updateDailyGoalDisplay(); // ✅ updates 0h 50m / 13h
		  updateGoalProgress(); // ✅ updates progress bar

		  // Optional: sync localStorage
		  localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, totalFocusedMinutes);
		  localStorage.setItem(STORAGE_KEYS.SESSIONS, completedSessions);
		});
	}
	
	function updateDashboardStats() {
	  fetch(`/api/focus-sessions/user/${userId}/daily-stats`)
	    .then(res => res.json())
	    .then(stats => {
	      const percent = (stats.dailyFocusMinutes / stats.dailyGoalMinutes) * 100;
	      document.getElementById("goalProgressBar").style.width = `${percent}%`;
	      document.getElementById("goalText").textContent =
	        `${Math.floor(stats.dailyFocusMinutes/60)}h ${stats.dailyFocusMinutes%60}m / ${stats.dailyGoalMinutes/60}h`;

	      document.getElementById("sessionCount").textContent = stats.sessionsCompletedToday;
	      document.getElementById("totalFocusHours").textContent =
	        (stats.totalFocusMinutes / 60).toFixed(2);
	    });
	}


async function completeSessionBackend() {
  try {
    const res = await fetch("/api/focus-sessions/complete", {
      method: "POST"
    });

    if (!res.ok) {
      throw new Error("Failed to complete session");
    }

    console.log("✅ Session saved to backend");

    // ✅ Only update quota if session was Focus
    if (currentSessionType === "focus") {
      const prevFocusMinutes = Number(localStorage.getItem("focusMinutesToday")) || 0;
      const sessionMinutes = timerMinutes + Math.floor(timerSeconds / 60);
      const newTotal = prevFocusMinutes + sessionMinutes;

      localStorage.setItem("focusMinutesToday", newTotal);
      updateFocusProgressBar(newTotal); // ← your UI update function
    }

    // 🔥 Refresh backend history UI
    if (typeof fetchSessions === "function") {
      fetchSessions();
    }

  } catch (err) {
    console.error("❌ Backend error:", err);
  }
}
const STORAGE_KEYS = {
  DAILY_GOAL: "focus_daily_goal",
  REMAINING_GOAL: "focus_remaining_goal",
  TOTAL_MINUTES: "focus_total_minutes",
  SESSIONS: "focus_sessions_today",
  THEME: "focus_theme",
  LAST_DATE: "focus_last_date",

  SESSION_HISTORY: "focus_session_history",
  LAST_SESSION_DATE: "focus_last_session_date"
};

const TIMER_KEYS = {
  START: "focus_timer_start",
  DURATION: "focus_timer_duration",
  RUNNING: "focus_timer_running",
  REMAINING: "focus_timer_remaining" 
};


const progressCircle = document.querySelector(".progress-ring-circle");
const radius = progressCircle ? progressCircle.r.baseVal.value : 0;
const circumference = 2 * Math.PI * radius;


let timerEl, startBtn, pauseBtn, resetBtn, continueBtn;
let hoursInput, minsInput, remainingGoalEl, totalHoursEl, sessionsCompletedEl;
let remainingGoalMinutes = 0;
let customDurationInput;

function getStoredDailyGoal() {
  return Number(localStorage.getItem(STORAGE_KEYS.DAILY_GOAL)) || 5 * 60;
}

let dailyGoalMinutes = getStoredDailyGoal();

let state = {
  timerMinutes: 25,
  timerSeconds: 0,
  interval: null,
  running: false,

  totalFocusedMinutes:
    Number(localStorage.getItem(STORAGE_KEYS.TOTAL_MINUTES)) || 0,

  completedSessions:
    Number(localStorage.getItem(STORAGE_KEYS.SESSIONS)) || 0,

  sessionHistory: JSON.parse(
    localStorage.getItem(STORAGE_KEYS.SESSION_HISTORY) || "[]"
  ),

  sessionType: "FOCUS",

  startTime: null
};


// ✅ Helper to format minutes into "xh ym"
function formatMinutesToHM(minutes) {
  const hrs = Math.floor(minutes / 60);
  const mins = Math.round(minutes % 60);
  return `${hrs}h ${mins}m`;
}
function getUserId() {
  return localStorage.getItem("userId");
}


function checkNewDay() {
  const today = new Date().toDateString();
  const lastDate = localStorage.getItem(STORAGE_KEYS.LAST_DATE);

  if (lastDate !== today) {
    localStorage.setItem(STORAGE_KEYS.LAST_DATE, today);

    // Reset daily stats
    state.totalFocusedMinutes = 0;
    state.completedSessions = 0;
    updateStats();

	// Reset remaining goal (in minutes)
	   const goalMinutes = Number(localStorage.getItem(STORAGE_KEYS.DAILY_GOAL)) || (5 * 60);
	   remainingGoalMinutes = goalMinutes;
	   remainingGoalEl.textContent = formatMinutesToHM(remainingGoalMinutes);
	   updateGoalProgress();

	   // Save resets in localStorage
	   localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, "0");
	   localStorage.setItem(STORAGE_KEYS.SESSIONS, "0");
	   localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, remainingGoalMinutes.toString());
	 }
	 updateDailyGoalDisplay();
	 updateGoalProgress();
}

function getDailyGoalMinutes() {
  const h = Number(document.getElementById('dailyGoalHours')?.value) || 0;
  const m = Number(document.getElementById('dailyGoalMins')?.value) || 0;
  return (h * 60) + m;
}

function updateRemainingGoalDisplay() {
  if (!remainingGoalEl) return;

  const dailyGoalMinutes = getDailyGoalMinutes();
  const remaining = Math.max(dailyGoalMinutes - state.totalFocusedMinutes, 0);

  remainingGoalEl.textContent = formatMinutesToHM(remaining);
}

function updateDisplay() {
  let min = String(state.timerMinutes).padStart(2, '0');
  let sec = String(state.timerSeconds).padStart(2, '0');
  if (timerEl) {
    timerEl.textContent = `${min}:${sec}`;
  }
}
function updateStats() {
	if(totalHoursEl){
		totalHoursEl.textContent = (state.totalFocusedMinutes / 60).toFixed(2);
	}
	if(sessionsCompletedEl){
		sessionsCompletedEl.textContent = state.completedSessions;
	}
}

// Auto-reset daily stats at midnight
function setupDailyReset() {
  setInterval(() => {
    const now = new Date();
    const todayStr = now.toDateString();

    if (window.lastResetDate !== todayStr) {
      // Reset daily stats
      state.totalFocusedMinutes = 0;
      state.completedSessions = 0;
      updateStats();

      // Reset progress circle
	  if (progressCircle) {
		progressCircle.style.strokeDashoffset = circumference;
	  }
      

      // Update last reset date
      window.lastResetDate = todayStr;
    }
  }, 60 * 1000); // check every minute
}

function startTimer() {
  if (state.running) return;

  state.running = true;

  if (!localStorage.getItem(TIMER_KEYS.RUNNING)) {
    const sessionTotal =
      Number(document.querySelector(".session-type.active")?.dataset.minutes || customInput.value);

    localStorage.setItem(TIMER_KEYS.START, Date.now());
    localStorage.setItem(TIMER_KEYS.DURATION, sessionTotal * 60 * 1000);
    localStorage.setItem(TIMER_KEYS.RUNNING, "true");
  }

  state.interval = setInterval(() => {
    const start = Number(localStorage.getItem(TIMER_KEYS.START));
	const duration = Number(localStorage.getItem(TIMER_KEYS.DURATION)) || 0;
	const sessionMinutes = duration > 0 ? duration / 60000 : state.timerMinutes;
    const elapsed = Date.now() - start;
    const remaining = duration - elapsed;

    if (remaining <= 0) {
      clearInterval(state.interval);
      state.running = false;

      localStorage.removeItem(TIMER_KEYS.RUNNING);

      state.timerMinutes = 0;
      state.timerSeconds = 0;
      updateDisplay();
	  if (progressCircle) {
		progressCircle.style.strokeDashoffset = 0;
	  }
      

      showToast("Session Complete! ✅");
      state.totalFocusedMinutes += sessionMinutes;
      state.completedSessions++;

      updateStats();
      

      const remainingMinutes = Math.max(dailyGoalMinutes - state.totalFocusedMinutes, 0);
      if(remainingGoalEl){
	   remainingGoalEl.textContent = formatMinutesToHM(remainingMinutes);
		}
      updateDailyGoalDisplay();

      const percent = ((dailyGoalMinutes - remainingMinutes) / dailyGoalMinutes) * 100;
      document.getElementById("goalProgress").style.width = percent + "%";

      localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, state.totalFocusedMinutes);
      localStorage.setItem(STORAGE_KEYS.SESSIONS, state.completedSessions);
      localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, remainingMinutes);
      return;
    }

    const totalSeconds = Math.floor(remaining / 1000);
    state.timerMinutes = Math.floor(totalSeconds / 60);
    state.timerSeconds = totalSeconds % 60;

    updateDisplay();

    const offset = circumference - (elapsed / duration) * circumference;
	if (progressCircle) {
		progressCircle.style.strokeDashoffset = Math.max(0, offset);
	}
    
  }, 1000);
}
function pauseTimer() {
  if (!state.running) return;

  clearInterval(state.interval);
  state.running = false;

  const remainingSeconds = state.timerMinutes * 60 + state.timerSeconds;

  localStorage.setItem(TIMER_KEYS.REMAINING, remainingSeconds);

  console.log("Paused at:", remainingSeconds);
}
  
function resetTimer() {
  pauseTimer();

  const sessionTotal =
    Number(document.querySelector(".session-type.active")?.dataset.minutes || customInput.value);

  state.timerMinutes = sessionTotal;
  state.timerSeconds = 0;
  updateDisplay();

  if (progressCircle) {
	progressCircle.style.strokeDashoffset = circumference;
  }
  

  localStorage.removeItem(TIMER_KEYS.START);
  localStorage.removeItem(TIMER_KEYS.DURATION);
  localStorage.removeItem(TIMER_KEYS.RUNNING); // ✅ ADD THIS
}
function continueTimer() {
  if (state.running) return;

  const remainingSeconds = Number(localStorage.getItem(TIMER_KEYS.REMAINING));
  if (!remainingSeconds || remainingSeconds <= 0) return;

  state.running = true;

  state.timerMinutes = Math.floor(remainingSeconds / 60);
  state.timerSeconds = remainingSeconds % 60;
  updateDisplay();

  let secondsLeft = remainingSeconds;

  state.interval = setInterval(() => {
    secondsLeft--;

    // 🔴 IMPORTANT: keep updating localStorage while running
    localStorage.setItem(TIMER_KEYS.REMAINING, secondsLeft);

    if (secondsLeft <= 0) {
      clearInterval(state.interval);
      state.running = false;

      localStorage.removeItem(TIMER_KEYS.REMAINING);

      state.timerMinutes = 0;
      state.timerSeconds = 0;
      updateDisplay();

	  if (progressCircle) {
		progressCircle.style.strokeDashoffset = 0;
	  }
      
      showToast("Session Complete! ✅");
      completeSessionBackend();
      return;
    }

    state.timerMinutes = Math.floor(secondsLeft / 60);
    state.timerSeconds = secondsLeft % 60;
    updateDisplay();

    const offset =
      circumference -
      ((remainingSeconds - secondsLeft) / remainingSeconds) * circumference;

	  if (progressCircle) {
	  		progressCircle.style.strokeDashoffset = Math.max(0, offset);
	  	}
	
  }, 1000);
}

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
  state.timerMinutes = Math.floor(totalSeconds / 60);
  state.timerSeconds = totalSeconds % 60;

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

function initState() {
  // ✅ restore daily goal
  dailyGoalMinutes = getStoredDailyGoal();

  // ✅ restore inputs
  if (hoursInput && minsInput) {
    hoursInput.value = Math.floor(dailyGoalMinutes / 60);
    minsInput.value = dailyGoalMinutes % 60;
  }

  // ✅ restore remaining goal
  remainingGoalMinutes =
    Number(localStorage.getItem(STORAGE_KEYS.REMAINING_GOAL)) ||
    Math.max(dailyGoalMinutes - state.totalFocusedMinutes, 0);

  if (remainingGoalEl) {
    remainingGoalEl.textContent = formatMinutesToHM(remainingGoalMinutes);
  }

  // ✅ restore stats UI
  updateStats();

  // ✅ restore goal UI
  updateDailyGoalDisplay();
  updateGoalProgress();

  // ✅ restore timer display
  updateDisplay();
 }

const body = document.body;

const savedTheme = localStorage.getItem(STORAGE_KEYS.THEME) || "light";

if (savedTheme === "dark") {
  body.classList.add("dark");
  body.classList.remove("light");
} else {
  body.classList.add("light");
  body.classList.remove("dark");
}
function showToast(message) {
  const toast = document.getElementById('toast');
  toast.textContent = message;
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 2500); // hide after 2.5s
}

// Update the 0h / Xh display
function updateDailyGoalDisplay() {
  document.getElementById('goalDone').textContent = formatMinutesToHM(state.totalFocusedMinutes);
  document.getElementById('goalTotal').textContent = formatMinutesToHM(dailyGoalMinutes);
}

function updateGoalProgress() {
  const goalProgress = document.getElementById("goalProgress");
  if (!goalProgress) return;

  const percent =
    dailyGoalMinutes > 0
      ? (state.totalFocusedMinutes / dailyGoalMinutes) * 100
      : 0;

  goalProgress.style.width = percent + "%";
}

function updateDailyProgress() {
	const userId = getUserId();
	if (!userId) return;

	fetch(`/api/users/${userId}/daily-progress`)
    .then(res => res.json())
    .then(progress => {
      const percent = (progress.dailyFocusMinutes / progress.dailyGoalMinutes) * 100;
      document.getElementById("goalProgressBar").style.width = `${percent}%`;
      document.getElementById("goalText").textContent =
        `${formatMinutesToHM(progress.dailyFocusMinutes)} / ${formatMinutesToHM(progress.dailyGoalMinutes)}`;
    });
}

function updateDailyStats() {
  const userId = getUserId();
  if (!userId) return;

  fetch(`/api/focus-sessions/user/${userId}/daily-stats`)
    .then(res => res.json())
    .then(stats => {
      const percent = (stats.dailyFocusMinutes / stats.dailyGoalMinutes) * 100;

      document.getElementById("goalProgressBar").style.width = `${percent}%`;
      document.getElementById("goalText").textContent =
        `${formatMinutesToHM(stats.dailyFocusMinutes)} / ${formatMinutesToHM(stats.dailyGoalMinutes)}`;

      document.getElementById("sessionCount").textContent =
        stats.sessionsCompletedToday;

      document.getElementById("totalFocusHours").textContent =
        (stats.totalFocusMinutes / 60).toFixed(2);
    });
}

function syncStatsFromBackend() {
  const userId = getUserId();
  if (!userId) return;
  fetch(`/api/focus-sessions/user/${userId}/daily-stats`)
    .then(res => res.json())
    .then(stats => {
      state.totalFocusedMinutes = stats.dailyFocusMinutes;
      state.completedSessions = stats.sessionsCompletedToday;

      updateStats();
      updateDailyGoalDisplay();
      updateGoalProgress();

      localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, state.totalFocusedMinutes);
      localStorage.setItem(STORAGE_KEYS.SESSIONS, state.completedSessions);
    });
}

function updateDashboardStats() {
	const userId = getUserId();
	if (!userId) return;
    fetch(`/api/focus-sessions/user/${userId}/daily-stats`)
    .then(res => res.json())
    .then(stats => {
      const percent = (stats.dailyFocusMinutes / stats.dailyGoalMinutes) * 100;
      document.getElementById("goalProgressBar").style.width = `${percent}%`;
      document.getElementById("goalText").textContent =
        `${formatMinutesToHM(stats.dailyFocusMinutes)} / ${formatMinutesToHM(stats.dailyGoalMinutes)}`;
      document.getElementById("sessionCount").textContent = stats.sessionsCompletedToday;
      document.getElementById("totalFocusHours").textContent =
        (stats.totalFocusMinutes / 60).toFixed(2);
    });
}

async function loadSessions() {
  try {
    const res = await fetch("/focus-sessions", {
      credentials: "include"
    });

    const data = await res.json();

    console.log("FULL RESPONSE:", data);

    renderSessionHistory(data.content || []);
  } catch (err) {
    console.error("Failed to load sessions", err);
  }
}

function waitForUserAndSync() {
  const userId = localStorage.getItem("userId");

  if (userId) {
    syncStatsFromBackend();
  } else {
    setTimeout(waitForUserAndSync, 500);
  }
}
async function completeSessionBackend() {
  try {
    const res = await fetch("/api/focus-sessions/complete", {
      method: "POST",
      credentials: "include"
    });

    if (!res.ok) throw new Error("Failed");

    console.log("Session completed in backend");
  } catch (err) {
    console.error(err);
  }
}
function renderSessionHistory(sessions) {
    const container = document.getElementById("sessionHistory");

    if (!container) return;

    container.innerHTML = "";

    sessions.forEach(s => {
        const div = document.createElement("div");

        div.innerHTML = `
            <div><b>${s.sessionType}</b></div>
            <div>${s.durationMinutes} min</div>
            <div>${s.successful ? "Done" : "Failed"}</div>
            <div>${new Date(s.startTime).toLocaleString()}</div>
        `;

        container.appendChild(div);
    });
}

document.addEventListener("DOMContentLoaded", () => {

  timerEl = document.getElementById("timer");
  startBtn = document.getElementById("startBtn");
  pauseBtn = document.getElementById("pauseBtn");
  resetBtn = document.getElementById("resetBtn");
  continueBtn = document.getElementById("continueBtn");
  const saveBtn = document.getElementById("saveBtn");

  hoursInput = document.getElementById("dailyGoalHours");
  minsInput = document.getElementById("dailyGoalMins");
  remainingGoalEl = document.getElementById("remainingGoal");
  totalHoursEl = document.getElementById("totalHours");
  sessionsCompletedEl = document.getElementById("sessionsCompleted");
  sessionType = document.querySelectorAll(".session-type");
  customInput = document.getElementById("customDuration");

  initState();      // 🔥 first restore everything
  checkNewDay();    // then check reset logic
  waitForUserAndSync();

  // listeners
  startBtn?.addEventListener("click", startTimer);
  pauseBtn?.addEventListener("click", pauseTimer);
  resetBtn?.addEventListener("click", resetTimer);
  continueBtn?.addEventListener("click", continueTimer);

  hoursInput?.addEventListener("input", updateRemainingGoalDisplay);
  minsInput?.addEventListener("input", updateRemainingGoalDisplay);
 
  sessionType.forEach(type => {
    type.addEventListener("click", () => {
      sessionType.forEach(t => t.classList.remove("active"));
      type.classList.add("active");

      state.timerMinutes = Number(type.dataset.minutes);
      state.timerSeconds = 0;

      // ✅ THIS LINE IS CRITICAL
      state.sessionType = type.dataset.type || "FOCUS";

      updateDisplay();

      if (progressCircle) {
        progressCircle.style.strokeDashoffset = circumference;
      }
    });
  });
  
  const storedGoal = Number(localStorage.getItem("focus_daily_goal"));

  if (storedGoal) {
    const h = Math.floor(storedGoal / 60);
    const m = storedGoal % 60;

    hoursInput.value = h;
    minsInput.value = m;
}
  customInput.addEventListener("change", () => {
    state.timerMinutes = Number(customInput.value);
    state.timerSeconds = 0;

    state.sessionType = "CUSTOM"; // 🔥 important

    sessionType.forEach(t => t.classList.remove("active"));

    updateDisplay();

    if (progressCircle) {
      progressCircle.style.strokeDashoffset = circumference;
    }
  });

  lightBtn.addEventListener("click", () => {
    body.classList.remove("dark");
    body.classList.add("light");

    localStorage.setItem(STORAGE_KEYS.THEME, "light"); // ✅ save
  });

  darkBtn.addEventListener("click", () => {
    body.classList.remove("light");
    body.classList.add("dark");

    localStorage.setItem(STORAGE_KEYS.THEME, "dark"); // ✅ save
  });
  
	if (saveBtn) {
	  saveBtn.addEventListener("click", () => {
	    const hours = Number(document.getElementById("dailyGoalHours").value) || 0;
	    const mins = Number(document.getElementById("dailyGoalMins").value) || 0;

	    const totalMinutes = hours * 60 + mins;

	    if (totalMinutes <= 0 || totalMinutes > 1440) {
	      showToast("Invalid goal time ⛔");
	      return;
	    }

	    dailyGoalMinutes = totalMinutes;
	    remainingGoalMinutes = Math.max(dailyGoalMinutes - state.totalFocusedMinutes, 0);

	    localStorage.setItem(STORAGE_KEYS.DAILY_GOAL, totalMinutes);
	    localStorage.setItem(STORAGE_KEYS.REMAINING_GOAL, remainingGoalMinutes);

	    updateDailyGoalDisplay();
	    updateGoalProgress();

	    if (remainingGoalEl) {
	      remainingGoalEl.textContent = formatMinutesToHM(remainingGoalMinutes);
	    }

	    showToast("Goal updated! 🎯");
	  });
	}
	
	loadSessions();
});
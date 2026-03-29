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
  RUNNING: "focus_timer_running",
  REMAINING: "focus_timer_remaining"

};

let timerMinutes = 25;
let timerSeconds = 0;
let interval = null;
let running = false;

// Stats
let totalFocusedMinutes = Number(localStorage.getItem(STORAGE_KEYS.TOTAL_MINUTES)) || 0;
let completedSessions = Number(localStorage.getItem(STORAGE_KEYS.SESSIONS)) || 0;
let sessionHistory = JSON.parse(localStorage.getItem(STORAGE_KEYS.SESSION_HISTORY)) || [];

// DOM elements
const timerEl = document.getElementById("timer");
const goalProgressEl = document.getElementById("goalProgress");
const goalDoneEl = document.getElementById("goalDone");
const goalTotalEl = document.getElementById("goalTotal");
const goalStatusEl = document.getElementById("goalStatus");
const startBtn = document.getElementById("startBtn");
const pauseBtn = document.getElementById("pauseBtn");
const resetBtn = document.getElementById("resetBtn");
const sessionTypes = document.querySelectorAll(".session-type");
const customInput = document.getElementById("customDuration");
const sessionsCompletedEl = document.getElementById("sessionsCompleted");
const streakEl = document.getElementById("streakCount");
const dailyGoalHoursInput = document.getElementById("dailyGoalHours");
const dailyGoalMinsInput = document.getElementById("dailyGoalMins");
const body = document.body;
const lightBtn = document.getElementById("lightBtn");
const darkBtn = document.getElementById("darkBtn");
const remainingGoalEl = document.getElementById("remainingGoal");

// Progress ring setup
const progressCircle = document.querySelector('.progress-ring__circle');
const radius = progressCircle.r.baseVal.value;
const circumference = 2 * Math.PI * radius;
progressCircle.style.strokeDasharray = `${circumference} ${circumference}`;
progressCircle.style.strokeDashoffset = circumference;

// Initialize goal
let dailyGoalHours = dailyGoalHoursInput ? Number(dailyGoalHoursInput.value) : 0;
let dailyGoalMinutes = dailyGoalMinsInput ? Number(dailyGoalMinsInput.value) : 0;

// ===== Functions =====
function updateDisplay() {
  let min = String(timerMinutes).padStart(2, '0');
  let sec = String(timerSeconds).padStart(2, '0');
  timerEl.textContent = `${min}:${sec}`;
}

function updateStats() {
  if (sessionsCompletedEl) {
    sessionsCompletedEl.textContent = completedSessions;
  }
}

function updateGoalProgress() {
  const remainingMinutes = Math.max(dailyGoalMinutes - totalFocusedMinutes, 0);
  const remainingHours = Math.floor(remainingMinutes / 60);
  const remainingMins = remainingMinutes % 60;

  if (remainingGoalEl) {
    remainingGoalEl.textContent = `${remainingHours}h ${remainingMins}m`;
  }

  const percent = dailyGoalMinutes > 0
    ? (totalFocusedMinutes / dailyGoalMinutes) * 100
    : 0;

  if (goalProgressEl) {
    goalProgressEl.style.width = `${Math.min(percent, 100)}%`;
  }

  if (goalDoneEl) {
    const doneHours = Math.floor(totalFocusedMinutes / 60);
    const doneMins = Math.round(totalFocusedMinutes % 60);
    goalDoneEl.textContent = `${doneHours}h ${doneMins}m`;
  }

  if (goalTotalEl) {
    const totalHours = Math.floor(dailyGoalMinutes / 60);
    const totalMins = dailyGoalMinutes % 60;
    goalTotalEl.textContent = `${totalHours}h ${totalMins}m`;
  }

  if (goalStatusEl) {
    if (percent >= 100) {
      goalStatusEl.textContent = "Goal achieved! 🎉";
    } else if (percent > 0) {
      goalStatusEl.textContent = "Keep going 💪";
    } else {
      goalStatusEl.textContent = "Let’s get started 🚀";
    }
  }
}

function updateRemainingGoalDisplay() {
  const remainingMinutes = Math.max(dailyGoalMinutes - totalFocusedMinutes, 0);
  const remainingHours = Math.floor(remainingMinutes / 60);
  const remainingMins = remainingMinutes % 60;
  remainingGoalEl.textContent = `${remainingHours}h ${remainingMins}m`;
}

function restoreStats() {
  const savedMinutes = localStorage.getItem(STORAGE_KEYS.TOTAL_MINUTES);
  const savedSessions = localStorage.getItem(STORAGE_KEYS.SESSIONS);

  totalFocusedMinutes = Number(savedMinutes) || 0;
  completedSessions = Number(savedSessions) || 0;

  if (sessionsCompletedEl) {
    sessionsCompletedEl.textContent = completedSessions;
  }

  updateStats();
  updateGoalProgress();
  updateRemainingGoalDisplay();
}

function checkNewDay() {
  const today = new Date().toDateString();
  const lastDate = localStorage.getItem(STORAGE_KEYS.LAST_DATE);

  if (lastDate === today) return;

  localStorage.setItem(STORAGE_KEYS.LAST_DATE, today);

  totalFocusedMinutes = 0;
  completedSessions = 0;
  localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, "0");
  localStorage.setItem(STORAGE_KEYS.SESSIONS, "0");

  updateStats();

  const goal = Number(localStorage.getItem(STORAGE_KEYS.DAILY_GOAL)) || 5;
  dailyGoalMinutes = goal * 60;
  updateGoalProgress();
}

function showToast(message) {
  const toast = document.getElementById('toast');
  toast.textContent = message;
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 2500);
}

function startTimer() {
  if (running) return;
  running = true;

  const activeType = document.querySelector(".session-type.active");
  const sessionMinutes = Number(activeType?.dataset.minutes || customInput.value);

  if (!localStorage.getItem(TIMER_KEYS.RUNNING)) {
    localStorage.setItem(TIMER_KEYS.START, Date.now());
    localStorage.setItem(TIMER_KEYS.DURATION, sessionMinutes * 60 * 1000);
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

      localStorage.setItem(STORAGE_KEYS.SESSIONS, completedSessions);
      localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, totalFocusedMinutes);
      localStorage.setItem("lastSessionDate", new Date().toDateString());

      updateStats();
      updateGoalProgress();
      updateDailyGoalDisplay();

      const sessionType = activeType?.textContent || "Custom";
      sessionHistory.push({
        date: new Date().toISOString(),
        type: sessionType,
        minutes: sessionMinutes
      });
      localStorage.setItem(STORAGE_KEYS.SESSION_HISTORY, JSON.stringify(sessionHistory));

      completeSessionBackend();
      return;
    }

    const totalSeconds = Math.floor(remaining / 1000);
    timerMinutes = Math.floor(totalSeconds / 60);
    timerSeconds = totalSeconds % 60;
    updateDisplay();

    const offset = circumference - (elapsed / duration) * circumference;
    progressCircle.style.strokeDashoffset = Math.max(0, offset);
  }, 1000);
}

function pauseTimer() {
  running = false;
  clearInterval(interval);

  const remaining = (timerMinutes * 60 + timerSeconds) * 1000;
  localStorage.setItem(TIMER_KEYS.REMAINING, remaining);

  localStorage.removeItem(TIMER_KEYS.RUNNING);
  localStorage.removeItem(TIMER_KEYS.START);
  localStorage.removeItem(TIMER_KEYS.DURATION);
}

function continueTimer() {
  if (running) return;
  running = true;

  const remaining = Number(localStorage.getItem(TIMER_KEYS.REMAINING));
  if (!remaining || remaining <= 0) return;

  const start = Date.now();
  localStorage.setItem(TIMER_KEYS.START, start);
  localStorage.setItem(TIMER_KEYS.DURATION, remaining);
  localStorage.setItem(TIMER_KEYS.RUNNING, "true");

  // ✅ Show paused time immediately
  const totalSeconds = Math.floor(remaining / 1000);
  timerMinutes = Math.floor(totalSeconds / 60);
  timerSeconds = totalSeconds % 60;
  updateDisplay();

  interval = setInterval(() => {
    const elapsed = Date.now() - start;
    const left = remaining - elapsed;

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

    const totalSeconds = Math.floor(left / 1000);
    timerMinutes = Math.floor(totalSeconds / 60);
    timerSeconds = totalSeconds % 60;
    updateDisplay();

    const offset = circumference - (elapsed / remaining) * circumference;
    progressCircle.style.strokeDashoffset = Math.max(0, offset);
  }, 1000);
}

function resetTimer() {
  pauseTimer();

  const activeType = document.querySelector(".session-type.active");
  const sessionMinutes = Number(activeType?.dataset.minutes || customInput.value);

  timerMinutes = sessionMinutes;
  timerSeconds = 0;
  updateDisplay();

  progressCircle.style.strokeDashoffset = circumference;

  localStorage.removeItem(TIMER_KEYS.START);
  localStorage.removeItem(TIMER_KEYS.DURATION);
  localStorage.removeItem(TIMER_KEYS.RUNNING);
  localStorage.removeItem(TIMER_KEYS.REMAINING);
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
  timerMinutes = Math.floor(totalSeconds / 60);
  timerSeconds = totalSeconds % 60;

  updateDisplay();
  startTimer();
})();
  // ===== Restore running timer if page reloads =====
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
    startTimer();
  })();

  // ===== Session type selection =====
  sessionTypes.forEach(type => {
    type.addEventListener("click", () => {
      sessionTypes.forEach(t => t.classList.remove("active"));
      type.classList.add("active");
      timerMinutes = Number(type.dataset.minutes);
      timerSeconds = 0;
      updateDisplay();
      progressCircle.style.strokeDashoffset = circumference;
    });
  });

  customInput.addEventListener("change", () => {
    timerMinutes = Number(customInput.value);
    timerSeconds = 0;
    sessionTypes.forEach(t => t.classList.remove("active"));
    updateDisplay();
    progressCircle.style.strokeDashoffset = circumference;
  });

  // ===== Button controls =====
  startBtn.addEventListener("click", startTimer);
  pauseBtn.addEventListener("click", pauseTimer);
  resetBtn.addEventListener("click", resetTimer);

  // ===== Theme toggle =====
  lightBtn.addEventListener("click", () => {
    body.classList.remove("dark");
    body.classList.add("light");
    lightBtn.classList.add("active");
    darkBtn.classList.remove("active");
  });
  darkBtn.addEventListener("click", () => {
    body.classList.remove("light");
    body.classList.add("dark");
    darkBtn.classList.add("active");
    lightBtn.classList.remove("active");
  });

  function showToast(message) {
    const toast = document.createElement("div");
    toast.className = "toast";
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
      toast.classList.add("visible");
    }, 100);

    setTimeout(() => {
      toast.classList.remove("visible");
      setTimeout(() => toast.remove(), 300);
    }, 3000);
  }

  function updateStats() {
    const sessionsEl = document.getElementById("completedSessions");
    const minutesEl = document.getElementById("totalMinutes");

    sessionsEl.textContent = completedSessions;
    minutesEl.textContent = totalFocusedMinutes;
  }

  function updateGoalProgress() {
    const goalBar = document.getElementById("goalProgressBar");
    const goalMinutes = Number(localStorage.getItem("dailyGoalMinutes") || 0);

    if (goalMinutes > 0) {
      const progress = Math.min(100, (totalFocusedMinutes / goalMinutes) * 100);
      goalBar.style.width = `${progress}%`;
    }
  }

  function updateDailyGoalDisplay() {
    const goalDisplay = document.getElementById("dailyGoalDisplay");
    const goalMinutes = Number(localStorage.getItem("dailyGoalMinutes") || 0);

    if (goalMinutes > 0) {
      goalDisplay.textContent = `${totalFocusedMinutes}/${goalMinutes} min`;
    } else {
      goalDisplay.textContent = `${totalFocusedMinutes} min`;
    }
  }
  // ===== Daily reset at midnight =====
  function setupDailyReset() {
    setInterval(() => {
      const now = new Date();
      const todayStr = now.toDateString();
      const lastDate = localStorage.getItem(STORAGE_KEYS.LAST_DATE);

      if (lastDate !== todayStr) {
        localStorage.setItem(STORAGE_KEYS.LAST_DATE, todayStr);

        totalFocusedMinutes = 0;
        completedSessions = 0;
        localStorage.setItem(STORAGE_KEYS.TOTAL_MINUTES, "0");
        localStorage.setItem(STORAGE_KEYS.SESSIONS, "0");

        updateStats();
        updateGoalProgress();
        updateRemainingGoalDisplay();

        progressCircle.style.strokeDashoffset = circumference;
      }
    }, 60 * 1000);
  }

  // ===== Streak tracking =====
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

    if (streakEl) {
      streakEl.textContent = `${streak} 🔥`;
    }
  }

  // ===== Page load =====
  document.addEventListener("DOMContentLoaded", () => {
    restoreStats();                // Step 1: restore local stats
    checkNewDay();                 // Step 2: reset if needed
    updateRemainingGoalDisplay();  // Step 3: update UI
    setupDailyReset();             // Step 4: auto-reset at midnight
    updateDisplay();
    updateStats();
  });
  
  function completeSessionBackend() {
    const activeType = document.querySelector(".session-type.active");
    const sessionType = activeType?.textContent || "Custom";
    const sessionMinutes = Number(localStorage.getItem(TIMER_KEYS.DURATION)) / 60000;

    // Save to local history
    sessionHistory.push({
      date: new Date().toISOString(),
      type: sessionType,
      minutes: sessionMinutes
    });
    localStorage.setItem(STORAGE_KEYS.SESSION_HISTORY, JSON.stringify(sessionHistory));

    // Send to backend (example API call)
    fetch("/api/sessions/complete", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        type: sessionType,
        minutes: sessionMinutes,
        date: new Date().toISOString()
      })
    })
    .then(res => res.json())
    .then(data => {
      console.log("Session saved:", data);
    })
    .catch(err => {
      console.error("Failed to save session:", err);
    });
  }

  function renderSessionHistory() {
    const historyContainer = document.getElementById("sessionHistory");
    historyContainer.innerHTML = "";

    sessionHistory.forEach(session => {
      const item = document.createElement("div");
      item.className = "session-item";
      item.textContent = `${session.type} — ${session.minutes} min (${new Date(session.date).toLocaleTimeString()})`;
      historyContainer.appendChild(item);
    });
  }
  function renderTasks() {
    const taskContainer = document.getElementById("taskContainer");
    taskContainer.innerHTML = "";

    tasks.forEach(task => {
      const card = document.createElement("div");
      card.className = "task-card";

      // Title
      const title = document.createElement("h4");
      title.textContent = task.title;
      if (task.completed) {
        title.classList.add("completed");
      }
      card.appendChild(title);

      // Priority badge
      const badge = document.createElement("span");
      badge.className = `priority-badge priority-${task.priority}`;
      badge.textContent = task.priority;
      card.appendChild(badge);

      // Editable priority
      const prioritySelect = document.createElement("select");
      ["High", "Medium", "Low"].forEach(level => {
        const option = document.createElement("option");
        option.value = level;
        option.textContent = level;
        if (task.priority === level) option.selected = true;
        prioritySelect.appendChild(option);
      });
      prioritySelect.addEventListener("change", e => {
        task.priority = e.target.value;
        saveTasks();
        renderTasks();
      });
      card.appendChild(prioritySelect);

      // Complete toggle
      const toggle = document.createElement("button");
      toggle.textContent = task.completed ? "Undo" : "Done";
      toggle.addEventListener("click", () => {
        task.completed = !task.completed;
        saveTasks();
        renderTasks();
      });
      card.appendChild(toggle);

      taskContainer.appendChild(card);
    });
  }

  function dayEndRefresh() {
    const today = new Date().toDateString();
    const lastRefresh = localStorage.getItem("lastRefreshDate");

    if (lastRefresh !== today) {
      tasks.forEach(task => {
        task.completed = false;
      });
      saveTasks();
      localStorage.setItem("lastRefreshDate", today);
      renderTasks();
    }
  }
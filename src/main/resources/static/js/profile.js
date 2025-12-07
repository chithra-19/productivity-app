// ===== Store original form values =====
let originalValues = {};

// ===== Enable Edit Mode =====
function enableEdit() {
  document.querySelectorAll("#profileForm .form-control").forEach(el => {
    originalValues[el.name] = el.value;
    el.disabled = false;
  });
  document.getElementById("saveBtn").style.display = "inline-block";
  document.getElementById("cancelBtn").style.display = "inline-block";

  // Show overlay on profile picture
  document.querySelector(".overlay").style.display = "block";
  document.getElementById("editBtn").style.display = "none";
}

// ===== Cancel Edit Mode =====
function cancelEdit() {
  Object.keys(originalValues).forEach(key => {
    const el = document.querySelector(`[name="${key}"]`);
    if (el) el.value = originalValues[key];
  });
  document.querySelectorAll("#profileForm .form-control").forEach(el => el.disabled = true);

  document.getElementById("saveBtn").style.display = "none";
  document.getElementById("cancelBtn").style.display = "none";

  // Hide overlay
  document.querySelector(".overlay").style.display = "none";
  document.getElementById("editBtn").style.display = "inline-block";
}

// ===== Preview Profile Picture =====
function previewImage(input) {
  if (input.files && input.files[0]) {
    const reader = new FileReader();
    reader.onload = e => document.getElementById('profilePreview').src = e.target.result;
    reader.readAsDataURL(input.files[0]);
  }
}

// ===== Update Stats Dynamically =====
function updateStats(data) {
  const currentStreak = document.getElementById('currentStreak');
  const completedTasks = document.getElementById('completedTasks');
  const tasksProgress = document.getElementById('tasksProgress');
  const productivityScore = document.getElementById('productivityScore');
  const badgesCount = document.getElementById('badgesCount');
  const badgesContainer = document.querySelector('.badges');

  if(currentStreak) currentStreak.innerText = data.streak;
  if(completedTasks) completedTasks.innerText = data.completedTasks;
  if(tasksProgress) tasksProgress.style.width = data.taskPercent + '%';
  if(productivityScore) productivityScore.innerText = data.productivityScore;
  if(badgesCount) badgesCount.innerText = data.badges.length;

  if(badgesContainer) {
    badgesContainer.innerHTML = '';
    data.badges.forEach(b => {
      const img = document.createElement('img');
      img.src = b.src;
      img.alt = b.name;
      img.title = b.name;
      img.classList.add('badge-img');
      badgesContainer.appendChild(img);
    });
  }
}

// ===== Render Timeline =====
function renderTimeline(activity) {
  const timeline = document.getElementById('recent-activities');
  if(!timeline) return;
  timeline.innerHTML = '';
  activity.forEach(a => {
    const li = document.createElement('li');
    li.classList.add("timeline-item");
    li.innerHTML = `<span class="time">${a.time}</span> <span class="event">${a.event}</span>`;
    timeline.appendChild(li);
  });
}

// ===== Theme Toggle =====
function initTheme() {
  const themeSwitch = document.getElementById('themeSwitch');

  // Load saved theme
  if (localStorage.getItem('theme') === 'dark') {
    document.body.classList.add('dark-mode');
    themeSwitch.checked = true;
  }

  // Toggle theme
  themeSwitch.addEventListener('change', () => {
    if (themeSwitch.checked) {
      document.body.classList.add('dark-mode');
      localStorage.setItem('theme', 'dark');
    } else {
      document.body.classList.remove('dark-mode');
      localStorage.setItem('theme', 'light');
    }
  });
}

// ===== Initialize Page =====
document.addEventListener('DOMContentLoaded', () => {
  initTheme();

  // Example stats (remove if dynamic from backend)
  updateStats({
    streak: 15,
    completedTasks: 52,
    taskPercent: 80,
    productivityScore: 135,
    badges: [
      {name: 'First Task Completed', src:'/images/badge1.png'},
      {name: '7-Day Streak', src:'/images/badge2.png'},
      {name: '100 Tasks Completed', src:'/images/badge3.png'},
      {name: 'Daily Winner', src:'/images/badge4.png'}
    ]
  });

  // Example timeline (remove if dynamic from backend)
  const recentActivity = [
    {time: 'Today', event: 'Completed task: “Build Profile Page” ✅'},
    {time: 'Yesterday', event: 'Unlocked Achievement: “7-Day Streak” 🏆'},
    {time: '2 days ago', event: 'Completed task: “Setup Heatmap” ✅'}
  ];
  renderTimeline(recentActivity);

  // Fetch dynamic recent activities if needed
  fetch('/profile/recent')
    .then(res => res.json())
    .then(data => {
      if(!data) return;
      const list = document.getElementById("recent-activities");
      if(!list) return;
      list.innerHTML = "";
      data.forEach(a => {
        const item = document.createElement('li');
        item.classList.add("timeline-item");
        item.innerHTML = `<span class="time">${a.activityDate}</span> <span class="event">${a.category} — ${a.taskCount} tasks — ${a.focusMinutes}min</span>`;
        list.appendChild(item);
      });
    });
});

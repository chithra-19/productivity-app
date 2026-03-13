// ===============================
// PROFILE EDIT MODE
// ===============================

const editBtn = document.getElementById("editBtn");
const saveBtn = document.getElementById("saveBtn");
const cancelBtn = document.getElementById("cancelBtn");
const profileInputs = document.querySelectorAll(".profile-input");

let originalValues = {};

// Enable Edit Mode
function enableEditMode() {
    profileInputs.forEach(input => {
        originalValues[input.name] = input.value;
        input.disabled = false;
    });

    saveBtn.classList.remove("d-none");
    cancelBtn.classList.remove("d-none");
    editBtn.classList.add("d-none");
}

// Cancel Edit Mode
function cancelEditMode() {
    profileInputs.forEach(input => {
        input.value = originalValues[input.name] || input.value;
        input.disabled = true;
    });

    saveBtn.classList.add("d-none");
    cancelBtn.classList.add("d-none");
    editBtn.classList.remove("d-none");
}

// Attach events safely
if (editBtn) editBtn.addEventListener("click", enableEditMode);
if (cancelBtn) cancelBtn.addEventListener("click", cancelEditMode);


// ===============================
// PROFILE IMAGE PREVIEW
// ===============================

function previewImage(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = e => {
            const preview = document.getElementById("profilePreview");
            if (preview) preview.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}



// ===============================
// DYNAMIC STATS UPDATE (Optional)
// ===============================

function updateStats(data) {
    const streakEl = document.getElementById("streakValue");
    const completedEl = document.getElementById("completedTasksValue");
    const progressBar = document.getElementById("completionBar");
    const productivityEl = document.getElementById("productivityValue");
    const badgesContainer = document.getElementById("badgesContainer");

    if (streakEl) streakEl.innerText = data.streak;
    if (completedEl) completedEl.innerText = data.completedTasks;

    if (progressBar) {
        progressBar.style.width = data.completionPercentage + "%";
        progressBar.innerText = data.completionPercentage + "%";
    }

    if (productivityEl) productivityEl.innerText = data.productivityScore;

    if (badgesContainer && data.badges) {
        badgesContainer.innerHTML = "";
        data.badges.forEach(badge => {
            const img = document.createElement("img");
            img.src = badge.imageUrl || "/images/default-badge.png";
            img.alt = badge.name;
            img.title = badge.name;
            img.classList.add("badge-img");
            badgesContainer.appendChild(img);
        });
    }
}


// ===============================
// TIMELINE RENDER (Optional AJAX)
// ===============================

function renderTimeline(activities) {
    const timeline = document.getElementById("recentActivitiesList");
    if (!timeline) return;

    timeline.innerHTML = "";

    activities.forEach(activity => {
        const li = document.createElement("li");
        li.classList.add("mb-3", "border-bottom", "pb-2");

        li.innerHTML = `
            <small class="text-muted">${activity.activityDate}</small>
            <div>${activity.description}</div>
        `;

        timeline.appendChild(li);
    });
}


// ===============================
// INITIALIZATION
// ===============================

document.addEventListener("DOMContentLoaded", () => {
    initTheme();

    // Optional: Fetch updated stats dynamically
    /*
    fetch("/api/profile/stats")
        .then(res => res.json())
        .then(data => updateStats(data))
        .catch(err => console.error("Stats fetch error:", err));
    */

    // Optional: Fetch recent activities dynamically
    /*
    fetch("/api/profile/recent")
        .then(res => res.json())
        .then(data => renderTimeline(data))
        .catch(err => console.error("Timeline fetch error:", err));
    */
});

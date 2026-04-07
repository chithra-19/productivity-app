/* ===============================
   PROFILE UI CONTROLLER
   =============================== */

const editBtn = document.getElementById("editBtn");
const saveBtn = document.getElementById("saveBtn");
const cancelBtn = document.getElementById("cancelBtn");

const editableFields = document.querySelectorAll(".js-editable");

let originalValues = {};


/* ===============================
   ENABLE EDIT MODE
   =============================== */
function enableEditMode() {
    editableFields.forEach(field => {
        if (!field.name) return;

        originalValues[field.name] = field.value;
        field.disabled = false;
    });

    toggleButtons(true);
}


/* ===============================
   CANCEL EDIT MODE
   =============================== */
function cancelEditMode() {
    editableFields.forEach(field => {
        if (!field.name) return;

        if (originalValues[field.name] !== undefined) {
            field.value = originalValues[field.name];
        }

        field.disabled = true;
    });

    toggleButtons(false);
}


/* ===============================
   TOGGLE BUTTON UI
   =============================== */
function toggleButtons(isEditing) {
    if (!editBtn || !saveBtn || !cancelBtn) return;

    editBtn.classList.toggle("hidden", isEditing);
    saveBtn.classList.toggle("hidden", !isEditing);
    cancelBtn.classList.toggle("hidden", !isEditing);
}


/* ===============================
   PROFILE IMAGE PREVIEW
   =============================== */
function previewImage(input) {
    const file = input.files?.[0];
    if (!file) return;

    const reader = new FileReader();

    reader.onload = (e) => {
        const preview = document.getElementById("profilePreview");
        if (preview) preview.src = e.target.result;
    };

    reader.readAsDataURL(file);
}


/* ===============================
   OPTIONAL: AUTO UPDATE UI STATS
   (if you later connect APIs)
   =============================== */
function updateStats(data) {
    const streak = document.getElementById("streakValue");
    const best = document.getElementById("bestStreakValue");
    const tasks = document.getElementById("taskValue");
    const productivity = document.getElementById("productivityValue");
    const xpBar = document.querySelector(".xp-fill");

    if (streak) streak.innerText = data.currentStreak ?? 0;
    if (best) best.innerText = data.bestStreak ?? 0;
    if (tasks) tasks.innerText = data.completedTasks ?? 0;
    if (productivity) productivity.innerText = data.productivityScore ?? 0;

    if (xpBar && data.xpPercentage != null) {
        xpBar.style.width = data.xpPercentage + "%";
    }
}


/* ===============================
   OPTIONAL: TIMELINE RENDER
   =============================== */
function renderTimeline(activities) {
    const container = document.querySelector(".timeline");
    if (!container || !Array.isArray(activities)) return;

    container.innerHTML = "";

    activities.forEach(a => {
        const item = document.createElement("div");
        item.className = "timeline-item";

        item.innerHTML = `
            <div class="dot"></div>
            <div class="content">
                <p>${a.description ?? ""}</p>
                <small>${a.activityDate ?? ""}</small>
            </div>
        `;

        container.appendChild(item);
    });
}


/* ===============================
   EVENT LISTENERS
   =============================== */
document.addEventListener("DOMContentLoaded", () => {

    // edit mode
    editBtn?.addEventListener("click", enableEditMode);
    cancelBtn?.addEventListener("click", cancelEditMode);

    // future: theme init
    // initTheme?.();
});
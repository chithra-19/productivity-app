// static/js/scripts.js

// Get JWT and CSRF token
const jwtToken = localStorage.getItem("jwtToken");
const csrfToken = document.querySelector('input[name="_csrf"]')?.value;

// Generic function to make secured POST requests
async function postRequest(url, data = {}) {
    try {
        const res = await fetch(url, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${jwtToken}`,
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
        return await res.json();
    } catch (err) {
        console.error(err);
        alert("⚠️ Something went wrong.");
        throw err;
    }
}

// Attach click listener for "Mark Successful" buttons
function attachMarkSuccessfulButtons(containerSelector) {
    document.querySelectorAll(`${containerSelector} .mark-success-btn`).forEach(btn => {
        btn.addEventListener("click", async (e) => {
            const li = e.target.closest("li");
            const sessionId = li.dataset.id;

            const result = await postRequest(`/api/focus/${sessionId}/mark-successful`);
            li.querySelector("span").textContent = "✅ Successful";
            e.target.remove();
        });
    });
}

// Optional: dynamically add items with proper event binding
function appendSession(containerSelector, session) {
    const li = document.createElement("li");
    li.className = "list-group-item";
    li.dataset.id = session.id;
    li.innerHTML = `
        <strong>${session.durationMinutes} min</strong> -
        <span>${session.sessionType}</span> - ${session.successful ? "✅ Successful" : "❌ Pending"}
        ${!session.successful ? `<button class="btn btn-sm btn-success float-end mark-success-btn">Mark Successful</button>` : ""}
        ${session.notes ? `<div><small>${session.notes}</small></div>` : ""}
    `;
    document.querySelector(containerSelector).prepend(li);

    // Attach listener to newly added button
    if (!session.successful) {
        li.querySelector(".mark-success-btn").addEventListener("click", async (e) => {
            const liInner = e.target.closest("li");
            const sid = liInner.dataset.id;
            await postRequest(`/api/focus/${sid}/mark-successful`);
            liInner.querySelector("span").textContent = "✅ Successful";
            e.target.remove();
        });
    }
}

const editBtn = document.getElementById("editBtn");
const saveBtn = document.getElementById("saveBtn");
const cancelBtn = document.getElementById("cancelBtn");
const fields = document.querySelectorAll(".editable");
const fileInput = document.getElementById("profilePictureFile");
const profilePreview = document.getElementById("profilePreview");

let originalValues = [];

function enableEditMode() {
    originalValues = Array.from(fields).map(f => f.value);
    fields.forEach(f => f.readOnly = false);

    // Enable file input
    fileInput.disabled = false;

    editBtn.classList.add("d-none");
    saveBtn.classList.remove("d-none");
    cancelBtn.classList.remove("d-none");
}

function cancelEditMode() {
    fields.forEach((f, i) => {
        f.value = originalValues[i];
        f.readOnly = true;
    });

    // Disable file input again
    fileInput.disabled = true;

    editBtn.classList.remove("d-none");
    saveBtn.classList.add("d-none");
    cancelBtn.classList.add("d-none");
}

function disableEditMode() {
    fields.forEach(f => f.readOnly = true);

    // Disable file input again
    fileInput.disabled = true;

    editBtn.classList.remove("d-none");
    saveBtn.classList.add("d-none");
    cancelBtn.classList.add("d-none");
}

function previewImage(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = e => {
            document.getElementById("profilePreview").src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

async function uploadProfilePicture(file) {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch("/api/profile/picture", {
        method: "POST",
        body: formData
    });

    if (response.ok) {
        const updated = await response.json();
        document.getElementById("profilePreview").src = updated.profilePictureUrl;
    } else {
        alert("Failed to upload picture");
    }
}

async function saveProfile() {
    const payload = {
        firstName: document.querySelector('[name="firstName"]').value,
        lastName: document.querySelector('[name="lastName"]').value,
        bio: document.querySelector('[name="bio"]').value
    };

    try {
        const response = await fetch('/api/profile', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            alert("Failed to update profile text.");
            return;
        }

        const updated = await response.json();
        console.log("Profile text updated:", updated);

        if (fileInput.files.length > 0) {
            await uploadProfilePicture(fileInput.files[0]);
        }

        disableEditMode();
        alert("Profile updated successfully!");
    } catch (err) {
        console.error(err);
        alert("Error updating profile.");
    }
}
profilePreview.addEventListener("click", () => {
    if (fileInput.disabled) {
        // View mode: show preview modal
        const modalImage = document.getElementById("modalImage");
        modalImage.src = profilePreview.src;
        const modal = new bootstrap.Modal(document.getElementById("imagePreviewModal"));
        modal.show();
    } else {
        // Edit mode: open file picker
        fileInput.click();
    }
});


editBtn?.addEventListener("click", enableEditMode);
cancelBtn?.addEventListener("click", cancelEditMode);
saveBtn?.addEventListener("click", e => {
    e.preventDefault();
    saveProfile();
});

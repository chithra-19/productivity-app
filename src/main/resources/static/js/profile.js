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

async function saveProfile() {
    const formData = new FormData();
    formData.append("firstName", document.querySelector('[name="firstName"]').value);
    formData.append("lastName", document.querySelector('[name="lastName"]').value);
    formData.append("bio", document.querySelector('[name="bio"]').value);

    // Add file if selected, otherwise append empty blob (required by Spring)
    if (fileInput.files.length > 0) {
        formData.append("profilePictureFile", fileInput.files[0]);
    } else {
        formData.append("profilePictureFile", new Blob([]), "");
    }

    try {
        const response = await fetch('/dashboard/profile/update', {
            method: 'POST',   // ← matches @PostMapping in your Java controller
            body: formData    // ← no Content-Type header! browser sets it automatically
        });

        if (response.ok || response.redirected) {
            window.location.href = '/dashboard/profile';
        } else {
            alert("Failed to update profile.");
        }

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

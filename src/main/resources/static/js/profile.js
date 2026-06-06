const saveBtn = document.getElementById("saveBtn");
const cancelBtn = document.getElementById("cancelBtn");
const fields = document.querySelectorAll(".editable");
const fileInput = document.getElementById("profilePictureFile");
const profilePreview = document.getElementById("profilePreview");

let originalValues = [];

function cancelEditMode() {
    fields.forEach((f, i) => {
        f.value = originalValues[i];
        f.readOnly = true;
    });
    fileInput.disabled = true;
}

function previewImage(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = e => {
            profilePreview.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

profilePreview.addEventListener("click", () => {
    if (fileInput.disabled) {
        const modalImage = document.getElementById("modalImage");
        modalImage.src = profilePreview.src;
        const modal = new bootstrap.Modal(document.getElementById("imagePreviewModal"));
        modal.show();
    } else {
        fileInput.click();
    }
});

cancelBtn?.addEventListener("click", cancelEditMode);

// Let the form submit naturally — no fetch needed
saveBtn?.addEventListener("click", () => {
    document.getElementById("profileForm").submit();
});
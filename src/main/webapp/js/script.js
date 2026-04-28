// Common Utility Functions
document.addEventListener('DOMContentLoaded', () => {
    // ----------------------------------------------------
    // URL Parameter parsing for alerts
    // ----------------------------------------------------
    const urlParams = new URLSearchParams(window.location.search);
    const errorMsg = urlParams.get('error');
    const successMsg = urlParams.get('msg');
    
    if (errorMsg) {
        showError(errorMsg);
    }
    if (successMsg) {
        showSuccess(successMsg);
    }

    // ----------------------------------------------------
    // Modal Logic
    // ----------------------------------------------------
    const addBtn = document.getElementById('add-complaint-btn');
    const modal = document.getElementById('complaint-modal');
    const closeBtn = document.querySelector('.close-btn');

    if(addBtn && modal && closeBtn) {
        addBtn.addEventListener('click', () => {
            modal.classList.add('active');
        });

        closeBtn.addEventListener('click', () => {
            modal.classList.remove('active');
        });

        window.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.remove('active');
            }
        });
    }
});

// Helper to show errors
function showError(msg) {
    const errorDiv = document.getElementById('error-alert');
    if (errorDiv) {
        errorDiv.textContent = msg;
        errorDiv.style.display = 'block';
        setTimeout(() => errorDiv.style.display = 'none', 3000);
    }
}

// Helper to show success
function showSuccess(msg) {
    const successDiv = document.getElementById('success-alert');
    if (successDiv) {
        successDiv.textContent = msg;
        successDiv.style.display = 'block';
        setTimeout(() => successDiv.style.display = 'none', 3000);
    }
}

// Helper to determine badge class based on status
function getBadgeClass(status) {
    if (status === 'Resolved') return 'badge-resolved';
    if (status === 'In Progress') return 'badge-progress';
    return 'badge-pending';
}

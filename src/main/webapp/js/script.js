// Common Utility Functions
document.addEventListener('DOMContentLoaded', () => {
    // ----------------------------------------------------
    // URL Parameter parsing for alerts
    // ----------------------------------------------------
    const urlParams = new URLSearchParams(window.location.search);
    const errorMsg = urlParams.get('error');
    const successMsg = urlParams.get('msg');

    if (errorMsg)   showError(errorMsg);
    if (successMsg) showSuccess(successMsg);

    // ----------------------------------------------------
    // Hamburger / Mobile Nav
    // ----------------------------------------------------
    const hamburger  = document.getElementById('hamburger');
    const mainNav    = document.getElementById('main-nav');
    const navOverlay = document.getElementById('nav-overlay');

    function openNav() {
        mainNav    && mainNav.classList.add('open');
        navOverlay && navOverlay.classList.add('open');
        hamburger  && hamburger.classList.add('open');
        hamburger  && hamburger.setAttribute('aria-expanded', 'true');
        document.body.style.overflow = 'hidden';
    }

    function closeNav() {
        mainNav    && mainNav.classList.remove('open');
        navOverlay && navOverlay.classList.remove('open');
        hamburger  && hamburger.classList.remove('open');
        hamburger  && hamburger.setAttribute('aria-expanded', 'false');
        document.body.style.overflow = '';
    }

    if (hamburger) {
        hamburger.addEventListener('click', () => {
            mainNav && mainNav.classList.contains('open') ? closeNav() : openNav();
        });
    }

    if (navOverlay) {
        navOverlay.addEventListener('click', closeNav);
    }

    // Close nav when a link is tapped on mobile
    if (mainNav) {
        mainNav.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', closeNav);
        });
    }

    // ----------------------------------------------------
    // Modal Logic
    // ----------------------------------------------------
    const addBtn  = document.getElementById('add-complaint-btn');
    const modal   = document.getElementById('complaint-modal');
    const closeBtn = document.querySelector('.close-btn');

    if (addBtn && modal && closeBtn) {
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

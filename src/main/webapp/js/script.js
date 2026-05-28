// Common Utility Functions
document.addEventListener('DOMContentLoaded', () => {
    // ----------------------------------------------------
    // URL Parameter parsing for alerts
    // ----------------------------------------------------
    const urlParams  = new URLSearchParams(window.location.search);
    const errorMsg   = urlParams.get('error');
    const successMsg = urlParams.get('msg');
    if (errorMsg)    showError(errorMsg);
    if (successMsg)  showSuccess(successMsg);

    // ----------------------------------------------------
    // Hamburger / Mobile Nav
    // ----------------------------------------------------
    const hamburger  = document.getElementById('hamburger');
    const mainNav    = document.getElementById('main-nav');
    const navOverlay = document.getElementById('nav-overlay');

    function openNav() {
        if (!mainNav) return;
        mainNav.classList.add('open');
        if (navOverlay) navOverlay.classList.add('open');
        if (hamburger)  hamburger.setAttribute('aria-expanded', 'true');
        hamburger && hamburger.classList.add('open');
    }

    function closeNav() {
        if (!mainNav) return;
        mainNav.classList.remove('open');
        if (navOverlay) navOverlay.classList.remove('open');
        if (hamburger)  hamburger.setAttribute('aria-expanded', 'false');
        hamburger && hamburger.classList.remove('open');
    }

    if (hamburger) {
        hamburger.addEventListener('click', function (e) {
            e.stopPropagation();
            mainNav && mainNav.classList.contains('open') ? closeNav() : openNav();
        });
    }

    if (navOverlay) {
        navOverlay.addEventListener('click', closeNav);
        navOverlay.addEventListener('touchend', function(e) {
            e.preventDefault();
            closeNav();
        });
    }

    // ✅ FIX: Force navigation on touch AND click for nav links
    if (mainNav) {
        mainNav.querySelectorAll('a').forEach(function (link) {
            // Handle touch devices
            link.addEventListener('touchend', function (e) {
                e.stopPropagation();
                const href = this.getAttribute('href');
                closeNav();
                if (href && href !== '#') {
                    setTimeout(function() {
                        window.location.href = href;
                    }, 50); // small delay to let menu close animation start
                }
            });

            // Handle mouse/desktop click
            link.addEventListener('click', function (e) {
                closeNav();
                // Let default navigation happen naturally
            });
        });
    }

    // ----------------------------------------------------
    // Modal Logic
    // ----------------------------------------------------
    const addBtn   = document.getElementById('add-complaint-btn');
    const modal    = document.getElementById('complaint-modal');
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
    if (status === 'Resolved')    return 'badge-resolved';
    if (status === 'In Progress') return 'badge-progress';
    return 'badge-pending';
}
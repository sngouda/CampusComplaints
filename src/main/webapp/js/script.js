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
        if (hamburger) {
            hamburger.setAttribute('aria-expanded', 'true');
            hamburger.classList.add('open');
        }
    }

    function closeNav() {
        if (!mainNav) return;
        mainNav.classList.remove('open');
        if (navOverlay) navOverlay.classList.remove('open');
        if (hamburger) {
            hamburger.setAttribute('aria-expanded', 'false');
            hamburger.classList.remove('open');
        }
    }

    if (hamburger) {
        hamburger.addEventListener('click', function (e) {
            e.stopPropagation();
            mainNav && mainNav.classList.contains('open') ? closeNav() : openNav();
        });
    }

    // Close nav when clicking the overlay
    if (navOverlay) {
        navOverlay.addEventListener('click', function(e) {
            closeNav();
        });
    }

    // Close nav when clicking anywhere outside the nav and hamburger
    document.addEventListener('click', function(e) {
        if (!mainNav) return;
        if (!mainNav.contains(e.target) && !hamburger.contains(e.target)) {
            closeNav();
        }
    });

    // ✅ THE REAL FIX: inline onclick on each link — bypasses all event blocking
    if (mainNav) {
        mainNav.querySelectorAll('a').forEach(function(link) {
            const href = link.getAttribute('href');
            if (href && href !== '#') {
                // Set onclick directly on the element — most reliable on mobile
                link.setAttribute('onclick', "window.location.href='" + href + "'; return false;");
            }
        });
    }

    // ----------------------------------------------------
    // Modal Logic
    // ----------------------------------------------------
    const addBtn   = document.getElementById('add-complaint-btn');
    const modal    = document.getElementById('complaint-modal');
    const closeBtn = document.querySelector('.close-btn');

    if (addBtn && modal && closeBtn) {
        addBtn.addEventListener('click', () => modal.classList.add('active'));
        closeBtn.addEventListener('click', () => modal.classList.remove('active'));
        window.addEventListener('click', (e) => {
            if (e.target === modal) modal.classList.remove('active');
        });
    }
});

function showError(msg) {
    const errorDiv = document.getElementById('error-alert');
    if (errorDiv) {
        errorDiv.textContent = msg;
        errorDiv.style.display = 'block';
        setTimeout(() => errorDiv.style.display = 'none', 3000);
    }
}

function showSuccess(msg) {
    const successDiv = document.getElementById('success-alert');
    if (successDiv) {
        successDiv.textContent = msg;
        successDiv.style.display = 'block';
        setTimeout(() => successDiv.style.display = 'none', 3000);
    }
}

function getBadgeClass(status) {
    if (status === 'Resolved')    return 'badge-resolved';
    if (status === 'In Progress') return 'badge-progress';
    return 'badge-pending';
}
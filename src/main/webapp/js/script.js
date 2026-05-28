// Common Utility Functions
document.addEventListener('DOMContentLoaded', function() {

    // URL Parameter alerts
    var params     = new URLSearchParams(window.location.search);
    var errorMsg   = params.get('error');
    var successMsg = params.get('msg');
    if (errorMsg)   showError(errorMsg);
    if (successMsg) showSuccess(successMsg);

    // Hamburger / Mobile Drawer
    var hamburger    = document.getElementById('hamburger');
    var mobileDrawer = document.getElementById('main-nav');
    var navOverlay   = document.getElementById('nav-overlay');

    function openNav() {
        if (mobileDrawer) mobileDrawer.classList.add('open');
        if (navOverlay)   navOverlay.classList.add('open');
        if (hamburger)    hamburger.classList.add('open');
    }

    function closeNav() {
        if (mobileDrawer) mobileDrawer.classList.remove('open');
        if (navOverlay)   navOverlay.classList.remove('open');
        if (hamburger)    hamburger.classList.remove('open');
    }

    if (hamburger) {
        hamburger.addEventListener('click', function(e) {
            e.stopPropagation();
            if (mobileDrawer && mobileDrawer.classList.contains('open')) {
                closeNav();
            } else {
                openNav();
            }
        });
    }

    if (navOverlay) {
        navOverlay.addEventListener('click', closeNav);
    }

    // Modal Logic
    var addBtn   = document.getElementById('add-complaint-btn');
    var modal    = document.getElementById('complaint-modal');
    var closeBtn = document.querySelector('.close-btn');

    if (addBtn && modal && closeBtn) {
        addBtn.addEventListener('click', function() {
            modal.classList.add('active');
        });
        closeBtn.addEventListener('click', function() {
            modal.classList.remove('active');
        });
        window.addEventListener('click', function(e) {
            if (e.target === modal) modal.classList.remove('active');
        });
    }
});

function showError(msg) {
    var el = document.getElementById('error-alert');
    if (el) { el.textContent = msg; el.style.display = 'block'; setTimeout(function(){ el.style.display = 'none'; }, 3000); }
}

function showSuccess(msg) {
    var el = document.getElementById('success-alert');
    if (el) { el.textContent = msg; el.style.display = 'block'; setTimeout(function(){ el.style.display = 'none'; }, 3000); }
}

function getBadgeClass(status) {
    if (status === 'Resolved')    return 'badge-resolved';
    if (status === 'In Progress') return 'badge-progress';
    return 'badge-pending';
}

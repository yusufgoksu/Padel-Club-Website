import { router } from '/static-content/router.js';

document.addEventListener('DOMContentLoaded', () => {
    document.body.addEventListener('click', function(e) {
        const target = e.target.closest('a[data-link]');
        if (target) {
            e.preventDefault();
            history.pushState(null, '', target.getAttribute('href'));
            router();
        }
    });

    window.addEventListener('popstate', router);
    router();
});

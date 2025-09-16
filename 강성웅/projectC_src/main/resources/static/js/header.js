(function initHeader() {
    const ready = (fn) =>
        (document.readyState === 'loading')
            ? document.addEventListener('DOMContentLoaded', fn, { once: true })
            : fn();

    ready(() => {
        const bell = document.getElementById('notificationBtn');
        const logoutLink = document.getElementById('logoutLink');

        // 알림 드롭다운 토글
        if (bell) {
            bell.addEventListener('click', (e) => {
                e.stopPropagation();
                bell.classList.toggle('active'); // .notification.active .dropdown {display:block;}
            });
            // 바깥 클릭 시 닫기
            document.addEventListener('click', () => bell.classList.remove('active'));
        }

        // 로그아웃
        if (logoutLink) {
            logoutLink.addEventListener('click', (e) => {
                e.preventDefault();
                document.getElementById('logoutForm')?.submit();
            });
        }
    });
})();
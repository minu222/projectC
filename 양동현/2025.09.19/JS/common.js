document.addEventListener("DOMContentLoaded", () => {
    /** ==============================
     *  알림 드롭다운
     * ============================== */
    const notification = document.querySelector(".notification");
    if (notification) {
        notification.addEventListener("click", (e) => {
            e.stopPropagation();
            notification.classList.toggle("active");
        });

        // 바깥 클릭 시 닫기
        document.addEventListener("click", () => {
            notification.classList.remove("active");
        });
    }

    /** ==============================
     *  사이드바 메뉴 활성화 표시
     * ============================== */
    const sidebarLinks = document.querySelectorAll(".side-menu ul li a");
    if (sidebarLinks.length > 0) {
        sidebarLinks.forEach(link => {
            link.addEventListener("click", () => {
                sidebarLinks.forEach(l => l.classList.remove("active"));
                link.classList.add("active");
            });
        });
    }

    /** ==============================
     *  드롭다운 메뉴 (헤더)
     * ============================== */
    const navDropdowns = document.querySelectorAll("nav ul li");
    navDropdowns.forEach(item => {
        item.addEventListener("mouseenter", () => {
            const dropdown = item.querySelector(".dropdown");
            if (dropdown) dropdown.style.display = "block";
        });
        item.addEventListener("mouseleave", () => {
            const dropdown = item.querySelector(".dropdown");
            if (dropdown) dropdown.style.display = "none";
        });
    });

    /** ==============================
     *  (선택) 페이지 전환 시 사이드메뉴 유지
     *  - 현재 URL 기준으로 활성화 표시
     * ============================== */
    const currentPath = window.location.pathname;
    sidebarLinks.forEach(link => {
        if (link.getAttribute("href") === currentPath) {
            link.classList.add("active");
        }
    });
});

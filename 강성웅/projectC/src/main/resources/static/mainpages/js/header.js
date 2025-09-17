document.addEventListener("DOMContentLoaded", () => {
    // 로그인 여부 (테스트용)
    const isLoggedIn = true; // 로그인: true / 로그아웃: false

    const notificationBtn = document.getElementById("notificationBtn");
    const loginBtn = document.getElementById("loginBtn");
    const signupBtn = document.getElementById("signupBtn");

    // 로그인 상태에 따라 표시/숨김
    if (isLoggedIn) {
        if (notificationBtn) notificationBtn.style.display = "inline-block";
        if (loginBtn) loginBtn.style.display = "none";
        if (signupBtn) signupBtn.style.display = "none";
    } else {
        if (notificationBtn) notificationBtn.style.display = "none";
        if (loginBtn) loginBtn.style.display = "inline";
        if (signupBtn) signupBtn.style.display = "inline";
    }

    // 알림 버튼 클릭 시 드롭다운 열기/닫기
    if (notificationBtn) {
        notificationBtn.addEventListener("click", function(event) {
            event.stopPropagation();
            this.classList.toggle("active");
        });
    }

    // 바깥 클릭 시 드롭다운 닫기
    document.addEventListener("click", function() {
        if (notificationBtn) notificationBtn.classList.remove("active");
    });
});

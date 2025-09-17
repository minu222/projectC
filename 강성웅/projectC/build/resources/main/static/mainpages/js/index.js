// JS로 외부 html 불러오기 (순수 웹페이지에서 include 효과 주기)
async function loadComponent(id, file) {
    const response = await fetch(file);
    const text = await response.text();
    document.getElementById(id).innerHTML = text;
}

loadComponent("header", "header.html"); // 헤더 고정값 사용
loadComponent("push", "push.html"); // 고정




// 사이드바 클릭시 표현 ( sidebar 네임 변경해서 진행 )

document.getElementById('sidebar').addEventListener('click', function(e){
    if(e.target.tagName === 'A'){
        e.preventDefault();
        const menuLinks = document.querySelectorAll('#sidebar .side-menu ul li a');
        menuLinks.forEach(l => l.classList.remove('active'));
        e.target.classList.add('active');
    }
});
// 알림 로직 //
async function loadComponent(id, file, callback) {
    const response = await fetch(file);
    const text = await response.text();
    document.getElementById(id).innerHTML = text;
    if (callback) callback(); // 로드 완료 후 콜백 실행
}

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






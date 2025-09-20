// ==========================
// 사용자 상태 (서버에서 내려줄 값)
// ==========================
// 타임리프에서 내려줄 값 예시:
// <script>const userType = [[${session.user != null ? session.user.role : 'guest'}]];</script>
let userType = window.userType || "guest";  // guest, student, teacher

// ==========================
// 사이드바 데이터 정의
// ==========================
const sideMenu = document.getElementById("sideMenu");

const sidebarData = {
    course: [
        { title: "교육 강의", items: [{ text: "강의목록", href: "#" }] },
        { title: "과정목록", items: [{ text: "이과", href: "#" }, { text: "문과", href: "#" }, { text: "예체능", href: "#" }] }
    ],
    info: [
        {
            title: "정보센터",
            items: [
                { text: "강사소개", href: "#" },
                { text: "도서추천", href: "#" },
                { text: "시험 일정", href: "#" },
                { text: "자료실", href: "#" }
            ]
        }
    ],
    board: [
        {
            title: "게시판",
            items: [
                { text: "공지사항", href: "#" },
                { text: "FAQ", href: "#" },
                { text: "자유게시판", href: "#" },
                { text: "문의 게시판", href: "#" },
                { text: "수강후기", href: "#" },
                { text: "강사후기", href: "#" }
            ]
        }
    ],
    myclass: []
};

// ==========================
// 사용자 타입에 따른 "나의 강의실" 메뉴 구성
// ==========================
if (userType === "student") {
    sidebarData.myclass = [
        {
            title: "나의 강의실",
            items: [
                { text: "수강목록", href: "#" },
                { text: "시험목록", href: "#" },
                { text: "결제내역", href: "#" },
                { text: "이메일/쪽지 정보", href: "#" },
                { text: "나의 게시판", href: "#" },
                { text: "회원정보 수정", href: "#" }
            ]
        }
    ];
} else if (userType === "teacher") {
    sidebarData.myclass = [
        {
            title: "강사 관리",
            items: [
                { text: "강의실 목록", href: "#" },
                { text: "강의실 등록", href: "#" },
                { text: "결제내역", href: "#" },
                { text: "이메일/쪽지 정보", href: "#" },
                { text: "나의 게시판", href: "#" },
                { text: "회원정보 수정", href: "#" }
            ]
        }
    ];
}

// ==========================
// 사이드바 렌더링 함수
// ==========================
function renderSidebar(category) {
    if (!sideMenu) return;
    sideMenu.innerHTML = "";
    if (!sidebarData[category]) return;

    sidebarData[category].forEach(sec => {
        const ul = document.createElement("ul");
        if (sec.title) {
            const h3 = document.createElement("h3");
            h3.textContent = sec.title;
            ul.appendChild(h3);
        }
        sec.items.forEach(item => {
            const li = document.createElement("li");
            const a = document.createElement("a");
            a.href = item.href;
            a.textContent = item.text;
            li.appendChild(a);
            ul.appendChild(li);
        });
        sideMenu.appendChild(ul);
    });

    sideMenu.classList.add("active");
}

// ==========================
// 헤더 메뉴 클릭 이벤트
// ==========================
document.querySelectorAll(".header-category").forEach(link => {
    link.addEventListener("click", e => {
        e.preventDefault();
        const category = link.dataset.category;
        renderSidebar(category);
        const mainContent = document.getElementById("mainContent");
        if (mainContent) {
            mainContent.innerHTML = `<h2>${category} 페이지</h2><p>콘텐츠 영역</p>`;
        }
    });
});

// ==========================
// 알림 드롭다운 제어
// ==========================
const notificationBtn = document.getElementById("notificationBtn");
if (notificationBtn) {
    notificationBtn.addEventListener("click", e => {
        e.stopPropagation();
        notificationBtn.classList.toggle("active");
    });

    document.addEventListener("click", () => {
        notificationBtn.classList.remove("active");
    });
}

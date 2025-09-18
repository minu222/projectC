const userType = "student"; // null=비회원, "student", "teacher"
const sideMenu = document.getElementById("sideMenu");

const sidebarData = {
    course: [
        {title: "교육 강의", items: [{text: "강의목록", href: "#"}]},
        {title: "과정목록", items: [{text: "이과", href: "#"}, {text: "문과", href: "#"}, {text: "예체능", href: "#"}]}
    ],
    info: [
        {title: "정보센터",
            items: [{text: "강사소개", href: "#"}, {text: "도서추천", href: "#"}, {text: "시험 일정", href: "#"}, {
                text: "자료실",
                href: "#"
            }]
        }
    ],
    board: [
        {title: "게시판",
            items: [{text: "공지사항", href: "#"}, {text: "FAQ", href: "#"}, {
                text: "자유게시판",
                href: "#"
            }, {text: "문의 게시판", href: "#"}, {text: "수강후기", href: "#"}, {text: "강사후기", href: "#"}]
        }
    ],
    myclass: []
};

if (userType === null || userType === "student") {
    sidebarData.myclass = [
        {title: "나의 강의실",
            items: [{text: "수강목록", href: "#"}, {text: "시험목록", href: "#"}, {
                text: "결제내역",
                href: "#"
            }, {text: "이메일/쪽지 정보", href: "#"}, {text: "나의 게시판", href: "#"}, {text: "회원정보 수정", href: "#"}]
        }
    ];
} else if (userType === "teacher") {
    sidebarData.myclass = [
        {title: "강사 관리",
            items: [{text: "강의실 목록", href: "#"}, {text: "강의실 등록", href: "#"}, {
                text: "결제내역",
                href: "#"
            }, {text: "이메일/쪽지 정보", href: "#"}, {text: "나의 게시판", href: "#"}, {text: "회원정보 수정", href: "#"}]
        }
    ];
}

function renderSidebar(category) {
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
    sideMenu.classList.add("active"); // 사이드바 표시
}

document.querySelectorAll(".header-category").forEach(link => {
    link.addEventListener("click", e => {
        e.preventDefault();
        const category = link.dataset.category;
        renderSidebar(category);
        document.getElementById("mainContent").innerHTML = `<h2>${category} 페이지</h2><p>콘텐츠 영역</p>`;
    });
});

const notificationBtn = document.getElementById("notificationBtn");
if (notificationBtn) {
    notificationBtn.addEventListener("click", e => {
        e.stopPropagation();
        notificationBtn.classList.toggle("active");
    });
}
document.addEventListener("click", () => {
    if (notificationBtn) notificationBtn.classList.remove("active");
});
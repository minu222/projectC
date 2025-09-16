document.addEventListener("DOMContentLoaded", function () {
    // 샘플 데이터
    const courses = [
        { id: 1, title: "자바스크립트 기초", teacher: "홍길동", period: "2025-01-01 ~ 2025-03-01", score: 80, progress: 50, status: "progress", purchaseDate: "2025-01-01", lastAccess: "2025-02-01" },
        { id: 2, title: "파이썬 데이터 분석", teacher: "이몽룡", period: "2025-01-15 ~ 2025-03-15", score: 90, progress: 100, status: "completed", purchaseDate: "2025-01-15", lastAccess: "2025-03-01" },
        { id: 3, title: "HTML & CSS", teacher: "성춘향", period: "2025-02-01 ~ 2025-04-01", score: 70, progress: 30, status: "progress", purchaseDate: "2025-02-01", lastAccess: "2025-02-20" },
        { id: 4, title: "Spring Boot 웹개발", teacher: "변학도", period: "2025-02-10 ~ 2025-04-10", score: 60, progress: 10, status: "progress", purchaseDate: "2025-02-10", lastAccess: "2025-02-15" },
        { id: 5, title: "React 심화", teacher: "홍길동", period: "2025-02-20 ~ 2025-04-20", score: 95, progress: 100, status: "completed", purchaseDate: "2025-02-20", lastAccess: "2025-04-20" },
        { id: 6, title: "Node.js 서버 개발", teacher: "이몽룡", period: "2025-03-01 ~ 2025-05-01", score: 85, progress: 60, status: "progress", purchaseDate: "2025-03-01", lastAccess: "2025-03-10" },
        { id: 7, title: "데이터베이스 기초", teacher: "성춘향", period: "2025-03-05 ~ 2025-05-05", score: 50, progress: 0, status: "progress", purchaseDate: "2025-03-05", lastAccess: "2025-03-06" },
        { id: 8, title: "AI 머신러닝", teacher: "변학도", period: "2025-03-10 ~ 2025-05-10", score: 77, progress: 45, status: "progress", purchaseDate: "2025-03-10", lastAccess: "2025-03-15" },
        { id: 9, title: "C언어 프로그래밍", teacher: "홍길동", period: "2025-03-15 ~ 2025-05-15", score: 66, progress: 70, status: "progress", purchaseDate: "2025-03-15", lastAccess: "2025-03-20" },
        { id: 10, title: "Kotlin 안드로이드", teacher: "이몽룡", period: "2025-03-20 ~ 2025-05-20", score: 88, progress: 20, status: "progress", purchaseDate: "2025-03-20", lastAccess: "2025-03-25" },
    ];

    const perPage = 9;
    let currentPage = 1;

    const courseGrid = document.getElementById("courseGrid");
    const pagination = document.getElementById("pagination");
    const filterSelect = document.getElementById("filter");
    const searchInput = document.getElementById("searchInput");
    const searchBtn = document.getElementById("searchBtn");

    function getFilteredCourses() {
        let result = [...courses];

        // 검색 적용
        const keyword = searchInput ? searchInput.value.trim().toLowerCase() : "";
        if (keyword) {
            result = result.filter(c =>
                c.title.toLowerCase().includes(keyword) || c.teacher.toLowerCase().includes(keyword)
            );
        }

        // 필터 적용
        if (filterSelect) {
            const filter = filterSelect.value;
            if (filter === "progress") {
                result = result.filter(c => c.status === "progress");
            } else if (filter === "completed") {
                result = result.filter(c => c.status === "completed");
            } else if (filter === "purchase") {
                result.sort((a, b) => new Date(b.purchaseDate) - new Date(a.purchaseDate)); // 최신 구매순
            } else if (filter === "recent") {
                result.sort((a, b) => new Date(b.lastAccess) - new Date(a.lastAccess)); // 최근 수강순
            }
        }

        return result;
    }

    function renderCourses() {
        if (!courseGrid) return;

        const filteredCourses = getFilteredCourses();

        courseGrid.innerHTML = "";

        const start = (currentPage - 1) * perPage;
        const end = start + perPage;
        const pageCourses = filteredCourses.slice(start, end);

        if (pageCourses.length === 0) {
            courseGrid.innerHTML = "<p>검색 결과가 없습니다.</p>";
            pagination.innerHTML = "";
            return;
        }

        pageCourses.forEach(c => {
            const card = document.createElement("div");
            card.className = "course-card";
            card.innerHTML = `
              <img src="https://picsum.photos/300/150?random=${c.id}" alt="강의 썸네일">
              <h3>${c.title}</h3>
              <p>강사: ${c.teacher}</p>
              <p>학습기간: ${c.period}</p>
              <p>시험: ${c.score} 점</p>
              <p>진도율: ${c.progress}%</p>
              <div class="actions">
                <button>강의실 입장</button>
                <button>시험보기</button>
                <button>후기 작성</button>
              </div>
            `;
            courseGrid.appendChild(card);
        });

        renderPagination(filteredCourses.length);
    }

    function renderPagination(totalItems) {
        if (!pagination) return;

        pagination.innerHTML = "";
        const totalPages = Math.ceil(totalItems / perPage);

        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement("button");
            btn.textContent = i;
            if (i === currentPage) btn.classList.add("active");
            btn.onclick = () => { currentPage = i; renderCourses(); };
            pagination.appendChild(btn);
        }
    }

    function applyFilter() {
        currentPage = 1;
        renderCourses();
    }

    function applySearch() {
        currentPage = 1;
        renderCourses();
    }

    // 안전한 이벤트 바인딩
    if (filterSelect) filterSelect.addEventListener("change", applyFilter);
    if (searchBtn) searchBtn.addEventListener("click", applySearch);
    if (searchInput) {
        searchInput.addEventListener("keypress", (e) => {
            if (e.key === "Enter") applySearch();
        });
    }

    renderCourses();
});

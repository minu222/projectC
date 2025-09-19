// 강의 데이터
let courses = [
    {no: 1, name: "VOD 기초 강의", period: "2025-01-01 ~ 2025-01-31", type: "VOD", status: "정상"},
    {no: 2, name: "개인 영어 회화", period: "2025-02-01 ~ 2025-02-28", type: "개인강의", status: "정상"},
    {no: 3, name: "다수강의 수학반", period: "2025-03-01 ~ 2025-03-31", type: "다수강의", status: "중지"}
];

// 학생 데이터
let students = {
    1: Array.from({length: 25}, (_, i) => ({
        photo: "https://via.placeholder.com/50",
        name: "학생" + (i + 1),
        username: "user" + (i + 1),
        gender: i % 2 ? "남" : "여",
        email: "user" + (i + 1) + "@example.com",
        age: 20 + i % 5,
        intro: "학생 소개 " + (i + 1)
    })),
    2: [{
        photo: "https://via.placeholder.com/50",
        name: "최지은",
        username: "choi789",
        gender: "여",
        email: "choi@example.com",
        age: 22,
        intro: "영어 회화 집중 수강생"
    }],
    3: [{
        photo: "https://via.placeholder.com/50",
        name: "홍길동",
        username: "hong321",
        gender: "남",
        email: "hong@example.com",
        age: 23,
        intro: "수학 다수강 수강생"
    }]
};

const courseGrid = document.getElementById("courseGrid");
const studentModal = document.getElementById("studentModal");
const studentList = document.getElementById("studentList");
const studentPagination = document.getElementById("studentPagination");
const modalTitle = document.getElementById("modalTitle");
const editModal = document.getElementById("editModal");
const editName = document.getElementById("editName");
const editPeriod = document.getElementById("editPeriod");
const editType = document.getElementById("editType");
const editStatus = document.getElementById("editStatus");
const editForm = document.getElementById("editForm");
const searchBox = document.getElementById("searchBox");
const searchBtn = document.getElementById("searchBtn");
const statusFilter = document.getElementById("statusFilter");

const rowsPerPage = 6;
let currentPage = 1;
let currentStudentPage = 1;
const studentsPerPage = 10;
let currentStudentList = [];
let editingCourseNo = null;

function filterCourses() {
    const search = searchBox.value.toLowerCase();
    const status = statusFilter.value;
    return courses.filter(c => c.name.toLowerCase().includes(search) && (status === "전체" || c.status === status));
}

function renderCourses() {
    courseGrid.innerHTML = "";
    const filtered = filterCourses();
    const start = (currentPage - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    filtered.slice(start, end).forEach(c => {
        const card = document.createElement("div");
        card.className = "course-card";
        card.innerHTML = `
            <h3>${c.name}</h3>
            <p>기간: ${c.period}</p>
            <p>유형: ${c.type}</p>
            <p>상태: ${c.status}</p>
            <div class="actions">
                <button class="edit-btn" data-no="${c.no}">수정</button>
                <button>삭제</button>
                <button class="student-btn" data-no="${c.no}">학생 리스트</button>
            </div>
        `;
        courseGrid.appendChild(card);
    });

    document.querySelectorAll(".student-btn").forEach(btn => {
        btn.addEventListener("click", e => {
            const no = e.target.getAttribute("data-no");
            currentStudentList = students[no] || [];
            currentStudentPage = 1;
            showStudentModal(no);
        });
    });

    document.querySelectorAll(".edit-btn").forEach(btn => {
        btn.addEventListener("click", e => {
            const no = e.target.getAttribute("data-no");
            startEditCourse(no);
        });
    });
}

function showStudentModal(no) {
    studentList.innerHTML = "";
    modalTitle.textContent = courses.find(c => c.no == no).name + " 학생 목록";
    renderStudentPage();
    studentModal.style.display = "flex";
}

function renderStudentPage() {
    studentList.innerHTML = "";
    const start = (currentStudentPage - 1) * studentsPerPage;
    const end = start + studentsPerPage;
    const pageItems = currentStudentList.slice(start, end);
    if (pageItems.length) {
        pageItems.forEach(s => {
            const li = document.createElement("li");
            li.innerHTML = `
                <div class="student-card">
                    <img src="${s.photo}">
                    <div class="student-info">
                        <strong>${s.name} (${s.username})</strong>
                        성별: ${s.gender} <br>
                        나이: ${s.age}<br>
                        이메일: ${s.email}<br>
                        소개: ${s.intro}
                    </div>
                </div>
            `;
            studentList.appendChild(li);
        });
    } else {
        studentList.innerHTML = "<li>학생이 없습니다.</li>";
    }

    studentPagination.innerHTML = "";
    const pageCount = Math.ceil(currentStudentList.length / studentsPerPage);
    for (let i = 1; i <= pageCount; i++) {
        const btn = document.createElement("button");
        btn.textContent = i;
        if (i === currentStudentPage) btn.classList.add("active");
        btn.addEventListener("click", () => {
            currentStudentPage = i;
            renderStudentPage();
        });
        studentPagination.appendChild(btn);
    }
}

// 강의 수정
function startEditCourse(no) {
    const course = courses.find(c => c.no == no);
    editingCourseNo = no;
    editName.value = course.name;
    editPeriod.value = course.period;
    editType.value = course.type;
    editStatus.value = course.status;
    editModal.style.display = "flex";
}

editForm.addEventListener("submit", e => {
    e.preventDefault();
    const course = courses.find(c => c.no == editingCourseNo);
    course.name = editName.value;
    course.period = editPeriod.value;
    course.type = editType.value;
    course.status = editStatus.value;
    editModal.style.display = "none";
    renderCourses();
});

// 이벤트
searchBtn.addEventListener("click", () => {
    currentPage = 1;
    renderCourses();
});
statusFilter.addEventListener("change", () => {
    currentPage = 1;
    renderCourses();
});

renderCourses();
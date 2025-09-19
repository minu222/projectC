document.addEventListener("DOMContentLoaded", () => {
    // 성별 버튼 선택
    document.querySelectorAll('.gender-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.gender-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
        });
    });

    // 회원 구분 버튼 선택
    const roleBtns = document.querySelectorAll('.role-btn');
    const teacherFields = document.getElementById('teacherFields');
    const roleInput = document.getElementById('role');

    roleBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            roleBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            const value = btn.textContent.trim() === '강사' ? 'teacher' : 'student';
            roleInput.value = value;

            teacherFields.style.display = (value === 'teacher') ? 'block' : 'none';
        });
    });

    // 페이지 로드 시 기본값 '회원' 활성화
    const defaultRole = document.querySelector('.role-btn.active') || roleBtns[0];
    if (defaultRole) defaultRole.classList.add('active');

    // 경력 추가
    const addCareerBtn = document.querySelector(".add-career-btn");
    const careerList = document.getElementById("careerList");

    addCareerBtn.addEventListener("click", () => {
        const div = document.createElement("div");
        div.classList.add("career-item");
        div.innerHTML = `
            <input type="text" name="career[]" placeholder="경력 입력">
            <button type="button" class="remove-btn">삭제</button>
        `;
        careerList.appendChild(div);

        div.querySelector(".remove-btn").addEventListener("click", () => div.remove());
    });
});

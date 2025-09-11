const form = document.getElementById("idFindForm");
const modal = document.getElementById("resultModal");
const resultText = document.getElementById("resultText");

form.addEventListener("submit", function(e) {
    e.preventDefault();

    // 실제 구현에서는 서버에서 아이디 조회 후 결과를 받아야 함
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;

    // 예시 출력
    resultText.innerHTML = `${name}님, 입력하신 이메일(${email})로 가입된 아이디는 <strong>sample123</strong> 입니다.`;

    modal.style.display = "flex";
});

function closeModal() {
    modal.style.display = "none";
}

// 모달 밖 클릭 시 닫기
window.onclick = function(event) {
    if (event.target === modal) {
        modal.style.display = "none";
    }
}
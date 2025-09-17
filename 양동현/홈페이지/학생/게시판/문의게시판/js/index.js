// JS로 외부 html 불러오기 (순수 웹페이지에서 include 효과 주기)
async function loadComponent(id, file) {
    const response = await fetch(file);
    const text = await response.text();
    document.getElementById(id).innerHTML = text;
}

loadComponent("header", "header.html"); // 헤더 고정값 사용
loadComponent("sidebar", "sidebar.html");  //사이드 바 내용변경으로 사용 (파일이름변경)
loadComponent("main", "main.html"); // 내용변경 (파일이름변경)
loadComponent("footer", "footer.html"); // 고정

// 사이드바 클릭시 표현 ( sidebar 네임 변경해서 진행 )

document.getElementById('sidebar').addEventListener('click', function (e) {
    if (e.target.tagName === 'A') {
        e.preventDefault();
        const menuLinks = document.querySelectorAll('#sidebar .side-menu ul li a');
        menuLinks.forEach(l => l.classList.remove('active'));
        e.target.classList.add('active');
    }
});




// main 댓글 기능 임시 구현 백엔드에서 다시구현해주세요


function addComment() {
    const input = document.getElementById('comment-input');
    const content = input.value.trim();
    if (!content) return alert('댓글 내용을 입력하세요.');

    const commentList = document.getElementById('comment-list');

    const newComment = document.createElement('div');
    newComment.className = 'comment-item';
    newComment.innerHTML = `
            <div class="author">사용자</div>
            <div class="date">${new Date().toLocaleString()}</div>
            <div class="content">${content}</div>
        `;
    commentList.appendChild(newComment);
    input.value = '';
}
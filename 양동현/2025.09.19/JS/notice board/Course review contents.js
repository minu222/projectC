
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
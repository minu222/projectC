const video = document.getElementById('video');
const videoContainer = document.getElementById('videoContainer');
const chat = document.getElementById('chat');
const chatToggle = document.getElementById('chatToggle');
const chatInput = document.getElementById('chatInput');
const sendBtn = document.getElementById('sendBtn');
const expandVideoBtn = document.getElementById('expandVideoBtn');
const fullscreenBtn = document.getElementById('fullscreenBtn');



// 채팅 접기/펼치기
chatToggle.addEventListener('click', () => {
    chat.classList.toggle('collapsed');
    chatToggle.textContent = chat.classList.contains('collapsed') ? '[+]' : '[-]';
});

// 채팅 전송
function addMessage(text) {
    if(!text) return;
    const msg = document.createElement('div');
    msg.className = 'message';
    msg.textContent = text;
    chat.appendChild(msg);
    chat.scrollTop = chat.scrollHeight; // 자동 스크롤
    chatInput.value = '';
}

// 버튼 클릭
sendBtn.addEventListener('click', () => addMessage(chatInput.value));

// 엔터 입력
chatInput.addEventListener('keydown', (e) => {
    if(e.key === 'Enter') addMessage(chatInput.value);
});

// 영상 전체화면 토글
function toggleFullscreen() {
    if (!document.fullscreenElement) {
        videoContainer.requestFullscreen().catch(err => {
            alert(`전체화면 요청 실패: ${err.message}`);
        });
    } else {
        document.exitFullscreen();
    }
}

// 버튼 클릭으로 전체화면
fullscreenBtn.addEventListener('click', toggleFullscreen);

// 더블클릭으로 전체화면
video.addEventListener('dblclick', toggleFullscreen);

// ESC 키 눌러 전체화면 종료
document.addEventListener('keydown', (e) => {
    if(e.key === 'Escape' && document.fullscreenElement) {
        document.exitFullscreen();
    }
});
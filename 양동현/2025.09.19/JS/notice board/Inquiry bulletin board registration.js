
// main 댓글 기능 임시 구현 백엔드에서 다시구현해주세요


function execCmd(command) {
    document.execCommand(command, false, null);
}

// 폰트 이름 적용
document.getElementById('fontName').addEventListener('change', function() {
    document.execCommand("fontName", false, this.value);
});

// 폰트 크기 적용
document.getElementById('fontSize').addEventListener('change', function() {
    document.execCommand("fontSize", false, "7"); // execCommand는 1~7 사이 크기
    // 실제 px 적용
    const editor = document.getElementById('editor');
    editor.querySelectorAll("font[size='7']").forEach(function(el) {
        el.removeAttribute("size");
        el.style.fontSize = document.getElementById('fontSize').value;
    });
});
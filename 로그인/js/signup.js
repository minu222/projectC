// 성별 버튼 선택 효과
document.querySelectorAll('.gender-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.gender-btn').forEach(b => b.classList.remove('active'));
        this.classList.add('active');
    });
});
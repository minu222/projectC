// 모달 열기
const detailBtns = document.querySelectorAll('.detailBtn');
const modal = document.getElementById('modal');
const modalClose = document.getElementById('modalClose');

detailBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        modal.style.display = 'flex';
    });
});

// 모달 닫기
modalClose.addEventListener('click', () => {
    modal.style.display = 'none';
});

// 모달 외부 클릭 시 닫기
window.addEventListener('click', (e) => {
    if(e.target === modal) {
        modal.style.display = 'none';
    }
});
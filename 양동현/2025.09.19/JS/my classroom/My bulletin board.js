document.addEventListener('DOMContentLoaded', () => {
    const tbody = document.getElementById('board-list');
    const selectAllCheckbox = document.getElementById('selectAll');
    const deleteSelectedBtn = document.getElementById('deleteSelectedBtn');

    // ------------------ 전체 선택 (현재 화면에 보이는 행만) ------------------
    selectAllCheckbox?.addEventListener('change', () => {
        const visibleRows = Array.from(tbody.querySelectorAll('tr')).filter(r => r.style.display !== 'none');
        visibleRows.forEach(r => {
            const cb = r.querySelector('.row-checkbox');
            if(cb) cb.checked = selectAllCheckbox.checked;
        });
    });

    // ------------------ 선택 삭제 (현재 화면에 보이는 행만) ------------------
    deleteSelectedBtn?.addEventListener('click', () => {
        const visibleRows = Array.from(tbody.querySelectorAll('tr')).filter(r => r.style.display !== 'none');
        const checkedRows = visibleRows.filter(r => r.querySelector('.row-checkbox')?.checked);

        if(checkedRows.length === 0) {
            alert("삭제할 게시글을 선택해주세요.");
            return;
        }
        if(confirm(`${checkedRows.length}개의 게시글을 삭제하시겠습니까?`)) {
            checkedRows.forEach(r => r.remove());
        }
        if(selectAllCheckbox) selectAllCheckbox.checked = false;
    });

    // ------------------ 기존 필터, 모달, 페이지네이션 기능 ------------------
    document.body.addEventListener('click', (e) => {
        // 필터 버튼
        if(e.target.classList.contains('filter-btn')) {
            const filterBtns = document.querySelectorAll('.filter-btn');
            filterBtns.forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');

            const filter = e.target.dataset.filter;
            const boardRows = tbody.querySelectorAll('tr');

            if(filter === 'all') {
                boardRows.forEach(r => r.style.display = '');
            } else if(filter === 'newest') {
                Array.from(tbody.children)
                    .sort((a, b) => new Date(b.children[2].textContent) - new Date(a.children[2].textContent))
                    .forEach(tr => tbody.appendChild(tr));
                boardRows.forEach(r => r.style.display = '');
            } else if(filter === 'oldest') {
                Array.from(tbody.children)
                    .sort((a, b) => new Date(a.children[2].textContent) - new Date(b.children[2].textContent))
                    .forEach(tr => tbody.appendChild(tr));
                boardRows.forEach(r => r.style.display = '');
            } else {
                boardRows.forEach(row => row.style.display = row.dataset.type === filter ? '' : 'none');
            }

            // 필터 후 전체선택 체크 초기화
            if(selectAllCheckbox) selectAllCheckbox.checked = false;
        }

        // 게시글 제목 클릭 → 모달 (기존 유지)
        if(e.target.classList.contains('board-title-link')) {
            const modal = document.getElementById('boardModal');
            const modalTitle = document.getElementById('modalTitle');
            const modalContent = document.getElementById('modalContent');
            const saveBtn = document.getElementById('saveModalBtn');
            const closeBtn = document.getElementById('closeModalBtn');

            modalTitle.textContent = e.target.textContent;
            modalContent.value = '기존 글 내용 예제';
            modal.style.display = 'flex';

            saveBtn.onclick = () => {
                alert(`저장 완료: ${modalContent.value}`);
                modal.style.display = 'none';
            };
            closeBtn.onclick = () => { modal.style.display = 'none'; };
        }

        // 페이지네이션 클릭 (기존 유지)
        if(e.target.parentElement && e.target.parentElement.classList.contains('pagination')) {
            const pages = e.target.parentElement.querySelectorAll('button');
            pages.forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');
        }
    });
});

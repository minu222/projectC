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



//main//
function filterTable() {
    const input = document.getElementById('searchInput').value.toLowerCase();
    const role = document.getElementById('roleFilter').value;
    const trs = document.querySelectorAll('#messageTable tbody tr');

    trs.forEach(tr => {
        const title = tr.cells[1].textContent.toLowerCase();
        const sender = tr.cells[2].textContent.toLowerCase();
        const trRole = tr.dataset.role;

        let matchesSearch = title.includes(input) || sender.includes(input);
        let matchesRole = role === "" || trRole === role;

        tr.style.display = (matchesSearch && matchesRole) ? "" : "none";
    });
}

function filterTab(tab) {
    const trs = document.querySelectorAll('#messageTable tbody tr');

    trs.forEach(tr => {
        if (tab === '전체') {
            tr.style.display = "";
        } else {
            tr.style.display = (tr.dataset.direction === tab) ? "" : "none";
        }
    });

    document.querySelectorAll('.tabs button').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
}

function toggleAll(source) {
    const checkboxes = document.querySelectorAll('#messageTable tbody input[type="checkbox"]');
    checkboxes.forEach(cb => cb.checked = source.checked);
}

function deleteRow(button) {
    const row = button.closest('tr');
    row.remove();
}

function deleteSelected() {
    const checkboxes = document.querySelectorAll('#messageTable tbody input[type="checkbox"]');
    checkboxes.forEach(cb => {
        if (cb.checked) {
            cb.closest('tr').remove();
        }
    });
    document.getElementById('selectAll').checked = false;
}





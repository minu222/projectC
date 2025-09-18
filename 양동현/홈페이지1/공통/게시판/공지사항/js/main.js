const notices = [];
for(let i=1; i<=53; i++){
    notices.push({
        id: i,
        title: `공지사항 제목 ${i}`,
        date: `2025-09-${(i%30 + 1).toString().padStart(2,'0')}`,
        link: `notice_detail.html?id=${i}`
    });
}

const rowsPerPage = 10;
let currentPage = 1;

function displayTable(page) {
    currentPage = page;
    const tbody = document.querySelector("#noticeTable tbody");
    tbody.innerHTML = "";

    const start = (page - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    const pageItems = notices.slice(start, end);

    pageItems.forEach(item => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
                <td>${item.id}</td>
                <td><a href="${item.link}">${item.title}</a></td>
                <td>${item.date}</td>
            `;
        tbody.appendChild(tr);
    });

    setupPagination();
}

function setupPagination() {
    const totalPages = Math.ceil(notices.length / rowsPerPage);
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    for(let i=1; i<=totalPages; i++){
        const btn = document.createElement("button");
        btn.innerText = i;
        btn.classList.toggle("active", i === currentPage);
        btn.addEventListener("click", () => displayTable(i));
        pagination.appendChild(btn);
    }
}

displayTable(1);
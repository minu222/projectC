document.addEventListener("DOMContentLoaded", function() {
    const orders = [];
    for(let i=1; i<=25; i++){
        orders.push({
            no:i,
            name:`강의 ${i}`,
            price:`${50 + i}000원`,
            qty:1,
            total:`${50 + i}000원`,
            method:i % 2 === 0 ? "카드" : "계좌이체",
            date:"2025-09-16",
            status:i % 3 === 0 ? "completed" : "pending"
        });
    }

    let currentFilter = "all";
    let currentPage = 1;
    const itemsPerPage = 10;

    function renderTable() {
        const tbody = document.querySelector("#orderTable tbody");
        const paginationDiv = document.getElementById("pagination");
        if (!tbody || !paginationDiv) return;

        const searchInputElem = document.getElementById("searchInput");
        const searchInput = searchInputElem ? searchInputElem.value.toLowerCase() : "";

        // ✅ 필터링 추가
        const filteredOrders = orders.filter(order => {
            const matchesSearch = order.name.toLowerCase().includes(searchInput);
            const matchesFilter = currentFilter === "all" || order.status === currentFilter;
            return matchesSearch && matchesFilter;
        });

        const totalPages = Math.ceil(filteredOrders.length / itemsPerPage);
        if(currentPage > totalPages) currentPage = totalPages || 1;

        tbody.innerHTML = "";
        const startIndex = (currentPage -1) * itemsPerPage;
        const pageOrders = filteredOrders.slice(startIndex, startIndex + itemsPerPage);

        pageOrders.forEach(order => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${order.no}</td>
                <td>${order.name}</td>
                <td>${order.price}</td>
                <td>${order.qty}</td>
                <td>${order.total}</td>
                <td>${order.method}</td>
                <td>${order.date}</td>
                <td><span class="status ${order.status}">${order.status === "pending" ? "주문대기" : "주문완료"}</span></td>
                <td>${order.status === "pending" ? '<button class="buy-btn" onclick="goToPayment(\'' + order.name + '\')">구매하기</button>'
                : '<img src="https://cdn-icons-png.flaticon.com/512/190/190411.png" alt="완료" class="completed-img">'}</td>
            `;
            tbody.appendChild(tr);
        });

        renderPagination(totalPages);
    }

    function renderPagination(totalPages) {
        const paginationDiv = document.getElementById("pagination");
        if (!paginationDiv) return;
        paginationDiv.innerHTML = "";
        for(let i=1; i<=totalPages; i++){
            const btn = document.createElement("button");
            btn.textContent = i;
            if(i === currentPage) btn.classList.add("active");
            btn.onclick = () => {
                currentPage = i;
                renderTable();
            }
            paginationDiv.appendChild(btn);
        }
    }

    window.goToPayment = function(orderName) {
        const modal = document.getElementById('paymentModal');
        if(!modal) return;
        modal.style.display = 'flex';
        document.getElementById('modalCourseName').textContent = `결제 - ${orderName}`;
        document.getElementById('modalCourseNameDetail').textContent = orderName;
    }

    window.closeModal = function() {
        const modal = document.getElementById('paymentModal');
        if(!modal) return;
        modal.style.display = 'none';
        const paymentMethod = document.getElementById('paymentMethod');
        if(paymentMethod) paymentMethod.value = '';
        hideAllPaymentForms();
    }

    window.showPaymentForm = function() {
        hideAllPaymentForms();
        const method = document.getElementById('paymentMethod')?.value;
        if(method) document.getElementById(method + 'Form').style.display = 'block';
    }

    function hideAllPaymentForms() {
        document.querySelectorAll('.payment-form').forEach(form => form.style.display = 'none');
    }

    window.confirmPayment = function() {
        const agree1 = document.getElementById('agreeTerms1')?.checked;
        const agree2 = document.getElementById('agreeTerms2')?.checked;
        if(!agree1 || !agree2){
            alert("약관에 모두 동의해야 결제할 수 있습니다.");
            return;
        }
        alert("결제가 완료되었습니다!");
        closeModal();
    }

    window.setFilter = function(button) {
        currentFilter = button.dataset.filter;
        document.querySelectorAll(".filter-buttons button").forEach(btn => btn.classList.remove("active"));
        button.classList.add("active");
        currentPage = 1;
        renderTable();
    }

    renderTable();

    // 검색 input 키 입력 시 테이블 갱신
    const searchInput = document.getElementById("searchInput");
    if(searchInput) {
        searchInput.addEventListener("keyup", renderTable);
    }
});

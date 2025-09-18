// URL에서 id 가져오기
const params = new URLSearchParams(window.location.search);
const id = params.get('id') || 1; // 기본값 1

// 예시 데이터 (실제 사용시 백엔드 연동)
const notices = [];
for(let i=1; i<=53; i++){
    notices.push({
        id: i,
        title: `공지사항 제목 ${i}`,
        date: `2025-09-${(i%30 + 1).toString().padStart(2,'0')}`,
        content: `공지사항 내용 예시 ${i}번입니다. 상세 내용을 여기에 표시합니다.`
    });
}

// 선택된 id의 데이터 가져오기
const notice = notices.find(n => n.id == id);

if(notice){
    document.getElementById("title").textContent = notice.title;
    document.getElementById("date").textContent = `등록일: ${notice.date}`;
    document.getElementById("content").textContent = notice.content;
}
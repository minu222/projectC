const LIST_ITEMS = 5;
const CARD_ITEMS = 12;

// 예제 데이터: type 필드 추가 (personal/multi/vod)
const lectures = [
    {
        title: 'HTML/CSS 완전정복',
        desc: '웹 페이지 구조와 스타일링 기초~실전',
        instructor: '김현수',
        level: '초급',
        img: 'https://picsum.photos/300/140?random=1',
        tags: ['NEW'],
        date: '2025-09-10',
        type: 'personal',
        link: '#'
    },
    {
        title: 'JavaScript 실무',
        desc: 'JS 문법과 실전 프로젝트 경험',
        instructor: '박민지',
        level: '중급',
        img: 'https://picsum.photos/300/140?random=2',
        tags: ['인기'],
        date: '2025-09-08',
        type: 'multi',
        link: '#'
    },
    {
        title: 'React 시작하기',
        desc: 'SPA 개발 React 기초와 컴포넌트',
        instructor: '이준호',
        level: '중급',
        img: 'https://picsum.photos/300/140?random=3',
        tags: ['NEW'],
        date: '2025-09-12',
        type: 'vod',
        link: '#'
    },
    {
        title: 'Python 데이터 분석',
        desc: 'Pandas/Matplotlib 데이터 분석',
        instructor: '정수아',
        level: '초급',
        img: 'https://picsum.photos/300/140?random=4',
        tags: [],
        date: '2025-09-05',
        type: 'personal',
        link: '#'
    },
    {
        title: 'AI 기초와 머신러닝',
        desc: 'Python 기반 AI/머신러닝 입문',
        instructor: '최동혁',
        level: '중급',
        img: 'https://picsum.photos/300/140?random=5',
        tags: ['인기', 'NEW'],
        date: '2025-09-14',
        type: 'vod',
        link: '#'
    },
    {
        title: 'Node.js 백엔드',
        desc: 'Express/MongoDB 서버 개발 기초',
        instructor: '박민수',
        level: '중급',
        img: 'https://picsum.photos/300/140?random=6',
        tags: [],
        date: '2025-09-02',
        type: 'multi',
        link: '#'
    },
    {
        title: '웹 퍼블리싱',
        desc: 'HTML/CSS/JS 반응형 실습',
        instructor: '정수아',
        level: '초급',
        img: 'https://picsum.photos/300/140?random=7',
        tags: ['NEW'],
        date: '2025-09-11',
        type: 'personal',
        link: '#'
    },
    {
        title: 'AI 심화',
        desc: '머신러닝 모델 실습',
        instructor: '최영희',
        level: '고급',
        img: 'https://picsum.photos/300/140?random=8',
        tags: ['인기'],
        date: '2025-09-01',
        type: 'vod',
        link: '#'
    },
    {
        title: 'React 프로젝트',
        desc: '실전 SPA 프로젝트',
        instructor: '이도현',
        level: '중급',
        img: 'https://picsum.photos/300/140?random=9',
        tags: [],
        date: '2025-09-09',
        type: 'multi',
        link: '#'
    },
    {
        title: 'Python 심화',
        desc: '데이터 분석 심화',
        instructor: '정수아',
        level: '중급',
        img: 'https://picsum.photos/300/140?random=10',
        tags: ['NEW'],
        date: '2025-09-13',
        type: 'personal',
        link: '#'
    }
];

let currentTab = 'personal';

function changeTab(tab) {
    currentTab = tab;
    document.querySelectorAll('.tabs button').forEach(b => b.classList.remove('active'));
    document.querySelector(`.tabs button[onclick="changeTab('${tab}')"]`).classList.add('active');
    renderLectures();
}

function renderLectures() {
    const search = document.getElementById('searchInput').value.toLowerCase();
    const sort = document.getElementById('sortFilter').value;

    let filtered = lectures.filter(l => l.type === currentTab && (l.title.toLowerCase().includes(search) || l.instructor.toLowerCase().includes(search)));

    // 정렬
    if (sort === 'latest') filtered.sort((a, b) => new Date(b.date) - new Date(a.date));
    if (sort === 'oldest') filtered.sort((a, b) => new Date(a.date) - new Date(b.date));
    if (sort === 'popular') filtered.sort((a, b) => (b.tags.includes('인기') ? 1 : 0) - (a.tags.includes('인기') ? 1 : 0));

    // 리스트형
    const listSection = document.getElementById('listSection');
    listSection.innerHTML = '';
    filtered.slice(0, LIST_ITEMS).forEach(lec => {
        const div = document.createElement('div');
        div.className = 'lecture';
        div.innerHTML = `
            <img src="${lec.img}" alt="강의 이미지">
            <div class="lecture-content">
                <h3>${lec.title}</h3>
                <p>${lec.desc}</p>
                <div class="info">강사: ${lec.instructor} | ${lec.level}</div>
                <button onclick="window.location='${lec.link}'">구매하기</button>
            </div>
        `;
        listSection.appendChild(div);
    });

    // 카드형
    const cardSection = document.getElementById('cardSection');
    cardSection.innerHTML = '';
    filtered.slice(0, CARD_ITEMS).forEach(lec => {
        const div = document.createElement('div');
        div.className = 'card';
        div.innerHTML = `
        <img src="${lec.img}" alt="강의 이미지">
        <div class="card-content">
            <h3>${lec.title}</h3>
            <p>${lec.desc}</p>
            ${lec.tags.map(t => `<span class="badge">${t}</span>`).join('')}
            <div class="level">강사: ${lec.instructor}</div>
            <button onclick="window.location='${lec.link}'">구매하기</button>
        </div>
    `;
        div.addEventListener('click', () => window.location = lec.link);
        cardSection.appendChild(div);
    });


// 초기 렌더링
renderLectures();

// 이벤트
document.getElementById('searchInput').addEventListener('input', renderLectures);
document.getElementById('sortFilter').addEventListener('change', renderLectures);

// 보기 전환
function showList() {
    document.getElementById('listSection').style.display = 'flex';
    document.getElementById('cardSection').style.display = 'none';
}

function showCard() {
    document.getElementById('listSection').style.display = 'none';
    document.getElementById('cardSection').style.display = 'grid';
}}

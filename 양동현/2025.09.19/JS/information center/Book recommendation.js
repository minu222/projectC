let isInstructor = true;

const books = [
    {img:'https://via.placeholder.com/200x150', instructor:'강사1', title:'책 제목1', desc:'소개글1'},
    {img:'https://via.placeholder.com/200x150', instructor:'강사2', title:'책 제목2', desc:'소개글2'},
    {img:'https://via.placeholder.com/200x150', instructor:'강사3', title:'책 제목3', desc:'소개글3'},
];

const itemsPerPage = 9;
let currentPage = 1;

// 카드 렌더
function renderCards(filteredBooks = books) {
    const start = (currentPage-1)*itemsPerPage;
    const end = start+itemsPerPage;
    const currentItems = filteredBooks.slice(start,end);

    const grid = document.getElementById('cardGrid');
    grid.innerHTML='';
    currentItems.forEach(book=>{
        const card=document.createElement('div');
        card.className='card';
        card.innerHTML=`
    <img src="${book.img}" alt="${book.title}">
    <div class="card-content">
        <p><strong>책 제목 :</strong> ${book.title}</p>
        <p><strong>강사 :</strong> ${book.instructor}</p>
        <p><strong>소개 :</strong> ${book.desc}</p>
    </div>
`;

        grid.appendChild(card);
    });
}

// 페이징
function renderPagination(filteredBooks = books){
    const totalPages=Math.ceil(filteredBooks.length/itemsPerPage);
    const pagination=document.getElementById('pagination');
    pagination.innerHTML='';
    for(let i=1;i<=totalPages;i++){
        const btn=document.createElement('button');
        btn.textContent=i;
        btn.className=i===currentPage?'active':'';
        btn.addEventListener('click',()=>{
            currentPage=i;
            renderCards(filteredBooks);
            renderPagination(filteredBooks);
        });
        pagination.appendChild(btn);
    }
}

// 검색
document.getElementById('searchBtn').addEventListener('click',()=>{
    const keyword=document.getElementById('searchInput').value.toLowerCase();
    const filtered=books.filter(book=>book.title.toLowerCase().includes(keyword) || book.instructor.toLowerCase().includes(keyword));
    currentPage=1;
    renderCards(filtered);
    renderPagination(filtered);
});

// 모달 열기/닫기
const modalOverlay=document.getElementById('modalOverlay');
const modalClose=document.getElementById('modalClose');
modalClose.addEventListener('click',()=>modalOverlay.style.display='none');

const deleteOverlay=document.getElementById('deleteOverlay');
const deleteClose=document.getElementById('deleteClose');
deleteClose.addEventListener('click',()=>deleteOverlay.style.display='none');

// 등록 버튼
document.getElementById('registerBtn').addEventListener('click',()=>{
    if(!isInstructor){alert('등록 권한이 없습니다.'); return;}
    openModal();
});

// 수정 버튼 (예: 첫 번째 도서 선택)
document.getElementById('editBtn').addEventListener('click',()=>{
    if(!isInstructor){alert('수정 권한이 없습니다.'); return;}
    openModal(books[0],0);
});

// 삭제 버튼
document.getElementById('deleteBtn').addEventListener('click',()=>{
    if(!isInstructor){alert('삭제 권한이 없습니다.'); return;}
    openDeleteModal();
});

function openModal(book=null, idx=null){
    modalOverlay.style.display='flex';
    const instructorInput=document.getElementById('instructorInput');
    const titleInput=document.getElementById('titleInput');
    const descInput=document.getElementById('descInput');
    const imgInput=document.getElementById('imgInput');

    if(book){
        instructorInput.value=book.instructor;
        titleInput.value=book.title;
        descInput.value=book.desc;
    } else {
        instructorInput.value='';
        titleInput.value='';
        descInput.value='';
    }

    document.getElementById('saveBtn').onclick=()=>{
        const reader=new FileReader();
        reader.onload=function(e){
            const imgData=e.target.result;
            if(book){
                books[idx]={instructor:instructorInput.value, title:titleInput.value, desc:descInput.value, img:imgData};
            } else {
                books.push({instructor:instructorInput.value, title:titleInput.value, desc:descInput.value, img:imgData});
            }
            modalOverlay.style.display='none';
            renderCards();
            renderPagination();
        }
        if(imgInput.files[0]){
            reader.readAsDataURL(imgInput.files[0]);
        } else {
            if(book){
                books[idx]={instructor:instructorInput.value, title:titleInput.value, desc:descInput.value, img:book.img};
            } else {
                books.push({instructor:instructorInput.value, title:titleInput.value, desc:descInput.value, img:'https://via.placeholder.com/200x150'});
            }
            modalOverlay.style.display='none';
            renderCards();
            renderPagination();
        }
    }
}

// 삭제 모달
function openDeleteModal(){
    deleteOverlay.style.display='flex';
    const listDiv=document.getElementById('deleteList');
    listDiv.innerHTML='';
    books.forEach((book, idx)=>{
        const label=document.createElement('label');
        label.innerHTML=`<input type="checkbox" value="${idx}"> ${book.title} (${book.instructor})`;
        listDiv.appendChild(label);
    });
}

document.getElementById('deleteConfirmBtn').addEventListener('click',()=>{
    const checked=document.querySelectorAll('#deleteList input[type="checkbox"]:checked');
    const indexes=[...checked].map(c=>parseInt(c.value)).sort((a,b)=>b-a);
    indexes.forEach(i=>books.splice(i,1));
    deleteOverlay.style.display='none';
    renderCards();
    renderPagination();
});

// 초기 렌더
renderCards();
renderPagination();
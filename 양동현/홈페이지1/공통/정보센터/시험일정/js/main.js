const currentUser = "teacher1";
const exams = [
    {course:"JAVA", name:"중간고사", date:"2025-09-03", author:"teacher1"},
    {course:"React", name:"기말고사", date:"2025-09-05", author:"teacher2"},
    {course:"Python", name:"퀴즈", date:"2025-09-03", author:"teacher1"}
];

const calendar = document.getElementById('calendar');
const registerModal = document.getElementById('registerModal');
const editModal = document.getElementById('editModal');
const deleteModal = document.getElementById('deleteModal');

const courseInput = document.getElementById('courseInput');
const examNameInput = document.getElementById('examName');
const examDateInput = document.getElementById('examDate');

let editingIndex = null;

function renderCalendar() {
    calendar.innerHTML = '';
    const monthDays = 30;
    for(let day=1; day<=monthDays; day++){
        const dayDiv = document.createElement('div'); dayDiv.className='day';
        dayDiv.innerHTML = `<h4>${day}일</h4>`;
        exams.filter(e=>new Date(e.date).getDate()===day)
            .forEach(e=>{
                const div = document.createElement('div');
                div.className='exam';
                div.textContent = `${e.course} - ${e.name}`;
                dayDiv.appendChild(div);
            });
        calendar.appendChild(dayDiv);
    }
}

function openModal(modal){ modal.style.display='flex'; }
function closeModal(modal){ modal.style.display='none'; }

document.getElementById('registerBtn').onclick = ()=>{
    courseInput.value=''; examNameInput.value=''; examDateInput.value='';
    editingIndex = null;
    openModal(registerModal);
};
document.getElementById('editBtn').onclick = ()=>{
    const listDiv = document.getElementById('editList'); listDiv.innerHTML='';
    exams.forEach((e,i)=>{
        if(e.author===currentUser){
            const div=document.createElement('div');
            div.className='modal-exam-item';
            div.textContent=`${e.date} - ${e.course} - ${e.name}`;
            div.style.cursor='pointer';
            div.onclick=()=>{
                courseInput.value=e.course;
                examNameInput.value=e.name;
                examDateInput.value=e.date;
                editingIndex=i;
                closeModal(editModal);
                openModal(registerModal);
            };
            listDiv.appendChild(div);
        }
    });
    openModal(editModal);
};
document.getElementById('deleteBtn').onclick = ()=>{
    const listDiv = document.getElementById('deleteList'); listDiv.innerHTML='';
    exams.forEach((e,i)=>{
        if(e.author===currentUser){
            const div=document.createElement('div'); div.className='modal-exam-item';
            const checkbox=document.createElement('input'); checkbox.type='checkbox'; checkbox.className='delete-checkbox'; checkbox.value=i;
            div.appendChild(checkbox);
            const label=document.createElement('span'); label.textContent=`${e.date} - ${e.course} - ${e.name}`;
            div.appendChild(label);
            listDiv.appendChild(div);
        }
    });
    openModal(deleteModal);
};

// 저장
document.getElementById('submitExam').onclick = ()=>{
    const course=courseInput.value.trim(), name=examNameInput.value.trim(), date=examDateInput.value;
    if(!course||!name||!date){ alert('모든 항목을 입력해주세요'); return; }
    if(editingIndex!==null){ exams[editingIndex]={course,name,date,author:currentUser}; editingIndex=null; }
    else{ exams.push({course,name,date,author:currentUser}); }
    closeModal(registerModal); renderCalendar();
};

// 삭제
document.getElementById('deleteConfirmBtn').onclick = ()=>{
    const checkboxes=document.querySelectorAll('#deleteList input[type="checkbox"]:checked');
    const indexes=[...checkboxes].map(cb=>parseInt(cb.value)).sort((a,b)=>b-a);
    indexes.forEach(i=>exams.splice(i,1));
    closeModal(deleteModal); renderCalendar();
};

// 모달 닫기
document.querySelectorAll('.modal-close').forEach(btn=>{
    btn.onclick=()=>{ closeModal(btn.parentElement.parentElement); };
});
document.querySelectorAll('.modal-overlay').forEach(mod=>{
    mod.onclick=e=>{ if(e.target===mod) closeModal(mod); };
});

renderCalendar();
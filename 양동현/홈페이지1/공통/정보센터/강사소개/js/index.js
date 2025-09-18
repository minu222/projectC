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


// main


document.addEventListener('DOMContentLoaded', () => {
    const emailContent = document.getElementById('emailContent');
    const emailAttachments = document.getElementById('emailAttachments');
    const fileList = document.getElementById('fileList');
    const form = document.getElementById('emailForm');
    const fontSizeSelect = document.getElementById('fontSizeSelect');
    const maxLength = 1000;
    let selectedFiles = [];

    // 편집기 버튼
    const btnMap = {
        boldBtn: 'bold',
        italicBtn: 'italic',
        underlineBtn: 'underline',
        ulistBtn: 'insertUnorderedList',
        olistBtn: 'insertOrderedList',
        leftBtn: 'justifyLeft',
        centerBtn: 'justifyCenter',
        rightBtn: 'justifyRight'
    };

    Object.keys(btnMap).forEach(id => {
        const btn = document.getElementById(id);
        if(btn){
            btn.addEventListener('click', () => {
                document.execCommand(btnMap[id]);
            });
        }
    });

    // 글자 크기 선택
    if(fontSizeSelect && emailContent){
        fontSizeSelect.addEventListener('change', () => {
            const size = fontSizeSelect.value;
            if(size){
                document.execCommand('fontSize', false, size);
                const pxMap = {1:"10px",2:"13px",3:"16px",4:"18px",5:"24px",6:"32px",7:"48px"};
                emailContent.querySelectorAll("font[size]").forEach(f => {
                    f.style.fontSize = pxMap[f.size] || pxMap[size];
                    f.removeAttribute("size");
                });
            }
        });
    }

    // 글자수 제한
    if(emailContent){
        emailContent.addEventListener('input', () => {
            let text = emailContent.innerText;
            if(text.length > maxLength){
                emailContent.innerText = text.substring(0, maxLength);
                alert("내용은 최대 1000글자까지 입력 가능합니다.");
                placeCaretAtEnd(emailContent);
            }
            const charCount = document.getElementById('charCount');
            if(charCount){
                charCount.textContent = `${emailContent.innerText.length} / ${maxLength}`;
            }
        });
    }

    function placeCaretAtEnd(el){
        el.focus();
        const range = document.createRange();
        range.selectNodeContents(el);
        range.collapse(false);
        const sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
    }

    // 첨부파일
    if(emailAttachments && fileList){
        emailAttachments.addEventListener('change', () => {
            const files = Array.from(emailAttachments.files);
            files.forEach(file => {
                if(selectedFiles.length >= 5){
                    alert("첨부파일은 최대 5개까지만 업로드 가능합니다.");
                    return;
                }
                if(file.size > 10*1024*1024){
                    alert(`${file.name} 파일이 10MB를 초과했습니다.`);
                    return;
                }
                selectedFiles.push(file);
            });
            renderFileList();
            emailAttachments.value = "";
        });
    }

    function renderFileList(){
        if(!fileList) return;
        fileList.innerHTML = "";
        selectedFiles.forEach((file, idx) => {
            const li = document.createElement('li');
            li.textContent = `${file.name} (${Math.round(file.size/1024)} KB) `;
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.textContent = '삭제';
            btn.className = 'removeFileBtn';
            btn.onclick = () => { selectedFiles.splice(idx,1); renderFileList(); };
            li.appendChild(btn);
            fileList.appendChild(li);
        });
    }

    // 전송
    if(form){
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            const recipient = document.getElementById('emailRecipient')?.value || '';
            const title = document.getElementById('emailTitle')?.value || '';
            const content = emailContent ? emailContent.innerHTML : '';

            let msg = `받는사람: ${recipient}\n제목: ${title}\n내용:\n${content}`;
            if(selectedFiles.length>0){
                msg += `\n첨부파일: ${selectedFiles.map(f=>f.name).join(', ')}`;
            }

            alert("이메일이 전송되었습니다!\n\n" + msg);

            form.reset();
            if(emailContent) emailContent.innerHTML = "";
            selectedFiles = [];
            renderFileList();
            const charCount = document.getElementById('charCount');
            if(charCount) charCount.textContent = `0 / ${maxLength}`;
        });
    }
});




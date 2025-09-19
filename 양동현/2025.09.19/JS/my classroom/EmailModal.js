// EmailModal/EmailModal.js
document.addEventListener('DOMContentLoaded', () => {
    const modalHTML = `
<div class="modal-overlay" id="emailModal">
    <div class="modal-container">
        <button class="modal-close" id="closeEmailModal">&times;</button>
        <div class="modal-header">이메일 보내기</div>
        <form id="emailForm">
            <label for="emailRecipient">받는사람 이메일</label>
            <input type="text" id="emailRecipient" placeholder="example@domain.com" required>
            <label for="emailTitle">제목</label>
            <input type="text" id="emailTitle" placeholder="제목을 입력하세요" required>
            <label for="emailContent">내용</label>
            <div class="toolbar">
                <button type="button" id="boldBtn"><b>B</b></button>
                <button type="button" id="italicBtn"><i>I</i></button>
                <button type="button" id="underlineBtn"><u>U</u></button>
                <button type="button" id="ulistBtn">• 리스트</button>
                <button type="button" id="olistBtn">1. 리스트</button>
                <button type="button" id="leftBtn">왼쪽</button>
                <button type="button" id="centerBtn">가운데</button>
                <button type="button" id="rightBtn">오른쪽</button>
                <select id="fontSizeSelect">
                    <option value="">글자 크기</option>
                    <option value="1">10px</option>
                    <option value="2">13px</option>
                    <option value="3">16px</option>
                    <option value="4">18px</option>
                    <option value="5">24px</option>
                    <option value="6">32px</option>
                    <option value="7">48px</option>
                </select>
            </div>
            <div id="emailContent" class="editor" contenteditable="true"></div>
            <div id="charCount">0 / 1000</div>
            <div class="file-input">
                <label for="emailAttachments">파일 첨부 (최대 5개, 1개당 10MB)</label>
                <input type="file" id="emailAttachments" multiple>
                <ul id="fileList"></ul>
            </div>
            <button type="submit" class="send-button">전송</button>
        </form>
    </div>
</div>`;
    document.body.insertAdjacentHTML('beforeend', modalHTML);

    const modal = document.getElementById('emailModal');
    const openBtn = document.getElementById('openEmailModal');
    const closeBtn = document.getElementById('closeEmailModal');
    const emailContent = document.getElementById('emailContent');
    const emailAttachments = document.getElementById('emailAttachments');
    const fileList = document.getElementById('fileList');
    const form = document.getElementById('emailForm');
    const fontSizeSelect = document.getElementById('fontSizeSelect');
    let selectedFiles = [];
    const maxLength = 1000;

    openBtn.addEventListener('click', () => modal.style.display='flex');
    closeBtn.addEventListener('click', () => modal.style.display='none');
    window.addEventListener('click', e => { if(e.target===modal) modal.style.display='none'; });

    document.getElementById('boldBtn').addEventListener('click', () => document.execCommand('bold'));
    document.getElementById('italicBtn').addEventListener('click', () => document.execCommand('italic'));
    document.getElementById('underlineBtn').addEventListener('click', () => document.execCommand('underline'));
    document.getElementById('ulistBtn').addEventListener('click', () => document.execCommand('insertUnorderedList'));
    document.getElementById('olistBtn').addEventListener('click', () => document.execCommand('insertOrderedList'));
    document.getElementById('leftBtn').addEventListener('click', () => document.execCommand('justifyLeft'));
    document.getElementById('centerBtn').addEventListener('click', () => document.execCommand('justifyCenter'));
    document.getElementById('rightBtn').addEventListener('click', () => document.execCommand('justifyRight'));

    fontSizeSelect.addEventListener('change', () => {
        const size = fontSizeSelect.value;
        if(size){
            document.execCommand('fontSize', false, size);
            const pxMap = {1:"10px",2:"13px",3:"16px",4:"18px",5:"24px",6:"32px",7:"48px"};
            emailContent.querySelectorAll("font[size]").forEach(f => { f.style.fontSize=pxMap[f.size]||pxMap[size]; f.removeAttribute("size"); });
        }
    });

    emailContent.addEventListener('input', () => {
        let text = emailContent.innerText;
        if(text.length>maxLength){ emailContent.innerText=text.substring(0,maxLength); placeCaretAtEnd(emailContent); alert("내용은 최대 1000글자"); }
        document.getElementById('charCount').textContent=`${emailContent.innerText.length} / ${maxLength}`;
    });

    emailAttachments.addEventListener('change', () => {
        const files = Array.from(emailAttachments.files);
        files.forEach(file => {
            if(selectedFiles.length>=5){ alert("첨부파일은 최대 5개"); return; }
            if(file.size>10*1024*1024){ alert(`${file.name} 10MB 초과`); return; }
            selectedFiles.push(file);
        });
        renderFileList(); emailAttachments.value='';
    });

    function renderFileList(){
        fileList.innerHTML='';
        selectedFiles.forEach((file, idx) => {
            const li=document.createElement('li');
            li.textContent=`${file.name} (${Math.round(file.size/1024)} KB) `;
            const btn=document.createElement('button');
            btn.type='button'; btn.textContent='삭제'; btn.className='removeFileBtn';
            btn.onclick=()=>{ selectedFiles.splice(idx,1); renderFileList(); };
            li.appendChild(btn); fileList.appendChild(li);
        });
    }

    function placeCaretAtEnd(el){ el.focus(); const range=document.createRange(); range.selectNodeContents(el); range.collapse(false); const sel=window.getSelection(); sel.removeAllRanges(); sel.addRange(range); }

    form.addEventListener('submit', e => {
        e.preventDefault();
        alert(`이메일 전송!\n받는사람: ${document.getElementById('emailRecipient').value}\n제목: ${document.getElementById('emailTitle').value}\n내용: ${emailContent.innerHTML}\n첨부파일: ${selectedFiles.map(f=>f.name).join(', ')}`);
        form.reset(); emailContent.innerHTML=''; selectedFiles=[]; renderFileList(); document.getElementById('charCount').textContent=`0 / ${maxLength}`;
        modal.style.display='none';
    });
});

// NoteModal/noteModal.js
document.addEventListener('DOMContentLoaded', () => {
    const modalHTML = `
<div class="modal-overlay" id="noteModal">
    <div class="modal-container">
        <button class="modal-close" id="closeNoteModal">&times;</button>
        <div class="modal-header">쪽지 보내기</div>
        <form id="noteForm">
            <label for="noteRecipient">받는사람 ID</label>
            <input type="text" id="noteRecipient" placeholder="ID 입력" required>
            <label for="noteTitle">제목</label>
            <input type="text" id="noteTitle" placeholder="제목을 입력하세요" required>
            <label for="noteContent">내용</label>
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
            <div id="noteContent" class="editor" contenteditable="true"></div>
            <div id="charCount">0 / 1000</div>
            <button type="submit" class="send-button">전송</button>
        </form>
    </div>
</div>`;
    document.body.insertAdjacentHTML('beforeend', modalHTML);

    const modal = document.getElementById('noteModal');
    const openBtn = document.getElementById('openNoteModal');
    const closeBtn = document.getElementById('closeNoteModal');
    const noteContent = document.getElementById('noteContent');
    const form = document.getElementById('noteForm');
    const fontSizeSelect = document.getElementById('fontSizeSelect');
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
            noteContent.querySelectorAll("font[size]").forEach(f => { f.style.fontSize=pxMap[f.size]||pxMap[size]; f.removeAttribute("size"); });
        }
    });

    noteContent.addEventListener('input', () => {
        let text = noteContent.innerText;
        if(text.length>maxLength){ noteContent.innerText=text.substring(0,maxLength); placeCaretAtEnd(noteContent); alert("내용은 최대 1000글자"); }
        document.getElementById('charCount').textContent=`${noteContent.innerText.length} / ${maxLength}`;
    });

    function placeCaretAtEnd(el){ el.focus(); const range=document.createRange(); range.selectNodeContents(el); range.collapse(false); const sel=window.getSelection(); sel.removeAllRanges(); sel.addRange(range); }

    form.addEventListener('submit', e => {
        e.preventDefault();
        alert(`쪽지 전송!\n받는사람: ${document.getElementById('noteRecipient').value}\n제목: ${document.getElementById('noteTitle').value}\n내용: ${noteContent.innerHTML}`);
        form.reset(); noteContent.innerHTML=''; document.getElementById('charCount').textContent=`0 / ${maxLength}`;
        modal.style.display='none';
    });
});

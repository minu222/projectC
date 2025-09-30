// /js/EmailModal.js
(function () {
    'use strict';

    function openModal() {
        const wrap = document.createElement('div');
        wrap.id = 'email-modal';
        wrap.style.cssText = 'position:fixed;inset:0;background:rgba(0,0,0,.45);display:flex;align-items:center;justify-content:center;z-index:10000;';
        wrap.innerHTML = `
      <div class="eml-card" role="dialog" aria-modal="true" aria-labelledby="eml-title">
        <style>
          .eml-card{width:600px;max-width:92vw;background:#fff;border-radius:14px;box-shadow:0 20px 60px rgba(0,0,0,.25);overflow:hidden;font-family:inherit}
          .eml-hd{padding:14px 18px;background:linear-gradient(90deg,#ffb400,#ffd36a);color:#222;font-weight:800}
          .eml-bd{padding:18px;display:grid;gap:12px}
          .eml-ft{padding:12px 18px;display:flex;gap:10px;justify-content:flex-end;border-top:1px solid #f0f0f0}
          .eml-label{font-size:12px;color:#555;margin-bottom:6px}
          .eml-hint{font-size:12px;color:#888;margin-top:4px}
          .eml-input,.eml-textarea{width:100%;padding:10px 12px;border:1px solid #e5e5e5;border-radius:10px;outline:none;transition:box-shadow .15s,border-color .15s}
          .eml-input:focus,.eml-textarea:focus{border-color:#ffb400;box-shadow:0 0 0 3px rgba(255,180,0,.18)}
          .eml-textarea{min-height:140px;resize:vertical}
          .eml-row{display:flex;flex-direction:column}
          .eml-btn{padding:9px 14px;border-radius:10px;border:1px solid transparent;background:#ffb400;color:#222;font-weight:700;cursor:pointer}
          .eml-btn:hover{filter:brightness(0.98)}
          .eml-btn-secondary{background:#fff;border-color:#e5e5e5}
        </style>

        <div class="eml-hd" id="eml-title">📮 이메일 보내기</div>

        <div class="eml-bd">
          <div class="eml-row">
            <label class="eml-label" for="email-receiver-nick">받는사람 아이디(닉네임) 입력</label>
            <input id="email-receiver-nick" class="eml-input" type="text" placeholder="예: tiger, admin, luna ..." autocomplete="off">
            <div class="eml-hint">* 사용자 아이디 또는 닉네임을 입력해 주세요.</div>
          </div>

          <div class="eml-row">
            <label class="eml-label" for="email-title">제목</label>
            <input id="email-title" class="eml-input" type="text" placeholder="제목 (표시용)">
          </div>

          <div class="eml-row">
            <label class="eml-label" for="email-body">내용</label>
            <textarea id="email-body" class="eml-textarea" placeholder="보낼 내용을 입력하세요. (Ctrl/Cmd + Enter 전송)"></textarea>
          </div>
        </div>

        <div class="eml-ft">
          <button id="email-cancel" class="eml-btn eml-btn-secondary">취소</button>
          <button id="email-send" class="eml-btn">보내기</button>
        </div>
      </div>`;

        document.body.appendChild(wrap);

        const card = wrap.querySelector('.eml-card');
        const $ = sel => wrap.querySelector(sel);
        const inputNick = $('#email-receiver-nick');
        const inputTitle = $('#email-title');
        const inputBody = $('#email-body');
        const btnSend = $('#email-send');
        const btnCancel = $('#email-cancel');

        // 닫기(오버레이, ESC, 취소 버튼)
        const close = () => wrap.remove();
        wrap.addEventListener('click', (e) => { if (e.target === wrap) close(); });
        card.addEventListener('click', (e) => e.stopPropagation());
        btnCancel.addEventListener('click', close);
        function escClose(e){ if (e.key === 'Escape') close(); }
        document.addEventListener('keydown', escClose);

        // 전송
        async function doSend() {
            const receiverNickname = (inputNick.value || '').trim();
            const title = (inputTitle.value || '').trim();
            const body  = (inputBody.value || '').trim();
            const content = `[이메일] ${title}\n${body}`.trim();

            // 간단 검증 + 강조
            [inputNick, inputBody].forEach(el => el.style.borderColor = '#e5e5e5');
            if (!receiverNickname) { inputNick.style.borderColor = '#ff5a5a'; inputNick.focus(); return; }
            if (!body && !title)   { inputBody.style.borderColor = '#ff5a5a'; inputBody.focus(); return; }

            btnSend.disabled = true; btnSend.textContent = '전송중...';

            try {
                const res = await fetch('/api/messages', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ receiverNickname, content })
                });
                if (!res.ok) throw 0;
                alert('전송 완료');
                close();
            } catch {
                alert('전송 실패');
            } finally {
                btnSend.disabled = false; btnSend.textContent = '보내기';
                document.removeEventListener('keydown', escClose);
            }
        }

        // 버튼/단축키
        btnSend.addEventListener('click', doSend);
        inputBody.addEventListener('keydown', (e) => {
            if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') doSend();
        });

        setTimeout(() => inputNick.focus(), 50);
    }

    window.openEmailModal = openModal;
})();

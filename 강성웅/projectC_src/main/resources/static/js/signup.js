(function () {
    const $  = (sel, p = document) => p.querySelector(sel);
    const $$ = (sel, p = document) => Array.from(p.querySelectorAll(sel));
    const showMsg = (el, text, ok) => { if (!el) return; el.textContent = text||''; if (!text) return; el.style.color = ok ? '#10b981' : '#e11d48'; };

    const formatKRPhone = (s) => {
        if (!s) return '';
        let d = String(s).replace(/[^\d-]/g, '');
        d = d.replace(/-+/g, '-').replace(/^-|-$|/g, '');
        const only = d.replace(/-/g, '');
        if (only.startsWith('02')) {
            if (only.length <= 2) return only;
            if (only.length <= 5) return only.replace(/(\d{2})(\d+)/, '$1-$2');
            if (only.length <= 9) return only.replace(/(\d{2})(\d{3,4})(\d+)/, '$1-$2-$3');
            return only.slice(0, 10).replace(/(\d{2})(\d{4})(\d{4})/, '$1-$2-$3');
        }
        if (only.length <= 3) return only;
        if (only.length <= 7)  return only.replace(/(\d{3})(\d+)/, '$1-$2');
        if (only.length <= 11) return only.replace(/(\d{3})(\d{3,4})(\d{1,4})/, '$1-$2-$3');
        return only.slice(0, 11).replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
    };
    const sanitizePhoneKeepHyphen = (s) => {
        if (!s) return '';
        let t = String(s).replace(/[^\d-]/g, '');
        t = t.replace(/-+/g, '-').replace(/^-|-$|/g, '');
        return t;
    };

    document.addEventListener('DOMContentLoaded', () => {
        const form          = $('#signupForm') || $('form'); // 새 html엔 id 없을 수 있어 폴백
        if (!form) return;

        const userid        = $('#userid');
        const idMsg         = $('#idMsg');
        const pw1           = $('#password');
        const pw2           = $('#password2');
        const pwMsg         = $('#pwMsg');
        const pw2Msg        = $('#pw2Msg');
        const phone         = $('#phone');
        const phoneMsg      = $('#phoneMsg');
        const hiddenGender  = $('#gender');
        const hiddenRole    = $('#role');
        const teacherFields = $('#teacherFields');

        const careerList    = $('#careerList');
        const addCareerBtn  = $('.add-career-btn');

        const modal         = $('#signupModal');
        const btnGo         = $('#goLoginNow');
        const errEl         = $('#signupError');

        // ---------- 성별 버튼 ----------
        $$('.gender-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                $$('.gender-btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                if (hiddenGender) hiddenGender.value = btn.dataset.gender ?? btn.textContent.trim();
            });
        });

        // ---------- 역할 버튼 (student | instructor) ----------
        $$('.role-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                $$('.role-btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                const v = btn.dataset.role || 'student';
                if (hiddenRole) hiddenRole.value = v;              // ★ 서버로 보낼 값
                if (teacherFields) teacherFields.style.display = (v === 'instructor') ? '' : 'none';
            });
        });

        // ---------- 아이디 중복 체크 ----------
        let idAvailable = false;
        const validateUseridShape = v => /^[A-Za-z0-9]{4,20}$/.test(v); // 밑줄 제외

        async function checkUserId() {
            const v = (userid?.value || '').trim();
            idAvailable = false; showMsg(idMsg, '');
            if (!validateUseridShape(v)) { showMsg(idMsg, '아이디는 영문/숫자 4~20자', false); return; }
            try {
                const res = await fetch(`/api/auth/check-userid?userid=${encodeURIComponent(v)}`);
                const json = await res.json().catch(() => ({}));
                if (json.ok && json.available) { showMsg(idMsg, '사용 가능한 아이디입니다.', true); idAvailable = true; }
                else { showMsg(idMsg, json.msg || '이미 사용 중인 아이디입니다.', false); }
            } catch { showMsg(idMsg, '중복 확인 중 오류가 발생했습니다.', false); }
        }
        userid?.addEventListener('blur', checkUserId);
        userid?.addEventListener('input', () => { idAvailable = false; showMsg(idMsg, ''); });

        // ---------- 비밀번호 즉시 검증 ----------
        const validatePwLen = () => {
            if (!pw1) return true;
            if (pw1.value.length < 8) { showMsg(pwMsg, '비밀번호는 8자 이상', false); return false; }
            showMsg(pwMsg, '사용 가능한 비밀번호입니다.', true); return true;
        };
        const validatePwEqual = () => {
            if (!pw1 || !pw2) return true;
            if (pw2.value && pw1.value !== pw2.value) { showMsg(pw2Msg, '비밀번호가 일치하지 않습니다.', false); return false; }
            showMsg(pw2Msg, pw2.value ? '비밀번호가 일치합니다.' : '', true); return true;
        };
        pw1?.addEventListener('input', () => { validatePwLen(); validatePwEqual(); });
        pw2?.addEventListener('input', validatePwEqual);

        // ---------- 휴대폰 제한/포맷 ----------
        phone?.addEventListener('beforeinput', (e) => {
            if (e.inputType === 'insertText' && e.data && !/[0-9-]/.test(e.data)) e.preventDefault();
        });
        phone?.addEventListener('input', () => {
            const cur = phone.value, formatted = formatKRPhone(cur);
            if (cur !== formatted) phone.value = formatted;
            const only = formatted.replace(/-/g, '');
            if (phoneMsg) showMsg(phoneMsg, (only.length >= 10 && only.length <= 11) ? '유효한 번호 형식입니다.' : '', true);
        });

        // ======================= 경력 추가 UI =======================
        // HTML 요구:
        // <div id="careerList"></div>
        // <button type="button" class="add-career-btn">+ 경력 추가</button>
        // (숨김 입력은 JS가 자동 생성: #careersJson)

        const ensureCareersHidden = () => {
            let h = $('#careersJson');
            if (!h) {
                h = document.createElement('input');
                h.type = 'hidden';
                h.name = 'careersJson';
                h.id   = 'careersJson';
                form.appendChild(h);
            }
            return h;
        };

        const addCareerRow = (data = {}) => {
            if (!careerList) return;
            const row = document.createElement('div');
            row.className = 'career-row';
            row.style.marginBottom = '8px';
            row.innerHTML = `
        <input type="text" class="c-org"   placeholder="기관/회사" style="width:180px;">
        <input type="text" class="c-title" placeholder="직책/역할" style="width:160px;">
        <input type="month" class="c-start" title="시작" style="width:135px;">
        <input type="month" class="c-end"   title="종료" style="width:135px;">
        <button type="button" class="remove-career" style="margin-left:6px;">삭제</button>
      `;
            // preset
            if (data.org)   row.querySelector('.c-org').value   = data.org;
            if (data.title) row.querySelector('.c-title').value = data.title;
            if (data.start) row.querySelector('.c-start').value = data.start;
            if (data.end)   row.querySelector('.c-end').value   = data.end;
            careerList.appendChild(row);
        };

        addCareerBtn?.addEventListener('click', () => addCareerRow());
        careerList?.addEventListener('click', (e) => {
            const btn = e.target.closest('.remove-career');
            if (!btn) return;
            const row = btn.closest('.career-row');
            if (row) row.remove();
        });

        const collectCareers = () => {
            if (!careerList) return [];
            return $$('.career-row', careerList).map(row => ({
                org:   $('.c-org', row)?.value?.trim() || '',
                title: $('.c-title', row)?.value?.trim() || '',
                start: $('.c-start', row)?.value || '',
                end:   $('.c-end', row)?.value || ''
            })).filter(c => c.org || c.title || c.start || c.end);
        };
        // ===========================================================

        // ---------- 성공 모달 ----------
        const showSuccess = () => {
            if (modal) { modal.classList.remove('hidden'); modal.setAttribute('aria-hidden','false'); btnGo?.focus(); setTimeout(() => location.href='/login', 1200); }
            else { alert('회원가입이 완료되었습니다.'); location.href = '/login'; }
        };
        btnGo?.addEventListener('click', () => location.href = '/login');

        // ---------- 제출 ----------
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (errEl) { errEl.style.display = 'none'; errEl.textContent = ''; }

            if (userid && !validateUseridShape(userid.value.trim())) { userid.focus(); showMsg(idMsg, '아이디는 영문/숫자 4~20자', false); return; }
            if (!validatePwLen() || !validatePwEqual()) return;

            if (!idAvailable) { await checkUserId(); if (!idAvailable) { userid?.focus(); return; } }

            const fd = new FormData(form);

            // 전화번호 정리(숫자/하이픈만)
            if (phone && fd.has('phone')) fd.set('phone', sanitizePhoneKeepHyphen(phone.value));

            // role/gender 방어
            if (hiddenRole && !fd.get('role'))   fd.set('role', hiddenRole.value || 'student');
            if (hiddenGender && !fd.get('gender')) fd.set('gender', hiddenGender.value || '');

            // 강사라면 경력 JSON 추가
            const roleVal = (fd.get('role') || '').toString();
            if (roleVal === 'instructor') {
                const careers = collectCareers();
                const hidden = ensureCareersHidden();
                hidden.value = JSON.stringify(careers);   // 서버로 careersJson 전송
            }

            const body = new URLSearchParams();
            fd.forEach((v, k) => body.append(k, v));

            try {
                const res = await fetch('/api/auth/signup', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                    body
                });

                const ct = (res.headers.get('content-type') || '').toLowerCase();
                if (res.status === 303 || res.redirected || ct.includes('text/html')) {
                    location.href = res.url || '/login?signup=1';
                    return;
                }

                const json = await res.json().catch(() => ({}));
                if (!res.ok || !json.ok) throw new Error(json.msg || '회원가입 실패');

                showSuccess();
            } catch (err) {
                if (errEl) { errEl.textContent = err.message || '오류가 발생했습니다.'; errEl.style.display = 'block'; }
                else alert(err.message || '오류가 발생했습니다.');
            }
        });
    });
})();
document.addEventListener('DOMContentLoaded', () => {
    const form   = document.getElementById('signupForm');
    if (!form) return;

    const modal  = document.getElementById('signupModal');   // optional
    const btnGo  = document.getElementById('goLoginNow');    // optional
    const errEl  = document.getElementById('signupError');   // optional

    // =========================
    // 성별 버튼 → hidden #gender
    // =========================
    const genderBtns   = document.querySelectorAll('.gender-btn');
    const hiddenGender = document.getElementById('gender');
    if (genderBtns.length && hiddenGender) {
        genderBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                genderBtns.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                hiddenGender.value = btn.dataset.gender ?? btn.textContent.trim(); // '남자'/'여자'
            });
        });
    }

    // =========================
    // 아이디(닉네임) 중복 체크
    // =========================
    const userid = document.getElementById('userid');
    const idMsg  = document.getElementById('idMsg'); // <small id="idMsg"></small>
    let idAvailable = false;

    async function checkUserId() {
        const v = (userid?.value || '').trim();
        idAvailable = false;
        if (idMsg) { idMsg.textContent = ''; idMsg.className = 'hint'; }

        // 형식: 영문/숫자 4~20 (밑줄 제외)
        if (!/^[A-Za-z0-9]{4,20}$/.test(v)) {
            if (idMsg) {
                idMsg.textContent = '아이디는 영문/숫자 4~20자';
                idMsg.classList.add('err');
            }
            return;
        }

        try {
            const res = await fetch(`/api/auth/check-userid?userid=${encodeURIComponent(v)}`, { method: 'GET' });
            if (!res.ok) throw new Error(`중복 확인 실패(${res.status})`);
            const ct = (res.headers.get('content-type') || '').toLowerCase();
            if (!ct.includes('application/json')) throw new Error('JSON 응답이 아님');

            const json = await res.json();
            if (json.ok && json.available === true) {
                if (idMsg) { idMsg.textContent = '사용 가능한 아이디입니다.'; idMsg.classList.add('ok'); }
                idAvailable = true;
            } else {
                if (idMsg) { idMsg.textContent = json.msg || '이미 사용 중인 아이디입니다.'; idMsg.classList.add('err'); }
                idAvailable = false;
            }
        } catch (e) {
            if (idMsg) { idMsg.textContent = '중복 확인 중 오류가 발생했습니다.'; idMsg.classList.add('err'); }
            idAvailable = false;
        }
    }
    userid?.addEventListener('blur',  checkUserId);
    userid?.addEventListener('input', () => { idAvailable = false; if (idMsg) idMsg.textContent = ''; });

    // =========================
    // 비밀번호 실시간 검증 (8자 이상 / 일치)
    // =========================
    const pw     = document.getElementById('password');
    const pw2    = document.getElementById('password2');
    const pwMsg  = document.getElementById('pwMsg');   // <small id="pwMsg"></small>
    const pw2Msg = document.getElementById('pw2Msg');  // <small id="pw2Msg"></small>

    let pwOk = false, pw2Ok = false;

    function validatePw() {
        const v = pw?.value ?? '';
        pwOk = v.length >= 8;

        if (pwMsg) {
            pwMsg.className = 'hint';
            if (v.length === 0) pwMsg.textContent = '';
            else if (!pwOk) { pwMsg.textContent = '비밀번호는 8자 이상이어야 합니다.'; pwMsg.classList.add('err'); }
            else { pwMsg.textContent = '사용 가능한 비밀번호입니다.'; pwMsg.classList.add('ok'); }
        }
        validatePw2(); // 길이 바뀌면 일치 여부도 갱신
    }

    function validatePw2() {
        const v1 = pw?.value ?? '';
        const v2 = pw2?.value ?? '';
        pw2Ok = v2.length > 0 && v1 === v2 && v1.length >= 8;

        if (pw2Msg) {
            pw2Msg.className = 'hint';
            if (v2.length === 0) pw2Msg.textContent = '';
            else if (!pw2Ok) { pw2Msg.textContent = '비밀번호가 일치하지 않습니다.'; pw2Msg.classList.add('err'); }
            else { pw2Msg.textContent = '비밀번호가 일치합니다.'; pw2Msg.classList.add('ok'); }
        }
    }

    pw?.addEventListener('input', validatePw);
    pw2?.addEventListener('input', validatePw2);
    pw?.addEventListener('blur', validatePw);
    pw2?.addEventListener('blur', validatePw2);

    // =========================
    // 휴대폰: 숫자/하이픈만 + 자동 하이픈 + 안내
    // =========================
    const phone    = document.getElementById('phone');
    const phoneMsg = document.getElementById('phoneMsg'); // <small id="phoneMsg"></small>

    function autoHyphen(s) {
        let v = String(s).replace(/\D/g, ''); // 숫자만
        if (v.startsWith('02')) {
            if (v.length <= 2) return v; // 02
            if (v.length <= 5)  return v.replace(/(\d{2})(\d+)/, '$1-$2');
            if (v.length <= 9)  return v.replace(/(\d{2})(\d{3,4})(\d{0,4})/, '$1-$2-$3');
            return v.slice(0,10).replace(/(\d{2})(\d{4})(\d{4})/, '$1-$2-$3');
        } else {
            if (v.length < 4)   return v;
            if (v.length < 7)   return v.replace(/(\d{3})(\d+)/, '$1-$2');
            if (v.length < 11)  return v.replace(/(\d{3})(\d{3,4})(\d{0,4})/, '$1-$2-$3');
            return v.slice(0,11).replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
        }
    }

    function validatePhoneUI() {
        if (!phone || !phoneMsg) return;
        const ok = /^(02-\d{3,4}-\d{4}|01\d-\d{3,4}-\d{4}|0\d{2}-\d{3,4}-\d{4})$/.test(phone.value);
        phoneMsg.className = 'hint ' + (ok ? 'ok' : 'err');
        phoneMsg.textContent = ok ? '형식이 올바릅니다.' : '숫자만 입력(예: 010-1234-5678)';
    }

    if (phone) {
        phone.addEventListener('input', () => {
            phone.value = autoHyphen(phone.value);
            validatePhoneUI();
        });

        phone.addEventListener('paste', (e) => {
            e.preventDefault();
            const text = (e.clipboardData || window.clipboardData).getData('text');
            const formatted = autoHyphen(text);
            document.execCommand('insertText', false, formatted);
            validatePhoneUI();
        });

        // 숫자/하이픈/편집키만 허용
        phone.addEventListener('keydown', (e) => {
            const allowed =
                (e.key >= '0' && e.key <= '9') ||
                ['Backspace','Delete','ArrowLeft','ArrowRight','Home','End','Tab'].includes(e.key) ||
                e.key === '-';
            const combo = (e.ctrlKey || e.metaKey) && ['a','c','v','x'].includes(e.key.toLowerCase());
            if (!allowed && !combo) e.preventDefault();
        });
    }

    // =========================
    // 성공 모달/이동
    // =========================
    const showSuccess = () => {
        if (modal) {
            modal.classList.remove('hidden');
            modal.setAttribute('aria-hidden', 'false');
            btnGo?.focus();
            setTimeout(() => location.href = '/login', 1200);
        } else {
            alert('회원가입이 완료되었습니다.');
            location.href = '/login';
        }
    };
    btnGo?.addEventListener('click', () => location.href = '/login');

    // =========================
    // 회원가입 제출
    // =========================
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (errEl) errEl.style.display = 'none';

        // 프런트 최종 검증
        if (userid) {
            if (!idAvailable) {
                await checkUserId();
                if (!idAvailable) { userid.focus(); return; }
            }
        }
        validatePw();
        validatePw2();
        if (!pwOk)  { pw?.focus();  return; }
        if (!pw2Ok) { pw2?.focus(); return; }

        const fd = new FormData(form);

        // 전화번호: 숫자/하이픈만 남기고 정리 (서버에서도 재검증)
        const phoneRaw = fd.get('phone');
        if (phoneRaw != null) {
            const phoneClean = String(phoneRaw)
                .replace(/[^\d-]/g, '')   // 숫자/하이픈 외 제거
                .replace(/-+/g, '-')      // 연속 하이픈 정리
                .replace(/^-+|-+$/g, ''); // 앞/뒤 하이픈 제거
            fd.set('phone', phoneClean);
        }

        // x-www-form-urlencoded 로 전송
        const body = new URLSearchParams(fd);

        try {
            const res = await fetch('/api/auth/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body
            });

            // 서버 리다이렉트/HTML이면 바로 이동
            const ct = (res.headers.get('content-type') || '').toLowerCase();
            if (res.status === 303 || res.redirected || ct.includes('text/html')) {
                location.href = res.url || '/login?signup=1';
                return;
            }

            const json = await res.json().catch(() => ({}));
            if (!res.ok || !json.ok) throw new Error(json.msg || '회원가입 실패');

            showSuccess();
        } catch (err) {
            if (errEl) {
                errEl.textContent = err.message || '오류가 발생했습니다.';
                errEl.style.display = 'block';
            } else {
                alert(err.message || '오류가 발생했습니다.');
            }
        }
    });
});
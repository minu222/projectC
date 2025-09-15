// ---- 성별 버튼(있다면) 선택 효과 ----
document.addEventListener('DOMContentLoaded', () => {
    const genderBtns = document.querySelectorAll('.gender-btn');
    const hiddenGender = document.getElementById('gender'); // 숨김 필드 쓰는 버전일 때만
    genderBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            genderBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            if (hiddenGender) hiddenGender.value = btn.dataset.gender ?? btn.textContent.trim();
        });
    });
});

// ---- 회원가입 제출 → /api/auth/signup (POST) ----
// /static/js/signup.js  << 전체 교체
document.addEventListener('DOMContentLoaded', () => {
    const form  = document.getElementById('signupForm');
    if (!form) return;

    const modal = document.getElementById('signupModal'); // optional
    const btnGo = document.getElementById('goLoginNow');  // optional
    const errEl = document.getElementById('signupError'); // optional

    // 성별 버튼 UI → hidden #gender에 값 세팅
    document.querySelectorAll('.gender-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.gender-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const hidden = document.getElementById('gender');
            if (hidden) hidden.value = btn.textContent.trim(); // '남자'/'여자'
        });
    });

    // 아이디 중복 체크
    const userid = document.getElementById('userid');
    const idMsg  = document.getElementById('idMsg'); // <small id="idMsg"></small> 가 있으면 사용
    let idAvailable = false;

    async function checkUserId() {
        const v = (userid?.value || '').trim();
        idAvailable = false;
        if (idMsg) { idMsg.textContent = ''; idMsg.style.color = ''; }

        // 형식 체크 (영/숫/밑줄 4~20)
        if (!/^[A-Za-z0-9_]{4,20}$/.test(v)) {
            if (idMsg) {
                idMsg.textContent = '아이디는 영문/숫자/밑줄 4~20자';
                idMsg.style.color = '#e11d48';
            }
            return;
        }

        try {
            const res = await fetch(`/api/auth/check-userid?userid=${encodeURIComponent(v)}`);
            const json = await res.json().catch(() => ({}));
            if (json.ok && json.available) {
                if (idMsg) {
                    idMsg.textContent = '사용 가능한 아이디입니다.';
                    idMsg.style.color = '#10b981';
                }
                idAvailable = true;
            } else {
                if (idMsg) {
                    idMsg.textContent = json.msg || '이미 사용 중인 아이디입니다.';
                    idMsg.style.color = '#e11d48';
                }
                idAvailable = false;
            }
        } catch (e) {
            if (idMsg) {
                idMsg.textContent = '중복 확인 중 오류가 발생했습니다.';
                idMsg.style.color = '#e11d48';
            }
            idAvailable = false;
        }
    }

    userid?.addEventListener('blur', checkUserId);
    userid?.addEventListener('input', () => { idAvailable = false; if (idMsg) idMsg.textContent = ''; });

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

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (errEl) errEl.style.display = 'none';

        // 프런트 사전 검증
        const pw1 = document.getElementById('password');
        const pw2 = document.getElementById('password2');
        if (!pw1 || !pw2) { alert('비밀번호 입력란이 없습니다.'); return; }
        if (pw1.value.length < 8 || pw2.value.length < 8) {
            alert('비밀번호는 8자 이상이어야 합니다.');
            pw1.focus();
            return;
        }
        if (pw1.value !== pw2.value) {
            alert('비밀번호가 일치하지 않습니다.');
            pw2.focus();
            return;
        }

        // 아이디 중복 최종 확인
        if (!idAvailable) {
            await checkUserId();
            if (!idAvailable) {
                userid?.focus();
                return;
            }
        }

        const fd = new FormData(form);

        // 전화번호: 하이픈 보존(공백/기타문자 제거)
        const phoneRaw = fd.get('phone');
        if (phoneRaw != null) {
            const phoneClean = String(phoneRaw).replace(/[^\d-]/g, '').replace(/-+/g, '-').replace(/^-|-$|/g, '');
            fd.set('phone', phoneClean);
        }

        const body = new URLSearchParams();
        fd.forEach((v, k) => body.append(k, v));

        try {
            const res = await fetch('/api/auth/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                body
            });

            // 서버가 303 리다이렉트/HTML을 주면 그대로 로그인으로 이동
            const ct = (res.headers.get('content-type') || '').toLowerCase();
            if (res.status === 303 || res.redirected || ct.includes('text/html')) {
                location.href = res.url || '/login?signup=1';
                return;
            }

            const json = await res.json().catch(() => ({}));
            if (!res.ok || !json.ok) {
                throw new Error(json.msg || '회원가입 실패');
            }

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

    btnGo?.addEventListener('click', () => location.href = '/login');
});
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

    const modal = document.getElementById('signupModal');
    const btnGo = document.getElementById('goLoginNow');
    const errEl = document.getElementById('signupError');

    // (선택) 성별 버튼 UI
    document.querySelectorAll('.gender-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.gender-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const hidden = document.getElementById('gender');
            if (hidden) hidden.value = btn.textContent.trim(); // '남자'/'여자'
        });
    });

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

        const fd = new FormData(form);

        // ✅ 하이픈은 유지하고 공백/기타 문자만 제거
        const phoneRaw = fd.get('phone');
        if (phoneRaw != null) {
            const phoneClean = String(phoneRaw).replace(/[^\d-]/g, ''); // 숫자/하이픈만
            fd.set('phone', phoneClean);
        }

        // 실제로 보내는 값 확인
        const body = new URLSearchParams();
        fd.forEach((v, k) => body.append(k, v));
        console.log('DEBUG sending phone =', body.get('phone')); // ← 여기에 010-1234-5678 식으로 보여야 함

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

            const json = await res.json();
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

    btnGo?.addEventListener('click', () => location.href = '/login');
});
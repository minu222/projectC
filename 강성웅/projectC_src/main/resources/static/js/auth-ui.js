document.addEventListener('DOMContentLoaded', async () => {
    const root = document.getElementById('authArea');
    if (!root) return;

    const showLoggedOut = () => {
        root.innerHTML = `<a href="/login">로그인</a> | <a href="/signup">회원가입</a>`;
    };

    try {
        const res  = await fetch('/api/auth/me', { headers: { 'Accept':'application/json' }, cache: 'no-store' });
        const data = await res.json().catch(() => ({}));

        if (!res.ok || !data.ok) {
            showLoggedOut();
            return;
        }

        const name = data.name || '사용자';
        root.innerHTML = `
      <span class="hello"><strong>${name}</strong>님</span>
      <button type="button" id="btnLogout" class="link-btn">로그아웃</button>
    `;

        document.getElementById('btnLogout')?.addEventListener('click', async () => {
            try {
                await fetch('/api/auth/logout', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
                    body: '' // CSRF 비활성화 상태 가정
                });
            } finally {
                // 새로고침해서 헤더 상태 갱신
                location.href = '/';
            }
        });
    } catch {
        showLoggedOut();
    }
});
// menu-loader.js
(function () {
  /**
   * 1) 메뉴 HTML을 div#global-menu 안에 주입
   * 2) 주입 후, 서브메뉴 토글 및 "현재 페이지 활성화" 처리
   */

  async function loadMenu() {
    const host = document.getElementById("global-menu");
    if (!host) return;

    // 어디에서 불러올지: <div id="global-menu" data-src="menu.html">
    const src = host.getAttribute("data-src") || "menu.html";

    try {
      const res = await fetch(src, { cache: "no-store" });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const html = await res.text();
      host.innerHTML = html;

      // 메뉴가 들어오고 나서 인터랙션 초기화
      initSidebarInteractions(host);
      highlightActiveLink(host);
    } catch (err) {
      console.error("[menu-loader] 메뉴 로드 실패:", err);
      host.innerHTML = `
        <div style="padding:12px; background:#fff3f3; color:#b00020; border:1px solid #f0c2c2; border-radius:8px;">
          메뉴를 불러오지 못했습니다. (${String(err)})
        </div>`;
    }
  }

  function initSidebarInteractions(scope) {
    // 원래 쓰던 .submenu-toggle 로직을 그대로 적용
    const toggles = scope.querySelectorAll(".submenu-toggle");
    toggles.forEach((btn) => {
      btn.addEventListener("click", () => {
        const submenu = btn.nextElementSibling;
        if (!submenu || !submenu.classList.contains("submenu")) return;

        const isOpen = submenu.classList.contains("open");
        if (isOpen) {
          submenu.style.height = submenu.scrollHeight + "px";
          submenu.classList.remove("open");
          requestAnimationFrame(() => (submenu.style.height = "0px"));
        } else {
          submenu.classList.add("open");
          submenu.style.height = submenu.scrollHeight + "px";
          submenu.addEventListener(
            "transitionend",
            () => {
              if (submenu.classList.contains("open")) {
                submenu.style.height = "auto";
              }
            },
            { once: true }
          );
        }
      });
    });
  }

  function highlightActiveLink(scope) {
    // 현재 페이지 파일명 기준으로 사이드바 a[href] 매칭
    // ex) http://.../강사정보.html -> "강사정보.html"에 active 클래스
    const here = location.pathname.split("/").pop(); // 파일명
    if (!here) return;

    const links = scope.querySelectorAll("a[href]");
    links.forEach((a) => {
      const href = a.getAttribute("href");
      if (!href) return;

      // 절대/상대경로 모두 고려: href의 마지막 파일명 비교
      const target = href.split("/").pop();
      if (target === here) {
        a.classList.add("active");
        // 스타일이 없는 프로젝트라면 기본 하이라이트 제공
        a.style.boxShadow = a.style.boxShadow || "inset 0 0 0 2px #2563eb";
        a.style.borderRadius = a.style.borderRadius || "8px";

        // 이 링크가 접힌 섹션 안에 있다면 펼쳐주기
        openParents(a);
      }
    });

    function openParents(el) {
      // 상위로 올라가며 .submenu 찾아서 펼침
      let node = el;
      while (node) {
        const parentSubmenu = node.closest("ul.submenu");
        if (parentSubmenu && !parentSubmenu.classList.contains("open")) {
          parentSubmenu.classList.add("open");
          parentSubmenu.style.height = "auto";
        }
        node = parentSubmenu ? parentSubmenu.parentElement : null;
      }
    }
  }

  // DOM 준비 후 실행
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", loadMenu);
  } else {
    loadMenu();
  }
})();

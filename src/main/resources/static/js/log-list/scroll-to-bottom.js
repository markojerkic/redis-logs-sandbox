const SCROLL_THRESHOLD = 10;

let logList = null;
let scrollLockIndicator = null;
let isScrollLocked = true;

function isAtBottom() {
  const scrollTop = logList.scrollTop;
  const scrollHeight = logList.scrollHeight;
  const clientHeight = logList.clientHeight;
  return scrollHeight - scrollTop - clientHeight <= SCROLL_THRESHOLD;
}

function scrollToBottom() {
  logList.scrollTop = logList.scrollHeight;
}

function debounce(func, wait) {
  let timeout;
  return function (...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => {
      func.apply(this, args);
    }, wait);
  };
}

function updateScrollLockIndicator() {
  if (!scrollLockIndicator) return;

  if (isScrollLocked) {
    scrollLockIndicator.textContent = "Auto-scroll: ON";
    scrollLockIndicator.className = "scroll-lock-active";
  } else {
    scrollLockIndicator.textContent = "Auto-scroll: OFF";
    scrollLockIndicator.className = "scroll-lock-inactive";
  }
}

function handleScroll() {
  if (isAtBottom()) {
    isScrollLocked = true;
  } else {
    isScrollLocked = false;
  }
  debounce(updateScrollLockIndicator, 100)();
}

function handleOobSwap(event) {
  const elementId = event?.detail?.target?.id;
  if (elementId !== "log-list") return;

  if (isScrollLocked) {
    scrollToBottom();
  }
}

function setup(shouldScrollToBottom = false) {
  logList = document.getElementById("log-list");
  if (!logList) return;

  scrollLockIndicator = document.getElementById("scroll-lock-indicator");
  if (shouldScrollToBottom) {
    scrollToBottom();
  }

  logList.addEventListener("scroll", handleScroll);
  document.body.addEventListener("htmx:oobAfterSwap", handleOobSwap);

  updateScrollLockIndicator();
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById("log-list")) {
      setup(true);
    }
  });
} else {
  if (document.getElementById("log-list")) {
    setup(true);
  }
}

document.addEventListener("htmx:afterSwap", (e) => {
  if (document.getElementById("log-list")) {
    setup();
  }
});

const SCROLL_THRESHOLD = 10;

/** @type {HTMLElement | null} The log list container element */
let logList = null;

/** @type {HTMLElement | null} The scroll lock indicator element */
let scrollLockIndicator = null;

/** @type {boolean} Whether auto-scroll is enabled */
let isScrollLocked = true;

/**
 * @returns {boolean} True if at bottom, false otherwise
 */
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

/**
 * Handles HTMX out-of-band swap events for new log entries
 * @param {CustomEvent} event - The HTMX OOB swap event
 * @returns {void}
 */
function handleOobSwap(event) {
  const elementId = event?.detail?.target?.id;
  if (elementId !== "log-list") return;

  if (isScrollLocked) {
    scrollToBottom();
  }
}

function setup() {
  logList = document.getElementById("log-list");
  if (!logList) throw new Error("Log list not found");

  scrollLockIndicator = document.getElementById("scroll-lock-indicator");
  if (!scrollLockIndicator) console.warn("Scroll lock indicator not found");
  scrollToBottom();

  logList.addEventListener("scroll", handleScroll);
  document.body.addEventListener("htmx:oobAfterSwap", handleOobSwap);

  updateScrollLockIndicator();
}

document.addEventListener("DOMContentLoaded", setup);
document.addEventListener("htmx:afterSwap", (e) => {
  if (e.detail?.isBoosted === true) {
    setup();
  }
});

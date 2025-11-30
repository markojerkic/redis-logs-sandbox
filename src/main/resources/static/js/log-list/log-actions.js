/**
 * Handle copy button clicks
 * @param {Event} e - The click event
 */
function handleCopyButtonClick(e) {
  const currentUrl = new URL(window.location.href);
  currentUrl.searchParams = new URLSearchParams();
  currentUrl.searchParams.set("logId", e.target.closest("pre").dataset.logId);
  console.log("Copy button clicked", currentUrl, e.target.closest("pre"));
  navigator.clipboard.writeText(currentUrl.toString());

  Toastify({
    text: "Copied link to clipboard",
    duration: 3_000,
    newWindow: true,
    close: true,
    gravity: "bottom",
    position: "right",
    style: {
      background: "#a6d189", // Frappé green
      color: "#232634", // Frappé crust
    },
  }).showToast();
}

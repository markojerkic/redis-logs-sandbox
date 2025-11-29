function setup() {
    const logList = document.getElementById("log-list");
    if (!logList) throw new Error("Log list not found");

    logList.addEventListener("htmx:oobAfterSwap", function (event) {
        console.log("oob event", event);
    });
}
document.addEventListener("DOMContentLoaded", setup);

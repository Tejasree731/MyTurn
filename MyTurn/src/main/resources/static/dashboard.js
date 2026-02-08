
   const token = localStorage.getItem("token");
const role = localStorage.getItem("role");

if (!token) {
    window.location.href = "login.html";
}

// Only ADMIN allowed
if (role !== "ADMIN") {
    alert("Access denied");
    window.location.href = "queue.html";
}


    if (role === "ADMIN") {

    const adminPanel = document.getElementById("adminPanel");
    const queueNav = document.getElementById("queueNav");
    const userNav = document.getElementById("userNav");
    const analyticsNav = document.getElementById("analyticsNav");

    if (adminPanel) adminPanel.classList.remove("hidden");
    if (queueNav) queueNav.classList.remove("hidden");
    if (userNav) userNav.classList.remove("hidden");
    if (analyticsNav) analyticsNav.classList.remove("hidden");
    }
    function showMsg(text) {

    const el = document.getElementById("msg");

    el.innerText = text;
    el.classList.remove("hidden");

    setTimeout(() => el.classList.add("hidden"), 3000);
    }

    function load() {

    fetch("/api/queue/dashboard", {
        headers: {
        Authorization: "Bearer " + token
        }
    })
    .then(res => {

        if (res.status === 401) {
        logout();
        return;
        }

        return res.json();
    })
    .then(list => {

        if (!list) return;

        let html = "";

        list.forEach(q => {

        html += `
        <div class="glass card p-6 rounded-2xl">

            <h3 class="text-2xl font-semibold mb-3">
            ${q.name}
            </h3>

            <p class="text-gray-300 mb-1">
            Now Serving:
            <span class="text-white font-bold">${q.currentToken}</span>
            </p>

            <p class="text-gray-300 mb-3">
            Waiting:
            <span class="text-white font-bold">${q.waiting}</span>
            </p>

            <div class="flex items-center justify-between mt-3">

            <span class="inline-block px-3 py-1 rounded-full text-sm
                ${q.active
                ? 'bg-green-400/20 text-green-300'
                : 'bg-red-400/20 text-red-300'}">

                ${q.active ? 'Active' : 'Closed'}

            </span>

            ${
                role === "ADMIN"
                ? `<button onclick="nextToken(${q.queueId})"
                    class="bg-indigo-500/40 px-3 py-1 rounded-lg
                    hover:bg-indigo-500/60 transition text-sm">

                    ▶ Next
                </button>`
                : ""
            }

            </div>

        </div>`;
        });

        document.getElementById("data").innerHTML = html;

    })
    .catch(() => {
        showMsg("Server error");
    });
    }

    function logout() {

    localStorage.clear();
    location.href = "login.html";
    }

    function createQueue() {

        const name = document.getElementById("queueName").value;
        if (!name) {
            showMsg("Enter queue name");
            return;
        }

        fetch(`/api/queue/create?name=${name}`, {
            method: "POST",
            headers: {
            Authorization: "Bearer " + token
            }
        })
        .then(res => res.text())
        .then(msg => {

            showMsg(msg);

            document.getElementById("queueName").value = "";

            load();
        });
    }

    function nextToken(qid) {

        fetch(`/api/queue/next/${qid}`, {
            method: "POST",
            headers: {
            Authorization: "Bearer " + token
            }
        })
        .then(res => res.text())
        .then(msg => {

            showMsg(msg);

            load();
        });
    }
    load();
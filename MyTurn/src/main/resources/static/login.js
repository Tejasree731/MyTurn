window.onload = () => {

    const savedUser = localStorage.getItem("rememberUser");

    if (savedUser) {
        username.value = savedUser;
        document.getElementById("rememberMe").checked = true;
    }
};

for(let i=0;i<50;i++){

    const p = document.createElement("div");
    p.className = "particle";

    p.style.left = Math.random()*100+"%";
    p.style.animationDuration = 8+Math.random()*12+"s";
    p.style.animationDelay = Math.random()*5+"s";

    document.body.appendChild(p);
}

document.addEventListener("mousemove", e => {

    const x = (e.clientX / window.innerWidth - .5)*20;
    const y = (e.clientY / window.innerHeight - .5)*20;

    document.querySelector(".light").style.transform =
        `translate(${x}px,${y}px) rotate(20deg)`;
});

function login() {

msg.innerText = "";

showLoader();

const data = {
    username: username.value.trim(),
    password: password.value.trim()
};

if (!data.username || !data.password) {
    hideLoader();
    msg.innerText = "Please fill all fields";
    return;
}

fetch("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
})

.then(res => {

    if (!res.ok) {
        throw new Error("Server error");
    }

    return res.json();
})

.then(result => {

    hideLoader();

    if (result.error) {
        msg.innerText = result.error;
        return;
    }

    if (document.getElementById("rememberMe").checked) {
        localStorage.setItem("rememberUser", data.username);
    } else {
        localStorage.removeItem("rememberUser");
    }
    localStorage.setItem("token", result.token);
    localStorage.setItem("role", result.role.toUpperCase());

    localStorage.setItem("username", result.username);

    showSuccess();
})

.catch(err => {

    hideLoader();

    msg.innerText = "Server unavailable. Try again later.";
    console.error(err);
});
}

function showSuccess() {

    const box = document.getElementById("successBox");
    const card = document.getElementById("successCard");

    box.classList.remove("hidden");

    setTimeout(() => {
        card.classList.add("show-success");
    }, 100);

    setTimeout(() => {

        const role = localStorage.getItem("role");

        if (role === "ADMIN") {
            window.location.href = "dashboard.html";
        } else {
            window.location.href = "queue.html";
        }

    }, 1800);
}

document.getElementById("closeBtn").addEventListener("click", () => {
    window.location.href = "home.html";
});

document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
        window.location.href = "home.html";
    }
});
function showLoader() {
document.getElementById("loader").classList.remove("hidden");
}

function hideLoader() {
document.getElementById("loader").classList.add("hidden");
}
function isTokenExpired(token) {

try {
    const payload = JSON.parse(atob(token.split(".")[1]));

    const exp = payload.exp * 1000;

    return Date.now() > exp;

} catch {
    return true;
}
}

const token = localStorage.getItem("token");

if (token && isTokenExpired(token)) {

localStorage.clear();

alert("Session expired. Please login again.");

window.location.href = "login.html";
}



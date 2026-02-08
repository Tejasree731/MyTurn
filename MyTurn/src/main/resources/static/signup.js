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

function signup() {
    const data = {
        username: username.value,
        email: email.value,
        password: password.value,
        role: "USER"
    };

    fetch("/api/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    .then(res => res.text())
    .then(result => {

    msg.innerText = result;

    if (result.toLowerCase().includes("success")) {

    msg.classList.remove("text-red-300");
    msg.classList.add("text-green-300");

    setTimeout(() => {
        window.location.href = "login.html";
    }, 1500);
    }
});
}
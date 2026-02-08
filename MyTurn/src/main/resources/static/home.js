document.addEventListener("mousemove", e => {

const x = (e.clientX / window.innerWidth - .5)*20;
const y = (e.clientY / window.innerHeight - .5)*20;

document.querySelector(".light").style.transform =
    `translate(${x}px,${y}px) rotate(20deg)`;

});

//magnifier
const canvas = document.getElementById("magnifier");
const ctx = canvas.getContext("2d");
canvas.style.display = "block";

const SIZE = 80;
const ZOOM = 2;

const DPR = window.devicePixelRatio || 1;

canvas.width = SIZE * DPR;
canvas.height = SIZE * DPR;

canvas.style.width = SIZE + "px";
canvas.style.height = SIZE + "px";

ctx.scale(DPR, DPR);

let mouseX = 0;
let mouseY = 0;

const hero = document.querySelector(".hero");

document.addEventListener("mousemove", e => {

  const heroRect = hero.getBoundingClientRect();
  const insideHero =
    e.clientY >= heroRect.top &&
    e.clientY <= heroRect.bottom;

  if (insideHero) {
    canvas.style.display = "block";

    mouseX = e.clientX;
    mouseY = e.clientY;

    canvas.style.left = mouseX + "px";
    canvas.style.top = mouseY + "px";

  } else {
    canvas.style.display = "none";
  }

});


    let screenCanvas = null;

    async function capture() {

    screenCanvas = await html2canvas(document.body, {
        backgroundColor: null,
        scale: DPR,
        ignoreElements: el => el.id === "magnifier"
    });

    }

    setInterval(capture, 1000);
    capture();

    function render() {

        if (!screenCanvas) {
            requestAnimationFrame(render);
            return;
        }

        ctx.clearRect(0, 0, SIZE, SIZE);

        ctx.save();

        ctx.beginPath();
        ctx.arc(SIZE/2, SIZE/2, SIZE/2, 0, Math.PI*2);
        ctx.clip();

        const srcX = mouseX * DPR - (SIZE/(2*ZOOM))*DPR;
        const srcY = mouseY * DPR - (SIZE/(2*ZOOM))*DPR;

        const srcW = (SIZE/ZOOM)*DPR;
        const srcH = (SIZE/ZOOM)*DPR;

        ctx.drawImage(
            screenCanvas,
            srcX, srcY, srcW, srcH,
            0, 0, SIZE, SIZE
        );

        ctx.restore();

        requestAnimationFrame(render);
    }

    render();

    const role = localStorage.getItem("role");
    if (role === "ADMIN") {
        document.getElementById("dashboardLink").classList.remove("hidden");
        document.getElementById("queueLink").classList.remove("hidden");
    }
    if (!localStorage.getItem("token")) {
        document.getElementById("dashboardLink").classList.add("hidden");
        document.getElementById("queueLink").classList.add("hidden");
    }

    document.getElementById("getStartedBtn").addEventListener("click", () => {
        const token = localStorage.getItem("token");
        const role = localStorage.getItem("role");
        if (token && role) {
            if (role === "ADMIN") {
                window.location.href = "dashboard.html";
            }
            else {
                window.location.href = "queue.html";
            }
        }else {
            window.location.href = "login.html";
        }

    });

const reveals = document.querySelectorAll(".reveal");

const observer = new IntersectionObserver(
  entries => {
    entries.forEach(entry => {

      if (entry.isIntersecting) {
        entry.target.classList.add("active");
      } 
      else {
        entry.target.classList.remove("active");
      }

    });
  },
  {
    threshold: 0.25
  }
);

reveals.forEach(el => observer.observe(el));

let stompClient = null;

const token = localStorage.getItem("token");

if (!token) {
  location.href = "login.html";
}

let queueId = localStorage.getItem("currentQueueId");
let queueName = localStorage.getItem("currentQueueName");

let selectedPay = null;

document.getElementById("title").innerText =
  queueName + " Queue";

loadQueueStatus();
connectWS();

function loadQueueStatus(){

  fetch(`/api/queue/position/${queueId}`, {
    headers: {
      Authorization: "Bearer " + token
    }
  })

  .then(res => {
    if(!res.ok) throw new Error("Forbidden");
    return res.json();
  })

  .then(data => {

    renderLine(
      data.totalPeople,
      data.myPosition
    );

    showInfo(
      "🎯 Your Token: " + data.token +
      " | Position: " + data.myPosition
    );

  })

  .catch(err=>{
    console.error(err);
    showInfo("❌ Not joined yet");
  });

}

function joinQueue(){

  fetch(`/api/queue/join/${queueId}`, {
    method: "POST",
    headers: {
      Authorization: "Bearer " + token
    }
  })

  .then(res => res.text())

  .then(msg => {

    showInfo(msg);

    loadQueueStatus();

  });

}

function renderLine(total, myPos){

  const line =
    document.getElementById("line");

  if(total === 0){

    line.innerHTML =
      "<p class='text-slate-400'>Empty Queue</p>";

    return;
  }

  let html = "";

  for(let i=1;i<=total;i++){

    const dist = total - i;

    const z = -dist * 80;
    const y = dist * 25;
    const scale = 1 - dist * 0.05;

    const style = `
      left:50%;
      transform:
      translateX(-50%)
      translateY(${y}px)
      translateZ(${z}px)
      scale(${scale});
    `;

    if(i === myPos){

      html += `
        <div class="person me" style="${style}">
          <div class="pos-badge">${i}</div>
          <div class="you-tag">YOU</div>
        </div>
      `;

    }else{

      html += `
        <div class="person" style="${style}">
          <div class="pos-badge">${i}</div>
        </div>
      `;
    }
  }

  line.innerHTML = html;
}

function addToken(){
  openPayment();
}

function confirmPay(){

  if(!selectedPay){
    alert("Select payment");
    return;
  }

  closePayment();

  joinQueue();
}

function selectPay(el,type){

  document
   .querySelectorAll(".pay-option")
   .forEach(e=>e.classList.remove("active"));

  el.classList.add("active");

  selectedPay = type;
}

function openPayment(){
  document.getElementById("payModal")
    .style.display = "flex";
}

function closePayment(){
  document.getElementById("payModal")
    .style.display = "none";
}

function connectWS(){

  const socket = new SockJS("/ws");

  stompClient = Stomp.over(socket);

  stompClient.connect({}, ()=>{

    console.log("WS Connected");

    stompClient.subscribe(
      `/topic/queue/${queueId}`,
      msg => {

        showInfo(msg.body);

        loadQueueStatus();
      }
    );
  });
}

function showInfo(msg){

  const el =
    document.getElementById("info");

  el.innerText = msg;

  setTimeout(()=>{
    el.innerText="";
  },3000);
}

function goBack(){
  location.href="queue.html";
}

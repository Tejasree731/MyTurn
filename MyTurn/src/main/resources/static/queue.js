const token = localStorage.getItem("token");
const role = localStorage.getItem("role");

if(!token) location.href="login.html";
if(role !== "USER") location.href="dashboard.html";


let stompClient=null;
let subscription=null;
let activeQueue=null;

function showMsg(t){
  const el=document.getElementById("msg");
  el.innerText=t;

  setTimeout(()=>{
    el.innerText="";
  },3000);
}

function connectWS(queueId){

  if(stompClient){
    stompClient.disconnect();
  }

  const socket=new SockJS("/ws");
  stompClient=Stomp.over(socket);

  stompClient.connect({},()=>{

    console.log("WS Connected");

    subscription=stompClient.subscribe(
      `/topic/queue/${queueId}`,
      msg=>{

        document.getElementById("liveBox")
          .classList.remove("hidden");

        document.getElementById("status")
          .innerText=msg.body;
      }
    );

  });
}

function loadQueues(){

  fetch("/api/queue/dashboard",{
    headers:{
      Authorization:"Bearer "+token
    }
  })

  .then(r=>r.json())

  .then(list=>{

    let html="";

    list.forEach(q=>{

      if(!q.active) return;

      html+=`

      <div class="queue-card cursor-pointer"
     onclick="openQueue(${q.queueId}, '${q.name}')">


        <h3 class="text-xl font-semibold mb-3 text-slate-200">
          ${q.name}
        </h3>

        <div class="flex justify-between items-center mb-4">

          <span class="text-slate-400 text-sm">
            In Queue
          </span>

          <span class="queue-count">
            ${q.waiting}
          </span>

        </div>

        <div class="text-center mt-5">

          <button
            onclick="takeToken(${q.queueId})"
            class="take-btn">

            🎟 Take Token

          </button>

        </div>

      </div>

      `;

    });

    document.getElementById("queues").innerHTML=html;

  })

  .catch(()=>{
    showMsg("Server error");
  });

}

function takeToken(id){

  fetch(`/api/queue/join/${id}`,{
    method:"POST",
    headers:{
      Authorization:"Bearer "+token
    }
  })

  .then(r=>r.text())

  .then(msg=>{

    showMsg(msg);

    activeQueue=id;

    connectWS(id);

    loadQueues();
  });

}

loadQueues();
function openQueue(id, name){

  localStorage.setItem("currentQueueId", id);
  localStorage.setItem("currentQueueName", name);

  location.href = "queue-view.html";
}


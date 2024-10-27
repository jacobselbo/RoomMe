
const socket = new WebSocket("ws://" + location.host + "/api/messages");

let messages = {
    "671d659f9d14ac05cf9e4eff": [
        {
            "sender": "671d659f9d14ac05cf9e4eff",
            "receiver": "671d6aa20c5d166618535a5a",
            "timestamp": 0,
            "message": "Hello world!"
        }
    ],
    "671d6aa20c5d166618535a5a": []
};

async function getSelf() {
    return (await fetch("/api/user/self", {
        method: "GET"
    })).json();
}

const userID = (await getSelf()).id;

async function populateMessages() {
    const resp = (await fetch("/api/user/messages", {
        method: "GET"
    })).json();

    messages = {};
    for (const msg of resp) {
        const otherUser = msg.sender === userID ? msg.receiver : msg.sender;
        if (!(otherUser in messages))
            messages[otherUser] = [];
        messages[otherUser].push(msg)
    }

    for (const otherUser of resp) {
        messages[otherUser].sort((a,b) => a.timestamp - b.timestamp)
    }
}

function sendMessage(id, message) {
    sock.send(JSON.stringify({
        id: id,
        message: message
    }));

    messages[id].push({
        sender: userID,
        receiver: id,
        message: message,
        timestamp: Date.now()
    });
}

// TODO: Uncomment
// await populateMessages();

socket.addEventListener("message", (event) => {
    const resp = JSON.parse(event.data);
    messages[resp.id].push({
        sender: resp.id,
        receiver: userID,
        message: resp.message,
        timestamp: resp.timestamp
    });
});

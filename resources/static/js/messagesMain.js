
const sock = new WebSocket("ws://" + location.host + "/api/messages");

let messages = {};

async function getSelf() {
    return await (await fetch("/api/user/self", {
        method: "GET"
    })).json();
}

async function getUser(id) {
    return await (await fetch("/api/user/get/" + id, {
        method: "GET"
    })).json();
}

let userID;
getSelf().then((resp) => {
    userID = resp.id;
})

async function populateMessages() {
    const resp = await (await fetch("/api/user/messages", {
        method: "GET"
    })).json();

    messages = {};
    for (const msg of resp) {
        const otherUser = msg.sender === userID ? msg.receiver : msg.sender;
        if (!(otherUser in messages))
            messages[otherUser] = [];
        if (msg.timestamp === 0)
            continue;
        messages[otherUser].push(msg);
    }

    for (const otherUser in messages) {
        messages[otherUser].sort((a,b) => a.timestamp - b.timestamp);
    }
    render();
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

    render();
}

populateMessages();

sock.addEventListener("message", (event) => {
    const resp = JSON.parse(event.data);
    messages[resp.id].push({
        sender: resp.id,
        receiver: userID,
        message: resp.message,
        timestamp: resp.timestamp
    });
    render();
});

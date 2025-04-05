let interval = null;

function start() {
    interval = setInterval(() => {
        Chat.say("Hello");
    }, 1000);
}

function stop() {
    if (interval !== null) {
        clearInterval(interval);
        interval = null;
    }
}

start();

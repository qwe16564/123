document.addEventListener("DOMContentLoaded", () => {
    const socket = io();

    const messageInput = document.getElementById("message_input");
    const sendButton = document.getElementById("send_button");
    const messagesDiv = document.getElementById("messages");

    // Function to add a message to the chat window
    function addMessage(message, type) {
        const messageElement = document.createElement("div");
        messageElement.textContent = message;
        messageElement.classList.add("message", type === "user" ? "user-message" : "bot-message");
        messagesDiv.appendChild(messageElement);
        messagesDiv.scrollTop = messagesDiv.scrollHeight; // Auto-scroll to the latest message
    }

    // Event listener for receiving messages from the server
    socket.on("message", (msg) => {
        if (msg.startsWith("You: ")) {
            addMessage(msg.substring(5), "user");
        } else if (msg.startsWith("Bot: ")) {
            addMessage(msg.substring(5), "bot");
        } else {
            addMessage(msg, "bot"); // Default to bot for any other message
        }
    });

    // Function to send a message
    function sendMessage() {
        const message = messageInput.value.trim();
        if (message) {
            socket.send(message);
            messageInput.value = "";
        }
    }

    // Event listener for the send button
    sendButton.addEventListener("click", sendMessage);

    // Event listener for pressing "Enter" in the input field
    messageInput.addEventListener("keypress", (event) => {
        if (event.key === "Enter") {
            sendMessage();
        }
    });

    console.log("main.js loaded and chat initialized");
});
from flask import Flask, render_template
from flask_socketio import SocketIO, send

app = Flask(__name__, static_folder='../static', template_folder='../static')
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)

@app.route('/')
def index():
    return render_template('index.html')

@socketio.on('message')
def handle_message(msg):
    print('Message from user: ' + msg)
    # Broadcast the user's message to all clients
    send("You: " + msg, broadcast=True)

    # Simple bot logic
    bot_response = "Bot: I am a simple bot. I received your message: '{}'".format(msg)
    print('Message from bot: ' + bot_response)
    # Send the bot's response back to all clients
    send(bot_response, broadcast=True)

if __name__ == '__main__':
    socketio.run(app, debug=True, allow_unsafe_werkzeug=True)
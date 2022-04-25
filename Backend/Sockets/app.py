from flask import Flask, request

import socket

import os
import threading

import userSocketHandler


app = Flask(__name__)

# IP and Ports for socket connections
HOST_ROBOT = '10.42.0.112'
PORT_ROBOT = 9999

HOST_USER = "192.168.101.1"
PORT_USER = 5000


# API routes
@app.route("/sendInstruction", methods = ['GET'])
def sendInstruction():
    global robotSocket
    if (request.method == 'GET'):
        send = b"action " + request.args.get('instruction').encode('UTF-8')
        robotSocket.sendall(send)
        return ""

@app.route("/ticTacToePosition", methods = ['GET'])
def ticTacToePosition():
    global robotSocket
    if (request.method == 'GET'):
        send = b"tic_tac_toe_put_chip " + " ".join([request.args.get('x'), request.args.get('y'), request.args.get('player')]).encode('UTF-8')
        robotSocket.sendall(send)
        return ""

@app.route("/connect4Start", methods = ['GET'])
def connect4Start():
    global robotSocket
    if (request.method == 'GET'):
        send=b"connect4_start "
        robotSocket.sendall(send)
        return ""

@app.route("/connect4Position", methods = ['GET'])
def connect4Position():
    global robotSocket
    if (request.method == 'GET'):
        send=b"connect4_put_chip " + " ".join([request.args.get('x'), request.args.get('y'), request.args.get('player')]).encode('UTF-8')
        robotSocket.sendall(send)
        robotSocket.recv(1024)
        return ""

def runClientSocket(socket):
    while True:
        try:
            connection, address = socket.accept()
            clientThread = threading.Thread(target = userSocketHandler.userConnection, args = (connection, address))
            clientThread.start()
            clientThread.join()
        except Exception:
            os._exit(1)
            break
    usersSocket.close()


if __name__ == '__main__':
    # Connection with robot
    #robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    #robotSocket.connect((HOST_ROBOT, PORT_ROBOT))

    # Connection with user's app
    usersSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    usersSocket.bind((HOST_USER, PORT_USER))
    usersSocket.listen(5)
    clientSocketThread = threading.Thread(target = runClientSocket, args = (usersSocket, ))
    clientSocketThread.start()

    # Serves API using Flask
    app.run(host = '0.0.0.0', port = 10000)
from flask import Flask, request

import socket


app = Flask(__name__)

HOST = '10.42.0.112'
PORT = 9999

socket_conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket_conn.connect((HOST, PORT))



@app.route("/sendInstruction", methods = ['GET'])
def sendInstruction():
    global socket_conn
    if (request.method == 'GET'):
        send = b"action " + request.args.get('instruction').encode('UTF-8')
        socket_conn.sendall(send)
        return ""

#@app.route("/sendCustomProgram", methods = ['GET', 'POST'])
#def sendCustomProgram():
#    global socket_conn
#    if (request.method == 'POST'):

@app.route("/ticTacToePosition", methods = ['GET'])
def ticTacToePosition():
    global socket_conn
    if (request.method == 'GET'):
        send = b"tic_tac_toe_put_chip " + " ".join([request.args.get('x'), request.args.get('y'), request.args.get('player')]).encode('UTF-8')
        socket_conn.sendall(send)
        return ""

@app.route("/connect4Start", methods = ['GET'])
def connect4Start():
    global socket_conn
    if (request.method == 'GET'):
        send=b"connect4_start "
        socket_conn.sendall(send)
        return ""

@app.route("/connect4Position", methods = ['GET'])
def connect4Position():
    global socket_conn
    if (request.method == 'GET'):
        send=b"connect4_put_chip " + " ".join([request.args.get('x'), request.args.get('y'), request.args.get('player')]).encode('UTF-8')
        socket_conn.sendall(send)
        socket_conn.recv(1024)
        return ""

@app.route("/requestPermission", methods = ['GET'])
def requestPermission():
    user = request.args.get('user')

    

def ticTacToe(player, positionX, positionY):
    pass

# [[[x], [x], [x]], 
#  [[x], [x], [x]], 
#  [[x], [x], [x]]]

def checkWinner(listPositions):
    pass



    


if __name__ == '__main__':

    app.run(host = '0.0.0.0', port = 10000)# , debug = True)#host = '0.0.0.0', , port = 10000)

from flask import Flask, request, jsonify

from user import *
from ticTacToe import *

import socket
import threading

app = Flask(__name__)


# IP and Ports for socket connections
HOST_ROBOT = '192.168.100.15'
PORT_ROBOT = 9999

# Global variables
connectedUsers = {}
connectedAdmins = 0
ticTacToe = None
actualActivity = None


# General functionallities
@app.route('/user/login', methods = ['POST'])
def loginUser():
    global connectedUsers
    if (request.method == 'POST'):
        connectedUsers[request.form['ipAddress']] = User(request.form['name'], request.form['surname'], request.form['isAuthorized'])
        response = {'response': 'login-user-success'}
        return jsonify(response)
    return ""

@app.route('/admin/login', methods = ['GET'])
def loginAdmin():
    global connectedAdmins
    if (request.method == 'GET'):
        if (connectedAdmins == 0):
            connectedAdmins += 1
            response = {'response': 'login-admin-success'}
            return jsonify(response)
        else: 
            response = {'response': 'another-admin-loged'}
            return jsonify(response)
    return ""

@app.route('/user/logout', methods = ['GET'])
def userLogout():
    global connectedUsers
    if (request.method == 'GET'):
        userIP = request.args.get('user')
        connectedUsers.pop(userIP)
        response = {'response': 'logout-user-success'}
        return jsonify(response)
    return ""

@app.route('/admin/logout', methods = ['GET'])
def adminLogout():
    global connectedAdmins
    if (request.method == 'GET'):
        connectedAdmins = 0 
        response = {'response': 'logout-admin-success'}
        return jsonify(response)
    return ""

@app.route('/check-robot-connection')
def checkRobotConnection():
    if (request.method == 'GET'):
        try:
            robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            robotSocket.connect((HOST_ROBOT, PORT_ROBOT))
            response = {'response': 'robot-connection-success'}
        except socket.error:
            response = {'response': 'robot-connection-failed'}
        return jsonify(response)

@app.route('/user/check-permissions', methods = ['GET'])
def checkPermissionsUser():
    global connectedUsers
    global actualActivity
    if (request.method == 'GET'):
        userIP = request.args.get('user')
        activity = request.args.get('activity')
        auth = connectedUsers[userIP].isAuthorized
        if (activity != actualActivity):
            response = {'response': auth, 'activity': 'not-match'}
        else:
            response = {'response': auth, 'activity': 'match'}
        return jsonify(response)
    return ""

@app.route('/admin/actual-activity')
def specifyActualRobotActivity():
    global actualActivity
    if (request.method == 'GET'):
        actualActivity = request.args.get('activity')
    return ""

@app.route('/user/change-permissions', methods = ['GET'])
def changePermissionsUser():
    global connectedUsers
    if (request.method == 'GET'):
        userIP = request.args.get('user')
        auth = request.args.get('isAuthorized')
        userObject = connectedUsers[userIP]
        userObject.setIsAuthorized(auth)
        connectedUsers[userIP] = userObject
        response = {'response': 'change-permissions-success'}
        return jsonify(response)
    return ""

@app.route('/list-users', methods = ['GET'])
def listUsers():
    global connectedUsers
    listResponse = []
    if (request.method == 'GET'):
        if (len(connectedUsers) != 0):
            response = {}
            for user in connectedUsers:
                value = connectedUsers[user]
                listResponse.append({'uid': user, 'name': value.getName(), 'surname': value.getSurname(), 'isAuthorized': value.getIsAuthorized()})
                response["users"] = listResponse
            return jsonify(response)
    return ""


# Specific functionallities
# Experiments
@app.route("/experiment", methods = ['GET'])
def experiment():
    robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    robotSocket.connect((HOST_ROBOT, PORT_ROBOT))
    if (request.method == 'GET'):
        name = request.args.get('name')
        if (name == "series_de_dissolucio"):
            send = b"experiment series_de_dissolucio"
        elif (name == "barreja_colors"):
            send = b"experiment barreja_colors"
        elif (name == "capes_de_densitat"):
            send = b"experiment capes_de_densitat"
        robotSocket.sendall(send)
        robotSocket.close()
    return ""

# Interact with robot
@app.route("/startInteract", methods = ['GET'])
def startInteract():
    if (request.method == 'GET'):
        robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        robotSocket.connect((HOST_ROBOT, PORT_ROBOT))
        send = "start_interact"
        robotSocket.sendall(send)
        robotSocket.close()
    return ""

@app.route("/sendInstruction", methods = ['GET'])
def sendInstruction():
    if (request.method == 'GET'):
        robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        robotSocket.connect((HOST_ROBOT, PORT_ROBOT))
        send = b"action " + request.args.get('instruction').encode('UTF-8')
        robotSocket.sendall(send)
        robotSocket.close()
    return ""

# Custom program
@app.route('/custom-program', methods = ['POST'])
def customProgram():
    if (request.method == 'POST'):
        actions = request.form['actions']
        if (len(actions) != 0):
            print(actions)
            robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            robotSocket.connect((HOST_ROBOT, PORT_ROBOT))
            send = b"program " + actions.encode('UTF-8')
            robotSocket.sendall(send)
            robotSocket.close()
            response = {'response': 'custom-program-actions-success'}
            return jsonify(response)
    return ""

# Tic tac toe
@app.route("/start-ticTacToe")
def startTicTacToe():
    global ticTacToe
    if (request.method == 'GET'):
        if (ticTacToe is None):
            ticTacToe = TicTacToe()
        ticTacToe.setTicTacToeStatus(1)
        if (ticTacToe.getTicTacToePlayers() == 0):
            ticTacToe.setTicTacToePlayers(1)
            return {'response': 'tic-tac-toe-start-success', 'player': 1}
        elif (ticTacToe.getTicTacToePlayers() == 1):
            ticTacToe.setTicTacToePlayers(2)
            return {'response': 'tic-tac-toe-start-success', 'player': 2}
        else:
            return {'response': 'game-is-full'}
    return ""

@app.route("/finish-ticTacToe")
def finishTicTacToe():
    global ticTacToe
    if (request.method == 'GET'):
        ticTacToe = None
    return ""

@app.route("/status-ticTacToe")
def statusTicTacToe():
    global ticTacToe
    if (request.method == 'GET'):
        status = ticTacToe.checkBoard()
        if (status == 1):
            return {'response': 'winner-1'}
        elif (status == 2):
            return {'response': 'winner-2'}
        else:
            if (ticTacToe.getTicTacToePlayers() != 2):
                print 
                return {'response': 'waiting-for-player'}
            elif (ticTacToe.getTicTacToeBoard() == [[0,0,0],[0,0,0],[0,0,0]]): 
                return {'response': 'tic-tac-toe-init', 'player': ticTacToe.getPlayer()}
            elif (ticTacToe.getTicTacToeStatus() == 0): 
                return {'response': 'game-is-over'}
            return {'response': 'no-winner', 'x': ticTacToe.getX(), 'y': ticTacToe.getY(), 'player': ticTacToe.getPlayer()}
    return ""
 
@app.route("/ticTacToePosition", methods = ['GET'])
def ticTacToePosition():
    global ticTacToe
    if (request.method == 'GET'):
        ticTacToe.setX(request.args.get('x')) 
        ticTacToe.setY(request.args.get('y')) 
        ticTacToe.setPlayer(request.args.get('player'))
        if (ticTacToe.getTicTacToeBoard()[int(ticTacToe.getX())][int(ticTacToe.getY())] == 0):
            board = ticTacToe.getTicTacToeBoard()
            board[int(ticTacToe.getX())][int(ticTacToe.getY())] = int(ticTacToe.getPlayer())
            ticTacToe.setTicTacToeBoard(board)
            robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            robotSocket.connect((HOST_ROBOT, PORT_ROBOT))
            send = b"tic_tac_toe_put_chip " + " ".join([ticTacToe.getX(), ticTacToe.getY(), ticTacToe.getPlayer()]).encode('UTF-8')
            robotSocket.sendall(send)
            robotSocket.close()
            return {'response': 'tic-tac-toe-position-success', 'x': ticTacToe.getX(), 'y': ticTacToe.getY()}       
        else:
            return {'response': 'tic-tac-toe-position-full'}
    return ""

if __name__ == '__main__':
    app.run(host = '0.0.0.0', port = 10000, debug = True)
    

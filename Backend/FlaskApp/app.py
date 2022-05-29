from flask import Flask, request, jsonify

from user import *
from ticTacToe import *
from connect4 import *
from robot import *

import socket
import threading

app = Flask(__name__)


# IP and Ports for socket connections
HOST_ROBOT = '192.168.100.10'
PORT_ROBOT = 9999

# Global variables
connectedUsers = {}
connectedAdmins = 0
ticTacToe = None
connect4 = None

robot = Robot()
robot.setActualRobot(2)
robot.setActualActivity("connect4")
robot.setNumberAuthorizedUsers(0)

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

@app.route('/admin/set-robot', methods = ['GET'])
def setRobot():
    global admin
    if (request.method == 'GET'):
        robot.setActualRobot(request.args.get('robot'))
    return ""

@app.route('/user/logout', methods = ['GET'])
def userLogout():
    global connectedUsers
    global robot
    if (request.method == 'GET'):
        userIP = request.args.get('user')
        user = connectedUsers[userIP]
        if (user.getIsAuthorized() == "true"):
            robot.setNumberAuthorizedUsers(robot.getNumberAuthorizedUsers() - 1)
        connectedUsers.pop(userIP)
        response = {'response': 'logout-user-success'}
        return jsonify(response)
    return ""

@app.route('/admin/logout', methods = ['GET'])
def adminLogout():
    global connectedAdmins
    global robot
    if (request.method == 'GET'):
        connectedAdmins = 0 
        robot.setActualRobot(0)
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
    global robot
    if (request.method == 'GET'):
        userIP = request.args.get('user')
        activity = request.args.get('activity')
        auth = connectedUsers[userIP].isAuthorized
        actRobot = robot.getActualRobot()
        if (activity != robot.getActualActivity()):
            response = {'response': auth, 'activity': 'not-match', 'robot': actRobot}
        else:
            response = {'response': auth, 'activity': 'match', 'robot': actRobot}
        return jsonify(response)
    return ""

@app.route('/admin/actual-activity')
def specifyActualRobotActivity():
    global robot
    if (request.method == 'GET'):
        robot.setActualActivity(request.args.get('activity'))
        robot.setNumberAuthorizedUsers(0)
        for user in connectedUsers:
            connectedUsers[user].setIsAuthorized("false")
    return ""

@app.route('/user/change-permissions', methods = ['GET'])
def changePermissionsUser():
    global connectedUsers
    global robot
    if (request.method == 'GET'):
        userIP = request.args.get('user')
        auth = request.args.get('isAuthorized')
        activity = robot.getActualActivity()
        num = robot.getNumberAuthorizedUsers()
        print (userIP, auth, activity, num)
        userObject = connectedUsers[userIP]
        if (auth == "true" and ((activity == "experiments" and num < 1) or (activity == "interact" and num < 1) or (activity == "custom_program" and num < 1) or (activity == "tic_tac_toe" and num < 2) or (activity == "connect4" and num < 2))):
            userObject.setIsAuthorized("true")
            robot.setNumberAuthorizedUsers(num + 1)
            response = {'response': 'change-permissions-success'}
        elif (auth == "false" and num > 0):
            userObject.setIsAuthorized("false")
            robot.setNumberAuthorizedUsers(num - 1)
            response = {'response': 'change-permissions-success'}
        else:
            if (auth == "true" and ((activity == "experiments" and num == 1) or (activity == "interact" and num == 1) or (activity == "custom_program" and num == 1) or (activity == "tic_tac_toe" and num == 2) or (activity == "connect4" and num == 2))):
                response = {'response': 'max-number-of-users'}
            else:
                response = {'response': 'change-permissions-error'}
        connectedUsers[userIP] = userObject
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
        #print ("aaa", robot.getRobotIP())
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
            #response = {'response': 'custom-program-actions-success'}
            return {'response': 'custom-program-actions-success'}#jsonify(response)
    return ""

# Tic tac toe
@app.route("/start-ticTacToe")
def startTicTacToe():
    global ticTacToe
    if (request.method == 'GET'):
        if (ticTacToe is None):
            ticTacToe = TicTacToe()
        ticTacToe.setTicTacToeStatus(1)
        ticTacToe.setTicTacToeBoard([[0,0,0],[0,0,0],[0,0,0]])      ##
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
    #ticTacToe.setTicTacToePlayers(0)
    if (request.method == 'GET'):
        ticTacToe = None
    return ""

@app.route("/status-ticTacToe")
def statusTicTacToe():
    global ticTacToe
    if (request.method == 'GET'):
        try:
            status = ticTacToe.checkBoard()
        except:
            return {'response': 'game-is-over'}
        if (status == 1):
            return {'response': 'winner-1'}
        elif (status == 2):
            return {'response': 'winner-2'}
        else:
            if (ticTacToe.getTicTacToePlayers() != 2):
                return {'response': 'waiting-for-player'}
            elif (ticTacToe.getTicTacToeBoard() == [[0,0,0],[0,0,0],[0,0,0]]): 
                return {'response': 'tic-tac-toe-init', 'player': ticTacToe.getPlayer()}
            elif (ticTacToe.getTicTacToeStatus() == 0):
                #try:
                #    ticTacToe.setTicTacToePlayers(0)        ###
                #except:
                #    pass
                return {'response': 'game-is-over'}
            elif (ticTacToe.checkBoardFull()):
                #ticTacToe.setTicTacToePlayers(0)            ###
                return {'response': 'board-is-full'}
            return {'response': 'no-winner', 'x': ticTacToe.getX(), 'y': ticTacToe.getY(), 'player': ticTacToe.getPlayer()}
    return ""
 
@app.route("/ticTacToePosition", methods = ['GET'])
def ticTacToePosition():
    global ticTacToe
    if (request.method == 'GET'):
        x = request.args.get('x')
        y = request.args.get('y')
        if (ticTacToe.getTicTacToeBoard()[int(x)][int(y)] == 0):
            ticTacToe.setX(x) 
            ticTacToe.setY(y)
            ticTacToe.setPlayer(request.args.get('player'))
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

#Connect 4
@app.route("/start-connect4")
def startConnect4():
    global connect4
    if (request.method == 'GET'):
        if (connect4 is None):
            print ("INIT CONNECT 4")
            connect4 = Connect4()
        connect4.setConnect4Status(1)
        connect4.setConnect4Board([[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]])
        if (connect4.getConnect4Players() == 0):
            connect4.setConnect4Players(1)
            return {'response': 'connect4-start-success', 'player': 1}
        elif (connect4.getConnect4Players() == 1):
            connect4.setConnect4Players(2)
            return {'response': 'connect4-start-success', 'player': 2}
        else:
            return {'response': 'game-is-full'}
    return ""

@app.route("/finish-connect4")
def finishConnect4():
    global connect4
    if (request.method == 'GET'):
        connect4 = None
    return ""

@app.route("/status-connect4")
def statusConnect4():
    global connect4
    status = 0
    if (request.method == 'GET'):
        try:
            status = int(connect4.checkBoard())
            if (status == 1):
                return {'response': 'winner-1'}
            elif (status == 2):
                return {'response': 'winner-2'}
            else:
                if (connect4.getConnect4Players() != 2):
                    return {'response': 'waiting-for-player'}
                elif (connect4.getConnect4Board() == [[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]]): 
                    return {'response': 'connect4-init', 'player': connect4.getPlayer()}
                elif (connect4.getConnect4Status() == 0):
                    connect4.setConnect4Players(0)
                    return {'response': 'game-is-over'}
                elif (connect4.checkBoardFull()):
                    connect4.setConnect4Players(0)
                    return {'response': 'board-is-full'}
                return {'response': 'no-winner', 'x': connect4.getX() + 1, 'y': connect4.getY() + 1, 'player': connect4.getPlayer()}
        except:
            return {'response': 'game-is-over'}
    return ""

@app.route("/connect4Position", methods = ['GET'])
def connect4Position():
    global connect4
    if (request.method == 'GET'):
        column = int(request.args.get('column')) - 1
        print("Column ", column)
        row = int(connect4.checkRowPosition(column)) - 1
        print("row ", row)
        if (connect4.getConnect4Board()[row][column] == 0):
            connect4.setX(row) 
            connect4.setY(column)
            connect4.setPlayer(request.args.get('player'))
            board = connect4.getConnect4Board()
            board[int(connect4.getX())][int(connect4.getY())] = int(connect4.getPlayer())
            connect4.setConnect4Board(board)
            #robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            #robotSocket.connect((HOST_ROBOT, PORT_ROBOT))
            #send = b"connect4_put_chip " + " ".join([connect4.getX(), connect4.getY(), connect4.getPlayer()]).encode('UTF-8')
            #robotSocket.sendall(send)
            #robotSocket.close()
            return {'response': 'connect4-position-success', 'x': connect4.getX() + 1, 'y': connect4.getY() + 1}       
        else:
            return {'response': 'connect4-position-full'}
    return ""

@app.after_request
def after(response):
    print ("")
    print(response.get_data())
    print ("")
    return response

if __name__ == '__main__':
    app.run(host = '0.0.0.0', port = 10000, debug = True)
    

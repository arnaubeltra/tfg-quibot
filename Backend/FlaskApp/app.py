from flask import Flask, request, jsonify
import socket

from user import *
from robot import *
from ticTacToe import *
from connect4 import *


# Flask instance
app = Flask(__name__)

# Port for socket connections with robot
PORT_ROBOT = 9999

# Global variables
connectedUsers = {}
connectedAdmins = 0

robot = Robot()
#robot.setCurrentRobot('2')
#robot.setNumberAuthorizedUsers(0)

ticTacToe = None
connect4 = None


# General functionalities

"""
Route to perform a login for a "Normal user".
Creates a User object for each login, and stores it at the connectedUsers dictionary.

connectedUsers dictionary:
    - key --> IP address
    - value --> User object

Input arguments in body (POST):
    - ipAddress --> string
    - name --> string
    - surname --> string
    - isAuthorized --> string
"""
@app.route('/user/login', methods = ['POST'])
def loginUser():
    global connectedUsers
    if (request.method == 'POST'):
        connectedUsers[request.form['ipAddress']] = User(request.form['name'], request.form['surname'], request.form['isAuthorized'])
        return {'response': 'login-user-success'}
    return ""


"""
Route to perform a login for an "Administrator user".
Checks connectedAdmins value to make sure that only 1 admin is logged at a time.
"""
@app.route('/admin/login', methods = ['GET'])
def loginAdmin():
    global connectedAdmins
    if (request.method == 'GET'):
        if (connectedAdmins == 0):
            connectedAdmins += 1
            return {'response': 'login-admin-success'}
        else: 
            return {'response': 'another-admin-loged'}
    return ""


"""
Route to perform a logout for a "Normal user".
Erases User object from the connectedUsers dictionary by getting the IP address of the user (key).

Input arguments (GET):
    - userIp --> string
"""
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
        return {'response': 'logout-user-success'}
    return ""


"""
Route to perform a logout for an "Administrator user".
Sets connectedAdmins value to 0.
"""
@app.route('/admin/logout', methods = ['GET'])
def adminLogout():
    global connectedAdmins
    global robot
    if (request.method == 'GET'):
        connectedAdmins = 0 
        robot.setCurrentRobot(0)
        return {'response': 'logout-admin-success'}
    return ""


"""
Sets the current robot being used, selected by the administrator.

- ROBOT 1D --> 1
- ROBOT 2D --> 2

Input arguments (GET):
    - robot --> string
"""
@app.route('/admin/set-robot', methods = ['GET'])
def setRobot():
    global robot
    if (request.method == 'GET'):
        robot.setCurrentRobot(request.args.get('robot'))
    return ""


"""
Sets the current activity that the robot has to perform.
Selected by the administrator when controling authorizations.
All previous authorizations are automaticaly revoked.

Input arguments (GET):
    - activity --> string
"""
@app.route('/admin/current-activity', methods = ['GET'])
def specifyCurrentRobotActivity():
    global robot
    if (request.method == 'GET'):
        robot.setCurrentActivity(request.args.get('activity'))
        robot.setNumberAuthorizedUsers(0)
        for user in connectedUsers:
            connectedUsers[user].setIsAuthorized("false")
    return ""


"""
Checks robot connection, to make sure the server is able to establish communication.
"""
@app.route('/check-robot-connection', methods = ['GET'])
def checkRobotConnection():
    global robot
    if (request.method == 'GET'):
        robotNum = request.args.get('robot')
        if (robotNum == '1'):
            ip = '192.168.100.5'
        elif (robotNum == '2'):
            ip = '192.168.100.6'
        try:
            robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            robotSocket.connect((ip, PORT_ROBOT))
            return {'response': 'robot-connection-success'}
        except socket.error:
            return {'response': 'robot-connection-failed'}
    return ""    


"""
Checks permissions of a certain user, to know if it can interact with the robot.
It also checks if the robot current activity equals to the user current activity.

Input arguments (GET):
    - user --> string
    - activity --> string
"""
@app.route('/user/check-permissions', methods = ['GET'])
def checkPermissionsUser():
    global connectedUsers
    global robot
    if (request.method == 'GET'):
        userIP = request.args.get('user')
        activity = request.args.get('activity')
        auth = connectedUsers[userIP].isAuthorized
        actRobot = robot.getCurrentRobot()
        if (activity != robot.getCurrentActivity()):
            return {'response': auth, 'activity': 'not-match', 'robot': actRobot}
        else:
            return {'response': auth, 'activity': 'match', 'robot': actRobot}
    return ""


"""
Changes permissions for a certain user. Gives or revokes permission of interaction.
It checks if the current activity has the maximum number of users authorized to avoid 
having too many authorized users.

Input arguments (GET):
    - user --> string
    - isAuthorized --> string
"""
@app.route('/user/change-permissions', methods = ['GET'])
def changePermissionsUser():
    global connectedUsers
    global robot
    if (request.method == 'GET'):
        userIP = request.args.get('user')
        auth = request.args.get('isAuthorized')
        activity = robot.getCurrentActivity()
        num = robot.getNumberAuthorizedUsers()
        userObject = connectedUsers[userIP]
        if (auth == "true" and ((activity == "experiments" and num < 1) or (activity == "interact" and num < 1) or (activity == "custom_program" and num < 1) or (activity == "tic_tac_toe" and num < 2) or (activity == "connect4" and num < 2))):
            userObject.setIsAuthorized("true")
            robot.setNumberAuthorizedUsers(num + 1)
            return {'response': 'change-permissions-success'}
        elif (auth == "false" and num > 0):
            userObject.setIsAuthorized("false")
            robot.setNumberAuthorizedUsers(num - 1)
            return {'response': 'change-permissions-success'}
        else:
            if (auth == "true" and ((activity == "experiments" and num == 1) or (activity == "interact" and num == 1) or (activity == "custom_program" and num == 1) or (activity == "tic_tac_toe" and num == 2) or (activity == "connect4" and num == 2))):
                return {'response': 'max-number-of-users'}
            else:
                return {'response': 'change-permissions-error'}
        connectedUsers[userIP] = userObject
    return ""


"""
Returns a list of the users that performed a login as "Normal users".
List is returned in a JSON format.
"""
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


# Specific functionalities
# Experiments
"""
Makes the robot to execute an experiment.
Experiments can be:
    - Sèries de dissolució
    - Barreja de colors primaris
    - Capes de densitat

Input arguments (GET):
    - name --> string
"""
@app.route("/experiment", methods = ['GET'])
def experiment():
    global robot
    if (request.method == 'GET'):
        robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        robotSocket.connect((robot.getRobotIP(), PORT_ROBOT))
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
"""
Makes the robot to start the interaction, configuring the Medium board to be used.
"""
@app.route("/startInteract", methods = ['GET'])
def startInteract():
    global robot
    if (request.method == 'GET'):
        robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            robotSocket.connect((robot.getRobotIP(), PORT_ROBOT))
            send = "start_interact"
            robotSocket.sendall(send)
            robotSocket.close()
        except:
            pass
    return ""


"""
Sends an instruction that the robot will perform.
This is the manual movement system.

Input arguments (GET):
    - instruction --> string
"""
@app.route("/sendInstruction", methods = ['GET'])
def sendInstruction():
    global robot
    if (request.method == 'GET'):
        robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        robotSocket.connect((robot.getRobotIP(), PORT_ROBOT))
        send = b"action " + request.args.get('instruction').encode('UTF-8')
        robotSocket.sendall(send)
        robotSocket.close()
    return ""


# Custom program
"""
The user creates a custom program for the robot, and this route sends this
list to the robot. Actions will be executed one after the other.

Input arguments in body (POST):
    - actions --> string
"""
@app.route('/custom-program', methods = ['POST'])
def customProgram():
    global robot
    if (request.method == 'POST'):
        actions = request.form['actions']
        if (len(actions) != 0):
            robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            robotSocket.connect((robot.getRobotIP(), PORT_ROBOT))
            send = b"program " + actions.encode('UTF-8')
            robotSocket.sendall(send)
            robotSocket.close()
            return {'response': 'custom-program-actions-success'}
    return ""


# Tic tac toe
"""
Route to start Tic tac toe game. 
Asigns a number to indentify each player and avoids having too much players in a game.
"""
@app.route("/start-ticTacToe", methods = ['GET'])
def startTicTacToe():
    global ticTacToe
    if (request.method == 'GET'):
        if (ticTacToe is None):
            ticTacToe = TicTacToe()
        ticTacToe.setTicTacToeStatus(1)
        ticTacToe.setTicTacToeBoard([[0,0,0],[0,0,0],[0,0,0]])
        if (ticTacToe.getTicTacToePlayers() == 0):
            ticTacToe.setTicTacToePlayers(1)
            return {'response': 'tic-tac-toe-start-success', 'player': 1}
        elif (ticTacToe.getTicTacToePlayers() == 1):
            ticTacToe.setTicTacToePlayers(2)
            return {'response': 'tic-tac-toe-start-success', 'player': 2}
        else:
            return {'response': 'game-is-full'}
    return ""


"""
Route to finish a Tic tac toe game by forcing its end.
Used when a user whats to finish the game without waiting for it to end.
"""
@app.route("/finish-ticTacToe", methods = ['GET'])
def finishTicTacToe():
    global ticTacToe
    if (request.method == 'GET'):
        ticTacToe = None
    return ""


"""
Returns the status of the current Tic tac toe game.
Checks multiple aspects, such as waiting for players, updating the board status
giving the winners, updating when a game is over...
"""
@app.route("/status-ticTacToe", methods = ['GET'])
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
                return {'response': 'game-is-over'}
            elif (ticTacToe.checkBoardFull()):
                return {'response': 'board-is-full'}
            return {'response': 'no-winner', 'x': ticTacToe.getX(), 'y': ticTacToe.getY(), 'player': ticTacToe.getPlayer()}
    return ""


"""
This route is used when users select a position to place its piece.
Checks if the position is free and makes the robot to move to the position to pour the liquid
that represents each player.

Input arguments (GET):
    - x --> string
    - y --> string
    - player --> string
"""
@app.route("/ticTacToePosition", methods = ['GET'])
def ticTacToePosition():
    global ticTacToe
    global robot
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
            robotSocket.connect((robot.getRobotIP(), PORT_ROBOT))
            send = b"tic_tac_toe_put_chip " + " ".join([ticTacToe.getX(), ticTacToe.getY(), ticTacToe.getPlayer()]).encode('UTF-8')
            robotSocket.sendall(send)
            robotSocket.close()
            return {'response': 'tic-tac-toe-position-success', 'x': ticTacToe.getX(), 'y': ticTacToe.getY()}       
        else:
            return {'response': 'tic-tac-toe-position-full'}
    return ""


#Connect 4
"""
Route to start Connect 4 game. 
Asigns a number to indentify each player and avoids having too much players in a game.
"""
@app.route("/start-connect4", methods = ['GET'])
def startConnect4():
    global connect4
    global robot
    if (request.method == 'GET'):
        if (connect4 is None):
            connect4 = Connect4()
        connect4.setConnect4Status(1)
        connect4.setConnect4Board([[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]])
        if (connect4.getConnect4Players() == 0):
            connect4.setConnect4Players(1)
            return {'response': 'connect4-start-success', 'player': 1}
        elif (connect4.getConnect4Players() == 1):
            connect4.setConnect4Players(2)
            robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            robotSocket.connect((robot.getRobotIP(), PORT_ROBOT))
            send = b"connect4_start"
            robotSocket.sendall(send)
            robotSocket.close()
            return {'response': 'connect4-start-success', 'player': 2}
        else:
            return {'response': 'game-is-full'}
    return ""


"""
Route to finish a Connect 4 game by forcing its end.
Used when a user whats to finish the game without waiting for it to end.
"""
@app.route("/finish-connect4", methods = ['GET'])
def finishConnect4():
    global connect4
    if (request.method == 'GET'):
        connect4 = None
    return ""


"""
Returns the status of the current Connect 4 game.
Checks multiple aspects, such as waiting for players, updating the board status
giving the winners, updating when a game is over...
"""
@app.route("/status-connect4", methods = ['GET'])
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


"""
This route is used when users select a column to place its piece.
Checks if the column has free space and makes the robot to move to the position to pour the liquid
that represents each player into the position that the piece is placed.

Input arguments (GET):
    - column --> string
    - player --> string
"""
@app.route("/connect4Position", methods = ['GET'])
def connect4Position():
    global robot
    global connect4
    if (request.method == 'GET'):
        column = int(request.args.get('column')) - 1
        row = int(connect4.checkRowPosition(column)) - 1
        if (connect4.getConnect4Board()[row][column] == 0):
            connect4.setX(row) 
            connect4.setY(column)
            connect4.setPlayer(request.args.get('player'))
            board = connect4.getConnect4Board()
            board[int(connect4.getX())][int(connect4.getY())] = int(connect4.getPlayer())
            connect4.setConnect4Board(board)
            robotSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            robotSocket.connect((robot.getRobotIP(), PORT_ROBOT))
            send = b"connect4_put_chip " + " ".join([str(connect4.getX()), str(connect4.getY()), str(connect4.getPlayer())]).encode('UTF-8')
            robotSocket.sendall(send)
            robotSocket.close()
            return {'response': 'connect4-position-success', 'x': connect4.getX() + 1, 'y': connect4.getY() + 1}       
        else:
            return {'response': 'connect4-position-full'}
    return ""

@app.after_request
def after(response):
    print ("")
    print(response.get_data())
    return response

if __name__ == '__main__':
    """
    A Flask server is used to serve the API.

    Host = 0.0.0.0 --> to make sure that all devices in the local area network can access the server.
    """
    app.run(host = '0.0.0.0', port = 10000, debug = True)
    

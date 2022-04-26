from flask import Flask, request, jsonify

from user import *

app = Flask(__name__)

connectedUsers = {}
connectedAdmins = 0

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

@app.route('/user/check-permissions', methods = ['GET'])
def checkPermissionsUser():
    global connectedUsers
    if (request.method == 'GET'):
        userIP = request.args.get('user')
        auth = connectedUsers[userIP].isAuthorized
        response = {'response': auth}
        return jsonify(response)
    return ""

@app.route('/list-users', methods = ['GET'])
def listUsers():
    global connectedUsers
    if (request.method == 'GET'):
        if (len(connectedUsers) != 0):
            response = {}
            for user in connectedUsers:
                value = connectedUsers[user]
                response[user] = {'name': value.getName(), 'surname': value.getSurname(), 'isAuthorized': value.getIsAuthorized()}
                return jsonify(response)
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

#Interaction with robot
@app.route("/sendInstruction", methods = ['GET'])
def sendInstruction():
    global robotSocket
    if (request.method == 'GET'):
        send = b"action " + request.args.get('instruction').encode('UTF-8')
        robotSocket.sendall(send)
    return ""

if __name__ == '__main__':
    app.run(host = '0.0.0.0', port = 10000, debug = True)
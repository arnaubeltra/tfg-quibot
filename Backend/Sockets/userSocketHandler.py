import socket
import os
import threading
import json
from collections import namedtuple

from user import *
from admin import *

HOST = "192.168.101.1"
PORT = 5000

connectedUsers = {}
connectedAdmin = {}

def userConnection(connection, address):
    with connection:
        print(f"Connected with {address}")
        while True:
            dataSocket = connection.recv(1024)
            data = dataSocket.decode("utf-8")
            #print (address, '>>', str(data))

            if (data == "check_permissions\n"):
                connection.sendall((str(connectedUsers[address[0]].isAuthorized) + "\n").encode())
            elif (data == "game_tic_tac_toe\n"):
                pass
            elif (data == "game_connect_4\n"):
                pass
            else:
                try:
                    userDataToClean = data.split('\n')[0]
                    userData = json.loads(userDataToClean)

                    if (userData['userType'] == "user"):
                        user = User(userData['ipAddress'], userData['name'], userData['surname'], userData['isAuthorized'], userData['userType'])
                        connectedUsers[address[0]] = user
                    elif (userData['userType'] == "admin"):
                        admin = Admin(userData['ipAddress'], userData['userType'])
                        connectedAdmin[address[0]] = admin
                except:
                    pass
    connection.close()


if __name__ == '__main__':
    UsersSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    UsersSocket.bind((HOST, PORT))
    UsersSocket.listen(5)

    while True:
        try:
            connection, address = UsersSocket.accept()
            clientThread = threading.Thread(target = userConnection, args = (connection, address))
            clientThread.start()
            clientThread.join()
        except KeyboardInterrupt:
            os._exit(1)
            break

    UsersSocket.close()

"""
Class used to handle user information when it logs in, and to manage 
the state when the user participates to some multiplayer games.

Handles the following:
    - Name of the user that logged in
    - Surname of the user that logged in
    - Authorization of interaction
    - Identification number of player when playing multiplayer games
    - Current user actvity
"""
class User(object):
    def __init__(self, name, surname, isAuthorized, player = None, activity = None):
        self.name = name
        self.surname = surname
        self.isAuthorized = isAuthorized
        self.player = player
        self.activity = activity

    def getName(self):
        return self.name

    def getSurname(self):
        return self.surname

    def getIsAuthorized(self):
        return self.isAuthorized

    def setIsAuthorized(self, value):
        self.isAuthorized =  value

    def getPlayer(self):
        return self.player

    def setPlayer(self, newPlayer):
        self.player = newPlayer

    def getActivity(self):
        return self.activity

    def setActivity(self, newActivity):
        self.activity = newActivity
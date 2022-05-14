class User(object):
    def __init__(self, name, surname, isAuthorized, player = None, activity = None):
        self.name = name
        self.surname = surname
        self.isAuthorized = isAuthorized
        self.player = player

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

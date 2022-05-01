class User:
    def __init__(self, name, surname, isAuthorized):
        self.name = name
        self.surname = surname
        self.isAuthorized = isAuthorized

    def getName(self):
        return self.name

    def getSurname(self):
        return self.surname

    def getIsAuthorized(self):
        return self.isAuthorized

    def setIsAuthorized(self, value):
        self.isAuthorized =  value

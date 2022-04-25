class User:
    def __init__(self, ipAddress, name, surname, isAuthorized, userType):
        self.ipAddress = ipAddress
        self.name = name
        self.surname = surname
        self.isAuthorized = isAuthorized
        self.userType = userType

    def getIpAddress(self):
        return self.ipAddress

    def getName(self):
        return self.name

    def getSurname(self):
        return self.surname

    def getIsAuthorized(self):
        return self.isAuthorized

    def setIsAuthorized(self, value):
        self.isAuthorized =  value

    def getUserType(self):
        return self.userType

class Admin:
    def __init__(self, ipAddress, userType):
        self.ipAddress = ipAddress
        self.userType = userType

    def getIpAddress(self):
        return self.ipAddress

    def getUserType(self):
        return self.userType

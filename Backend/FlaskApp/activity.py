class Activity(object):
    def __init__(self):
        actualActivity = ""
        numberAuthorizedUsers = 0

    def getActualActivity(self):
        return self.actualActivity

    def setActualActivity(self, newActivity):
        self.actualActivity = newActivity

    def getNumberAuthorizedUsers(self):
        return self.numberAuthorizedUsers

    def setNumberAuthorizedUsers(self, newNumberAuthorizedUsers):
        self.numberAuthorizedUsers = newNumberAuthorizedUsers

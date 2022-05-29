class Robot(object):
    def __init__(self):
        self.actualRobot = 0
        self.actualActivity = ""
        self.numberAuthorizedUsers = 0

    def getRobotIP(self):
        if (self.actualRobot == '1'):
            return '192.168.100.1'
        elif (self.actualRobot == '2'):
            return '192.168.100.1'

    def getActualRobot(self):
        return self.actualRobot

    def setActualRobot(self, newRobot):
        self.actualRobot = newRobot

    def getActualActivity(self):
        return self.actualActivity

    def setActualActivity(self, newActivity):
        self.actualActivity = newActivity

    def getNumberAuthorizedUsers(self):
        return self.numberAuthorizedUsers

    def setNumberAuthorizedUsers(self, newNumberAuthorizedUsers):
        self.numberAuthorizedUsers = newNumberAuthorizedUsers

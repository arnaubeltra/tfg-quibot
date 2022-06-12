"""
Class used to handle robot status:
    - Current IP to reach the robot
    - Current robot used
    - Current activity that the robot is performing
    - Number of authorized users in a given activity
"""
class Robot(object):
    def __init__(self):
        self.currentRobot = 0
        self.currentActivity = ""
        self.numberAuthorizedUsers = 0

    """
    Returns the robot IP depending on the current used robot.
    Robots have an assigned IP, that never changes.
        - ROBOT 1D (1) --> 192.168.100.5
        - ROBOT 2D (2) --> 192.168.100.6

    Return type: string
    """
    def getRobotIP(self):
        if (self.currentRobot == '1'):
            return '192.168.100.5'
        elif (self.currentRobot == '2'):
            return '192.168.100.6'

    def getCurrentRobot(self):
        return self.currentRobot

    def setCurrentRobot(self, newRobot):
        self.currentRobot = newRobot

    def getCurrentActivity(self):
        return self.currentActivity

    def setCurrentActivity(self, newActivity):
        self.currentActivity = newActivity

    def getNumberAuthorizedUsers(self):
        return self.numberAuthorizedUsers

    def setNumberAuthorizedUsers(self, newNumberAuthorizedUsers):
        self.numberAuthorizedUsers = newNumberAuthorizedUsers

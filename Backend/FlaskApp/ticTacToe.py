"""
Class used to handle status of a Tic tac toe game:
    - Number of players
    - Current board status
    - Game status
    - Last player that made a movement
    - Coordinates of the last movement
"""
class TicTacToe(object):
    def __init__(self):
        self.ticTacToePlayers = 0
        self.ticTacToeBoard = [[0,0,0],[0,0,0],[0,0,0]]
        self.ticTacToeStatus = 0
        self.player = 0
        self.X = 0
        self.Y = 0

    def getTicTacToePlayers(self):
        return self.ticTacToePlayers

    def setTicTacToePlayers(self, players):
        self.ticTacToePlayers = players
    
    def getTicTacToeBoard(self):
        return self.ticTacToeBoard

    def setTicTacToeBoard(self, board):
        self.ticTacToeBoard = board

    def getTicTacToeStatus(self):
        return self.ticTacToeStatus

    def setTicTacToeStatus(self, status):
        self.ticTacToeStatus = status

    def getPlayer(self):
        return self.player

    def setPlayer(self, player):
        self.player = player

    def getX(self):
        return self.X

    def setX(self, X):
        self.X = X

    def getY(self):
        return self.Y

    def setY(self, Y):
        self.Y = Y


    """
    Looks the Tic tac toe board and checks if there is any "Tic tac toe", meaning that three 
    pieces of any of the players are in a line. This line can be vertical, horizontal or diagonal.
    
    Returns the number of the player that made a "Tic tac toe".

    Return type: int
    """
    def checkBoard(self):
        ticTacToeBoard = self.getTicTacToeBoard()

        #Horizontal
        for i in range(3):
            if ((ticTacToeBoard[i][0] == 1) and (ticTacToeBoard[i][1] == 1) and (ticTacToeBoard[i][2] == 1)):
                return 1
        for i in range(3):
            if ((ticTacToeBoard[i][0] == 2) and (ticTacToeBoard[i][1] == 2) and (ticTacToeBoard[i][2] == 2)):
                return 2

        #Vertical
        for j in range(3):
            if ((ticTacToeBoard[0][j] == 1) and (ticTacToeBoard[1][j] == 1) and (ticTacToeBoard[2][j] == 1)):
                return 1
        for j in range(3):
            if ((ticTacToeBoard[0][j] == 2) and (ticTacToeBoard[1][j] == 2) and (ticTacToeBoard[2][j] == 2)):
                return 2

        #Diagonal right
        if ((ticTacToeBoard[0][0] == 1) and (ticTacToeBoard[1][1] == 1) and (ticTacToeBoard[2][2] == 1)):
                return 1
        if ((ticTacToeBoard[0][0] == 2) and (ticTacToeBoard[1][1] == 2) and (ticTacToeBoard[2][2] == 2)):
                return 2

        #Diagonal left
        if ((ticTacToeBoard[0][2] == 1) and (ticTacToeBoard[1][1] == 1) and (ticTacToeBoard[2][0] == 1)):
                return 1
        if ((ticTacToeBoard[0][2] == 2) and (ticTacToeBoard[1][1] == 2) and (ticTacToeBoard[2][0] == 2)):
                return 2
        return 0


    """
    Checks if there are no movements left because the board is full.
    Returns true/false depending on the board status.

    Return type: boolean
    """
    def checkBoardFull(self):
        counter = 0
        for i in range(3):
            for j in range(3):
                if (self.ticTacToeBoard[i][j] == 0):
                    counter += 1
        return (not counter > 0)



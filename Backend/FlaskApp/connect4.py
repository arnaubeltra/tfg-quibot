"""
Class used to handle status of a Connect 4 game:
    - Number of players
    - Current board status
    - Game status
    - Last player that made a movement
    - Coordinates of the last movement
"""
class Connect4(object):
    def __init__(self):
        self.connect4Players = 0
        self.connect4Board = [[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0],[0,0,0,0,0,0]]
        self.connect4Status = 0
        self.player = 0
        self.X = 0
        self.Y = 0

    def getConnect4Players(self):
        return self.connect4Players

    def setConnect4Players(self, players):
        self.connect4Players = players
    
    def getConnect4Board(self):
        return self.connect4Board

    def setConnect4Board(self, board):
        self.connect4Board = board

    def getConnect4Status(self):
        return self.connect4Status

    def setConnect4Status(self, status):
        self.connect4Status = status

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
    Looks the Connect 4 board and checks if there are four pieces of the same player in a row. 
    Four pieces in a row can be vertical, horizontal or diagonal.
    
    Returns the number of the player has four pieces in a row.

    Return type: int
    """
    def checkBoard(self):
        board = self.getConnect4Board()
        #Horizontal
        for i in range(5):
            for j in range(3):
                if ((board[i][j] == 1) and (board[i][j+1] == 1) and (board[i][j+2] == 1) and (board[i][j+3] == 1)):
                    return 1
                if ((board[i][j] == 2) and (board[i][j+1] == 2) and (board[i][j+2] == 2) and (board[i][j+3] == 2)):
                    return 2
                
        #Vertical
        for i in range(2):
            for j in range(6):
                if ((board[i][j] == 1) and (board[i+1][j] == 1) and (board[i+2][j] == 1) and (board[i+3][j] == 1)):
                    return 1
                if ((board[i][j] == 2) and (board[i+1][j] == 2) and (board[i+2][j] == 2) and (board[i+3][j] == 2)):
                    return 2

        #Diagonal right
        for i in range(2):
            for j in range(3):
                if ((board[i][j] == 1) and (board[i+1][j+1] == 1) and (board[i+2][j+2] == 1) and (board[i+3][j+3] == 1)):
                    return 1
                if ((board[i][j] == 2) and (board[i+1][j+1] == 2) and (board[i+2][j+2] == 2) and (board[i+3][j+3] == 2)):
                    return 2
        
        #Diagonal left
        for i in range(3,5):
            for j in range(3):
                if ((board[i][j] == 1) and (board[i-1][j+1] == 1) and (board[i-2][j+2] == 1) and (board[i-3][j+3] == 1)):
                    return 1
                if ((board[i][j] == 2) and (board[i-1][j+1] == 2) and (board[i-2][j+2] == 2) and (board[i-3][j+3] == 2)):
                    return 2
        return 0


    """
    Checks if there are no movements left because the board is full.
    Returns true/false depending on the board status.

    Return type: boolean
    """
    def checkBoardFull(self):
        counter = 0
        for i in range(5):
            for j in range(6):
                if (self.connect4Board[i][j] == 0):
                    counter += 1
        return (not counter > 0)


    """
    Checks the row position of the piece that has to be introduced to the board, as Connect 4 game
    works by placing pieces one over the other. Given a column calculates where the piece has to be put.
    Returns a row number.

    Input type: int
    Return type: int
    """
    def checkRowPosition(self, column):
        for row in range(5, 0, -1):
            if (self.connect4Board[row-1][column] == 0):
                return row
        return 0
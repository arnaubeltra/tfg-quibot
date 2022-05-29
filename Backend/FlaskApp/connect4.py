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

    def checkBoardFull(self):
        counter = 0
        for i in range(5):
            for j in range(6):
                if (self.connect4Board[i][j] == 0):
                    counter += 1
        return (not counter > 0)

    def checkRowPosition(self, column):
        for row in range(5, 0, -1):
            if (self.connect4Board[row-1][column] == 0):
                return row
        return 0
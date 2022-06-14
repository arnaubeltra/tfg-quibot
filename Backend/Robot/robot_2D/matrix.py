class Matrix(object):
    def __init__(self, x_frame_constant, y_frame_constant, x_matrix_constant, y_matrix_constant, max_x, max_y, x = 0, y = 0):
        self.x = x
        self.y = y
        self.x_frame_constant = x_frame_constant
        self.y_frame_constant = y_frame_constant
        self.x_matrix_constant = x_matrix_constant
        self.y_matrix_constant = y_matrix_constant
        self.max_x = max_x
        self.max_y = max_y

    def update_position(self, x, y):
        self.x = x
        self.y = y

    def get_position(self):
        return (self.x, self.y)

    def get_xy_frame_constants(self):
        return (self.x_frame_constant, self.y_frame_constant)

    def get_xy_matrix_constants(self):
        return (self.x_matrix_constant, self.y_matrix_constant)

    def get_xy_max(self):
        return (self.max_x, self.max_y)

class Large_Matrix(Matrix):
    def __init__(self):
        super().__init__(75, 115, 140, 135, 4, 6)

class Medium_Matrix(Matrix):
    def __init__(self):
        super().__init__(100, 185, 180, 180, 3, 4)

class Small_Matrix(Matrix):
    def __init__(self):
        super().__init__(135, 175, 280, 280, 2, 3)

class Frame_Matrix(Matrix):
    def __init__(self, config):
        super().__init__(600, 900, 0, 0, 2, 2)
        self.config = config

    def get_config(self):
        return self.config
    
    def current_matrix(self):
        return self.config[self.x][self.y]

class Frame_Matrix_All_Medium(Matrix):
    def __init__(self):
        super().__init__(100, 190, 180, 180, 6, 8)
        self.x_between_constant = 250
        self.y_between_constant = 365

    def get_xy_between_constants(self):
        return (self.x_between_constant, self.y_between_constant)
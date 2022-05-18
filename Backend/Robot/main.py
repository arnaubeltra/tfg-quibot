#!/usr/bin/env pybricks-micropython
from pybricks.hubs import EV3Brick
from pybricks.ev3devices import (Motor, TouchSensor, ColorSensor,
                                 InfraredSensor, UltrasonicSensor, GyroSensor)
from pybricks.parameters import Port, Stop, Direction, Button, Color
from pybricks.tools import wait, StopWatch, DataLog
from pybricks.robotics import DriveBase
from pybricks.media.ev3dev import SoundFile, ImageFile

import time
import threading
from matrix import *
import random
import os
import socket


# Brick elements
ev3 = EV3Brick()

Motor_x = Motor(Port.D)
Motor_y = Motor(Port.A)
Motor_pipette = Motor(Port.C)
Motor_plunger = Motor(Port.B)

Touch_sensor_x = TouchSensor(Port.S1)
Touch_sensor_y = TouchSensor(Port.S2)
Color_sensor_pipette = ColorSensor(Port.S3)
Color_sensor_liquid = ColorSensor(Port.S4)


# Layout configuration (board)
Frame = Frame_Matrix([[None, None], [None, None]])
Frame_interact = Frame_Matrix_All_Medium()
liquid_quantity = 0


# Basic movement functions
def reset_x():
    while  not Touch_sensor_x.pressed():
        Motor_x.run(250)
    Motor_x.reset_angle(0)
    ev3.speaker.beep()
    return

def reset_y():
    while not Touch_sensor_y.pressed():
        Motor_y.run(-250)
    Motor_y.reset_angle(0)
    ev3.speaker.beep()
    return

def reset_x_y():
    x = threading.Thread(target=reset_x)
    x.start()
    y = threading.Thread(target=reset_y)
    y.start()
    while (not Touch_sensor_x.pressed() or not Touch_sensor_y.pressed()):
        continue
    return

def raise_pipette():
    while Color_sensor_pipette.color() != Color.YELLOW:
        Motor_pipette.run(-300)
    Motor_pipette.stop()
    Motor_pipette.reset_angle(0)
    ev3.speaker.beep()
    return

def lower_pipette():
    while Color_sensor_pipette.color() != Color.RED:
        Motor_pipette.run(300)
    Motor_pipette.stop()
    Motor_pipette.reset_angle(0)
    ev3.speaker.beep()
    return

def lower_plunger():
    global liquid_quantity
    if (liquid_quantity == 1):
        Motor_plunger.run_target(250, 610)
    else:
        Motor_plunger.run_target(250, (610 - (610*(1 - liquid_quantity))))
    liquid_quantity = 0
    Motor_plunger.reset_angle(0)
    return

def raise_plunger():
    global liquid_quantity
    if (liquid_quantity == 0):
        Motor_plunger.run_target(250, -610)
    else:
        Motor_plunger.run_target(250, (-610 + (610*liquid_quantity)))
    liquid_quantity = 1
    Motor_plunger.reset_angle(0)
    return

def plunger_get_quantity(quantity):
    Motor_plunger.run_target(250, -(61*quantity))
    Motor_plunger.reset_angle(0)
    return

def plunger_expulse_quantity(quantity):   
    Motor_plunger.run_target(250, (61*quantity))
    Motor_plunger.reset_angle(0)
    return

def reset_plunger():
    Motor_plunger.run_until_stalled(250, then=Stop.HOLD, duty_limit=20)
    Motor_plunger.reset_angle(0)
    return

def read_liquid_color():    
    return Color_sensor_liquid.color()

def loop_read_liquid_color():   #delete
    while (1):
        print (Color_sensor_liquid.color())


# Complex functions (using basic functions)
def get_liquid_interact():
    raise_plunger()
    liquid_quantity = 1
    return

def deposit_liquid_interact():
    lower_plunger()
    liquid_quantity = 0
    returnind

def get_liquid():
    lower_pipette()
    raise_plunger()
    raise_pipette()
    return

def deposit_liquid():
    lower_pipette()
    lower_plunger()
    raise_pipette()
    return

def reset_robot():
    global Frame
    Frame.update_position(0, 0)
    raise_pipette()
    reset_plunger()
    reset_x_y()
    time.sleep(1)


# Board functions
def set_frame_config(matrix):
    global Frame
    Frame = matrix

def set_plate_frame(x,y):
    global Frame
    reset_robot()
    if (x):
        Motor_x.run_target(400, Frame.get_xy_frame_constants()[0]*-1)
    if (y):
        Motor_y.run_target(400, Frame.get_xy_frame_constants()[1])

    Motor_x.reset_angle(0)
    Motor_y.reset_angle(0)
    Frame.update_position(x, y)
    Motor_x.run_target(250, Frame.current_matrix().get_xy_frame_constants()[0]*-1)
    Motor_y.run_target(250, Frame.current_matrix().get_xy_frame_constants()[1])
    Frame.current_matrix().update_position(0, 0)
    Motor_x.reset_angle(0)
    Motor_y.reset_angle(0)
    return

def set_bucket_plate(x, y):
    global Frame
    plate = Frame.current_matrix()
    xy = plate.get_position()
    mx = abs(x - xy[0])
    my = abs(y - xy[1])
    if(x >= plate.get_xy_max()[0] or y >= plate.get_xy_max()[1]):
        return
    if (x < 0 or y < 0):
        return
    if (x > xy[0]):
        Motor_x.run_target(250, mx*plate.get_xy_matrix_constants()[0]*-1)
    elif (x < xy[0]):
        Motor_x.run_target(250, mx*plate.get_xy_matrix_constants()[0])
    else:
        pass
    if (y > xy[1]):
        Motor_y.run_target(250, my*plate.get_xy_matrix_constants()[1])
    elif (y < xy[1]):
        Motor_y.run_target(250, my*plate.get_xy_matrix_constants()[1]*-1)
    else:
        pass
    plate.update_position(x, y)
    Motor_x.reset_angle(0)
    Motor_y.reset_angle(0)
    return


# Functionalities
# Experiments
def series_de_disolucio():
    set_frame_config(Frame_Matrix([[Large_Matrix(), Large_Matrix()], [Medium_Matrix(), Medium_Matrix()]]))
    set_plate_frame(0,0)
    reset_plunger()
    for i in range(6):
        set_bucket_plate(3,i)
        if (i == 0):
            lower_pipette()
        else:
            lower_pipette()
            lower_plunger()
            raise_plunger()
            lower_plunger()
        if (i != 5):
            raise_plunger()
        raise_pipette()
    set_plate_frame(1,1)

def barrejaColors(color1, color2):
    set_frame_config(Frame_Matrix([[Medium_Matrix(), Medium_Matrix()], [Medium_Matrix(), Medium_Matrix()]]))
    set_plate_frame(0,0)
    reset_plunger()

    pos1, pos2 = findColors(color1, color2)

    for i in range(2):
        set_bucket_plate(0,pos1+1)
        lower_pipette()
        raise_plunger()
        raise_pipette()
        set_bucket_plate(2, 0)
        lower_pipette()
        lower_plunger()
        raise_pipette()
        set_bucket_plate(0,pos2+1)
        lower_pipette()
        raise_plunger()
        raise_pipette()
        set_bucket_plate(2, 0)
        lower_pipette()
        lower_plunger()
        raise_pipette()
    set_plate_frame(1,1)

def findColors(color1, color2):
    pos1 = 0
    pos2 = 0
    for i in range(3):
        set_bucket_plate(0,i)
        color = read_liquid_color()
        if (color == color1):
            pos1 = i
        if (color == color2):
            pos2 = i
    return pos1, pos2

def capes_de_densitat():
    pass

def start_experiment(instruction):
    if instruction=='series_de_dissolucio':
        series_de_disolucio()
    elif instruction=='barreja_colors':
        barrejaColors(Color.YELLOW,Color.BLUE)
    elif instruction=='capes_de_densitat':
        capes_de_densitat()

#Interact with robot
def start_interact(matrix_size):
    global Frame_interact
    set_frame_config(Frame_Matrix([[Medium_Matrix(), Medium_Matrix()], [Medium_Matrix(), Medium_Matrix()]]))
    x, y = Frame_interact.get_position()
    Frame_interact.update_position(0,0)
    set_plate_frame(0, 0)

def move_interact(direction):
    if (Color_sensor_pipette.color() == Color.RED):
        return
    global Frame_interact
    current_x, current_y = Frame_interact.get_position()
    max_x, max_y = Frame_interact.get_xy_max()
    if (direction == "up" and  current_y < max_y-1):#and current_y+1 < max_y
        if (current_y == 3):
            Motor_y.run_target(250, Frame_interact.get_xy_between_constants()[1])
        else:
            Motor_y.run_target(250, Frame_interact.get_xy_matrix_constants()[1])
        Frame_interact.update_position(current_x, current_y+1)
    elif (direction == "down" and current_y > 0):
        if (current_y == 4):
            Motor_y.run_target(250, Frame_interact.get_xy_between_constants()[1]*-1)
        else:
            Motor_y.run_target(250, Frame_interact.get_xy_matrix_constants()[1]*-1)
        Frame_interact.update_position(current_x, current_y-1)
    elif (direction == "left" and current_x > 0):
        if (current_x == 3):
            Motor_x.run_target(250, Frame_interact.get_xy_between_constants()[0])
        else:
            Motor_x.run_target(250, Frame_interact.get_xy_matrix_constants()[0])
        Frame_interact.update_position(current_x-1, current_y)
    elif (direction == "right" and current_x < max_x-1):
        if (current_x == 2):
            Motor_x.run_target(250, Frame_interact.get_xy_between_constants()[0]*-1)
        else:
            Motor_x.run_target(250, Frame_interact.get_xy_matrix_constants()[0]*-1)
        Frame_interact.update_position(current_x+1, current_y)
    Motor_y.reset_angle(0)
    Motor_x.reset_angle(0)
    ev3.speaker.beep()

# Custom program
def custom_program(instruction):
    global liquid_quantity
    if instruction in ["up", "down", "left", "right"]:
        move_interact(instruction)
    elif (instruction == "raise_pipette"):
        raise_pipette()
    elif (instruction == "lower_pipette"):
        lower_pipette()
    elif (instruction == "suck"):
        if (liquid_quantity < 1):
            get_liquid_interact()
    elif (instruction == "unsuck"):
        if (liquid_quantity > 0):
            deposit_liquid_interact()
    else:
        ins = instruction.split("_")
        if (ins[0] == "suck"):
            if ((liquid_quantity + float(ins[1])) <= 1):
                plunger_get_quantity(int(float(ins[1])*10))
                liquid_quantity += float(ins[1])
        elif (ins[0] == "unsuck"):
            if ((liquid_quantity - float(ins[1])) >= 0):
                plunger_expulse_quantity(int(float(ins[1])*10))
                liquid_quantity -= float(ins[1])

# Tic tac toe
def play_tic_tac_toe(x, y, player_chip):
    print(x,y)
    set_plate_frame(0,0)
    if (player_chip == '1'):
        set_bucket_plate(0,3)
    elif (player_chip == '2'):
        set_bucket_plate(1,3)
    else:
        return
    get_liquid()
    set_bucket_plate(y,abs(x-2))
    deposit_liquid()
    return

# Connect 4
def play_connect4(x, y, player_chip, connect4_liquid_count):
    print(x,y)
    set_plate_frame(0,1)
    if (player_chip == '1'):
        if (connect4_liquid_count[0] < 11):
            set_bucket_plate(1,0)
        elif (connect4_liquid_count[0] < 22):
            set_bucket_plate(1,1)
        else:
            set_bucket_plate(1,2)
        connect4_liquid_count[0] += 1
    elif (player_chip == '2'):
        if (connect4_liquid_count[1] < 11):
            set_bucket_plate(0,0)
        elif (connect4_liquid_count[1] < 22):
            set_bucket_plate(0,1)
        else:
            set_bucket_plate(0,2)
        connect4_liquid_count[1] += 1
    else:
        return connect4_liquid_count
    get_liquid()
    if (y < 4):
        set_plate_frame(0,0)
        set_bucket_plate(y,abs(x-5))
        deposit_liquid()
    else:
        set_plate_frame(1,0)
        set_bucket_plate(y-4,abs(x-5))
        deposit_liquid()
    return connect4_liquid_count


if __name__ == "__main__":
    ind = 0
    ev3.speaker.set_speech_options(language='ca',voice='f1')
    HOST = "192.168.100.16" #ip
    PORT = 9999
    s=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((HOST, PORT))
    s.listen(2)
    while True:
        conn, addr = s.accept()
        #print('Connected by', addr)
        while True:
            data = conn.recv(1024)
            if not data:
                break
            #print(data)
            raw_instruccio=data.decode("utf-8")
            instruccio=raw_instruccio.split()
            ev3.speaker.beep()
            if instruccio[0]=='experiment':
                start_experiment(instruccio[1])
                conn.send(b"finished")
            elif (instruccio[0] == "battleship"):
                set_frame_config(Frame_Matrix([[Large_Matrix(), Small_Matrix()], [Large_Matrix(), Large_Matrix()]]))
                instruccio.pop(0)
                board_robot = instruccio
                liquid_battle_count = [0,0]
                print_battleship_map(instruccio)
            elif (instruccio[0] == "battleship_check_board"):
                liquid_battle_count = play_battleship(board_robot, int(instruccio[1]), int(instruccio[2]), liquid_battle_count)
                conn.send(b"finished")
            elif (instruccio[0] == "connect4_start"):
                set_frame_config(Frame_Matrix([[Large_Matrix(), Small_Matrix()], [Large_Matrix(), Large_Matrix()]]))
                connect4_liquid_count = [0,0]
            elif (instruccio[0] == "connect4_put_chip"):
                connect4_liquid_count = play_connect4(int(instruccio[1]), int(instruccio[2]), instruccio[3], connect4_liquid_count)
                conn.send(b"finished")
            elif (instruccio[0] == "tic_tac_toe_put_chip"):
                set_frame_config(Frame_Matrix([[Medium_Matrix(), Small_Matrix()], [Large_Matrix(), Large_Matrix()]]))
                play_tic_tac_toe(int(instruccio[1]), int(instruccio[2]), instruccio[3])
                conn.send(b"finished")
            elif (instruccio[0] == "start_interact"):
                #start_interact(instruccio[1])
                set_frame_config(Frame_Matrix([[Medium_Matrix(), Medium_Matrix()], [Medium_Matrix(), Medium_Matrix()]]))
                start_interact("")
                conn.send(b"finished")
            elif (instruccio[0] == "action"):
                if (ind == 0):
                    start_interact("")
                    ind+=1
                if instruccio[1] in ["up", "down", "left", "right"]:
                    move_interact(instruccio[1])
                elif (instruccio[1] == "raise_pipette"):
                    raise_pipette()
                elif (instruccio[1] == "lower_pipette"):
                    lower_pipette()
                elif (instruccio[1] == "suck"):
                    if (have_liquid):
                        deposit_liquid_interact()
                    else:
                        get_liquid_interact()
                elif (instruccio[1] == "reset"):
                    reset_robot()
                    Frame_interact.update_position(0,0)
                    set_plate_frame(0, 0)
                conn.send(b"finished")
            elif instruccio[0]=='program':
                ins=instruccio[1].split(',')
                print (ins)
                for i in range(len(ins)): 
                    custom_program(ins[i])
                    time.sleep(0.5)
    time.sleep(1)
    ev3.speaker.beep()
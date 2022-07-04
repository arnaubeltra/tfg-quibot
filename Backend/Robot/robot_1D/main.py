#!/usr/bin/env pybricks-micropython
from pybricks.hubs import EV3Brick
from pybricks.ev3devices import (Motor, TouchSensor, ColorSensor, UltrasonicSensor, GyroSensor)
from pybricks.parameters import Port, Stop, Direction, Button, Color
from pybricks.tools import wait, StopWatch, DataLog
from pybricks.robotics import DriveBase
from pybricks.media.ev3dev import SoundFile, ImageFile

from pybricks.iodevices import Ev3devSensor

import time
import socket
import urequests as requests
import os


# Creation of the EV3 object
ev3 = EV3Brick()

# Declaration of ports used for actuators and sensors
Motor_Pipeta = Motor(Port.B)
Motor_Xuclar = Motor(Port.A)
Motor_Carreta = Motor(Port.C)
Motor_Carreta.set_dc_settings(50, 0)
Sensor_RGB = ColorSensor(Port.S1)

# Global variables
cubeta_actual=1
baixada = False

# Stores liquid quantity in pipe.
liquid_quantity = 0

"""
Lowers the pipete from the highest position to the lowest.
"""
def baixar_pipeta():
    global baixada
    Motor_Pipeta.run_target(100,180)
    baixada = True

"""
Raises the pipette from the lowest position to the highest.
"""
def pujar_pipeta():
    global baixada
    Motor_Pipeta.run_target(100,0)
    baixada = False

"""
Gets or expulses the liquid using the pipette.
Old version of raise_plunger() and lower_plunger() but not deleted as
it is useful for some experiments.
"""
def xuclar_cubeta():
    Motor_Xuclar.run_target(100,180)
    Motor_Pipeta.stop()
    Motor_Xuclar.reset_angle(0)

"""
Gets or expulses the liquid using the pipette, softly
"""
def xuclar_cubeta_suau():
    Motor_Xuclar.run_target(25,180)
    Motor_Pipeta.stop()
    Motor_Xuclar.reset_angle(0)

"""
Raises the plunger to get liquid. 
Checks if the pipette is empty or mid empty to move the motor proportionaly.
"""
def raise_plunger():
    global liquid_quantity
    if (liquid_quantity == 0):
        Motor_Xuclar.run_target(100,180)
    else:
        Motor_Xuclar.run_target(100, (180 - (180*(1 - liquid_quantity))))
    liquid_quantity = 1
    Motor_Xuclar.reset_angle(0)

"""
Lowers the plunger to expulse liquid. 
Checks if the pipette is full or mid full to move the motor proportionaly.
"""
def lower_plunger():
    global liquid_quantity
    if (liquid_quantity == 1):
        Motor_Xuclar.run_target(100,180)
    else:
        Motor_Xuclar.run_target(100, (-180 + (180*(1 - liquid_quantity))))
    liquid_quantity = 0
    Motor_Xuclar.reset_angle(0)

"""
Gets a certain liquid quantity.

Input: quantity --> float
"""
def plunger_get_quantity(quantity):
    Motor_Xuclar.run_target(100, 180-(18*(10-quantity)))
    Motor_Xuclar.reset_angle(0)

"""
Expulses a certain liquid quantity.

Input: quantity --> float
"""
def plunger_expulse_quantity(quantity):
    Motor_Xuclar.run_target(100, -180+(18*(10-quantity)))
    Motor_Xuclar.reset_angle(0)

"""
Mixes the liquid getting the liquid and expulsing it once.
Function used for experiments. 
"""
def barrejar_amb_pipeta():
    xuclar_cubeta()
    xuclar_cubeta()

"""
Moves the platform to a certain position, to be aligned with the
bucket number given.

Input: num_cubeta --> int
"""
def moure_cubeta(num_cubeta):
    global cubeta_actual
    global baixada
    if not baixada:
        if num_cubeta>cubeta_actual:
            d=1
        elif num_cubeta<cubeta_actual:
            d=-1
        else:
            return
        
        while cubeta_actual!=num_cubeta:
            Motor_Carreta.run_target(100,38*d)
            Motor_Carreta.reset_angle(0)
            cubeta_actual=cubeta_actual+d
        Motor_Carreta.stop()

"""
Moves the platform a certain amount of times (num) to the specified direction (direccio),
to align a certain bucket with the pipette.

Inputs:
    - num --> int
    - direccio --> string
"""
def moure_cubeta_x(num,direccio):
    global cubeta_actual
    global baixada
    if not baixada:
        if direccio=="right" and cubeta_actual-num<1:
            return
        elif direccio=="left" and cubeta_actual+num>6:
            return
        else:
            num=abs(num)
            if direccio=="right":
                d=-1
            else:
                d=1
        while num!=0:
            Motor_Carreta.run_target(100,38*d)
            Motor_Carreta.reset_angle(0)
            cubeta_actual=cubeta_actual+d
            num-=1
        Motor_Carreta.stop()

"""
Resets the robot, raising the pipette, and aligning the pipette to a bucket.
"""
def reset_carreta():
    global cubeta_actual
    global baixada
    if baixada:
        pujar_pipeta()
    while Sensor_RGB.color()!=None:
        Motor_Carreta.run(-50)
    Motor_Carreta.stop()
    Motor_Carreta.reset_angle(0)
    Motor_Carreta.run_target(100,15)
    Motor_Carreta.reset_angle(0)
    Motor_Carreta.stop()
    cubeta_actual=1

"""
Resets the robot, but the buckets do not have the cover.
"""
def reset_carreta_sense_tapa():
    global cubeta_actual
    global baixada
    if baixada:
        pujar_pipeta()
    if Sensor_RGB.color()==None:
        return
    while Sensor_RGB.color()!=None:
        Motor_Carreta.run(-50)
    Motor_Carreta.stop()
    Motor_Carreta.reset_angle(0)
    Motor_Carreta.run_target(100,-15)
    Motor_Carreta.reset_angle(0)
    Motor_Carreta.stop()
    cubeta_actual=1

"""
Series de dissolució experiment. All the functions needed are called
as if it was a list of actions.
"""
def series_de_disolucio():
    reset_carreta_sense_tapa()
    baixar_pipeta()
    xuclar_cubeta()
    pujar_pipeta()
    i=0
    while i<4:
        moure_cubeta_x(1,'left')
        baixar_pipeta()
        xuclar_cubeta()
        barrejar_amb_pipeta()
        xuclar_cubeta()
        pujar_pipeta()
        i+=1
    moure_cubeta_x(1,'left')
    baixar_pipeta()
    xuclar_cubeta()
    pujar_pipeta()
    reset_carreta_sense_tapa()

"""
Barreja colors primaris experiment. All the functions needed are called
as if it was a list of actions.
"""
def barrejaColors(color1,color2):
    reset_carreta()
    buscar_color(color1)
    buscar_color(Color.WHITE)
    buscar_color(color2)
    buscar_color(Color.WHITE)
    reset_carreta()

"""
Looks for the bucket that contains the given color, using the color sensor.

Input: color --> Color class
"""
def buscar_color(color):
    while Sensor_RGB.color()!=None:
        Motor_Carreta.run(-50)
    Motor_Carreta.stop()
    Motor_Carreta.reset_angle(0)
    Motor_Carreta.run_target(100,50)
    Motor_Carreta.reset_angle(0)
    while True:
        if Sensor_RGB.color()==color:
            Motor_Carreta.run_target(100,-25)
            Motor_Carreta.reset_angle(0)
            baixar_pipeta()
            xuclar_cubeta()
            pujar_pipeta()
            reset_carreta()
            return
        else:
            Motor_Carreta.run_target(100,38)
            Motor_Carreta.reset_angle(0)

"""
Makes the brick say the color that read, translating each color to his name.
"""
def traductor_color(color_RGB):
    if color_RGB==Color.BLUE:
        ev3.speaker.say("Blau")
    elif color_RGB==Color.RED:
        ev3.speaker.say("Vermell")
    elif color_RGB==Color.ORANGE:
        ev3.speaker.say("Taronja")
    elif color_RGB==Color.GREEN:
        ev3.speaker.say("Verd")
    elif color_RGB==Color.WHITE:
        ev3.speaker.say("Blanc")
    elif color_RGB==Color.BLACK:
        ev3.speaker.say("Negre")
    elif color_RGB==Color.YELLOW:
        ev3.speaker.say("Groc")
    else:
        ev3.speaker.say("Error llegint el color")

"""
Reads the color of the current aligned bucket.
"""
def llegir_color_cubeta_actual():
    Motor_Carreta.run_target(100,35)
    Motor_Carreta.stop()
    Motor_Carreta.reset_angle(0)
    traductor_color(Sensor_RGB.color())
    Motor_Carreta.run_target(100,-35)
    Motor_Carreta.stop()
    Motor_Carreta.reset_angle(0)    

"""
Capes de densitat experiment. All the functions needed are called
as if it was a list of actions.
"""
def capes_de_densitat():
    reset_carreta_sense_tapa()
    for c in [1,1,2,3,3]:
        moure_cubeta(c)
        baixar_pipeta()
        xuclar_cubeta()
        pujar_pipeta()
        moure_cubeta(4)
        xuclar_cubeta_suau()
    reset_carreta_sense_tapa()

"""
Used to call the function used to start any experiment.

Input: instruccio --> string
"""
def start_programa(instruccio):
    if instruccio=='series_de_dissolucio':
        series_de_disolucio()
    elif instruccio=='barreja_colors':
        barrejaColors(Color.YELLOW,Color.BLUE)
    elif instruccio=='capes_de_densitat':
        capes_de_densitat()

"""
Executes any instruction that the user has asked the robot to execute.
Used at Interact with robot, and Custom Program.
"""
def executa_instruccions(instruccio):
    global liquid_quantity
    if instruccio=='right' or instruccio=='left':
        moure_cubeta_x(1,instruccio)
    elif instruccio=='lower_pipette':
        baixar_pipeta()
    elif instruccio=='raise_pipette':
        pujar_pipeta()
    elif instruccio=='suck_unsuck':
        raise_plunger()
    elif (instruccio == "suck"):
        if (liquid_quantity < 1):
            raise_plunger()
    elif (instruccio == "unsuck"):
        if (liquid_quantity > 0):
            lower_plunger()
    elif instruccio=='color':
        llegir_color_cubeta_actual()
    elif instruccio=='reset':
        reset_carreta()
    elif instruccio=='reset_sense_tapa':
        reset_carreta_sense_tapa()
    else:
        ins = instruccio.split("_")
        if (ins[0] == "suck"):
            if ((liquid_quantity + float(ins[1])) <= 1):
                plunger_get_quantity(int(float(ins[1])*10))
                liquid_quantity += float(ins[1])
        elif (ins[0] == "unsuck"):
            if ((liquid_quantity - float(ins[1])) >= 0):
                plunger_expulse_quantity(int(float(ins[1])*10))
                liquid_quantity -= float(ins[1])

if __name__ == "__main__":    
    ev3.speaker.set_speech_options(language='ca',voice='f1')
    HOST = "192.168.100.5"
    PORT = 9999
    s=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((HOST, PORT))
    s.listen(2)
    while True:
        conn, addr = s.accept()
        while True:
            data = conn.recv(1024)
            if not data:
                break
            instruccio=data.decode("utf-8")
            instruccio=instruccio.split()
            ev3.speaker.beep()
            if instruccio[0]=='experiment':
                start_programa(instruccio[1])
            elif instruccio[0]=='action':
                executa_instruccions(instruccio[1])
            elif instruccio[0]=='program':
                ins=instruccio[1].split(',')
                for i in range(len(ins)):
                    executa_instruccions(ins[i])
                    time.sleep(0.5)
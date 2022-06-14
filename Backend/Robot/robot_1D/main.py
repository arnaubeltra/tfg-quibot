#!/usr/bin/env pybricks-micropython
from pybricks.hubs import EV3Brick
from pybricks.ev3devices import (Motor, TouchSensor, ColorSensor,
                                 InfraredSensor, UltrasonicSensor, GyroSensor)
from pybricks.parameters import Port, Stop, Direction, Button, Color
from pybricks.tools import wait, StopWatch, DataLog
from pybricks.robotics import DriveBase
from pybricks.media.ev3dev import SoundFile, ImageFile

from pybricks.iodevices import Ev3devSensor

import time
import socket
import urequests as requests
import os


ev3 = EV3Brick()

Motor_Pipeta = Motor(Port.B)
Motor_Xuclar = Motor(Port.A)
Motor_Carreta = Motor(Port.C)
Motor_Carreta.set_dc_settings(50, 0)
Sensor_RGB = ColorSensor(Port.S1)
Sensor_IF = InfraredSensor(Port.S2)

cubeta_actual=1
baixada = False

def baixar_pipeta():
    global baixada
    Motor_Pipeta.run_target(100,180)
    baixada = True

def pujar_pipeta():
    global baixada
    Motor_Pipeta.run_target(100,0)
    baixada = False

def xuclar_cubeta():
    Motor_Xuclar.run_target(100,180)
    Motor_Pipeta.stop()
    Motor_Xuclar.reset_angle(0)

def xuclar_cubeta_suau():
    Motor_Xuclar.run_target(25,180)
    Motor_Pipeta.stop()
    Motor_Xuclar.reset_angle(0)

def barrejar_amb_pipeta():
    xuclar_cubeta()
    xuclar_cubeta()

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

def wait_IF_pressed(canal=1):
    IF_Buttons = {Button.LEFT_UP: 1, Button.LEFT_DOWN: 2, Button.RIGHT_UP: 3, Button.RIGHT_DOWN: 4}
    while True:
        b = Sensor_IF.buttons(canal)
        if b==[]:
            continue
        elif IF_Buttons[b[0]]==1:
            #ev3.speaker.beep()
            moure_cubeta_x(1,'left')
        elif IF_Buttons[b[0]]==2:
            #ev3.speaker.beep()
            moure_cubeta_x(1,'right')
        elif IF_Buttons[b[0]]==3:
            #ev3.speaker.beep()
            baixar_pipeta()
            xuclar_cubeta()
            pujar_pipeta()
        elif IF_Buttons[b[0]]==4:
            #ev3.speaker.beep()
            return "break"

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
            print(Sensor_RGB.color())
            Motor_Carreta.run_target(100,38)
            Motor_Carreta.reset_angle(0)

def barrejaColors(color1,color2):#falta acabar
    reset_carreta()
    buscar_color(color1)
    buscar_color(Color.WHITE)
    buscar_color(color2)
    buscar_color(Color.WHITE)
    reset_carreta()

def traductor_color(color_RGB):
    print(color_RGB)
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

def llegir_color_cubeta_actual():
    Motor_Carreta.run_target(100,35)
    Motor_Carreta.stop()
    Motor_Carreta.reset_angle(0)
    traductor_color(Sensor_RGB.color())
    Motor_Carreta.run_target(100,-35)
    Motor_Carreta.stop()
    Motor_Carreta.reset_angle(0)    

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

def executa_instruccions(instruccio):
    if instruccio=='right' or instruccio=='left':
        moure_cubeta_x(1,instruccio)
    elif instruccio=='lower_pipette':
        baixar_pipeta()
    elif instruccio=='raise_pipette':
        pujar_pipeta()
    elif instruccio=='suck':
        xuclar_cubeta()
    elif instruccio=='sensor_IF':
        while True:
            b_pressed=wait_IF_pressed()
            if b_pressed=="break":
                break
    elif instruccio=='color':
        llegir_color_cubeta_actual()
    elif instruccio=='reset':
        reset_carreta()
    elif instruccio=='reset_sense_tapa':
        reset_carreta_sense_tapa()

def start_programa(instruccio):
    if instruccio=='series_de_dissolucio':
        series_de_disolucio()
    elif instruccio=='barreja_colors':
        barrejaColors(Color.YELLOW,Color.BLUE)
    elif instruccio=='capes_de_densitat':
        capes_de_densitat()


if __name__ == "__main__":    
    ev3.speaker.set_speech_options(language='ca',voice='f1')
    HOST = "192.168.100.5"
    PORT = 9999
    s=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((HOST, PORT))
    s.listen(2)
    while True:
        conn, addr = s.accept()
        print('Connected by', addr)
        while True:
            data = conn.recv(1024)
            print(data)
            if not data:
                break
            instruccio=data.decode("utf-8")
            instruccio=instruccio.split()
            #ev3.speaker.beep()
            if instruccio[0]=='experiment':
                start_programa(instruccio[1])
            elif instruccio[0]=='action':
                executa_instruccions(instruccio[1])
            elif instruccio[0]=='program':
                ins=instruccio[1].split(',')
                for i in range(len(ins)):
                    executa_instruccions(ins[i])
                    time.sleep(0.5)
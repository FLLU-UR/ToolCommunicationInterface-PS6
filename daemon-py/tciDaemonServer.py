#!/usr/bin/env python

#import xmlrpclib
import sys


from xmlrpc.server import SimpleXMLRPCServer
from socketserver import ThreadingMixIn


import serial
import logging
import serial.rs485

from time import sleep

#port = '/dev/ttyTool' #ROBOT
#port = '/dev/ttyS0'  #NOT IN USE
port = '/dev/ttyUSB0' #ROBOT USB/SIM

term = b'\r' #Define EOS(End Of Stream) Sequence. b'\r' for CR or b'\n' for LF
 
ser = serial.Serial(timeout = 5,
                    baudrate = 19200,
                    parity = serial.PARITY_NONE,
                    stopbits = serial.STOPBITS_ONE,
                    bytesize = serial.EIGHTBITS,
                    )
ser.port = port

def configure(timeout, baudrate, parity, stopbits, bytesize):
    if stopbits == 0:
        ser.stopbits = serial.STOPBITS_ONE
    elif stopbits == 1:
        ser.stopbits = serial.STOPBITS_ONE_POINT_FIVE
    elif stopbits == 2:
        ser.stopbits = serial.STOPBITS_TWO
        
    if bytesize == 0:
        ser.bytesize = serial.FIVEBITS
    elif bytesize == 1:
        ser.bytesize = serial.SIXBITS
    elif bytesize == 2:
        ser.bytesize = serial.SEVENBITS
    elif bytesize == 3:
        ser.bytesize = serial.EIGHTBITS
    
    #if parity == 0:  --- NOT IN USE PARITY MUST ALSWAYS BE _NONE TO WORK WITH REAL ROBOT. ONLY ACTIVATE FOR dev/ttyUSB0
        #ser.parity = serial.PARITY_NONE
    #elif parity == 1:
        #ser.parity = serial.PARITY_EVEN
    #elif parity == 2:
        #ser.parity = serial.PARITY_ODD
    #elif parity == 3:
        #ser.parity = serial.PARITY_MARK
    #elif parity == 4:
        #ser.parity = serial.PARITY_SPACE
    ser.timeout = timeout
    ser.baudrate = baudrate
    ser.parity = serial.PARITY_NONE
    
    return ser.name
    


def ping():
    print("got a ping")
    return "pong"

#Returns True if Port is Opened
def isOpen():
    return ser.isOpen()

#Opens Connection and Returns True if Port is Opened
def open():
    ser.open()
    print(ser.VERSION)  
    return ser.isOpen()

#Closes Connection and Returns True if Port is Closed
def close():
    ser.close()
    return not ser.isOpen()

#Writes String and Returns True if successful
def write(message):
    success = False
    if(not ser.isOpen()):
        print("Port is not open")
        ser.write(message)
    else:
        print("Writing:test ", message)
        ser.write(message)
        success = True
    return success

#Reads Data until Timeout or byteCount is reached 
def read(byteCount):
    reply = ""
    if(not ser.isOpen()):
        print("Port is not open")
        reply = "ERR: Port is not open"
    else:
        print("Bytes to read: ", byteCount)
        ser.reset_input_buffer() #Flushes the input buffer
        reply = ser.read(byteCount)
        print("Read: ", reply)
    return reply

#Reads Data until Timeout, until byteCount is reached or until EOS sequence is read
def readUntil(byteCount):
    reply = ""
    if(not ser.isOpen()):
        print("Port is not open")
        reply = "ERR: Port is not open"
    else:
        print("Read until: ", byteCount)
        ser.reset_input_buffer() #Flushes the input buffer
        reply = ser.read_until(terminator = term, size = byteCount)
        print("Read: ", reply)
    return reply


print("Opening XML-RPC Server")
#server = SimpleXMLRPCServer(("127.0.0.1", 25000), allow_none=True)
class MultithreadedSimpleXMLRPCServer(ThreadingMixIn, SimpleXMLRPCServer):
    pass

server = MultithreadedSimpleXMLRPCServer(("0.0.0.0", 40405))
#server.RequestHandlerClass.protocol_version ="HTTP/1.1"
server.register_function(write, "write")
server.register_function(read, "read")
server.register_function(readUntil, "readUntil")
server.register_function(ping, "ping")
server.register_function(open, "open")
server.register_function(close, "close")
server.register_function(isOpen, "isOpen")
server.register_function(configure, "configure")

server.serve_forever()
#rtscts = True,
#dsrdtr=True

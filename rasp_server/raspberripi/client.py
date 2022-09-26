from inspect import walktree
from this import d
import requests
from enum import Enum
from datetime import datetime
import time
#import RPi.GPIO as GPIO
#from lib_nrf24 import NRF24
#import spidev


#GPIO.setmode(GPIO.BCM)

sensor_channel = 0x76
water_pipe = 0xE1
ultra_pipe = 0xE2


class MSG_TYPE(Enum):
    RPI_SETUP_REQUEST = 0
    RPI_SEND_RESULT = 1
    RPI_SETUP_MICRO = 2
    MICRO_SEND_RESULT = 3
    LEVEL_SEND_RESULT = 4

class rpiClient:
    
    def __init__(self):
        self.config = {
            'server_ip': '127.0.0.1',
            'server_port': 3000,
            'id': -1,
            'clients': [],
            'timestamp': time.mktime(datetime.now().timetuple()),
            'pos': {'loc': 37.555914, 'lat': 127.049506},
            'floor': 6,
            'warning_level': 1,
            'water_level': 20
        }


    def setConfig(self, config):
        for k, v in config.items():
            if k in self.config.keys():
                self.config[k] = v


    def send_result(self, LOGGING=False):
        server_url = f'http://{self.config["server_ip"]}:{self.config["server_port"]}'
        server_url += '/rasp/result'

        msg = {
            'id': self.config['id'],
            'timestamp': self.__update_timestamp(),
            'warning_level': self.config['warning_level'],
            'water_level': self.config['water_level']
        }

        if LOGGING:
            print(f'Send result to server...{msg}')

        res = requests.post(server_url, json=msg)

    def request_setup(self, LOGGING=False):
        server_url = f'http://{self.config["server_ip"]}:{self.config["server_port"]}'
        server_url += '/rasp/start'

        msg = {
            'pos': self.config['pos'],
            'floor': self.config['floor'],
            'timestamp': self.config['timestamp'],
            'num_of_devices': len(self.config['clients'])
        }

        if LOGGING:
            print(f'Send result to server...{msg}')

        res = requests.post(server_url, json=msg)
        print(res.json())
        self.config['id'] = res.json()['id']

        if LOGGING:
            print(f'Get ID from server: {self.config["id"]}')

    def run(self, LOGGING=False):

        self.request_setup(LOGGING=LOGGING)

        while True:

            self.send_result(LOGGING=LOGGING)
            time.sleep(2)


    def __update_timestamp(self):
        self.config['timestamp'] = time.mktime(datetime.now().timetuple())
        return self.config['timestamp']


    def __len__(self):
        return len(self.config['clients'])

class rpiServer:
    def __init__(self):
        self.pipes = [[0xE8, 0xE8, 0xF0, 0xF0, 0xE1],[0xF0, 0xF0, 0xF0, 0xF0, 0xE1]]
        self.radio = NRF24(GPIO, spidev.SpiDev())
        self.radio.setChannel(sensor_channel)

    def radio_setup(self, pipe):
        self.pipes[1][4] = pipe
        self.radio.begin(0, 17,4000000)
        self.radio.setPayloadSize(32)
        self.radio.setDataRate(NRF24.BR_1MBPS)
        self.radio.setPALevel(NRF24.PA_MIN)

        self.radio.setAutoAck(True)
        self.radio.enableDynamicPayloads()
        self.radio.enableAckPayload()
        self.radio.openReadingPipe(1, self.pipes[1])

        self.radio.printDetails()
        self.radio.startListening()

        
    def water_listening(self):
        while True:
            while not self.radio.available(0):
                
                time.sleep(1/1000)
                # print("WAIT FOR")
            
            receivedMessage = []
            self.radio.read(receivedMessage, self.radio.getDynamicPayloadSize())
            print("Received: {}".format(receivedMessage))

            print("Translating our received Message into unicode characters...")
            string = ""

            for n in receivedMessage:
                if (n >= 32 and n <=126):
                    string += chr(n)
            print("Our received message decodes to :{}".format(string))

if __name__ == '__main__':
    
    # rasp = rpiClient()
    # rasp.run(LOGGING=True)
    
    
    rasp = rpiServer()
    rasp.radio_setup(water_pipe)
    rasp.water_listening()
    rasp.radio_setup(ultra_pipe)
    

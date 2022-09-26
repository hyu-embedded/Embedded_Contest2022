from inspect import walktree
from this import d
import requests
from enum import Enum
from datetime import datetime
import time
import RPi.GPIO as GPIO
from lib_nrf24 import NRF24
import spidev


GPIO.setmode(GPIO.BCM)

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
    def __init__(self, water_channel, micro_channel, delay=1/1000, threshold = 0, height = 300):
        self.pipes = [[0xE8, 0xE8, 0xF0, 0xF0, 0xE1],[0xF0, 0xF0, 0xF0, 0xF0, 0x00]]
        self.radio = NRF24(GPIO, spidev.SpiDev())
        self.delay = delay
        self.threshold = threshold
        self.height = height


    def radio_setup(self,pipe):
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


    def getMessage(self, LOGGING=False, target=""):
        receivedMessage = []
        self.radio.read(receivedMessage, self.radio.getDynamicPayloadSize())
        
        message = ""
        
        for ch in receivedMessage:
            if (ch >= 32 and ch <= 126):
                message += chr(ch)

        if LOGGING:
            print(f"Received from {target}: {message}")

        
        return int(message)

        
    def run_water(self, LOGGING=False):
        print('Getting data from water level sensor...')
        
        self.radio.setChannel(self.water_channel)
        start_micro = False

        while start_micro == False:
            while not self.radio.available(0):
                time.sleep(self.delay)
                waterlevel = self.getMessage(LOGGING, target="water level sensor")

                if waterlevel >= self.threshold:
                    start_micro = True
                    break



    def run_micro(self, LOGGING=False):
        print('Getting data from micro sensor...')
        self.radio.setChannel(self.micro_channel)

        find_start = False
        start = 0
        waterlevel = 0

        while True:
            while not self.radio.available(0):                
                time.sleep(self.delay)
                print("--")

                
                data = self.getMessage(LOGGING, target="microwave sensor")
                if find_start == False:
                    start = data
                    find_start = True

        
                waterlevel = start - data + self.threshold
            
                print(f'Water level: {waterlevel}')
                


    def run(self, LOGGING=False):
        
        self.run_water(LOGGING)
        self.run_micro(LOGGING)

        


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
                    print(string)
                    info = float(string)
            print("water energy:{}".format(info))

            if (info > 500):
                break

    def ultra_listening(self):
        
        while True:
            iteration = 10
            sum_height = 0
            for i in range(iteration):
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
                        print(string)
                info = float(string)/100
                sum_height += info
            avg_height = sum_height/iteration
            print("water average height :{}cm".format(avg_height))
            return avg_height


if __name__ == '__main__':
    # rasp = rpiClient()
    # rasp.run(LOGGING=True)
    
    
    rasp = rpiServer(water_channel=0x76, micro_channel=0x77, delay=1/1000, threshold=400)
    rasp.radio_setup(water_pipe)
    # # elif message == 2:
    #     # rasp.ultrasensor(water_sensor)
    rasp.water_listening()
    print("hi1")
    rasp.radio_setup(ultra_pipe)
    # rasp.setChannel(ultra_sensor_channel)
    while(1):
        height = rasp.ultra_listening()
        # communication with node server
    print("hi2")
    

    #rasp.run(LOGGING=True)    

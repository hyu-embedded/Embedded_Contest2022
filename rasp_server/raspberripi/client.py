import requests
from enum import Enum
from datetime import datetime
import time


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
        self.config['id'] = res.json()['id']

        if LOGGING:
            print(f'Get ID from server: {self.config["id"]}')

    def __update_timestamp(self):
        self.config['timestamp'] = time.mktime(datetime.now().timetuple())
        return self.config['timestamp']


    def __len__(self):
        return len(self.config['clients'])
    

if __name__ == '__main__':
    
    rasp = rpiClient()
    LOGGING = True

    rasp.request_setup(LOGGING=LOGGING)

    while True:

        rasp.send_result(LOGGING=LOGGING)

        time.sleep(2)


import requests
from datetime import datetime
import time

def warning(a):
    if(a >=100):
        return 5
    elif(80<=a<100):
        return 4
    elif(60<=a<80):
        return 3
    elif(40<=a<60):
        return 2
    else:
        return 1


start_url = 'http://192.168.0.113:3000/rasp/start'
result_url = 'http://192.168.0.113:3000/rasp/result'
# r = requests.get(url)
# print(r.text)
# print(r.status_code)
# r = requests.get(url)
# print(r.url)
# print(r.text)
# print(r.status_code)

# TODO: if(config_file!)

device = ['수위측정', '초음파센서']

hyu_loc = 37.555914
hyu_lat = 127.049506
floor = 6
r = requests.post(start_url, json = {'pos': {'loc': hyu_loc, 'lat' : hyu_lat}, 'floor' : floor, 'num_of_devices' : len(device)})
server_id = r.json()['id']
a = 1
water_sensor = 0
while a > 0:
    if(water_sensor):
        # Receive the data from arduino using ultrasound

        x = datetime.now()
        print(x)
        timestamp = time.mktime(x.timetuple())
        print(timestamp)
        # warning level in terms of height
        b = warning(a)
        r = requests.post(result_url, json = {'height' : a, 'warning_level' : b , 'timestamp' : timestamp })
        print(r.text)
        a += 1
    else:
    # # Receive the data from arduino using water detection(implementing function)
        water_sensor = 1    
    time.sleep(0.5)

# print(r.status_code)
# r = requests.get(url)

# print(r.url)
# print(r.text)
# print(r.status_code)

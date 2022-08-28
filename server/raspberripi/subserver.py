import requests

url = 'http://127.0.0.1:3000'
r = requests.get(url)
print(r.text)
print(r.status_code)

payload = { 'key1' : 'value1', 'key2' : 'value2'}
r = requests.get(url, data = payload)

print(r.url)
print(r.text)
print(r.status_code)

r = r.request.post(url, data = {'key' : 'value'})

print(r.text)
print(r.status_code)


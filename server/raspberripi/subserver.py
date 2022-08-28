import requests

url = 'http://127.0.0.1:3000'
r = requests.get(url)
print(r.text)
print(r.status_code)

r = requests.get(url)

print(r.url)
print(r.text)
print(r.status_code)

r = requests.post(url, data = {'name': 'Test User'})

print(r.text)
print(r.status_code)

r = requests.get(url)

print(r.url)
print(r.text)
print(r.status_code)


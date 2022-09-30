# Embedded_Contest2022

# 프로젝트 개요
본 프로젝트는 집중호우 시 차오르는 물의 높이를 실시간으로 측정하고 해당 어플리케이션에 보내기 위한 프로젝트이다.
파트는 **수위측정, 센서제어, 집계,** 그리고 **어플리케이션** 총 4개의 파트로 구성되었다.

1. **수위측정**은 아두이노 우노와 아두이노 나노로 구성되어있으며 **센서제어**와의 연결은 nRF24l01모듈을 이용하여 RF통신을 했다.
아두이노 나노에서는 빗물감지센서와 RF모듈을 사용하여 데이터를 전송하고,
아두이노 우노에서는 초음파센서와 RF모듈을 사용하여 데이터를 전송한다.
코드는 arduino 폴더에 저장되어 있다.

2. **센서제어**는 라즈베리파이로 구성되어있으며 **수위측정**과 **집계**와의 연결을 해주는 역할을 한다.
**수위측정**과의 연결은 위에 언급한 RF모듈을 통해 통신을 진행했고, **집계**와의 연결은 HTTP연결을 통해 데이터를 주고받는다.
코드는 rasp_server 폴더에 저장되어있다.

3. **집계**는 라즈베리파이로 구성되어있으며 **센서제어**와 **어플리케이션**과의 연결을 해주는 역할을 하면서 동시에 Mongo DB 라이브러리를 사용한 데이터베이스를 구축하여 수신받는 데이터를 집계하고 이를 추가적인 정보와 함께 **어플리케이션**에 보내는 역할을 한다.
코드는 server 폴더에 저장되어있다.

4. **어플리케이션**은 **집계**의 서버로부터 받는 데이터를 화면에 띄우고 동작을 시도하는 곳이다.
코드는 android 폴더에 저장되어있다.

# 설치 환경
nRF24l01 모듈을 사용하기 위해 라이브러리를 설치해줘야한다.
raspberry pi - GPIO, NRF24
arduino - NRF24
해당 사용 방법은 **rasp_server** 폴더에 자세하게 나와있다.

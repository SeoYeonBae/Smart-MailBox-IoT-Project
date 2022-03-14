import RPi.GPIO as GPIO
import time
import sys
import threading
import serial
from socket import *
from select import select
import sys

# Thread names
ledPostTh= threading.Thread()
ledDelTh= threading.Thread()
swTh = threading.Thread()
cdsPostTh = threading.Thread()
cdsDelTh = threading.Thread()
fndTh= threading.Thread()
piazzoCorrectTh= threading.Thread()
piazzoWrongTh= threading.Thread()
piazzoInputTh = threading.Thread()
ultraTh= threading.Thread()
smPostLockTh = threading.Thread()
smPostUnlockTh = threading.Thread()
smDelLockTh = threading.Thread()
smDelUnlockTh = threading.Thread()

# Set up the pin mode
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

#Set up switch
switch_first = 13
switch_second = 19
switch_send = 26
GPIO.setup(switch_first,GPIO.IN)
GPIO.setup(switch_second,GPIO.IN)
GPIO.setup(switch_send,GPIO.IN)
num_first, num_second = 0,0

#cds의 pin번호
cds = 5
GPIO.setup(cds,GPIO.IN)
cds_working = False

# LED의 pin 번호
post_led_l = 4
post_led_r = 17
del_led_l = 27
del_led_r = 22
GPIO.setup(post_led_l,GPIO.OUT)
GPIO.setup(post_led_r,GPIO.OUT)
GPIO.setup(del_led_l,GPIO.OUT)
GPIO.setup(del_led_r,GPIO.OUT)

#서브모터의 pin 번호
motor_post = 8
motor_del = 7
GPIO.setup(motor_post,GPIO.OUT)
GPIO.setup(motor_del,GPIO.OUT)

# Piazzo (music) 의 pin번호 및 연주 계명
GPIO_PIAZZO = 6
GPIO.setup(GPIO_PIAZZO, GPIO.OUT)			# Uses GPIO number

#ultra 시리얼 통신 설정
BAUDRATE = 9600
ser = serial.Serial(port = "/dev/ttyUSB0", baudrate=BAUDRATE, timeout=2)
post_arrive =False
del_arrive = False

# FND 의 pin 번호, 출력모드 설정 및 숫자 배열
gpiopins_first = [25, 24, 23, 18]
gpiopins_second = [21, 20, 16, 12]
number = [ [0,0,0,0] , [0,0,0,1] , [0,0,1,0] , [0,0,1,1] , [0,1,0,0] , [0,1,0,1] , [0,1,1,0] , [0,1,1,1] , [1,0,0,0] , [1,0,0,1] ]  	# 0~9
clear = [1,1,1,1] 																				# Output to clear	
for i in gpiopins_first[0:] :
	GPIO.setup(i,GPIO.OUT)			# 해당 핀을 출력으로 설정함
	GPIO.output(i, 1)					# 초기에는 아무 숫자도 안나타나도록 함
for i in gpiopins_second[0:] :
	GPIO.setup(i,GPIO.OUT)		
	GPIO.output(i, 1)

# socket 설정
HOST = '127.0.0.1'
PORT = 8765
BUFSIZE = 1024

clientSocket = socket(AF_INET,SOCK_STREAM)

try:
	clientSocket.connect((HOST,PORT))
except Exception as e:
	print('')
#led 제어 함수
def ledControlPost() :
	try:
		if GPIO.input(post_led_l) == 1:
			GPIO.output(post_led_l, False)
			GPIO.output(post_led_r, False)
		else:
			GPIO.output(post_led_l, True)
			GPIO.output(post_led_r, True)		
	except:
		return

def ledControlDel():
	try:
		if GPIO.input(del_led_l) == 1:
			GPIO.output(del_led_l, False)
			GPIO.output(del_led_r, False)
		else:
			GPIO.output(del_led_l, True)
			GPIO.output(del_led_r, True)
	except:
		return

#스위치 제어 함수 	
def button_push() :
	global num_first, num_second, post_arrive, del_arrive
	try:
		while True:
			if GPIO.input(switch_first) == 0:
				if num_first < 10:
					num_first  = num_first + 1
					fndTh = threading.Thread(target=fndControl(num_first, num_second))
					fndTh.start()
				else:
					num_first = -1
			elif GPIO.input(switch_second) == 0:
				if num_second < 10:
					num_second  = num_second + 1
					fndTh = threading.Thread(target=fndControl(num_first, num_second))
					fndTh.start()
				else:
					num_second = -1
			elif GPIO.input(switch_send) == 0:
				s_num_first = str(num_first)
				s_num_second = str(num_second)
				s_num = s_num_first+s_num_second+"\n"
				clientSocket.sendall(bytes(s_num,'UTF-8'))
				if (post_arrive):
					smPostLockTh = threading.Thread(target=smPostLock)
					smPostLockTh.start()
				elif (del_arrive):
					smDelLockTh = threading.Thread(target=smDelLock)
					smDelLockTh.start()			
				piazzoInputTh = threading.Thread(target=piazzoInputPw)
				piazzoInputTh.start()			
				num_first = 50				# FND를 다시 초기화
				num_second = 50
				fndTh = threading.Thread(target=fndControl(num_first,num_second))
				fndTh.start()
				num_first, num_second = 0,0
				time.sleep(1)
	except:
		return

#cds 제어 함수 
def postLightAuto() :
	global cds_working
	try :
		while cds_working:
			if GPIO.input(cds) :
				GPIO.output(post_led_l, GPIO.LOW)
				GPIO.output(post_led_r, GPIO.LOW)
			else :
				GPIO.output(post_led_l, GPIO.HIGH)
				GPIO.output(post_led_r, GPIO.HIGH)
	except :
		return

def delLightAuto() :
	global cds_working
	while cds_working:
		try :
			if GPIO.input(cds) :
				GPIO.output(del_led_l, GPIO.LOW)
				GPIO.output(del_led_r, GPIO.LOW)
			else :
				GPIO.output(del_led_l, GPIO.HIGH)
				GPIO.output(del_led_r, GPIO.HIGH)
		except :
			return

#fnd 제어 함수 
def fndControl(num_first, num_second) :
	global gpiopins, number, clear
	if num_first == 50 or num_second == 50:
		for i in range(0,len(gpiopins_first[0:])) :
			GPIO.output( gpiopins_first[i] , clear[i])
		for i in range(0,len(gpiopins_second[0:])) :
			GPIO.output( gpiopins_second[i] , clear[i])
	try :
		# display number 0~9
		for i in range(0,len(gpiopins_first[0:])) :
			GPIO.output( gpiopins_first[i] , number[num_first][i])	
		for j in range(0,len(gpiopins_second[0:])) :
			GPIO.output( gpiopins_second[j] , number[num_second][j])	
		time.sleep(0.5)
	except Exception as msg:
		# clear the display
		for i in range(0,len(gpiopins_first[0:])) :
			GPIO.output( gpiopins_first[i] , clear[i])
		for i in range(0,len(gpiopins_second[0:])) :
			GPIO.output( gpiopins_second[i] , clear[i])
	except:
		return

#피에조 제어 함수 
def piazzoCorrect() :
	global GPIO_PIAZZO
	p = GPIO.PWM(GPIO_PIAZZO, 200)
	note_correct = [261,329,391,554]
	try :
		for i in note_correct[0:] :
			p.start(99)
			p.ChangeFrequency(i)
			time.sleep(0.5)
			p.stop()
	except:
		p.stop()
		return

def piazzoWrong():
	global GPIO_PIAZZO
	p = GPIO.PWM(GPIO_PIAZZO, 200)
	note_wrong = [440, 440, 440]
	try :	
		for i in note_wrong[0:] :
			time.sleep(0.5)
			p.start(99)
			p.ChangeFrequency(i)
			time.sleep(0.5)
			p.stop()
	except:
		p.stop()
		return	

def piazzoInputPw():
	global GPIO_PIAZZO
	p = GPIO.PWM(GPIO_PIAZZO, 200)
	note_wrong = [261,329,391,554]
	try :	
		for i in note_wrong[0:] :
			p.start(99)
			p.ChangeFrequency(i)
			time.sleep(0.5)
			p.stop()
	except:
		p.stop()
		return	
			
# 초음파 제어 함수	
def ultraControl():
	global post_arrive, del_arrive
	if (ser.isOpen() == False):
		ser.open()
	#만약 포트에 데이터가 남아있으면 비우고 새로 시작한다.
	ser.flushInput()
	ser.flushOutput()
	data = ser.readline()			# 초기에 이상한 데이터가 들어올 수 있어서 버리고 시작함 (필요하면 루프를 돌려서 몇개 버리고 시작할 수 있음)
	try:
		while True : 
			ser.flushInput()
			ser.flushOutput()
			# 들어온 패킷을 읽는다.
			data = ser.readline()
			if (len(data) > 0):
				str = data.decode("utf-8")
				if (str[1]=="D"):
					del_dist = eval(str[2: str.find("\n")])
					if del_dist >2000:
						clientSocket.send("del_y\n".encode('utf-8'))
						del_arrive = True
					else:
						clientSocket.sendall(bytes("del_n\n",'UTF-8'))
						del_arrive = False					
				else:
					post_dist = eval(str[2: str.find("\n")])
					if post_dist > 2000:
						clientSocket.sendall(bytes("post_y\n",'UTF-8'))
						post_arrive = True
					else:
						clientSocket.sendall(bytes("post_n\n",'UTF-8'))
						post_arrive = False						
	except Exception as ex:
		return
	finally:
		ser.close()
		
# 모터 제어하는 함수
def smPostLock():
	p=GPIO.PWM(motor_post,50)
	p.start(0)
	cnt = 0
	try:
		p.ChangeDutyCycle(12)
		time.sleep(1)
	except:
		p.stop()
		d.stop()
		return

def smPostUnlock():
	p=GPIO.PWM(motor_post,50)
	p.start(0)
	cnt = 0
	try:
		p.ChangeDutyCycle(7.5)
		time.sleep(1)
	except:
		p.stop()
		d.stop()
		return
		
def smDelLock():
	d=GPIO.PWM(motor_del,50)
	d.start(0)
	cnt = 0
	try:
		d.ChangeDutyCycle(12)
		time.sleep(1)
	except:
		p.stop()
		d.stop()
		return

def smDelUnlock():
	d=GPIO.PWM(motor_del,50)
	d.start(0)
	cnt = 0
	try:
		d.ChangeDutyCycle(7.5)
		time.sleep(1)
	except:
		p.stop()
		d.stop()
		return

# 메인
def main():
	global ledPostTh,ledDelTh, ultraTh, cds_working
	ultraTh = threading.Thread(target=ultraControl)
	ultraTh.start()	
	swTh = threading.Thread(target=button_push)
	swTh.start()
	try:
		while True:				#무한 루프		
			choice = clientSocket.recv(1024)
			c = choice.decode('utf-8')
			if (c =='post_light_man\n'):
				cds_working = False
				ledPostTh = threading.Thread(target=ledControlPost)
				ledPostTh.start()	
			elif (c =='del_light_man\n'):
				cds_working = False
				ledDelTh = threading.Thread(target=ledControlDel)
				ledDelTh.start()	
			elif (c =='post_light_auto\n'):
				if cds_working == True:
					cds_working = False
				else:
					cds_working = True
					cdsPostTh = threading.Thread(target=postLightAuto)
					cdsPostTh.start()                                                                               
			elif (c =='del_light_auto\n'):
				if cds_working == True:
					cds_working = False
				else:
					cds_working = True
					cdsDelTh = threading.Thread(target=delLightAuto)
					cdsDelTh.start()  									
			elif (c =='m_wrong\n'):
				piazzoWrongTh = threading.Thread(target=piazzoWrong)
				piazzoWrongTh.start()
			elif (c == 'm_correct\n'):
				piazzoCorrectTh = threading.Thread(target=piazzoCorrect)
				piazzoCorrectTh.start()				
			elif (c  == 'post_lock\n'):
				smPostLockTh = threading.Thread(target=smPostLock)
				smPostLockTh.start()
			elif (c  == 'del_lock\n'):
				smDelLockTh = threading.Thread(target=smDelLock)
				smDelLockTh.start()		
			elif (c  == 'post_unlock\n'):
				smPostUnlockTh = threading.Thread(target=smPostUnlock)
				smPostUnlockTh.start()
			elif (c  == 'del_unlock\n'):
				smDelUnlockTh = threading.Thread(target=smDelUnlock)
				smDelUnlockTh.start()			
			elif (c =='exit\n'):
				return
			else:
				return
	except KeyboardInterrupt:
		clientSocket.sendall(bytes("Goodbye~~\n",'UTF-8'))
		return
main()
clientSocket.close()
GPIO.cleanup()

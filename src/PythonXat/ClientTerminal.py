import threading
import socket

def thread_lectura():
    try:
        while (msc_recv := sock.recv(1024)):
            if(msc_recv.decode('utf-8')[1] == 'm'):
                print(msc_recv.decode('utf-8').replace("\n", "").replace("[m]", ""))
    finally:
        sock.close()

def thread_escriptura():
    while (msg_sent := input()):
        msg_sent += "\n"
        sock.send(msg_sent.encode('utf-8'))

username = input("username: ") + "\n"

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect(('127.0.0.1', 8080))
sock.sendall(username.encode('utf-8'))

lect = threading.Thread(target = thread_lectura)
escr = threading.Thread(target = thread_escriptura)

lect.start()
escr.start()

lect.join()
escr.join()

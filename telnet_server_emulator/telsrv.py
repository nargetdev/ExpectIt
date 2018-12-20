import socket, threading

HOST = '127.0.0.1'
PORT = 51234 

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(4)
clients = [] #list of clients connected
lock = threading.Lock()


srv_string = \
"""Intf      Detection      Class   Consumed(W) Voltage(V) Current(mA) Temperature(C)
--------- -------------- ------- ----------- ---------- ----------- --------------
0/1       Good           Class4        18.96      52.43      361.69             41
0/2       Good           Class4        23.51      52.31      449.46             41
0/3       Good           Class4        27.06      52.37      516.72             41
0/4       Good           Class4        27.44      52.11      526.60             41
0/5       Good           Class4        29.67      52.05      570.06             45
0/6       Good           Class4        22.35      52.11      428.95             45
0/7       Good           Class4        18.62      52.24      356.44             45
0/8       Good           Class4        14.41      52.31      275.51             45
0/9       Good           Class4        11.46      52.69      217.52             41
0/10      Open Circuit   Unknown        0.00       0.00        0.00             41
0/11      Open Circuit   Unknown        0.00       0.00        0.00             41
0/12      Open Circuit   Unknown        0.00       0.00        0.00             41"""


class chatServer(threading.Thread):
    def __init__(self, (socket,address)):
        threading.Thread.__init__(self)
        self.socket = socket
        self.address= address

    def run(self):
        lock.acquire()
        clients.append(self)
        lock.release()
        print '%s:%s connected.' % self.address
        while True:
            data = self.socket.recv(1024)
            if not data:
                break
            for c in clients:
                c.socket.send(data)
                c.socket.send(srv_string)
        self.socket.close()
        print '%s:%s disconnected.' % self.address
        lock.acquire()
        clients.remove(self)
        lock.release()

while True: # wait for socket to connect
    # send socket to chatserver and start monitoring
    chatServer(s.accept()).start()
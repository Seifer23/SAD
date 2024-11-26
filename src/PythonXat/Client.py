import tkinter as tk
from tkinter import ttk
import sv_ttk
import socket
import threading
import re

class Window(tk.Tk):
    
    def __init__(self):
        super().__init__()

        self.title("Xat")
        self.geometry("400x500")

        self.grid_rowconfigure(0, weight=1)
        self.grid_rowconfigure(1, weight=0)
        self.grid_columnconfigure(0, weight=9)
        self.grid_columnconfigure(1, weight=0)

        ## --- Login window ---
        self.login_frame = ttk.Frame(self)
        self.login_prompt = ttk.Label(self.login_frame, text="Nom d'usuari?")
        self.login_prompt.grid(pady=15)

        self.login_bframe = ttk.Frame(self.login_frame)
        self.login_bframe.grid(row=1)
        self.login_input = ttk.Entry(self.login_bframe)
        self.login_input.bind("<Return>", lambda event: self.login())
        self.login_input.pack(fill="x", expand=True, side=tk.LEFT, padx=5)
        self.login_button = ttk.Button(self.login_bframe, text="Connectar", command=self.login)
        self.login_button.pack(padx=5, side=tk.LEFT)
        self.login_frame.grid()

        ## --- Main window ---
        self.mframe = ttk.Frame(self)
        self.mframe.grid_rowconfigure(0, weight=1)
        self.mframe.grid_rowconfigure(1, weight=0)
        self.mframe.grid_columnconfigure(0, weight=1)
        self.mframe.grid_columnconfigure(1, weight=0)

        self.main_tframe = ttk.Frame(self.mframe)
        self.main_tframe.grid(row=0, sticky="nsew", padx=0, pady=0)
        self.main_tframe.grid_rowconfigure(0, weight=1)
        self.main_tframe.grid_columnconfigure(0, weight=1)
        self.main_tframe.grid_columnconfigure(1, weight=0)
        self.users_frame = ttk.LabelFrame(self.main_tframe, text="Usuaris:")
        self.users_frame.grid(padx=(0, 5), pady=5, column=1, row=0, sticky="ns")
        self.users_frame.grid_propagate(False)
        self.users_frame.config(width=200)
        self.users_list = tk.Text(self.users_frame, state="disabled", width=20, height=10, bd=0, highlightthickness=0)
        self.users_list.pack(fill="both", expand=True, padx=5, pady=5)
        self.userlist= []
    
        self.chat_frame = ttk.LabelFrame(self.main_tframe, text="Chat")
        self.chat_frame.grid(row=0, column=0, padx=5, pady=5, sticky="nsew")
        self.chat_log = tk.Text(self.chat_frame, state="disabled", bd=0, highlightthickness=0)
        self.chat_log.pack(fill="both", expand=True, padx=5, pady=5)

        for color in ["red", "green", "yellow", "blue", "magenta", "cyan"]:
            self.chat_log.tag_configure(color, foreground=color)
        self.main_bframe = ttk.Frame(self.mframe)
        self.main_bframe.grid(row=1, sticky="ew", padx=5, pady=(0,5))
        
        self.main_input = ttk.Entry(self.main_bframe)
        self.main_input.bind("<Return>", lambda event: self.send_msg())
        self.main_input.pack(fill="x", expand=True, side=tk.LEFT)

        self.main_button = ttk.Button(self.main_bframe, text="Envia", command=self.send_msg)
        self.main_button.pack(side=tk.LEFT, padx=(5,0), pady=5)

        sv_ttk.set_theme("dark")

        self.protocol("WM_DELETE_WINDOW", self.on_close) 

    def login(self):
        
        username = self.login_input.get() + "\n"
        
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect(('127.0.0.1', 8080))
        self.sock.sendall(username.encode('utf-8'))

        recvmsg_to_parse = self.sock.recvmsg(1024)[0].decode('utf-8').split("\n")

        frame_title = "Nom d'usuari: " + recvmsg_to_parse[0].replace("[n]", "")
        self.chat_frame.configure(text=frame_title)
        self.mframe.grid(row=0,sticky="news")

        lect = threading.Thread(target = self.recv_msg)
        lect.start()

        self.login_frame.grid_remove()
        if(len(recvmsg_to_parse[1]) > 0):
            self.create_userlist(recvmsg_to_parse[1])
    
    def send_msg(self):
        
        message=self.main_input.get() + "\n"
        if(message == "\n"):
            return
        self.main_input.delete(0, 'end')
        self.chat_log.configure(state='normal')
        self.chat_log.insert(tk.END, (">" + message))
        self.chat_log.configure(state='disabled')
        self.chat_log.see(tk.END)
        self.sock.send(message.encode('utf-8'))

    def recv_msg(self):

        while(msg_recv := self.sock.recv(1024).decode('utf-8')):

            if(msg_recv[1] == 'm'):
                self.print_msg(msg_recv)
            elif(msg_recv[1] == 'u'):
                self.create_userlist(msg_recv)
            else:
                self.update_userlist(msg_recv)


    def print_msg(self, msg):

        color_table = ["red", "green", "yellow", "blue", "magenta", "cyan"]
        color = color_table[int(re.findall(r'\[3(\d)', msg)[0])-1]
        username = msg[msg.find('<') + 1 : msg.find('>')]
        message = msg[msg.find('0m') + 2 : ]
        self.chat_log.configure(state='normal')
        self.chat_log.insert(tk.END, ("<" + username + "> "), color)
        self.chat_log.insert(tk.END, (message))
        self.chat_log.configure(state='disabled')
        self.chat_log.see(tk.END)
   
    def create_userlist(self, msg):
        
        self.userlist = msg.replace("[u]", "").replace("\n", "").split(",")
        self.users_list.configure(state='normal')        
        for username in self.userlist:
            self.users_list.insert(tk.END, (username+"\n"))
        self.users_list.configure(state='disabled')

    def update_userlist(self, msg):

        if(msg[1] == 'n'):
            self.userlist.append(msg.replace("[n]", "").replace("\n", ""))
            self.users_list.configure(state='normal')
            self.users_list.insert(tk.END, (self.userlist[len(self.userlist)-1] + "\n"))
            self.users_list.configure(state='disabled')
        else:
            user = msg.replace("[d]", "").replace("\n", "")
            self.users_list.configure(state='normal')
            self.users_list.delete(str(self.userlist.index(user)+1)+'.0', str(self.userlist.index(user)+2)+'.0')
            self.users_list.configure(state='disabled')
            self.userlist.remove(user)

    def on_close(self):

        if hasattr(self, 'sock'):
            self.sock.shutdown(socket.SHUT_RDWR)
            self.sock.close()
        self.destroy()

    def run(self):

        self.mainloop()

if __name__ == "__main__":
    
    w = Window()
    w.run()

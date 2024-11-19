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

        self.grid_rowconfigure(0,weight=1)
        self.grid_rowconfigure(1,weight=0)
        self.grid_columnconfigure(0,weight=1)

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
        self.mframe.grid_rowconfigure(0,weight=1)
        self.mframe.grid_rowconfigure(1,weight=0)
        self.mframe.grid_columnconfigure(0,weight=1)
        
        self.chat_frame = ttk.LabelFrame(self.mframe, text="")
        self.chat_frame.grid(sticky="news", padx=5, pady=5)
        
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

        frame_title = "Nom d'usuari: " + self.sock.recv(1024).decode('utf-8').replace("\n", "")
        self.chat_frame.configure(text=frame_title)
        self.mframe.grid(row=0,sticky="news")

        lect = threading.Thread(target = self.recv_msg)
        lect.start()

        self.login_frame.grid_remove()
    
    def send_msg(self):
        
        message=self.main_input.get() + "\n"
        self.main_input.delete(0, 'end')
        self.chat_log.configure(state='normal')
        self.chat_log.insert(tk.END, (">" + message))
        self.chat_log.configure(state='disabled')
        self.chat_log.see(tk.END)
        self.sock.send(message.encode('utf-8'))

    def recv_msg(self):

        color_table = ["red", "green", "yellow", "blue", "magenta", "cyan"]

        while(msg_recv := self.sock.recv(1024).decode('utf-8')):
            color = color_table[int(re.findall(r'\[3(\d)', msg_recv)[0])-1]
            username = msg_recv[msg_recv.find('<') + 1 : msg_recv.find('>')]
            message = msg_recv[msg_recv.find('0m') + 2 : ]
            self.chat_log.configure(state='normal')
            self.chat_log.insert(tk.END, ("<" + username + "> "), color)
            self.chat_log.insert(tk.END, (message))
            self.chat_log.configure(state='disabled')
            self.chat_log.see(tk.END)
    
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

import time, cyberpi, mbot2, random, mbuild, socket
from mqtt import MQTTClient
import os
files = os.listdir("/")

cyberpi.console.set_font(12)
# WIFI
SSID = "HONOR200"
PASSWORD = "qm3b4cu9"

# MQTT
BROKER = "10.177.12.169"
PORT = 1883
TOPICcontrole = "Feldspath/controle"
TOPICvitesse  = "Feldspath/vitesse"
TOPIClight    = "Feldspath/light"
TOPICMusic    = "Feldspath/musique"
musique=False

cyberpi.wifi.connect(SSID, PASSWORD)
while not cyberpi.wifi.is_connect():
    cyberpi.console.println("Connexion WiFi...")
    time.sleep(1)
cyberpi.console.println("WiFi connecte")
ip = socket.getaddrinfo("0.0.0.0", 8080)[0][-1][0]
cyberpi.console.println("IP: " + ip)
vitesse = 20

def avancer():
    mbot2.drive_speed(vitesse, -vitesse)
    cyberpi.console.println("avancer")

def reculer():
    mbot2.drive_speed(-vitesse, vitesse)
    cyberpi.console.println("reculer")

def arreter():
    mbot2.drive_power(0, 0)
    cyberpi.console.println("arreter")

def tournerDr():
    mbot2.drive_speed(vitesse/2, vitesse/2)
    cyberpi.console.println("tournerDr")

def tournerGa():
    mbot2.drive_speed(-vitesse/2, -vitesse/2)
    cyberpi.console.println("tournerGa")
    
def light(etat):
        if etat == "ON":
            cyberpi.console.println("LED : ON")
            cyberpi.led.show('green green green green green')
            mbuild.ultrasonic2.set_bri(100, "all", 1)
            
        elif etat == "OFF":
            cyberpi.console.println("LED : OFF")
            cyberpi.led.off("all")
            mbuild.ultrasonic2.set_bri(0, "all", 1)

def create_mqtt_client():
    global client

    client_id = "mbot2_" + str(random.randint(1000, 9999))

    client = MQTTClient(
        client_id,
        BROKER,
        port=PORT,
        keepalive=20
    )
    client.set_callback(on_mqtt_message)
    
def on_mqtt_message(topic, message):
    if isinstance(message, bytes):
        message = message.decode("utf-8")
    test = str(topic)[2:-1]
    cyberpi.console.println(test)

    if test == TOPICcontrole:
        if   message == "avancer":   avancer()
        elif message == "reculer":   reculer()
        elif message == "arreter":   arreter()
        elif message == "tournerDr": tournerDr()
        elif message == "tournerGa": tournerGa()
    elif test == TOPICvitesse:
        global vitesse
        vitesse = int(message)
        cyberpi.console.println("Vitesse: " + str(vitesse))
    elif test == TOPIClight:
        light(message)
    elif test == TOPICMusic:
        cyberpi.console.println("musique")
        global musique
        if musique:
            musique = False
        else:
            musique = True

def start_http_server():
    cyberpi.console.println("1")
    srv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    cyberpi.console.println("2")
    srv.bind(("0.0.0.0", 80))
    cyberpi.console.println("3")
    srv.listen(1)
    cyberpi.console.println("4")
    srv.settimeout(0.05)
    cyberpi.console.println("5")
    return srv

def handle_request(conn):
    conn.settimeout(10)
    try:
        # Lire les headers uniquement
        data = b""
        while b"\r\n\r\n" not in data:
            chunk = conn.recv(256)
            if not chunk:
                break
            data += chunk

        if b"\r\n\r\n" not in data:
            return

        sep = data.find(b"\r\n\r\n")
        headers_raw = data[:sep]
        body_start = data[sep + 4:]
        headers = headers_raw.decode("utf-8")

        content_length = 0
        for line in headers.split("\r\n"):
            if "Content-Length" in line:
                content_length = int(line.split(":")[1].strip())
                break

        # Écriture directe sur disque chunk par chunk — pas de RAM utilisée
        with open("nosEnregistrement", "wb") as f:
            f.write(body_start)
            bytes_received = len(body_start)
            while bytes_received < content_length:
                chunk = conn.recv(256)
                if not chunk:
                    break
                f.write(chunk)
                bytes_received += len(chunk)
        os.rename("nosEnregistrement","nosEnregistrement.mp3")
        #cyberpi.console.println(str(os.listdir(".")))
        cyberpi.console.println("Taille: " + str(bytes_received))
        cyberpi.audio.set_volume(100)
        cyberpi.audio.play("nosEnregistrement.mp3")
        conn.send(b"HTTP/1.1 200 OK\r\nContent-Length: 2\r\n\r\nOK")

    except Exception as e:
        cyberpi.console.println("HTTP err: " + str(e))
    finally:
        conn.close()

def functionMusique(mus):
    if mus:
        cyberpi.console.println("musique")
        cyberpi.audio.play_music(60, 0.45)
        cyberpi.audio.play_music(67, 0.45)
        cyberpi.audio.play_music(71, 0.45)
        time.sleep(0.5)
        cyberpi.audio.play_music(67, 0.2)
        cyberpi.audio.play_music(72, 0.2)
        cyberpi.audio.play_music(71, 0.2)
        cyberpi.audio.play_music(69, 0.2)
        cyberpi.audio.play_music(67, 0.2)
        cyberpi.audio.play_music(69, 0.2)
        cyberpi.audio.play_music(71, 0.2)
        cyberpi.audio.play_music(67, 0.5)
        cyberpi.audio.play_music(62, 0.5)
        cyberpi.audio.play_music(67, 0.5)
        cyberpi.audio.play_music(71, 0.5)
        time.sleep(0.5)
        cyberpi.audio.play_music(67, 0.2)
        cyberpi.audio.play_music(72, 0.2)
        cyberpi.audio.play_music(71, 0.2)
        cyberpi.audio.play_music(69, 0.2)
        cyberpi.audio.play_music(67, 0.2)
        cyberpi.audio.play_music(69, 0.2)
        cyberpi.audio.play_music(71, 0.2)
        cyberpi.audio.play_music(74, 0.2)
        cyberpi.audio.play_music(71, 0.2)
        time.sleep(0.125)
def connect_mqtt():

    while True:
        try:
            create_mqtt_client()
            cyberpi.console.println("Connexion MQTT...")
            client.connect()
            cyberpi.console.println("test1")
            client.subscribe(TOPIClight)
            client.subscribe(TOPICcontrole)
            client.subscribe(TOPICvitesse)
            client.subscribe(TOPICMusic)
            cyberpi.console.println("test2")
            
            
            cyberpi.console.println("MQTT connecte !")
            return

        except Exception as e:
             cyberpi.console.println("MQTT: " + str(e))
             time.sleep(2)

# Connexion initiale
connect_mqtt()

http_server = start_http_server()

last_ping = time.time()

while True:

    # MQTT
    try:
        client.check_msg() 
        functionMusique(musique)
        # Keepalive manuel
        if time.time() - last_ping > 10:
            client.ping()
            last_ping = time.time()

    except Exception as e:

        cyberpi.console.println("MQTT perdu")
        arreter()

        try:
            client.disconnect()
        except:
            pass

        # Reconnexion WiFi
        if not cyberpi.wifi.is_connect():

            cyberpi.console.println("Reconnexion WiFi")

            cyberpi.wifi.connect(SSID, PASSWORD)

            while not cyberpi.wifi.is_connect():
                time.sleep(1)

            cyberpi.console.println("WiFi OK")

        # Reconnexion MQTT
        connect_mqtt()

    # HTTP AUDIO
    try:
        
        conn, addr = http_server.accept()

        conn.settimeout(10)
        
        cyberpi.console.println("Audio de " + str(addr[0]))

        handle_request(conn)

    except OSError:
        pass
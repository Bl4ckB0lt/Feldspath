import time, cyberpi, mbot2, random, mbuild, socket
from mqtt import MQTTClient

# WIFI
SSID = "HONOR200"
PASSWORD = "qm3b4cu9"

# MQTT
BROKER = "10.218.228.169"
PORT = 1883
TOPICcontrole = "Feldspath/controle"
TOPICvitesse  = "Feldspath/vitesse"
TOPIClight    = "Feldspath/light"

cyberpi.wifi.connect(SSID, PASSWORD)
while not cyberpi.wifi.is_connect():
    cyberpi.console.println("Connexion WiFi...")
    time.sleep(1)
cyberpi.console.println("WiFi connecte")

vitesse = 20

def avancer():
    mbot2.drive_speed(vitesse, -vitesse)

def reculer():
    mbot2.drive_speed(-vitesse, vitesse)

def arreter():
    mbot2.drive_power(0, 0)

def tournerDr():
    mbot2.drive_speed(vitesse/2, vitesse/2)

def tournerGa():
    mbot2.drive_speed(-vitesse/2, -vitesse/2)

def light(etat):
    if etat == "ON":
        cyberpi.led.show('green green green green green')
        mbuild.ultrasonic2.set_bri(100, "all", 1)
    elif etat == "OFF":
        cyberpi.led.off("all")
        mbuild.ultrasonic2.set_bri(0, "all", 1)

def on_mqtt_message(topic, message):
    if isinstance(message, bytes):
        message = message.decode("utf-8")
    test = str(topic)[2:-1]

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

def start_http_server():
    srv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    srv.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    srv.bind(("0.0.0.0", 8080))
    srv.listen(1)
    srv.settimeout(0.05)
    cyberpi.console.println("HTTP ready :8080")
    return srv

def handle_request(conn):
    data = b""
    while b"\r\n\r\n" not in data:
        data += conn.recv(1024)

    headers_raw, body_start = data.split(b"\r\n\r\n", 1)
    headers = headers_raw.decode("utf-8", errors="ignore")

    content_length = 0
    for line in headers.split("\r\n"):
        if "Content-Length" in line:
            content_length = int(line.split(":")[1].strip())
            break

    body = body_start
    while len(body) < content_length:
        body += conn.recv(4096)

    if b"Content-Type: audio" in body:
        audio_start = body.index(b"\r\n\r\n", body.index(b"Content-Type: audio")) + 4
        boundary_line = headers.split("boundary=")[1].split("\r\n")[0]
        end_marker = ("\r\n--" + boundary_line + "--").encode()
        audio_end = body.rfind(end_marker)
        audio_data = body[audio_start:audio_end]

        with open("/tmp/received.wav", "wb") as f:
            f.write(audio_data)

        cyberpi.console.println("Audio recu, lecture...")
        cyberpi.audio.play_file("/tmp/received.wav")
        conn.send(b"HTTP/1.1 200 OK\r\nContent-Length: 2\r\n\r\nOK")
    else:
        conn.send(b"HTTP/1.1 400 Bad Request\r\nContent-Length: 3\r\n\r\nERR")

    conn.close()

client_id = "mbot2_" + str(random.randint(1000, 9999))
client = MQTTClient(client_id, BROKER, port=PORT, keepalive=60)
client.set_callback(on_mqtt_message)

connected = False
attempts = 0
while not connected and attempts < 5:
    try:
        cyberpi.console.println("Connexion MQTT...")
        client.connect()
        connected = True
        cyberpi.console.println("MQTT connecte !")
    except Exception as e:
        attempts += 1
        cyberpi.console.println("Echec " + str(attempts) + "/5")
        time.sleep(3)

if not connected:
    cyberpi.console.println("MQTT impossible")
else:
    client.subscribe(TOPIClight)
    client.subscribe(TOPICcontrole)
    client.subscribe(TOPICvitesse)

    http_server = start_http_server()

    while True:
        client.check_msg()
        try:
            conn, addr = http_server.accept()
            cyberpi.console.println("Audio de " + str(addr[0]))
            handle_request(conn)
        except OSError:
            pass
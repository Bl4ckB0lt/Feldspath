import time, cyberpi, mbot2, random
from mqtt import MQTTClient

# WIFI
SSID = "HONOR200"
PASSWORD = "qm3b4cu9"

# MQTT
BROKER = "broker.emqx.io"
PORT = 1883
TOPIC = "mbot2/distance"
TOPICcontrole = "Feldspath/controle"
TOPICvitesse = "Feldspath/vitesse"
TOPIClight = "Feldspath/light"

# Connexion WiFi
cyberpi.wifi.connect(SSID, PASSWORD)

while not cyberpi.wifi.is_connect():
    cyberpi.console.println("Connexion WiFi...")
    time.sleep(1)

cyberpi.console.println("WiFi connecte")

vitesse = 20

def avancer():
    mbot2.drive_speed(vitesse, -vitesse)
    cyberpi.console.println("avancer")
    

def reculer():
    mbot2.drive_speed(-vitesse, vitesse)
    cyberpi.console.println("reculer")

def arreter():
    mbot2.drive_speed(0, 0)
    cyberpi.console.println("arreter")

def tournerDr():
    mbot2.drive_speed(vitesse, vitesse)
    cyberpi.console.println("tournerDr")

def tournerGa():
    mbot2.drive_speed(-vitesse, -vitesse)
    cyberpi.console.println("tournerGa")
    
def light(etat):
    if etat == "ON":
        mbuild.quad_rgb_sensor.set_fill_light("white", 1)
    elif etat == "OFF":
        mbuild.quad_rgb_sensor.set_fill_light("OFF", 1)
    
def on_mqtt_message(topic, message):
    if isinstance(message, bytes):
        message = message.decode("utf-8")
    cyberpi.console.println(message)
    
    if topic == TOPICcontrole:
        if message == "avancer":
            avancer()
        elif message == "reculer":
            reculer()
        elif message == "arreter":
            arreter()
        elif message == "tournerDr":
            tournerDr()
        elif message == "tournerGa":
            tournerGa()
        #    arreter()
        #time.sleep(1)
    elif topic == TOPICvitesse:
        vitesse = message * 100
    elif topic == TOPIClight:
        light(message)
        
        

# Client MQTT
client_id = "mbot2_" + str(random.randint(1000, 9999))
client = MQTTClient(client_id, BROKER, port=PORT, keepalive=60)
client.set_callback(on_mqtt_message)

# Connexion MQTT
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
    client.subscribe(TOPICcontrole)
    client.subscribe(TOPICvitesse)
    client.subscribe(TOPIClight)
    while True:
        client.check_msg()
        time.sleep(1)

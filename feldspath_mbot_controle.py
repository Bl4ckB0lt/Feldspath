import time, cyberpi, mbot2, random,mbuild
from mqtt import MQTTClient

# WIFI
SSID = "HONOR200"
PASSWORD = "qm3b4cu9"

# MQTT
BROKER = "10.218.228.169"
PORT = 1883
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

            

    
def on_mqtt_message(topic, message):
    if isinstance(message, bytes):
        message = message.decode("utf-8")
        test = str(topic)[2:-1]
    
    if test == TOPICcontrole:
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
    elif test == TOPICvitesse:
        global vitesse 
        vitesse = int(message)
        cyberpi.console.println(vitesse)
    elif test == TOPIClight:
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
    client.subscribe(TOPIClight)    
    client.subscribe(TOPICcontrole)
    client.subscribe(TOPICvitesse)
    
    while True:
        client.check_msg()

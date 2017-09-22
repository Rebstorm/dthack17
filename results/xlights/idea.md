# iLight - Die sprechende Ampel

Wir entwickeln eine günstige Ampelnachrüstung für die SmartCity, das Autofahrer*innen und Fußgänger*innen auf ihrem Smartphone über eine einfache Push-Nachricht informiert, sobald ihre Ampel Grün wird. Somit wird das Problem aufgegriffen, dass sehr viele Verkehrsteilnehmer während der Wartezeit an der Ampel auf ihr Smartphone schauen. 

<<<<<<< 5d05b7cc3fd3192574a72f742eb8e2560d00979a:smartampel/idea.md
  
=======
# Bestandteile 

## Prototyp der smarten Ampel

- RaspberryPi 3 als iBeacon
- iBeacon sendet Ampelsignal aus (Fussgängerampel)
  - ID der Ampel, die zur ID in der STadtdankenbank passt 
- Orientierung und Co. wird vom native vom iBeacon gelöst 
- Richtantenne, um die genaue Richtung der Ampel zu beeinflussen 

## Die Stadtdatenbank 

- Backend mit Datenbank, dass die Zustände aller Ampel darstellt 
- Hat ein Endpunkt die mir für ID einen Ampelzustand liefert 
- dreamofjapan.de
- in der ID zurückgeleifert werden sollte der Straßenname 
- In der Datenbank 
  - ID 
  - Straßenname
  - AKtuelleer status
  - Entfernung Ampel 
  - GPS Koordinate der Ampel
  - GPS Koordinate des Beacon 

## Die Empfängerapp 

- NAtive Android-App
- Background-Prozess ohen eigene Oberfläche 
- Konfigurationsoberfläche
  - Darstellugn der Notifacations 
  - Nur Fußgängerampel  / Alle Ampelarten 
  - ..  
- Push Notifactions 
  - 
>>>>>>> added boilerplate for city-backend;:smartampel/idea.txt

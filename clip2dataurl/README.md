# Clip2DataUrl
Motiviert von einem Artikel in der c´t 
[Gut gebettet](https://www.heise.de/select/ct/2023/10/2307509274576696945)
habe ich ein entsprechendes Werkzeug in Java programmiert, welches unter Linux für alle
bisher getesteten Formate funktioniert. 

- Nachteil: Java (mindestens Version 1.8) muss installiert sein.
- Vorteil: Identischer Code für Linux, MacOS, Windows, getestet unter Linux.

### Verwendung
- Ein gewünschtes Objekt (Bild, Html, Text, Datei) in die Zwischenablage kopieren
	- Datei: nur eine Datei selektieren!
- Konvertierung starten mit: `java -jar Clip2DataUrl.jar`
- Die Zwischenablage enthält nun die Daten-URL.
- Testen: einfach in die URL-Zeile eines Browsers einfügen.
- Für einfachere Verwendung: eine Tastaturabkürzung oder ein Icon anlegen.
---
J. Weimar



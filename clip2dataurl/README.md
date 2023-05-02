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

### Changes for java9 (needed in MacOsX)
Java 9 introduced a new interface for images: MultiResolutionImage
where Java 8 uses BufferedImage. Solution: use a multi-release jar and code different version sof a helper class.
See [How To Use Multi-release JARs To Target Multiple Java Versions](https://nipafx.dev/multi-release-jars-multiple-java-versions/)

`javac --release 8 -d bin src/clip2dataurl/*.java`

`javac --release 9 -d bin-9 src-9/clip2dataurl/*.java`

`jar --create --file Clip2DataUrl.jar --main-class=clip2dataurl.ClipToDataUrl -C bin . --release 9 -C bin-9 . `

---
J. Weimar



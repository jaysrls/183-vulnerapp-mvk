<# Vulnerapp

-- A Vulnerable Sample Spring Boot Application

This application uses a relatively modern stack but is still vulnerable to a set of attacks.
Featuring:

- [XSS](https://portswigger.net/web-security/cross-site-scripting)
- [SQLi](https://portswigger.net/web-security/sql-injection)
- [CSRF](https://portswigger.net/web-security/csrf)
- [SSRF](https://portswigger.net/web-security/ssrf)
- Fake Logins
- Info Exposure
- Plain Passwords
- ...

Either start it via IDE or start it with the following command (it will hang). Then visit http://localhost:8080/

```console
./gradlew bootRun
```

**Security Discussion**

  Ich habe diverse Sicherheitsmechanismen implementiert oder verbessert. Ich habe die Filter Chain von Spring Security verwendet. Dort wird die Authentifizierung und Autorisierung zentral konfiguriert. Ich habe dort eine Rollenbasierte Zugriffsregelung eingefügt, damit nur Admins Admin-Endpunkte aufrufen können etc. Die zwei Rollen sind Admin und User. Dies hilft der Sicherheit, weil es die beiden Endpunkte gut trennen kann und die verschiedenen Benutzer auch. Ausserdem gibt es Session-basierte Authentifizierung. Die Session wird im Backend gespeichert mit Hilfe vom Authentication manager. Das Token wird dann gesendet an das Frontend und dort gespeichert. Dies hilft dabei die Benutzer nicht konstant als eingeloggt zu betrachten sondern nur ihre aktive Session zu beachten. Ich habe ausserdem einen sauberen Exception Handler mit ChatGPT bauen lassen. Ausserdem wurden die Passwörter angepasst und werden encoded bevor sie auf die Datenbank geschrieben werden. Kleine Anpassungen habe ich auch vorgenommen, wie zum Beispiel die Anpassung der Rest-Verben und berechtigungen für verschiedene Endpoints. Durch all diese Sicherheitsanpassungen ist die Sicherheit der Applikation grundlegend verbessert worden.


Was man noch implementieren könnte, wäre noch sichereres Speichern der Passwörter, zum Beispiel mit Salts, oder stärkeres Encrypten der Passwörter. Dies könnte man warscheinlich relativ einfach auch im AdminService.java implementieren. Ein Secret für die stärkere Encryption müsste man gut in ein Secret geeignetes Medium auslagern. Man könnte zusätzlich die Sessions noch customizen, damit sie kürzer gültig sind. 

Bei der Umsetzung des Projektes gab es diverse Schwierigkeiten. Die Umsetzung der CSRF protection ging enorm lange und es gab konstant Fehler, bis endlich das Backend mit dem Frontend richtig konfiguriert war. Ausserdem war KI praktisch nutzlos beim Debuggen dieser Fehler. Es gab auch noch andere Fehler, etwa das alle Requests 403 oder 401 Fehler warfen. Ich konnte im Endeffekt aber alle Fehler lösen und habe jetzt (hoffentlich) eine sicherere Applikation als vorher. 

Bei dieser Spezifischen Aufgabe sehe ich den Ertrag natürlicherweise nur in Form von beispielen, da die Applikation von Anfang an bewusst unsicher war. Bis ich endlich verstanden habe wie die Applikation funktioniert und alle Changes implementieren konnte hatte ich einen sehr grossen Zeitaufwand. Beim Betrieb habe ich weniger Aufwand für die Security, weil ich selte eine Applikation neu aufbaue und in den bestehenden Applikationen die Security-Massnahmen grundlegend schon gegeben sind. 


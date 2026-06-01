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


Was man noch implementieren könnte, wäre noch sichereres Speichern der Passwörter, zum Beispiel mit Salts, oder stärkeres Encrypten der Passwörter. Dies könnte man warscheinlich relativ einfach auch im AdminService.java implementieren. Ein Secret für die stärkere Encryption müsste man gut in ein Secret geeignetes Medium auslagern.


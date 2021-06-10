# Note App


### **Linkki Backend koodiin**

[https://github.com/Mahamurahti/Memoable_Backend](https://github.com/Mahamurahti/Memoable_Backend)

### Projektin raportti

[https://github.com/Mahamurahti/Memoable/blob/main/docs/otp2_project%20report.pdf](https://github.com/Mahamurahti/Memoable/blob/main/docs/otp2_project%20report.pdf)

## **Projektista:**

Sovelluksen kehittämisessä käytimme IntelliJ IDEA ohjelmistoympäristönä, Java-Spring sovelluskehystä back end:iin ja MongoDB tietokantaa muistiinpanojen tallettamiseen. Jenkins palvelinta käytettiin jatkuvan integroinnin ja testauksen ylläpitoon.

### Sovelluksen ajaminen

Lataa sovelluksen suoritettava .jar [tiedosto](https://gitlab.metropolia.fi/nicoja/ohjelmistotuotantoprojekti-1/-/blob/master/Note-App-1.0.jar)

Siirry komentorivillä kansioon, jossa .jar tiedosto on `cd` komennolla


Suorita sovellus komennolla

`java -jar Note-App-1.0.jar`


### **Kehitys- ja konfigurointiohjeet:**

Sovellus vaatii Apache-Maven asennuksen, jos ohjelma ei ole asennettu voi seurata ohjeita valmistajan sivuilta
[https://maven.apache.org/install.html](https://maven.apache.org/install.html)

Sovelluksen voi asentaa hakemalla projektin tästä repositoriosta seuraavilla komennoilla.

1. Ensin siirry kansioon johon haluat projektin sijoittaa `cd` komennolla.

2. Seuraavaksi lataa projekti omalle laitteelle käyttämällä `git clone` komentoa

    `git clone https://gitlab.metropolia.fi/nicoja/ohjelmistotuotantoprojekti-1.git`

3. Siirry ladattuun kansioon 

    `cd ohjelmistotuotantoprojekti-1`

4. Käynnistä sovellus käyttämällä maven javafx:run komentoa

    `mvn javafx:run`

Sovelluksen back end vaatii yhteyden Metropolian omaan verkkoon joko VPN-etäyhteydellä tai fyysisellä yhteydellä Metropolian verkkoon
VPN asennusohjeet löytyvät sivuilta [https://wiki.metropolia.fi/pages/viewpage.action?pageId=149652071](https://wiki.metropolia.fi/pages/viewpage.action?pageId=149652071)

-------

### Tekijät: Eric Keränen, Matias Vainio, Jere Salmensaari, Teemu Viljanen & Nico Järvinen


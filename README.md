# Bildr

Dette er mitt bidrag til intervjucase
Case – TSA
 
Lag en applikasjon som tar et bilde med mobilens kamera og viser bildet i appen.

•	Velg selv teknologi/rammeverk/plattform. Tenk at valget du tar skal kunne benyttes i en produksjonsapp hos Politiet. Tenk gjennom hvorfor du velger som du gjør.

•	Forsøk å lage ryddig og oversiktlig kode som følger best practices for valgt programmeringsspråk

•	Bruk rimelig tid på oppgaven, og forklar heller hva du ville gjort videre med mer tid i intervjuet

 
Ekstra: Hvis du har god tid, se om du kan la appen ta tre bilder på rad. Vis alle i appen som thumbnails, i en liste el.


# Løsning
<p align"center">
 <img src="https://raw.githubusercontent.com/airien/Bildr/master/screenshots/bildr5.jpg" width="210"/>
 <img src="https://raw.githubusercontent.com/airien/Bildr/master/screenshots/bildr1.jpg" width="210"/>
 <img src="https://raw.githubusercontent.com/airien/Bildr/master/screenshots/bildr2.jpg" width="210"/>
 <img src="https://raw.githubusercontent.com/airien/Bildr/master/screenshots/bildr6.jpg" width="210"/>
</p>

Jeg valgte å løse dette i Kotlin kjørende på native Android.
Grunnen til at jeg valgte å løse det i native Android er at oppgaven innebærer å jobbe med hardware på mobilen, kameraet. 

Kamera er noe av det mer kompliserte man kan gjøre på mobil. Man må få tilgang til hardware, passe på at man ikke lukker kamera mens man åpner det, man kan ikke ta bilde når kameraet er lukket, man må følge med på hvilken retning mobilen holdes i, og aspekt ratio må tas hensyn til.
Det involverer å få tilgang til systemressurser, og man må håndtere tilgang fra brukeren.

Dette har vært prøvd før med mer eller mindre hell på kryssplattformrammeverk. Det er mulig, og det kan bli helt greit, men det vil være litt mer vondt å implementere og vanskeligere å vedlikeholde.

Kotlin ble valgt fordi det gir mye mer robust, og mindre kode. Det håndterer ting som nullpointers, lambdas og binding mot view-objekter innebygget i språket. Jeg kunne løse oppgaven med et minimum av eksterne biblioteker, selv med en veldig lett versjon av dependency injection. 

Det med å ikke bruke flere biblioteker enn nødvendig er et poeng hos politiet, da det er begrenset tilgang til eksterne repositories, og man ikke bare kan bruke alt man kommer over. Det skal være sikkert og det skal være helt nødvendig. 

Det er veldig mange apputviklere som nå kun jobber med Kotlin på Android, på samme måte som mange nå jobber med Swift i stedet for Objective-C på iOS. Å jobbe opp kompetanse på dette er dermed smart.
I tillegg er det mulig å jobbe med både Java og Kotlin i samme prosjekt, så man ekskluderer ikke Java hvis det er nødvendig. Man kan også bruke alle de samme bibliotekene.

Hadde jeg hatt mer tid, ville jeg gjort følgende:


•	Implementert capture callbacks

•	Vist thumbnails på kamerasiden etterhvert som bilder ble tatt

•	Laget en wrappet versjon for SecureHub for å vise at det fungerer i politiets sikkerhetssystem

•	Laget ferdig innlogging

•	Laget en versjon i Swift også

•	Stylet det litt mer, og gjort det litt mer shiny





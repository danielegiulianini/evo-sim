# Evolution Simulator (evo-sim)

Il progetto Evolution Simulator consiste nella realizzazione di un simulatore di evoluzione naturale di entità chiamate blob, in grado di muoversi e interagire
con altre entità all’interno di un mondo. Il comportamento di un blob è definito dalla sua tipologia, che determina l’aggiornamento delle sue proprietà nel corso della simulazione e il risultato prodotto da una collisione con un’altra entità della simulazione, dipendente dalla tipologia di quest’ultima. Per sopravvivere i blob devono muoversi all’interno del mondo della simulazione alla ricerca di cibo nutriente per aumentare la loro vita corrente. La proprietà di effetto del cibo con cui un blob collide determina le variazioni delle sue proprietà, oltre all’eventuale introduzione di ulteriori blob nel mondo della simulazione. La simulazione può inoltre presentare ostacoli statici che, analogamente ai cibi, comporteranno delle modifiche allo stato del blob a seguito di una collisione. I cibi vengono generati da entità chiamate piante, che a intervalli regolari producono un cibo con un effetto di collisione dipendente dal comportamento della pianta. La sessione di simulazione è inoltre caratterizzata da un valore di luminosità e un valore di temperatura variabili, che influenzano rispettivamente la dimensione del campo visivo e la velocità di tutti i blob presenti. La quantità delle diverse entità, la luminosità, la temperatura e la quantità di giornate di cui è composta la simulazione sono parametrizzabili dall’utente attraverso un’apposita interfaccia. Al termine della simulazione verranno rappresentati mediante grafici informazioni sulle entità osservate nelle diverse giornate in base alla loro categoria e alle loro proprietà

## Guida utente(swing):
La  schermata  iniziale  dell’applicazione  consiste  in  un’interfaccia  che  consente  all’utente  di  impostare  i  seguenti  parametri  per  l’avvio  di  una  sessione  di simulazione:

* il  numero  di  blob,  distribuiti  in  equa  misura  tra  Base  Blob  e  CannibalBlob
* il numero di piante, distribuite tra Standard Plant, Reproducing Plant ePoisonous Plant
* il numero di ostacoli, distribuiti tra Base Obstacle condamageEffecteslowEffect
* il valore della luminosità minima
* il valore della temperatura minima
* la durata in giornate della sessione di simulazione

<img src="https://github.com/alessandro-oliva4/evo-sim/blob/develop/doc/report/img/InputInterface.png" width="300">

Cliccando il pulsante Start si procederà all’avvio della simulazione.La schermata della simulazione è caratterizzata da una barra di indicatoriin real-time e da un pannello rappresentare il mondo di della simulazione.

<img src="https://github.com/alessandro-oliva4/evo-sim/blob/develop/doc/report/img/SimulationInterface.png" width="600">

* i blob sono rappresentati da circonferenze. È rappresentata anche l’ampiezza del loro campo visivo mediante una circonferenza con perimetro dicolore giallo.  Il colore è determinato dal suo tipo:
  * blu per i Base Blob 
  * rosso per i Cannibal Blob 
  * magenta per i Poisonous Blob 
  * grigio per gli Slow Blob
* i cibi sono rappresentati da triangoli verdi. I cibi con effettostandardFoodEffectsono più piccoli dei cibi con effettopoisonousFoodEffect, e questi ultimisono più piccoli dei cibi con effettoreproducingFoodEffect;
* gli ostacoli sono rappresentati da rettangoli rossi.  Gli ostacoli con effettodamageEffectsono più piccoli degli ostacoli con effettoslowEffect;
* le piante sono rappresentate da rettangoli con colore dipendente dal lorotipo:
  ** verde per le Standard Plant
  ** rosa per le Reproducing Plant
  ** magenta per le Poisonous Plant
Il colore di sfondo varia da blu a rosso in base al valore della temperaturacorrente, e vi è un filtro nero con un valore di trasparenza dipendente dal valoredi  luminosità  che  copre  le  entità  diverse  dai  blob  nel  caso  queste  non  siano all’interno del loro campo visivo. La barra degli indicatori mostra in tempo reale il giorno corrente, il numerodi entità blob presenti nella simulazione e i valori di temperatura e luminosità.

Al termine della simulazione, verrà visualizzato un pannello riassuntivo
<img src="https://github.com/alessandro-oliva4/evo-sim/blob/develop/doc/report/img/ResultsInterface.png" width="600">

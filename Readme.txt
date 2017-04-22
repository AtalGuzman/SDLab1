El presente laboratorio consiste en la simulaci�n de una red p2p con el patr�n de publisher subscriber.

Para la implementaci�n se crean tres interfaces publisher, topics y subscriber, para modelar
los tres posibles comportamientos del nodo en la red y no volver a los comportamientos excluyentes.

Por otro lado, se crean tres tipos de mensajes TopicMsg, SubMsg y PubMsg con el mismo objetivo
anterior, pero a nivel de los mensajes que est�n siendo enviados en la red.

Al inicializar la red se necesita al menos un t�pico, es por ello que en el archivo de configuraci�n 
se agrega el atributo cantTopic, el cual es utilizado durante la inicializaci�n para este objetivo. 
Dicho lo anterior este debe tomar al menos el valor 1.
Ahora, dado que se asume que un nodo tendr� un t�pico, cantTopic debe ser inicializado a lo m�s con el mismo
n�mero de nodos de la red.

Durante la inicializaci�n se realiza de modo aleatorio la asignaci�n de t�pico, registro de publicadores
y subscripci�n. As� se crean todos los t�picos creados y todos quedan publicando en alg�n t�pico, aunque no todos 
quedan subscriptos.

Cuando, los mensajes se env�an a trav�s deun flooding controlado cuando los mensajes llega a su destino y adem�s
el nodo contiene el t�pico solicitado, o est� subscrito o registrado como publicador.
	Si es t�pico realiza las notificaciones pertinentes
	Si es subscriptor recibe el mensaje y lo muestra por pantalla
	Si es publicador lo reconoce como updaterequest y crea un nuevo post

Cada vez que un mensaje de registro o subscripci�n llega a un t�pico y son correctos, es decir, no llegan
desde subscriptores o publicadores ya ingresados, se muestra la nueva configuraci�n de la red

Lo mismo ocurre cuando se crea un nuevo t�pico.

Para evitar que la red colapse en mensajes se les da un ttl, sin embargo, esto implica una mejora que puede ser 
realizada en cuando al manejo de mensajes ca�dos y acusos de recibo.
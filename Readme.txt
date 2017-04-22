El presente laboratorio consiste en la simulación de una red p2p con el patrón de publisher subscriber.

Para la implementación se crean tres interfaces publisher, topics y subscriber, para modelar
los tres posibles comportamientos del nodo en la red y no volver a los comportamientos excluyentes.

Por otro lado, se crean tres tipos de mensajes TopicMsg, SubMsg y PubMsg con el mismo objetivo
anterior, pero a nivel de los mensajes que están siendo enviados en la red.

Al inicializar la red se necesita al menos un tópico, es por ello que en el archivo de configuración 
se agrega el atributo cantTopic, el cual es utilizado durante la inicialización para este objetivo. 
Dicho lo anterior este debe tomar al menos el valor 1.
Ahora, dado que se asume que un nodo tendrá un tópico, cantTopic debe ser inicializado a lo más con el mismo
número de nodos de la red.

Durante la inicialización se realiza de modo aleatorio la asignación de tópico, registro de publicadores
y subscripción. Así se crean todos los tópicos creados y todos quedan publicando en algún tópico, aunque no todos 
quedan subscriptos.

Cuando, los mensajes se envían a través deun flooding controlado cuando los mensajes llega a su destino y además
el nodo contiene el tópico solicitado, o está subscrito o registrado como publicador.
	Si es tópico realiza las notificaciones pertinentes
	Si es subscriptor recibe el mensaje y lo muestra por pantalla
	Si es publicador lo reconoce como updaterequest y crea un nuevo post

Cada vez que un mensaje de registro o subscripción llega a un tópico y son correctos, es decir, no llegan
desde subscriptores o publicadores ya ingresados, se muestra la nueva configuración de la red

Lo mismo ocurre cuando se crea un nuevo tópico.

Para evitar que la red colapse en mensajes se les da un ttl, sin embargo, esto implica una mejora que puede ser 
realizada en cuando al manejo de mensajes caídos y acusos de recibo.
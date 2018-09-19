# chat-server-client (broadcaster)

## English

Simple chat server (Broadcaster) using Java and Sockets, influenced by many other similar projects found on the web. This project also contains a client with GUI for the enduser to be able to connect and properly use this service, this can be used both locally in the users intranet to chat with other people inside the same network, and through the internet. Setting the desired enviroment is easy, and can be done as follows:

  1- Be it locally or through the web, first decide what port you want your server to listen to, and set that in the main() method of chat-server-client/serverSide/Server.java (lines 43 to 47). 
```	
Server sv = new Server(9191); 
sv.run();
```
  When an instance of server is created its argument is an int that, as you might have guessed already, decides the port it will listen to, by default its set to 9191, but can be changed to the port of your preference.
  
  2- As for the client, you have to set both the host and port you want to connect to, and to do so you have to go to the main() method of chat-server-client/clientSide/Client.java (lines 170 to 173).
```
Client c = new Client("localhost", 9191);
c.run();
```
If you're using this locally, set the first argument (which decides the host it'll be connecting to) to "localhost" and the port number you gave the server to listen. If its through the internet, instead of "localhost" you'll have to specify the IP address of where the server is located.

## Español

Un simple servidor de mensajeria/chat hecho en Java utilizando Sockets, influenciado por varios proyectos similares encontrados en internet. Este proyecto tambien contiene un cliente con interfaz gráfica para que el usuario pueda conectarse y usar el servicio mas facilmente, esto puede ser utilizado localmente en la red del usuario y, tambien, a traves de internet. Establecer la conección deseada es facil, y puede ser realizado de la siguiente manera:

  1- Sea utilizado localmente o a travez de la web, lo primero es decidir en que puerto va a estar escuchando nuestro servidor y establecerlo en el método main() de la clase chat-server-client/serverSide/Server.java (lineas 43 a 47).
 ```	
Server sv = new Server(9191); 
sv.run();
```
Cuando una instancia de la clase Server es creada, el argumento que se pide es un int que decide el puerto al que va a escuchar, el puerto predeterminado es el 9191 pero puede ser cambiado acorde a tu preferencia.

2- Por el lado del cliente, hay que decidir el puerto y el host al cual se va a conectar, para hacerlo vamos al método main() de Cliente.java en chat-server-client/clientSide/Client.java (lineas 170 a 173).
```
Client c = new Client("localhost", 9191);
c.run();
```
Si estas utilizando el chat localmente, haz que el primer argumento (que decide el host al cual se intenta conectar) sea "localhost" y que el segundo argumento (que decide el puerto al que va a conectarse) sea el puerto que se designo al servidor en el paso 1. Si piensas usarlo a traves de internet, designa al primer argumento la dirección IP de donde se encuentre el servidor. 

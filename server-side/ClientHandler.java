package serverSide;

import java.util.ArrayList;
import java.util.Random;

public class ClientHandler{
	
	private ArrayList<Client> clients = new ArrayList<Client>();
	/* This class va a estar a cargo de almacenar todas las instancias de Client que hayan, añadir nuevas instancias, y remover las que 
	 * se hayan desconectado/ido. Tambien va a distribuir los mensajes de las instancias de Client a todas las instancias 
	 * que existan en tal momento de ser recibido el mensaje */
	
	public void removeClient(Client c){
		if (clients.contains(c)){
			clients.remove(c);
			broadCastOnlineClients();
		}
	}
	
	public void addClient(Client c){
		clients.add(c);
		broadCastOnlineClients();
	}
	
	public void broadcastMessageToAll(String msg){
		for (Client c : clients){
			c.getPrintWriter().println("$MESSAGE$ " +msg);
		}
	}
	
	/* Slightly formats it so the client can understand the purpose of the message and then know what to do
	 * with it, like the previous function broadcastMessageToAll(String) */
	public void broadCastOnlineClients(){
		String usersOn = "";
		for (Client c : clients){
			usersOn = c.getClientName() + "|" + usersOn;
		}
		
		for (Client c : clients){
			c.getPrintWriter().println("$CLIENTLIST$" + usersOn);
		}
	}
	
	/* Not in use(yet) */
	public void forwardMessageTo(String msg, int recipientSID){
		System.out.println(">>looking for recipient");
		for (Client c : clients){
			if (c.getSID() == recipientSID){
				System.out.println(">>recipient found");
				c.getPrintWriter().println(msg);
			}
		}
	}
	
}

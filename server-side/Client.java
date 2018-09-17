package serverSide;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/* SERVERSIDE CLIENT(SV side of socket) to be used by server (duhh), ONLY LISTENS FOR CLIENT INPUT*/
public class Client extends Thread{
	private final int SID;
	private Socket clientSocket;
	private BufferedReader inFromClient;
	private PrintWriter outToClient;
	private String clientName;
	private ClientHandler handler;
	
	
	
	public Client(Socket clientSocket , ClientHandler handler, int SID){
		super ("clientThread");
		
		/* CLientHandler (isntanciado en Server.class) da funcionalidad para broadcastear los mensajes a todos los clientes.*/
		this.handler = handler;		
		
		/*Parameter socket is ServerSocket.accept();*/
		this.clientSocket = clientSocket;	

		/*Initialize IO*/
		try {
			this.inFromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			this.outToClient = new PrintWriter(this.clientSocket.getOutputStream(), true);
			
			/* Server assumes (probably shouldn't tho) that the client already sent their 
			 * username, so it reads it and saves it in variable. */
			this.clientName = inFromClient.readLine();
			
			/* Greet user, thus initiating both OutPutStream and InPutStream by this point (on both sides of the connection)*/
			outToClient.println("$MESSAGE$ (Server) Welcome " + this.clientName + ", you are now connected!");
			
		} catch (IOException e) {
			System.out.println("Error initializing Client IO, serverside problem > Message: " + e.getMessage());
			e.printStackTrace();
		}
	
		this.SID = SID;
	}
	
	/* Server uses this class' super start(), said method (at some point -_u.u_-) runs this run(), which 
	 * starts the thread and keeps listening for (clientSide) input and sends corresponding output. */
	public void run(){

		try {
			String input;
			while ((input = inFromClient.readLine()) != null){
				decideWhatToDoWithMessage(input);
			}	
		} catch (Exception e){
			System.out.println(">>>"+ this.clientName + " ("+this.SID+")  disconnected.");
			System.out.println("   >>>"+e.getMessage());
		}
		try {
			this.inFromClient.close();
			this.outToClient.close();
			this.clientSocket.close();
			this.handler.removeClient(this);
		} catch (Exception e){
			System.out.println("PROBLEM REMOVING CLIENT FROM HANDLER: ");
			System.out.println(e.getMessage());
		}

	}
		
	public BufferedReader getBufferedReader(){return this.inFromClient;}
	public PrintWriter getPrintWriter(){return this.outToClient;}
	public String getClientName(){return this.clientName;}
	public int getSID(){return this.SID;}
	
	/*For now it will only accept messages starting with $MESSAGE$, to be expanded in the future. */
	private void decideWhatToDoWithMessage(String msg){ 		
		if (msg.startsWith("$MESSAGE$")){
			msg = msg.substring(9);
			msg = clientName + ": " + msg;
			handler.broadcastMessageToAll(msg);
		} else {
			System.out.println("Weird message found from " + this.clientName + " (" + this.SID + ")");
			System.out.println("    >MESSAGE: " + msg);
		}
	}
}

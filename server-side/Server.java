package serverSide;
import java.io.IOException;
import java.net.ServerSocket;

/*MAIN CLASS*/
public class Server {
	private int PORT;
	
	private ServerSocket listener;
	private ClientHandler clientHandler;
	/* For now the sid has no real use, in the future it will help me make private messages */
	private int CURRENTSID = 0;
	
	public Server(int port) throws IOException{
		this.PORT = port;
		this.clientHandler = new ClientHandler();
		/* Initiate listener on given port */
		this.listener = new ServerSocket(PORT);
	}
	
	public void run() throws IOException{
		System.out.println("Running");

		/* Listener escucha requests al puerto determinado, tras aceptar un request crea una instancia de
		 * Cliente que maneja al cliente en su propio thread(inside Client.class), luego regresa a escuchar. */
		try {			
			while (true){
				Client newClient = new Client(listener.accept() , this.clientHandler, CURRENTSID);
				this.CURRENTSID = this.CURRENTSID+1;
				newClient.start();
				clientHandler.addClient(newClient);
			}	
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			/* Cuando termina el while true se cierra el serversocket, seguido se termina el programa.*/
			listener.close();
			System.exit(0);
		}
	}
		
	/* Main method, here it is decided the port, might later change to be an argument passed in initialization(console)*/
	public static void main(String[] args) throws IOException{
		Server sv = new Server(9191);
		sv.run();
	}
}

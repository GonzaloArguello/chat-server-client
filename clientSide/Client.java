package clientSide;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.Document;


/* CLIENT SIDE CLIENT (WITH GUI), (Client side socket) to be used by enduser*/
public class Client extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private Socket socket;
	private BufferedReader inputFromServer;
	private PrintWriter outputToServer;
	
	private String clientName;
	
	private JTextField inputField;
	private JTextPane outputField;
	private JTextPane clientListField;
	
	
	public Client (String host, int port){	
		
		/* Prompt client's name before connection (until apropiate one is given)*/
		String tempName = JOptionPane.showInputDialog("Please, enter your name:");
		while (tempName == null || tempName == "" || tempName.length() > 15 || tempName.length()<4 || tempName.contains("|")){
			if (tempName == null) {
				System.exit(0);
			}
			tempName = JOptionPane.showInputDialog("Invalid name, try again: ");
		}
		tempName = tempName.trim();
		tempName = tempName.replace(" ", "-");
		this.clientName = tempName;
		
	    startGUI();
		
		/*Start connection to server through socket*/
		try { 
			this.socket = new Socket(host, port);
			
		} catch (UnknownHostException e) {
			System.out.println("---UnknownHostException while initializing Socket (ClientSideError)"
					+ "\n>>> Message: " + e.getMessage()
					+ "\n>>> StackTrace: " + e.getStackTrace()
					+ "\n---------");
		} catch (IOException e) {
			System.out.println("---IOException while initializing Socket (ClientSideError)"
					+ "\n>>> Message: " + e.getMessage()
					+ "\n>>> StackTrace: " + e.getStackTrace()
					+ "\n---------");
		}		
	}
	
	private void startGUI(){
		this.setTitle("Chatting as: " + this.clientName);
		/*Set up frame and components*/
		
			/*Frame*/
		this.setLayout(null);
		this.setSize(600,500);
	    this.setResizable(false);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			/*Input from server*/
		this.outputField = new JTextPane();
		this.outputField.setBounds(10, 10, 400, 400);
		this.outputField.setMargin(new Insets(6,6,6,6));
		this.outputField.setEditable(false);
	    JScrollPane outputFieldScroll = new JScrollPane(this.outputField);
	    outputFieldScroll.setBounds(this.outputField.getBounds());
	    
			/*Client list*/
		this.clientListField = new JTextPane();
		this.clientListField.setBounds(420, 10, 150, 350);
		this.clientListField.setMargin(new Insets(6,6,6,6));
		this.clientListField.setEditable(false);
		JScrollPane clientListScroll = new JScrollPane(this.clientListField);
		clientListScroll.setBounds(this.clientListField.getBounds());
		
			/*Input from user, which is then sent to the server (after being formatted)*/
		this.inputField = new JTextField();
		this.inputField.setMargin(new Insets(6, 6, 6, 6));
		this.inputField.setBounds(10, 420, 560, 30);
		this.inputField.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent event){
				if (event.getKeyCode() == KeyEvent.VK_ENTER){
					sendMessageToServer();
				}
			}
		});

			/*Add all to JFrame and set it visible*/
		this.add(this.inputField);
		this.add(clientListScroll);
		this.add(outputFieldScroll);
		
		this.setVisible(true);

	}

	public void run() throws IOException  {
		
		try {
			/* Initialize IO to contact server */
			this.outputToServer  = new PrintWriter(socket.getOutputStream(), true);
			this.inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			/* Start conversation by sending clientName */
			this.outputToServer.println(this.clientName);
			
			/* Initiate thread in charge of reading messages from server*/
			ReadFromServer readFromServer = new ReadFromServer(this);
			readFromServer.start();

		} catch (Exception e) {
			System.out.println("ERROR INITIALIZING IO:" + e.getMessage());
		} 
	}
	
	/* Formats the server adding $MESSAGE$ for the server to understand the content and procceed to
	 * proccess it acordingly. for now $MESSAGE$ is the only kind of message the server can and will
	 * recieve, adding it for possible future updates. */
	public void sendMessageToServer(){
		String input = this.inputField.getText();
		if (input == ""){
			return;
		} else {
			this.outputToServer.println("$MESSAGE$" + input);
		}
		this.inputField.setText("");
	}
	
	/* Print parameter string into GUI outputfield */
	public void printMessageInFrame(String msg){
		msg = "\n" + msg;
		Document doc = this.outputField.getDocument();
		try {
			doc.insertString(doc.getLength(), msg, null);
		} catch(Exception e){
		    e.printStackTrace();
		}
	}
	
	/* As the name implies, this updates la lista de clientes en el GUI (clientListField a la derecha)*/
	public void updateClientList(String clientList){
		clientList = clientList.replace("|", "\n");
		this.clientListField.setText("");
		this.clientListField.setText(clientList);
	}
	
	public BufferedReader getInputStream() {return this.inputFromServer;}
	public PrintWriter getOutPutStream() {return this.outputToServer;}
	public Socket getSocket() {return this.socket;}
	
	
	public static void main (String[]args) throws IOException {
		Client c = new Client("localhost", 9191);
		c.run();
	}
}

class ReadFromServer extends Thread{
	private BufferedReader inputFromServer;
	private Client client;
	
	public ReadFromServer(Client c){
		this.inputFromServer = c.getInputStream();
		this.client = c;
	}
	
	public void run() {
	
		try {
			/* When this recieves a message from the server, it decides what to do with it (and does it)*/
			String serverInput;		
			while ((serverInput = inputFromServer.readLine()) !=null){
				decideWhatToDoWithMessage(serverInput);
			}
		} catch (Exception e){
			System.out.println("ERROR RUNNING SV TO SYS (READFROMSERVER(RUN)): " );
			e.printStackTrace();
		} 
		try {
			this.inputFromServer.close();
			this.client.getOutPutStream().close();
			this.client.getInputStream().close();
			this.client.getSocket().close();
		} catch (Exception e){
			System.out.println("ERROR CLOSING IO AND SOCKET: ");
			System.out.println(e.getMessage());
		}

		
	}
	
	public void decideWhatToDoWithMessage(String msg){
		if (msg.startsWith("$MESSAGE$")){
			this.client.printMessageInFrame(msg.substring(10, msg.length()));
		} else if (msg.startsWith("$CLIENTLIST$")){
			this.client.updateClientList(msg.substring(12, msg.length()));
		} else {
			System.out.println("INPUT FROM SERVER NOT UNDERSTOOD: " + msg);
		}
	}
	
}

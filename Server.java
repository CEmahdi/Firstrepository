import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;

public class Server {
	
	public static Map <String, ClientHandler> users;//for save username of client
	
	public static int GoldenNumber;
	
	public static void main(String[] args) throws Exception{
		
		ServerSocket serversocket = new ServerSocket(888);
		users = new ConcurrentHashMap<>();
		GoldenNumber = (int)(Math.random()*90000) + 10000;
		boolean condition = true;
		System.out.println("The Server is Online");
		
		while( true ) {
			
			Socket socket = serversocket.accept();
			
			//The Game will start when first client connect to server
			if(condition) {
				System.out.println("The Game is on");
				condition = false;
			}
			
			ClientHandler clienthandler = new ClientHandler(socket,GoldenNumber);
			( new Thread( clienthandler ) ).start();
			
		}
	}
}

//for handling clients
class ClientHandler implements Runnable {
	
	Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	int GoldenNumber;
	String username;
	
	ClientHandler(Socket socket,int GoldenNumber)throws Exception{
		this.socket = socket;
		dos = new DataOutputStream(socket.getOutputStream());
		dis = new DataInputStream(socket.getInputStream());
		this.GoldenNumber = GoldenNumber;
	}
	
	@Override
	public void run() {
		try {
			//for recognize unique username
			boolean condition = true;
			String username = "";
			while(condition) {
				username = dis.readUTF();
				if(Server.users.containsKey(username)) {
					this.dos.writeUTF("This username is not unique");
				}
				else {
					this.dos.writeUTF("Your username is good");
					condition = false;
				}
			}
			System.out.println("Client "+ username + " Connected!");
			Server.users.put(username, this);
			this.username = username; 
			
			while( true ) {
				
				int number = dis.readInt();
				if(number == GoldenNumber) {
					//send message for winer
					String message = "You Win...";
					System.out.println("New game started!");
					Server.GoldenNumber = (int)(Math.random()*90000) + 10000;
					
					for (Map.Entry<String, ClientHandler> entry : Server.users.entrySet()) {
						
						ClientHandler clienthandler = entry.getValue();
						clienthandler.GoldenNumber = Server.GoldenNumber;
					}
					this.dos.writeUTF(message);
					//send message for other participants
					for (Map.Entry<String, ClientHandler> entry : Server.users.entrySet()) {
						ClientHandler clienthandler = entry.getValue();
						String key = entry.getKey().toString();
						if(key.equals(username)) {
							continue;
						}
					    String messforclient = username+" won the round";
					    clienthandler.dos.writeUTF(messforclient);
					}
					break;
					
					}else {
					String message = "The numbers are not equal";
					this.dos.writeUTF(message);
				}
			}
		}catch(SocketException e) {
			//when one client disconnect!
			System.out.println(this.username+" is disconnected!");
			Server.users.remove(this.username);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}

//In The Name Of GOd
import java.util.*;
import java.net.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Thread;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner scan = new Scanner(System.in);
		Socket socket = new Socket("localhost",888);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		boolean condition = true;
		while( condition ) {
			System.out.print("Pleas Enter your username :");
			String username = scan.nextLine();
			dos.writeUTF(username);
			String servermessage = dis.readUTF();
			if(!servermessage.startsWith("This")) {
				condition = false;
			}else {
				System.out.println("This username already exists!");
			}	
		}
		
        Thread messageListener = new Thread(new Runnable() {
            @Override
            public void run() {
                while( true ) {
                	
                	try {
                		
                        String message = dis.readUTF();
                        System.out.println("Server Says : " + message);
                        
                    } catch (IOException e) {
                    	
                        e.printStackTrace();
                        
                    }
                }
            }
        });
        messageListener.start();
        
		while( true ) {
			for(int i=0;i<100000;i++);//for delay
			System.out.println("Pleas Enter your number(Between 10,000 to 100,000) :");
			int number =  scan.nextInt();
			if(!(number >= 10000 && number <100000)) {
				System.out.println("Your number is not in the desired range!");
				continue;
			}
			dos.writeInt(number);
			
			
		}
	}
}

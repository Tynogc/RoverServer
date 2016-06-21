package comm;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import javax.print.attribute.standard.MediaSize.Other;

public class SoketLinker {

	private Socket socket;
	private PrintWriter writer;
	private Scanner scanner;
	
	private FiFo in;
	private FiFo out;
	
	private Semaphore sema;
	
	private boolean threadIsRunning = true;
	
	public boolean trusty;
	public boolean internal;
	
	public String socketIp;
	
	public SoketLinker(Socket s){
		socket = s;
		
		trusty = false;
		internal = false;
		
		debug.Debug.println("* New Connection!", debug.Debug.MASSAGE);
		
		try {
			scanner = new Scanner(socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			threadIsRunning = false;
			debug.Debug.println(e.toString(), debug.Debug.ERROR);
		}
		
		sema = new Semaphore(1);
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sema.release();
		
		in = new FiFo();
		out = new FiFo();
		
		String hello = scanner.nextLine();
		debug.Debug.println("   Connection Data: "+hello);
		try {
			Thread.currentThread().sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		writer.println(main.RoverServer.name+"-"+hello+" "+main.RoverServer.id);
		
		socketIp = socket.getInetAddress().getHostAddress();
		debug.Debug.println("   IP "+socketIp);
		
		if(socketIp.compareTo("127.0.0.1")==0){
			debug.Debug.println("   LocalHost");
			if(hello.compareTo(main.RoverServer.internalCode)==0){
				internal = true;
			}
		}
		
		debug.Debug.println("   Internal "+internal);
		//Abfrage des Vertrauens
		FingerPrint f = new FingerPrint();
		int fpi = (int)(Math.random()*f.lenght);
		String fp = f.getFingerprintAt(fpi);
		debug.Debug.println("   Random String No. "+fpi);
		writer.println(""+fpi);
		String fps = scanner.nextLine();
		debug.Debug.println("   "+fp);//TODO entfernen!
		debug.Debug.println("   "+fps);
		if(fps.compareTo(fp)==0){
			trusty = true;
			writer.println("TRUSTY");
			debug.Debug.println("   Trusty true");
		}else{
			trusty = false;
			writer.println("Response_dosn't_match!");
			debug.Debug.println("   Trusty FALSE", debug.Debug.WARN);
			threadIsRunning = false;
			return;
		}
		
		Thread th = new Thread(){
			@Override
			public void run() {
				read();
				rover.RoverControle.rover.ssk.externInterupt = true;
				debug.Debug.println("* Reader Thread terminated on: "+socketIp, debug.Debug.ERROR);
			}
		};
		th.start();
		th = new Thread(){
			@Override
			public void run() {
				write();
				debug.Debug.println("* Writer Thread terminated on: "+socketIp, debug.Debug.ERROR);
			}
		};
		th.start();
	}
	
	private void read(){
		while (threadIsRunning) {
			if(scanner.hasNext()){
				try {
					sema.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				in.in(scanner.nextLine());
				sema.release();
			}
		}
	}
	
	@SuppressWarnings("static-access")
	private void write(){
		while (threadIsRunning) {
			try {
				sema.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(out.contains()){
				writer.println(out.out());
				sema.release();
			}else{
				sema.release();
				try {
					Thread.currentThread().sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(writer.checkError()){
				debug.Debug.println("* OutputStream Error! "+socketIp, debug.Debug.ERROR);
				threadIsRunning = false;
			}
			if(socket.isClosed()){
				debug.Debug.println("* Connection to server closed! "+socketIp, debug.Debug.ERROR);
				threadIsRunning = false;
			}
		}
	}
	
	public boolean linkerAnythingToRead(){
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean b = in.contains();
		sema.release();
		
		return b;
	}
	
	public String linkerRead(){
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String b = in.out();
		sema.release();
		return b;
	}
	
	public void linkerWrite(String s){
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		out.in(s);
		sema.release();
	}
	
	public void terminate(){
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		threadIsRunning = false;
		sema.release();
	}
	
	public boolean isRunning(){
		return threadIsRunning;
	}
}

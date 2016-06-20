package comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Communication {

	private SoketLinker internal;
	private SoketLinker external;
	
	private ServerSocket server;
	
	private boolean threadIsRunning = true;
	
	private Semaphore sema;
	
	public static Communication com;
	
	public static int delay = 0;
	
	private String[] connectionInfo;
	private long connInfoUpdt;
	private static final int timeToSendInfo = 30000;
	
	public Communication(){
		
		connectionInfo = new String[]{
				"N_N_N_X","N_N_N_X","N_N_N_X"
		};
		
		sema = new Semaphore(1);
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.println(e.toString(), debug.Debug.ERROR);
		}
		sema.release();
		
		 try {
			server = new ServerSocket( 3141 );
		} catch (IOException e) {
			debug.Debug.println(e.toString(), debug.Debug.ERROR);
		}
		 Thread th = new Thread(){
			 @Override
			 public void run() {
				connectionTraier();
			}
		 };
		 th.setPriority(2);
		 th.start();
		 
		 th = new Thread(){
			 @Override
			 public void run() {
				runConnection();
				debug.Debug.println("* Connection-Worker Terminated!", debug.Debug.FATAL);
			}
		 };
		 th.setPriority(6);
		 th.start();
		 com = this;
	}
	/**
	 * Connection and I/O Methods
	 */
	@SuppressWarnings("static-access")
	private void runConnection(){
		while (threadIsRunning) {
			try {
				sema.acquire();
			} catch (InterruptedException e) {
				debug.Debug.println(e.toString(), debug.Debug.ERROR);
			}
			boolean sleep = false;
			try {
				sleep = rcmtr();
			} catch (Exception e) {
				debug.Debug.printExeption(e);
			}
			sema.release();
			if(sleep){
				try {
					Thread.currentThread().sleep(100);
				} catch (InterruptedException e) {
					debug.Debug.println(e.toString(), debug.Debug.ERROR);
				}
			}
		}
	}
	
	
	private boolean rcmtr(){
		if(internal == null && external == null){
			return true;
		}
		boolean sleep = true;
		if(external != null){
			if(external.linkerAnythingToRead()){
				sleep = false;
				handleExternalInput(external.linkerRead());
			}
		}
		
		
		return sleep;
	}
	
	@SuppressWarnings("static-access")
	private void handleExternalInput(String s){
		if(s == null){
			debug.Debug.println("* ERROR Communication01a: String is null [External]", debug.Debug.ERROR);
			return;
		}
		if(s.length() == 0){
			debug.Debug.println("* ERROR Communication01a: String.lenght is 0 [External]", debug.Debug.ERROR);
		}
		rover.RoverControle.rover.ssk.recivedExternalMsg();
		
		CommAction.handleExtern(s);
		
		if(delay > 2){
			try {
				Thread.currentThread().sleep(delay);
			} catch (InterruptedException e) {
				debug.Debug.println(e.toString(), debug.Debug.ERROR);
			}
		}
		
		printExternal(s+"RESPONSE");
	}
	
	/**
	 * Print Methods
	 * @param s Print the String/String Array to the given Connection
	 */
	public void printExternal(String s){
		if(external != null){
			external.linkerWrite(s);
		}
	}
	public void printExternal(String[] s){
		if(external != null){
			for (int i = 0; i < s.length; i++) {
				external.linkerWrite(s[i]);
			}
		}
	}
	
	/**
	 * Server Traier
	 */
	@SuppressWarnings("static-access")
	private void connectionTraier(){
		while(true){
			Socket sc = null;
			try {
				sc = server.accept();
			} catch (IOException e) {
				debug.Debug.println(e.toString(), debug.Debug.ERROR);
			}
			handleConnection(sc);
			
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				debug.Debug.println(e.toString(), debug.Debug.ERROR);
			}
		}
	}
	
	private void handleConnection(final Socket sc){
		Thread th = new Thread(){
			public void run() {
				if(sc != null){
					SoketLinker sl = new SoketLinker(sc);
					
					setConnInfo(sl);
					
					if(!sl.trusty){
						printExternal("/ANot-Trusty connection:");
						printExternal("/AIP "+sc.getLocalAddress().getHostAddress()+" TERMINATED!");
						return;
					}
					
					try {
						sema.acquire();
					} catch (InterruptedException e) {
						debug.Debug.println(e.toString(), debug.Debug.ERROR);
					}
					if(sl.internal){
						if(internal!= null)
							internal.terminate();
						internal = sl;
					}else{
						if(external != null)
							external.terminate();
						external = sl;
						main.StartUpNotification.createNotification(sl);
					}
					sema.release();
				}
			}
		};
		th.start();
	}
	
	private void setConnInfo(SoketLinker sl){
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.println(e.toString(), debug.Debug.ERROR);
		}
		connInfoUpdt = main.RoverServer.systemTime()-timeToSendInfo+1000;
		
		connectionInfo[2] = connectionInfo[1];
		connectionInfo[1] = connectionInfo[0];
		connectionInfo[0] = getAInfo(sl);
		
		sema.release();
	}
	
	private String getAInfo(SoketLinker sl){
		if(sl == null)return "N_N_N_X";
		
		String s = sl.socketIp;
		
		if(sl.internal) s += "_INT";
		else s += "_EXT";
		if(sl.trusty) s += "_TRUSTY";
		else s += "_NOTTRUSTY";
		if(sl.isRunning()) s += "_O";
		else s += "_X";
		
		return s;
	}
	
	public void check(){
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.println(e.toString(), debug.Debug.ERROR);
		}
		
		if(main.RoverServer.systemTime()-connInfoUpdt >timeToSendInfo){
			connInfoUpdt = main.RoverServer.systemTime();
			
			String s = "*COI_";
			s+=getAInfo(internal)+"_"+getAInfo(external);
			for (int i = 0; i < connectionInfo.length; i++) {
				s+="_"+connectionInfo[i];
			}
			printExternal(s);
		}
		
		sema.release();
	}
	
}

package main;

import javax.swing.JFrame;

import debug.DebugFrame;

public class RoverServer extends Thread{

	public static final String name = "TKT";
	public static final long id = 15751;
	
	public static final boolean forceCypherConection = true;
	public static final String internalCode = "TME";
	
	
	private comm.Communication com;
	private rover.RoverControle rover;
	
	private boolean threadIsRunning;
	
	public static boolean sendTelem = true;
	private static boolean sendTelemNow = false;
	public static int timeToTelemetry = 1000;
	
	public RoverServer(){
		DebugFrame db = new DebugFrame();
		db.setTitle("Rover-Server");
		db.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		debug.Debug.bootMsg("* DebugFrame Started!", 0);
		rover = new rover.RoverControle();
		com = new comm.Communication();
		
		Runtime.getRuntime().addShutdownHook(new ExitThread());
		
		threadIsRunning = true;
		start();
	}
	
	public void run(){
		long lastTL = 0;
		while(threadIsRunning){
			sendTelemNow = false;
			if(systemTime()-lastTL>timeToTelemetry){
				lastTL = systemTime();
				sendTelemNow = true;
			}
			try{
				loop();
			}catch(Exception e){
				debug.Debug.printExeption(e);
				threadIsRunning = false;
				ExitThread.exeption = e;
				ExitThread.shutDownCause = "FATAL System Error at MAIN - Shutdown!";
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				debug.Debug.printExeption(e);
			}
		}
		debug.Debug.println("* FATAL: MainThread terminated!", debug.Debug.FATAL);
		try {
			sleep(10000);//TODO weniger!
		} catch (InterruptedException e) {
			debug.Debug.printExeption(e);
		}
		System.exit(-1);
	}
	
	private void loop(){
		com.check();
		rover.check();
	}
	
	public static void main(String[] a){
		new RoverServer();
	}
	
	public static long systemTime(){
		return System.currentTimeMillis();
	}
	
	public static boolean sendTelemetry(){
		return sendTelem&&sendTelemNow;
	}
}

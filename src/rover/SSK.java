package rover;

import java.util.concurrent.Semaphore;

import main.RoverServer;
import comm.Communication;

public class SSK {

	public static final int OFF = 0;
	public static final int RESET = 1;
	public static final int STARTUP = 2;
	public static final int OK = 3;
	public static final int INTERUPT = 4;
	
	public static final String[] cs0 = new String[]{
		"OFF","RES","STU","OK","INT"
	};
	public static final String[] cs1 = new String[]{
		"NOI","ECS","OMP","QDP"
	};
	public static final String[] cs2 = new String[]{
		"NOI","ALCS","DIS"
	};
	public static final String[] cs3 = new String[]{
		"NOI","OK","CONN","OASIS","ECAM","PING"
	};
	public static final String[] cs4 = new String[]{
		"NOI","PCC","SIMP","TEMP"
	};
	
	private int state;
	
	private Semaphore sema;
	
	private boolean interupt;
	
	private long startUpAt;
	private static final int startUpTime = 7000;
	
	private byte[] breakers;
	
	public boolean externInterupt;
	public boolean ecamInterupt;
	public boolean oasisInterupt;
	private long lastPing;
	private static final int pingDelayToError = 5000;
	
	public SSK(){
		sema = new Semaphore(1);
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.printExeption(e);
		}
		sema.release();
		state = OFF;
		interupt = false;
		
		breakers = new byte[]{
				0,0,0,0
		};
	}
	
	public void reset(){
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.printExeption(e);
		}
		debug.Debug.println("* RESET--------------------");
		if(interupt){
			debug.Debug.println(" Can't Reset!", debug.Debug.ERROR);
			Communication.com.printExternal("/ACan't reset due to Intererupt/Fault-State!");
		}else if(state == OK){
			debug.Debug.println(" Reset isn't needed", debug.Debug.WARN);
		}else{
			state = RESET;
			for (int i = 0; i < breakers.length; i++) {
				breakers[i] = 0;
			}
			externInterupt = false;
		}
		sema.release();
	}
	
	public void check(){
		
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.printExeption(e);
		}
		
		boolean[] bts = new boolean[breakers.length];
		
		/*
		 * checking
		 */
		if(RoverIO.roverIO.getPin(RoverIO.PIN_SSK_REMOTE)){
			if(breakers[0] == 0)
				breakers[0] = 1;
			
			bts[0] = false;
		}else{
			breakers[0] = 2;
			bts[0] = true;
		}
		
		if(externInterupt){
			if(breakers[2]<2)
				breakers[2] = 2;
		}
		if(oasisInterupt){
			if(breakers[2]<3)
				breakers[2] = 3;
			bts[2] = true;
		}else if(ecamInterupt){
			if(breakers[2]<4)
				breakers[2] = 4;
			bts[2] = true;
		}
		if(main.RoverServer.systemTime()-lastPing >= pingDelayToError){
			breakers[2] = 5;
			bts[2] = true;
		}
		/*
		 * processing
		 */
		boolean nothing = true;
		boolean alarm = false;
		for (int i = 0; i < breakers.length; i++) {
			if(breakers[i] > 1){
				alarm = true;
			}
			if(bts[i]){
				state = INTERUPT;
				nothing = false;
			}
		}
		if(nothing){
			if(state == INTERUPT)
				state = OFF;
		}
		if(alarm){
			if(state != INTERUPT)
				state = OFF;
		}
		
		interupt = !RoverIO.roverIO.getPin(RoverIO.PIN_SSK_MAIN);
		if(interupt)
			state = INTERUPT;
		
		if(state == RESET){
			if(RoverIO.roverIO.getPin(RoverIO.PIN_SSK_RESTART)){
				state = STARTUP;
				startUpAt = RoverServer.systemTime();
			}
		}
		if(state == STARTUP){
			if(!RoverIO.roverIO.getPin(RoverIO.PIN_SSK_RESTART)){
				state = OFF;
				Communication.com.printExternal("/ASSK StartUp interupted!");
				debug.Debug.println("* SSK StartUp interupted!");
			}else if(RoverServer.systemTime()-startUpAt>startUpTime){
				state = OK;
				Communication.com.printExternal("/CSSK StartUp done!");
				debug.Debug.println("* SSK StartUp done!");
			}
		}
		
		if(RoverServer.sendTelemetry()){
			String s = "*SSK_";
			try {
				s+=cs0[state]+"_"+cs1[breakers[0]]+"_"+cs2[breakers[1]]+"_"+cs3[breakers[2]]+"_"+cs4[breakers[3]];
			} catch (ArrayIndexOutOfBoundsException e) {
				debug.Debug.printExeption(e);
				s = "#SSK_FAULT_CONVERSION";
			}
			s += "_";
			for (int i = 0; i < bts.length; i++) {
				if(bts[i]) s+="x";
				else s+= "o";
			}
			Communication.com.printExternal(s);
		}
		
		sema.release();
	}
	
	public void process(String[] s){
		if(s == null){
			debug.Debug.println("* Problem handling Input: SSK: Array null!", debug.Debug.WARN);
			return;
		}
		if(s.length<2){
			debug.Debug.println("* Problem handling Input: SSK: Array to short!", debug.Debug.WARN);
			return;
		}
		if(s[1].compareToIgnoreCase("RES")==0){
			reset();
		}
		if(s[1].compareToIgnoreCase("BREAK")==0){
			externInterupt = true;
		}
	}
	
	public void recivedExternalMsg(){
		lastPing = main.RoverServer.systemTime();
	}
}

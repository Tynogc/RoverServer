package rover;

import java.util.concurrent.Semaphore;

import comm.Communication;

import main.RoverServer;

public class Battery {

	private int[] m1;
	private int[] m2;
	
	private static final int[] m1ADCs = new int[]{10,11,12,13,14};
	private static final int[] m2ADCs = new int[]{20,21,22,23,24};
	
	private final int m1Size = m1ADCs.length;
	private final int m2Size = m2ADCs.length;
	
	private static final double RESISTO_DIVIDER_MAIN = 10.0;
	private static final double RESISTO_DIVIDER_SUB = 10.0;
	
	//ADC-Error Threshhold (absolut minimum Voltage)
	private static final int ERROR_THRESHHOLD = 100;
	
	private long lastSend;
	private static final int timeToSend = 10000;
	private boolean haslastAsk;
	private static final int timeToAsk = 5000;
	
	private Semaphore sema;
	
	public Battery(){
		sema = new Semaphore(1);
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.printExeption(e);
		}
		sema.release();
		m1 = new int[m1Size];
		m2 = new int[m2Size];
		for (int i = 0; i < m1.length; i++) {
			m1[i] = -1;
		}
		for (int i = 0; i < m2.length; i++) {
			m2[i] = -1;
		}
	}
	
	public void check(){
		if(RoverServer.systemTime()-lastSend>timeToSend){
			RoverIO r = RoverIO.roverIO;
			m1[0] = (int)(RESISTO_DIVIDER_MAIN*r.getAdc(m1ADCs[0]));
			for (int i = 1; i < m1.length; i++) {
				m1[i] = (int)(RESISTO_DIVIDER_SUB*r.getAdc(m1ADCs[i]));
			}
			for (int i = 0; i < m1.length; i++) {
				if(m1[i]< ERROR_THRESHHOLD)m1[i]= -1;
			}
			m2[0] = (int)(RESISTO_DIVIDER_MAIN*r.getAdc(m2ADCs[0]));
			for (int i = 1; i < m2.length; i++) {
				m2[i] = (int)(RESISTO_DIVIDER_SUB*r.getAdc(m2ADCs[i]));
			}
			for (int i = 0; i < m2.length; i++) {
				if(m2[i]< ERROR_THRESHHOLD)m2[i]= -1;
			}
			
			haslastAsk = false;
			lastSend = RoverServer.systemTime();
			try {
				sema.acquire();
			} catch (InterruptedException e) {
				debug.Debug.printExeption(e);
			}
			String m1s = "*BAT_M1";
			for (int i = 0; i < m1.length; i++) {
				m1s+="_"+m1[i];
			}
			String m2s = "*BAT_M2";
			for (int i = 0; i < m2.length; i++) {
				m2s+="_"+m2[i];
			}
			sema.release();
			Communication.com.printExternal(m1s);
			Communication.com.printExternal(m2s);
		}
		
		if(RoverServer.systemTime()-lastSend>timeToAsk && !haslastAsk){
			haslastAsk = true;
			//TODO Comand internal: Read Bat-Voltages
		}
	}
}

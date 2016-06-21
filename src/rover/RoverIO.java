package rover;

import java.util.concurrent.Semaphore;

public class RoverIO {
	
	private boolean[] input;
	private int[] adc;
	private static final int inputLenght = 100;
	private static final int adcLenght = 40;
	
	public static final int PIN_SSK_MAIN = 10;
	public static final int PIN_SSK_RESTART = 14;
	public static final int PIN_SSK_REMOTE = 15;
	
	private Semaphore sema;
	
	public static RoverIO roverIO;
	
	private int sendIO = -1;
	
	public RoverIO(){
		sema = new Semaphore(1);
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.printExeption(e);
		}
		sema.release();
		
		input = new boolean[inputLenght];
		for (int i = 0; i < input.length; i++) {
			input[i] = false;
		}
		
		adc = new int[adcLenght];
		for (int i = 0; i < adc.length; i++) {
			adc[i] = 0;
		}
		
		roverIO = this;
	}
	
	public void setPin(int pos, boolean b){
		if(pos>=inputLenght || pos<0){
			debug.Debug.println("* ERROR RoverIO 01: Pos oob! "+pos, debug.Debug.ERROR);
			return;
		}
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.printExeption(e);
		}
		input[pos] = b;
		sema.release();
	}
	
	public boolean getPin(int pos){
		if(pos>=inputLenght || pos<0){
			debug.Debug.println("* ERROR RoverIO 01: Pos oob! "+pos, debug.Debug.ERROR);
			return false;
		}
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			debug.Debug.printExeption(e);
		}
		boolean b = input[pos];
		sema.release();
		return b;
	}
	
	public int getAdc(int pos){
		if(pos>=adcLenght || pos<0){
			debug.Debug.println("* ERROR RoverIO 01: Pos oob! "+pos, debug.Debug.ERROR);
			return 0;
		}
		return adc[pos];
	}
	
	public void setAdc(int pos, int value){
		if(pos>=adcLenght || pos<0){
			debug.Debug.println("* ERROR RoverIO 01: Pos oob! "+pos, debug.Debug.ERROR);
			return;
		}
		adc[pos] = value;
	}
	
	public void sendIOs(){
		sendIO = 0;
	}
	
	public void check(){
		if(sendIO<0 || sendIO >= inputLenght+adcLenght){
			return;
		}
		if(sendIO<inputLenght){
			String s = "*RIO_"+sendIO+"_";
			int i = sendIO;
			for (; i < sendIO+30; i++) {
				if(i>=inputLenght)break;
				if(input[i]){
					s+='t';
				}else{
					s+='f';
				}
			}
			sendIO = i;
			comm.Communication.com.printExternal(s);
		}
	}

}

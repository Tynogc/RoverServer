package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Settings {
	
	private static final String file = "res/settings.txt";
	
	private int currentLog;
	private int lastError;
	private int currentError;
	public boolean shutdownErrors;
	
	public static final int NUMBER_OF_LOGS = 30;
	
	public Settings(){
		try{
			FileReader fr = new FileReader(file);
		    BufferedReader br = new BufferedReader(fr);
		    
		    try {
				currentLog = Integer.parseInt(br.readLine());
				lastError = Integer.parseInt(br.readLine());
				currentError = Integer.parseInt(br.readLine());
			} catch (Exception e) {
				debug.Debug.println(e.getMessage(), debug.Debug.ERROR);
			}
		    
		    String s = br.readLine();
		    shutdownErrors = s.contains("true");
		    		    
		    br.close();
			}catch (FileNotFoundException e) {
				debug.Debug.println(e.getMessage(), debug.Debug.ERROR);
				return;
			}catch (IOException e) {
				debug.Debug.println(e.getMessage(), debug.Debug.ERROR);
			}catch (NullPointerException e) {
				debug.Debug.println(e.getMessage(), debug.Debug.ERROR);
			}
	}
	
	public void end(){
		PrintWriter writer = null; 
		try { 
			writer = new PrintWriter(new FileWriter(file)); 
			writer.println(currentLog);
			writer.println(lastError);
			writer.println(currentError);
			writer.println(shutdownErrors);
			
		} catch (IOException ioe) { 
			ioe.printStackTrace(); 
		} finally { 
			if (writer != null){ 
				writer.flush(); 
				writer.close(); 
			} 
		}
	}

	public int getCurrentLog() {
		return currentLog;
	}

	public void upCurrentLog() {
		currentLog++;
		if(currentLog >NUMBER_OF_LOGS)
			currentLog = 0;
	}

	public int getLastError() {
		return lastError;
	}

	public void upLastError() {
		lastError++;
		if(lastError >NUMBER_OF_LOGS)
			lastError = 0;
	}

	public int getCurrentError() {
		return currentError;
	}

	public void upCurrentError() {
		currentError++;
		if(currentError >NUMBER_OF_LOGS)
			currentError = 0;
	}

}

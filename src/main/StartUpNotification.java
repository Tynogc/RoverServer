package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import comm.SoketLinker;

public class StartUpNotification {
	
	public static void createNotification(SoketLinker s){
		
		Settings set = new Settings();
		s.linkerWrite("/~~START");
		
		boolean dispErr = false;
		if(set.getCurrentError() != set.getLastError()){
			if(set.shutdownErrors){
				s.linkerWrite("/A#Errors at ShutDown!");
				set.shutdownErrors = false;
			}
			dispErr = true;
			s.linkerWrite("/WErrors to be displayed!");
		}
		
		s.linkerWrite("/IError Msg: Current "+set.getCurrentError()+" Last Displ "+set.getLastError());
		s.linkerWrite("/CConnection Status Trusty: "+s.trusty);
		s.linkerWrite("/CComunicating on IP: "+s.socketIp);
		s.linkerWrite("/IRover-Server v0.0.1a");
		s.linkerWrite("/~INFO External connection StatUp");
		
		s.linkerWrite("/~");
		
		if(dispErr){
			int fgt = 0;
			while (set.getCurrentError() != set.getLastError()&&fgt<5) {
				set.upLastError();
				fgt++;
				dispErr(set.getLastError(), s);
			}
		}
		
		set.end();
	}
	
	private static void dispErr(int err, SoketLinker st){
		String[] a = new String[30];
		int i = 0;
		try{
			FileReader fr = new FileReader("log/err/"+err+".txt");
		    BufferedReader br = new BufferedReader(fr);
		    
		    while (true) {
				String s = br.readLine();
				if(s == null)break;
				if(s.length() <= 1)break;
				a[i]=s;
				i++;
				if(i>=a.length)break;
			}
		    
		    br.close();
		}catch (FileNotFoundException e) {
			debug.Debug.println(e.getMessage(), debug.Debug.ERROR);
			return;
		}catch (IOException e) {
				debug.Debug.println(e.getMessage(), debug.Debug.ERROR);
		}catch (NullPointerException e) {
			debug.Debug.println(e.getMessage(), debug.Debug.ERROR);
			return;
		}
		st.linkerWrite("/IWriting Fault-Msg Nr:"+err);
		st.linkerWrite("/~FAULT");
		st.linkerWrite("/~INFO "+a[0]);
		st.linkerWrite("/C-------------------------");
		
		i--;
		
		boolean exeptionIsPrinting = false;
		
		for (int j = i; j >= 0; j--) {
			if(a[j] == null)continue;
			
			if(a[j].compareTo(FaultNotification.exStrEnd)==0)
				exeptionIsPrinting = true;
			if(a[j].compareTo(FaultNotification.exStr)==0)
				exeptionIsPrinting = false;
			if(exeptionIsPrinting){
				st.linkerWrite("/A#"+a[j]);
			}else{
				st.linkerWrite("/A"+a[j]);
			}
		}
		
		st.linkerWrite("/~");
		
	}

}

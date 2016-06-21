package debug;

public class DebugComand {
	
	private static boolean printDevidedCom = true;
	
	private static String st;
	private static DebugFrame dt;
	
	public static void operateComand(String s, DebugFrame d){
		st = s;
		dt = d;
		
		Thread th = new Thread(){
			public void run() {
				operateC(st, dt);
			};
		};
		th.setPriority(2);
		th.start();
	}
	
	private static void operateC(String s, DebugFrame d){
		if(s.length()==0)return;
		String[][] str = devideComand(s);
		if(s.charAt(0)=='F'){
			return;
		}
		if(s.charAt(0)=='/'){
			inRover(str, d);
			return;
		}
		if(s.charAt(0)=='*'){
			inSystem(str, d);
			return;
		}
		if(s.charAt(0)=='<'&&s.charAt(s.length()-1)=='>'){
			return;
		}
	}
	
	private static void inRover(String[][] s, DebugFrame d){
		if(s[1][0].compareToIgnoreCase("pin")== 0){
			if(s[2][0].compareToIgnoreCase("get")== 0){
				int i = -1;
				try {
					i = Integer.parseInt(s[0][1]);
				} catch (Exception e) {
					Debug.println(" Can't convert "+s[0][1]+" to an Number!", Debug.COMERR);
					i = -1;
				}
				if(i < 0){
					return;
				}else{
					Debug.println("PIN "+i+" is: " +rover.RoverIO.roverIO.getPin(i), Debug.COM);
				}
			}else if(s[2][0].compareToIgnoreCase("set")== 0){
				int i = -1;
				try {
					i = Integer.parseInt(s[0][1]);
				} catch (Exception e) {
					Debug.println(" Can't convert "+s[0][1]+" to an Number!", Debug.COMERR);
					i = -1;
					return;
				}
				
				if(s[1][1] != null){
					if(s[1][1].length() >= 1){
						if(s[1][1].compareToIgnoreCase("0")==0 || s[1][1].compareToIgnoreCase("f")==0 || s[1][1].compareToIgnoreCase("false")==0){
							Debug.println("PIN "+i+" set to: false", Debug.COM);
							rover.RoverIO.roverIO.setPin(i, false);
							return;
						}
						if(s[1][1].compareToIgnoreCase("1")==0 || s[1][1].compareToIgnoreCase("t")==0 || s[1][1].compareToIgnoreCase("true")==0){
							Debug.println("PIN "+i+" set to: true", Debug.COM);
							rover.RoverIO.roverIO.setPin(i, true);
							return;
						}
					}
				}
				Debug.println(" Can't convert "+s[1][1]+" is not valid!", Debug.COMERR);
			}
			if(s[2][0].compareToIgnoreCase("adc")== 0){
				if(s[3][0].compareToIgnoreCase("get")== 0){
					int i = -1;
					try {
						i = Integer.parseInt(s[0][1]);
					} catch (Exception e) {
						Debug.println(" Can't convert "+s[0][1]+" to an Number!", Debug.COMERR);
						i = -1;
					}
					if(i < 0){
						return;
					}else{
						Debug.println("PIN ADC "+i+" is: " +rover.RoverIO.roverIO.getAdc(i), Debug.COM);
					}
				}else if(s[3][0].compareToIgnoreCase("set")== 0){
					int i = -1;
					try {
						i = Integer.parseInt(s[0][1]);
					} catch (Exception e) {
						Debug.println(" Can't convert "+s[0][1]+" to an Number!", Debug.COMERR);
						i = -1;
						return;
					}
					
					if(s[1][1] != null){
						int q = 0;
						try {
							q = Integer.parseInt(s[1][1]);
						} catch (Exception e) {
							Debug.println(" Can't convert "+s[1][1]+" is not valid!", Debug.COMERR);
							return;
						}
						Debug.println("PIN ADC "+i+" is set to: " +q, Debug.COM);
						rover.RoverIO.roverIO.setAdc(i, q);
						return;
					}
					Debug.println(" Can't convert "+s[1][1]+" is not valid!", Debug.COMERR);
				}
			}
			
		}
	}
	
	
	private static void inSystem(String[][] s, DebugFrame d){
		if(s[1][0].compareToIgnoreCase("ramDet")== 0){
			Runtime r = Runtime.getRuntime();
			long a = r.freeMemory();
			long b = r.maxMemory();
			long c = r.totalMemory();
			
			Debug.println("* Detailed Ram Information:", Debug.COM);
			Debug.println(" Free:  "+a+" byte", Debug.SUBCOM);
			Debug.println(" MAX:  "+b+" byte", Debug.SUBCOM);
			Debug.println(" Total: "+c+" byte", Debug.SUBCOM);
		}
		if(s[1][0].compareToIgnoreCase("ram")== 0){
			Runtime r = Runtime.getRuntime();
			long a = r.freeMemory();
			long b = r.maxMemory();
			long c = r.totalMemory();
			
			Debug.println(" Maximum Ram:", Debug.COM);
			Debug.println(" ", Debug.COM);
			Debug.printProgressBar(c-a, b, Debug.SUBCOM, true);
			Debug.println(" "+(c-a)/1000000+" Mb of "+b/1000000+" Mb", Debug.SUBCOM);
			Debug.println(" Used Ram:", Debug.COM);
			Debug.println(" ", Debug.COM);
			Debug.printProgressBar(c-a, c, Debug.SUBCOM, true);
			Debug.println(" "+(c-a)/1000000+" Mb of "+c/1000000+" Mb", Debug.SUBCOM);
			return;
		}
		if(s[1][0].compareToIgnoreCase("gc")== 0){
			long time = System.currentTimeMillis();
			Debug.println("Starting Garbage-Colector...", Debug.COM);
			Runtime.getRuntime().gc();
			Debug.println("DONE! Time: "+(System.currentTimeMillis()-time)+"ms", Debug.COM);
			return;
		}
		if(s[1][0].compareToIgnoreCase("stop")== 0){
			Debug.println("* Do you want to stop the Programm? [y/N]", Debug.COM);
			if(question(false, d)){
				main.ExitThread.restart = false;
				main.ExitThread.shutDownCause = main.ExitThread.SHDN_NORMAL;
				System.exit(0);
			}
			return;
		}
		if(s[1][0].compareToIgnoreCase("info")== 0){
			Debug.println("EMPTY "+Runtime.getRuntime().toString(), Debug.COM);
			return;
		}
		if(s[1][0].compareToIgnoreCase("system")== 0){
			
		}
		if(s[1][0].compareToIgnoreCase("delay")== 0){
			int i = 0;
			try {
				i = Integer.parseInt(s[0][1]);
			} catch (Exception e) {
				Debug.println(" Can't convert "+s[0][1]+" to an Number!", Debug.COMERR);
				i = 0;
			}
			Debug.println(" Comm-Delay is set to "+i, Debug.COM);
			comm.Communication.delay = i;
		}
		if(s[1][0].compareToIgnoreCase("crash")== 0){
			Debug.println("* This operation crashes the System, Continue? [y/N]", Debug.COM);
			if(question(false, d)){
				Debug.println("* This is Dangerous! Are You sure? [y/N]", Debug.COMERR);
				if(question(false, d)){
					rover.RoverControle.rover.ssk = null;
				}
			}
		}
		if(s[1][0].compareToIgnoreCase("cursor")== 0){
			DebGraphic.processCursor(s);
		}
		if(s[1][0].compareToIgnoreCase("help")== 0){
			Debug.println("List of Commands:", Debug.COM);
			Debug.println("  *pw ", Debug.SUBCOM);Debug.print("Change the Connection Password", Debug.PRICOM);
			Debug.println("  *gc ", Debug.SUBCOM);Debug.print("Runns the Garbage Collector", Debug.PRICOM);
			Debug.println("  *ram ", Debug.SUBCOM);Debug.print("Ram Satus", Debug.PRICOM);
			Debug.println("  *ramDet ", Debug.SUBCOM);Debug.print("Detailed Ram Satus", Debug.PRICOM);
			Debug.println("  *stop ", Debug.SUBCOM);Debug.print("Stops the System", Debug.PRICOM);
			Debug.println("  *crash ", Debug.SUBCOM);Debug.print("Forces a System Error", Debug.PRICOM);
		}
	}
	
	private static String[][] devideComand(String com){
		String[][] s = new String[5][2];
		int pos = 1;
		int i;
		for (int j = 0; j < s.length; j++) {
			s[j][0] = "";
			s[j][1] = "";
		}
		s[0][0] += com.charAt(0);
		for (i = 1; i < com.length(); i++) {
			char c = com.charAt(i);
			if(c =='.'){
				pos++;
				if(pos >= 5){
					Debug.println("- Comand got ignored elements!", Debug.COMERR);
				}
			}
			else if(c ==' '){
				break;
			}else{
				s[pos][0]+=c;
			}
		}
		pos = 0;
		for (i+=1; i < com.length(); i++) {
			char c = com.charAt(i);
			if(c ==' '){
				pos++;
				if(pos >= 5){
					Debug.println("- Comand got ignored elements!", Debug.COMERR);
				}
			}
			else{
				s[pos][1]+=c;
			}
		}
		
		if(printDevidedCom){
			Debug.println("Com: ", Debug.COM);
			for (int j = 0; j < s.length; j++) {
				Debug.print("["+s[j][0]+"] ", Debug.PRICOM);
			}
			Debug.println("Cap: ", Debug.COM);
			for (int j = 0; j < s.length; j++) {
				Debug.print("["+s[j][1]+"] ", Debug.PRICOM);
			}
		}
		
		return s;
	}
	
	public static int stringToInt(String s){
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			Debug.println("Can't convert "+s+"to a number :(", Debug.COMERR);
			return 0;
		}
	}
	
	public static boolean question(boolean enter, DebugFrame frame){
		boolean t = qute(enter, frame);
		frame.setCheckState(false);
		if(t){
			Debug.println("* YES", Debug.PRICOM);
		}else{
			Debug.println("* NO", Debug.PRICOM);
		}
		return t;
	}
	private static boolean qute(boolean enter, DebugFrame frame){
		frame.setCheckState(true);
		
		while (true){
			switch (frame.canState()) {
			case 0:
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
				
			case 1:
				return true;
			case 2:
				return false;
			case 3:
				return enter;

			default:
				break;
			}
			
		}
	}

}

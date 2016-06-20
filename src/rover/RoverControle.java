package rover;

public class RoverControle {
	
	public Battery bat;
	public SSK ssk;
	
	public static RoverControle rover;
	
	public RoverControle(){
		new RoverIO();
		bat = new Battery();
		ssk = new SSK();
		
		rover = this;
	}
	
	public void check(){
		bat.check();
		ssk.check();
	}
}

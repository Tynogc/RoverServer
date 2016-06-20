package comm;

public class FiElement {
	
	public FiElement next;
	public String str;
	
	public FiElement(String s){
		str = s;
		next = null;
	}
	
	public void add(FiElement f){
		if(next == null) next = f;
		else next.add(f);
	}
	
	public int lenght(){
		if(next == null)return 1;
		return next.lenght()+1;
	}

}

package debug;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class DebugFrame extends JFrame{
	
	private DebPanel panel;
	
	private String input = "";
	
	private int a = 0;
	private int b = 10;
	
	private byte canState;
	private boolean checkState;
	
	public static int dfl = -280;
	
	private String[] omtc;
	private int omtcPos = -1;
	
	private DebugFrame dbf;
	
	public DebugFrame(){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dfl = dim.height-(Debug.sizeY+100);
		if(dfl>0)dfl = 0;
		
		setBounds(100,0,Debug.sizeX+110,Debug.sizeY+80+dfl);
		panel = new DebPanel(this);
		add(panel);
		Debug.panel = panel;
		setVisible(true);
		panel.setFocusable(false);
		setFocusable(true);
		canState = 0;
		checkState = false;
		
		dbf = this;
		
		omtc = new String[10];
		for (int i = 0; i < omtc.length; i++) {
			omtc[i] = "";
		}
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getButton() == MouseEvent.BUTTON1){
					Debug.knowMemory();
					Debug.displayMemory(Debug.COM);
				}
				else{
					Debug.remove(3);
					Debug.print(b+"%", Debug.SUBWARN);
					b++;
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}
		});
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				char c = arg0.getKeyChar();
				if(checkState){
					if(c == '\n') canState = 3;
					if(c == 'y') canState = 1;
					if(c == 'n') canState = 2;
					
					return;
				}
				omtcPos = -1;
				if(c == '\n'){
					addOmtcString(input);
					Debug.println(input, Debug.PRICOM);
					DebugComand.operateComand(input, dbf);
					
					input = "";
				}else if((int)arg0.getKeyChar() == 8){
					if(input.length()>=1)
					input = input.substring(0, input.length()-1);
				}else{
					input += c;
				}
				
				panel.input = input;
				panel.paint(panel.getGraphics());
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_UP){
					omtcPos++;
					if(omtcPos>=omtc.length)omtcPos = omtc.length-1;
					input = omtc[omtcPos];
					panel.input = input;
					panel.paint(panel.getGraphics());
				}
				if(arg0.getKeyCode() == KeyEvent.VK_DOWN){
					omtcPos--;
					if(omtcPos<0)omtcPos = 0;
					input = omtc[omtcPos];
					panel.input = input;
					panel.paint(panel.getGraphics());
				}
			}
		});
		setVisible(true);
		
		paint(getGraphics());
	}
	
	public void setCheckState(boolean state){
		checkState = state;
		canState = 0;
	}
	
	public byte canState(){
		return canState;
	}
	
	private void addOmtcString(String s){
		for (int i = omtc.length-2; i >= 0; i--) {
			omtc[i+1]=omtc[i];
		}
		omtc[0] = s.substring(0);
	}

}

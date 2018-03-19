import java.util.ArrayList;

import bwapi.*;

public class Util {
	Game game;
	private ArrayList<String> Log = new ArrayList<String>();
	boolean Debug = false;
	Util(Game game) {
		
		this.game = game;
		
	}
	public boolean isDebug() {
		return Debug;
	}
	public void setDebug(boolean debug) {
		Debug = debug;
	}
	public void DebugPrint(String text) {
		
		if(Debug) {
			game.printf(text);
		}
		
	}
	public void DebugText(int x,int y,String text) {
		
		if(Debug) {
			game.drawTextScreen(x, y, text);
		}
		
	}
	public void DebugTextMap(int x,int y,String text) {
		
		if(Debug) {
			game.drawTextMap(x, y, text);
		}
		
	}
	public void AddLog(String str) {
		
		Log.add(str);
		
	}
	public void PopLog() {
		
		Log.remove(0);
		
	}
	public void DrawLog() {
		
		int Basex = 10;
		int Basey = 10;
		int i = 0;
		
		for (String str : Log) {
			
			DebugText(Basex,Basey + i,str);
			i += 10;
			
		}
		
	}
}

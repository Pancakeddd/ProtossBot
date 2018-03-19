import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jfap.*;


import bwapi.*;
import bwta.BWTA;

public class ArmyUnit {
	Unit unit;
	Player self;
	Game game;
	ArrayList<ArmyUnit> army;
	ArrayList<Position> bases;
	Random rand = new Random();
	int speakcounter;
	String str;
	JFAP simulator;
	int morale;
	
	boolean isleader = false;
	boolean dead = false;
	ArmyUnit(Unit unit,Player self,Game game,ArrayList<ArmyUnit> army,ArrayList<Position> bases) {
		
		this.unit = unit;
		this.self = self;
		this.game = game;
		this.army = army;
		this.bases = bases;
		this.simulator = new JFAP(game);
		
	}
	
	String GetRank() {
		int kills = unit.getKillCount();
		if (kills <= 1) {
			return "Peasant";
		} else if (kills <= 2) {
			return "Warrior";
		} else if (kills <= 4) {
			return "Master";
		} else if (kills <=  6) {
			return "Champion";
		} else if (kills > 6) {
			return "Messiah";
		}
		
		return "None";
	}
	
	String GetMorale() {
		if (morale <= 25) {
			return "Weak";
		} else if (morale <= 50) {
			return "Normal";
		} else if (morale <= 75) {
			return "Happy";
		} else if (morale <= 100) {
			return "Great";
		}
		
		return "None";
	}
	
	Position GetCenter(List<Unit> units) {
		if (units.size() <= 1) {
			return units.get(0).getPosition();
		}
		int x = 0;
		int y = 0;
		int count = 0;
		for (Unit u : units) {
			x += u.getPosition().getX();
			y += u.getPosition().getY();
			++count;
		}
		if (count > 0) {
			x = x / count;
			y = y / count;
		}
		System.out.println("X: " + x);
		System.out.println("Y: " + y);
		return new Position(x,y);
	}
	
	public Position Retreat (Position pos,List<Unit> buddieUnits) {
		
		Position newpos = unit.getPosition();
		int newposx = newpos.getX();
		int newposy = newpos.getY();
		int oldposx = pos.getX();
		int oldposy = pos.getY();
		newpos = new Position(newposx,newposy);
		for (Unit u : buddieUnits) {
			newposx = newposx + u.getPosition().getX();
			newposy = newposy + u.getPosition().getY();
		}
		newposx = newposx * oldposx;
		newposy = newposy * oldposy;
		return new Position(newposx,newposy);
		
	}
	
	public void Attack(Position pos) {
		
		unit.attack(pos);
		
	}
	
	public void Move(Position pos) {
		
		unit.move(pos);
		
	}
	
	String GenerateSoliderCatchPhrase(int num) {
		String outstr = "";
		switch(num) {
			case 1: outstr = "This sucks."; break;
			case 2: outstr = "My psi energy is running low."; break;
			case 3: outstr = "I hate mondays."; break;
			case 4: outstr = "I thought i saw something."; break;
			case 5: outstr = "En taro Adun!" ; break;
		}
		return outstr;
	}
	
	String GenerateLeaderCatchPhrase(int num) {
		String outstr = "";
		switch(num) {
			case 1: outstr = "Go Go Go!"; break;
			case 2: outstr = "Move out!"; break;
			case 3: outstr = "Follow my command!"; break;
			case 4: outstr = "Attack!."; break;
			case 5: outstr = "For aiur!" ; break;
		}
		return outstr;
	}
	
	public void Draw() {
		int speakint = rand.nextInt(500) + 1;
		game.drawCircleMap(unit.getPosition(), 3, new Color(255,0,0));
		if (isleader) {
			game.drawCircleMap(unit.getPosition(), 5, new Color(255,255,0));
		}
		if (speakint == 1) {
			if (!isleader)
				str = GenerateSoliderCatchPhrase(rand.nextInt(5) + 1);
			if (isleader)
				str = GenerateLeaderCatchPhrase(rand.nextInt(5) + 1);
			speakcounter = 50;
		}
		if (speakcounter > 0) {
			speakcounter -= 1;
			game.drawTextMap(unit.getPosition(), str);
		}
		game.drawTextMap(unit.getX() - 15, unit.getY() - 50, GetRank());
		game.drawLineMap(unit.getX(), unit.getY(), unit.getX(), unit.getY() - 40, new Color(255,255,255));
	}
	public void Update() {
		List<Unit> FoundUnits = game.enemy().getUnits();
		List<Unit> BuddieUnits = new ArrayList<Unit>();
		List<Unit> TempUnits = game.getAllUnits();
		BuddieUnits.add(unit);
		boolean otherleader = false;
		for (ArmyUnit u : army) {
			int dist = unit.getDistance(u.unit);
			if (u.isleader && u != this && dist < 250) {
				otherleader = true;
			}
		}
		if (otherleader) {
			isleader = false;
		} else {
			isleader = true;
		}
		for (Unit u :  TempUnits) {
			
			if(self.isAlly(u.getPlayer()) && !u.getType().isWorker()) {
				BuddieUnits.add(u);
			}
			
		}
		if (!FoundUnits.isEmpty() && isleader) {
			
			simulator.clear();
			for (Unit p1u : BuddieUnits) {
				simulator.addUnitPlayer1(new JFAPUnit(p1u));
			}
			
			for (Unit p2u : FoundUnits) {
				simulator.addUnitPlayer2(new JFAPUnit(p2u));
			}
			
			int myunitsbefore = simulator.getState().first.size();
			int hisunitsbefore = simulator.getState().second.size();
			this.simulator.simulate(250);
			int myunitsafter = simulator.getState().first.size();
			int hisunitsafater = simulator.getState().second.size();
			int mylosses = myunitsbefore - myunitsafter;
			int hislosses = hisunitsbefore - hisunitsafater;
			
			System.out.println("He losses: " + hislosses);
			System.out.println("I lose: " + mylosses);
			System.out.println(BuddieUnits);
			System.out.println(FoundUnits);
			
			if (mylosses <= hislosses) {
				for (Unit u :  BuddieUnits) {
					
					u.attack(GetCenter(FoundUnits));
					game.drawLineMap(unit.getPosition(), GetCenter(FoundUnits), new Color(255,255,255));
					
				}
				System.out.println("ATTACK");
			} else {
				for (Unit u :  BuddieUnits) {
					
					
					u.move(Retreat(GetCenter(FoundUnits),BuddieUnits));
					
				}
				System.out.println("RETREAT");
			}
			
			
		} else if(FoundUnits.size() < 1) {
			game.drawCircleMap(unit.getPosition(), 7, new Color(0,0,255));
			unit.move(BWTA.getNearestChokepoint(unit.getPosition()).getPoint());
		}
		
		if (!unit.exists()) {
			
			dead = true;
			
		}
	}
}

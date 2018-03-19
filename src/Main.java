import java.util.ArrayList;
import java.util.HashSet;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;

public class Main extends DefaultBWListener {

    private Mirror mirror = new Mirror();

    private Game game;

    private Player self;
    
    private Util util;
    
    private ConstructionManager mng;
    
    private TrainingManager umng;
    
    private int SlowTick;
    
    public HashSet<Position> Memory = new HashSet<Position>();
    
    
    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }
    
    public ArrayList<Worker> Workers = new ArrayList<Worker>();
    public ArrayList<ArmyUnit> Army = new ArrayList<ArmyUnit>();
    public ArrayList<Position> BaseLocations = new ArrayList<Position>();
    
    @Override
    public void onUnitComplete(Unit unit) {
    	
    	if (unit.getPlayer() == self) {
			if(unit.getType().canAttack() && !unit.getType().isWorker()) {
			    		
			    Army.add(new ArmyUnit(unit,self,game,Army,BaseLocations));
			    		
			}
    	}
    	
    } 
    @Override
    public void onUnitCreate(Unit unit) {
    	if (unit.getPlayer() == self) {
	    	 if(unit.getType() == UnitType.Protoss_Probe) {
	        	Worker worker = new Worker(unit,game,BaseLocations);
	        	Workers.add(worker);
	        } else if(unit.getType().isBuilding() && unit.isConstructing()) {
	        	mng.UnPay(unit.getType());
	        	
	        }
    	}
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        util = new Util(game);
        util.setDebug(true);
        umng = new TrainingManager(self);
        mng = new ConstructionManager(Workers,self,util);
        mng.AddConstructionItem(UnitType.Protoss_Pylon);
        mng.AddConstructionItem(UnitType.Protoss_Gateway);
        mng.AddConstructionItem(UnitType.Protoss_Pylon);
        mng.AddConstructionItem(UnitType.Protoss_Gateway);
        mng.AddConstructionItem(UnitType.Protoss_Gateway);
        mng.AddConstructionItem(UnitType.Protoss_Pylon);
        mng.AddConstructionItem(UnitType.Protoss_Pylon);
        mng.AddConstructionItem(UnitType.Protoss_Pylon);
        mng.AddConstructionItem(UnitType.Protoss_Pylon);
        mng.AddConstructionItem(UnitType.Protoss_Gateway);
        mng.AddConstructionItem(UnitType.Protoss_Gateway);
        mng.AddConstructionItem(UnitType.Protoss_Pylon);
        mng.AddConstructionItem(UnitType.Protoss_Pylon);
        
        game.setLocalSpeed(5);
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        
        for(BaseLocation baseLocation : BWTA.getBaseLocations()){
        	
        	if (baseLocation.isStartLocation() && !game.isVisible(baseLocation.getTilePosition())) {
        		
        		BaseLocations.add(baseLocation.getPosition());
        		
        	}
        	//System.out.println();
        }

    }
    
    private void UpdateMemory() {
    	
    	for (Unit u : game.enemy().getUnits()) {
    		//if this unit is in fact a unit
    		if (!u.getType().isBuilding()) {
    			//check if we have it's position in memory and add it if we don't
    			if (!Memory.contains(u.getPosition())) Memory.add(u.getPosition());
    		}
    	}

    	//loop over all the positions that we remember
    	for (Position p : Memory) {
    		// compute the TilePosition corresponding to our remembered Position p
    		TilePosition tileCorrespondingToP = new TilePosition(p.getX()/32 , p.getY()/32);

    		//if that tile is currently visible to us...
    		if (game.isVisible(tileCorrespondingToP)) {

    			//loop over all the visible enemy buildings and find out if at least
    			//one of them is still at that remembered position
    			boolean buildingStillThere = false;
    			for (Unit u : game.enemy().getUnits()) {
    				if ((!u.getType().isBuilding()) && (u.getPosition().equals(p))) {
    					buildingStillThere = true;
    					break;
    				}
    			}

    			//if there is no more any building, remove that position from our memory
    			if (buildingStillThere == false) {
    				Memory.remove(p);
    				break;
    			}
    		}
    	}
    	
    }
    
    @Override
    public void onFrame() {
        util.DrawLog();
        util.DebugText(200, 10, "Debt:"+ConstructionManager.virtualmineraldebt);
        util.DebugText(250, 10, "Army Supply:"+Army.size());
        for (Worker worker : Workers) {
        	
    		
        	worker.Update();
        		
        	
        }
        if(SlowTick >= 25) {
        	
        	UpdateMemory();
        	mng.TryConstruct();
        	if(Workers.size() < 14) {
        		umng.WorkerTrainTry();
        	}
        	
        	umng.ZealotTrainTry();
        	for (ArmyUnit unit : Army) {
            	
            	unit.Update();
            	if (unit.dead == true) {
            		
            		Army.remove(unit);
            		
            	}
            	
            }
        	
        	SlowTick = 0;
        	
        }
        for (ArmyUnit unit : Army) {
        	
        	unit.Draw();
        	
        }
        SlowTick += 1;
        
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
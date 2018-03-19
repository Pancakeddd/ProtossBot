import java.util.ArrayList;

import bwapi.*;

public class Worker {
	Unit unit;
	Game game;
	boolean isscouting = false;
	int basescouted = 0;
	ArrayList<Position> bases;
	Worker(Unit unit,Game game,ArrayList<Position> bases) {
		
		this.unit = unit;
		this.game = game;
		this.bases = bases;
		
	}
	
	TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
		TilePosition ret = null;
		int maxDist = 3;
		int stopDist = 40;

		// Refinery, Assimilator, Extractor
		if (buildingType.isRefinery()) {
			for (Unit n : this.game.neutral().getUnits()) {
				if ((n.getType() == UnitType.Resource_Vespene_Geyser) &&
						( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
						( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist )
						) return n.getTilePosition();
			}
		}

		while ((maxDist < stopDist) && (ret == null)) {
			for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
				for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
					if (this.game.canBuildHere(new TilePosition(i,j), buildingType, builder, false)) {
						// units that are blocking the tile
						boolean unitsInWay = false;
						for (Unit u : this.game.getAllUnits()) {
							if (u.getID() == builder.getID()) continue;
							if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
						}
						if (!unitsInWay) {
							return new TilePosition(i, j);
						}
						// creep for Zerg
						if (buildingType.requiresCreep()) {
							boolean creepMissing = false;
							for (int k=i; k<=i+buildingType.tileWidth(); k++) {
								for (int l=j; l<=j+buildingType.tileHeight();) {
									if (!this.game.hasCreep(k, l)) creepMissing = true;
									break;
								}
							}
							if (creepMissing) continue;
						}
					}
				}
			}
			maxDist += 2;
		}

		return null;
	}
	TilePosition getPylonPlacement(Unit builder, TilePosition aroundTile) {
		
		return aroundTile;
		
	}
	
	public boolean Build(UnitType type) {
		
		TilePosition pos = null;
		pos = getBuildTile(this.unit,type,this.unit.getTilePosition());
		if (pos != null) {
			this.unit.build(type,pos);
			return true;
		} else {
			
			return false;
			
		}
		
	}
	
	public void Mine() {
		
		Unit closestMineral = null;

        //find the closest mineral
        for (Unit neutralUnit : this.game.neutral().getUnits()) {
            if (neutralUnit.getType().isMineralField()) {
                if (closestMineral == null || this.unit.getDistance(neutralUnit) < this.unit.getDistance(closestMineral)) {
                    closestMineral = neutralUnit;
                }
            }
        }

        //if a mineral patch was found, send the worker to gather it
        if (closestMineral != null) {
            this.unit.gather(closestMineral, false);
        }
		
	}
	public void Update() {
		
		if(unit.isIdle() && isscouting) {
			//System.out.println(bases.size());
			if(basescouted == bases.size()) {
				
				Mine();
				
			} else {
				
				Scout();
				basescouted += 1;
				
			}
			
		} else if(unit.isIdle() && !isscouting) {
			
			Mine();
			
		}
		
	}
	public void Scout() {
		System.out.println("yes");
		unit.move(bases.get(basescouted));
		for(Unit un : unit.getUnitsInRadius(5)) {
			
			if(un.getType() == UnitType.Terran_Command_Center || un.getType() == UnitType.Zerg_Hatchery || un.getType() == UnitType.Protoss_Nexus) {
				
				isscouting = false;
				unit.move(unit.getInitialPosition());
				
			}
			
		}
		
	}
}

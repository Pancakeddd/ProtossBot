import bwapi.*;

public class TrainingManager {
	Player self;
	TrainingManager(Player self) {
		
		this.self = self;
		
	}
	public void Train(UnitType type) {
		if(ConstructionManager.Minerals() >= type.mineralPrice() && ConstructionManager.Gas() >= type.gasPrice()) {
			for (Unit unit : self.getUnits()) { 
				if(unit.getTrainingQueue().isEmpty())
				unit.train(type);
				
			}
		}
		
	}
	public void WorkerTrainTry() {
		
		Train(UnitType.Protoss_Probe);
		
	}
	public void ZealotTrainTry() {
		
		Train(UnitType.Protoss_Zealot);
		
	}
}

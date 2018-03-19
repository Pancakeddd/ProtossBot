import java.util.ArrayList;
import bwapi.*;

public class ConstructionManager {
	
	static Player self;
	
	public static int virtualmineraldebt;
	public static int virtualgasdebt;
	Util util;
	
	ArrayList<Worker> workers = null;
	ArrayList<UnitType> constructionlist = new ArrayList<UnitType>();
	ConstructionManager(ArrayList<Worker> workers,Player self,Util util) {
		
		this.workers = workers;
		ConstructionManager.self = self;
		this.util = util;
		
	}
	public void AddConstructionItem(UnitType type) {
		
		constructionlist.add(type);
		util.AddLog(type.toString());
		
	}
	void Pay(UnitType type) {
		
		virtualmineraldebt += type.mineralPrice();
		virtualgasdebt += type.gasPrice();
		
	}
	public void UnPay(UnitType type) {
		System.out.println(type.mineralPrice());
		virtualmineraldebt -= type.mineralPrice();
		virtualgasdebt -= type.gasPrice();
		
	}
	public static int Minerals() {
		
		return self.minerals() - virtualmineraldebt;
		
	}
	public static int Gas() {
		
		return self.gas() - virtualgasdebt;
		
	}
	public void TryConstruct() {
		if(!constructionlist.isEmpty() && Minerals() >= constructionlist.get(0).mineralPrice() && Gas() >= constructionlist.get(0).gasPrice() && constructionlist.get(0).requiredTech() != null) {
			for (Worker worker : workers) {
				if(worker.unit.isIdle() || worker.unit.isCarryingMinerals() || worker.unit.isGatheringMinerals()) {
					
					boolean worked = worker.Build(constructionlist.get(0));
					if(worked) {
						System.out.println(constructionlist.get(0).toString());
						Pay(constructionlist.get(0));
						constructionlist.remove(0);
						util.PopLog();
					}
					break;
				}
			}
			
		}
		
	}
}

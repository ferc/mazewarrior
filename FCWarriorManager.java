import ia.battle.camp.BattleField;
import ia.battle.camp.Warrior;
import ia.battle.camp.WarriorManager;
import ia.exceptions.RuleException;

public class FCWarriorManager extends WarriorManager {

	int counter;
	
    @Override
    public String getName() {
        return "FC Manager";
    }

    @Override
    public Warrior getNextWarrior() throws RuleException {

    	FCWarrior m;
    	
    	counter++;
    	
    	if (BattleField.getInstance().getSpecialItems().size() > 10) {
    		m = new FCWarrior("ferc speedy", 1, 1, 48, 40, 10);
    	}
    	else if (counter % 3 == 0) {
    		m = new FCWarrior("ferc balanced", 10, 25, 45, 10, 10);
    	}
    	else if (counter % 3 == 1) {
    		m = new FCWarrior("ferc defensive", 20, 45, 15, 10, 10);
    	}
    	else {
    		m = new FCWarrior("ferc offensive", 1, 1, 68, 20, 10);
    	}

        return m;
    }

}
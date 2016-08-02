import java.util.ArrayList;
import java.util.List;

import ia.battle.camp.Attackable;
import ia.battle.camp.BattleField;
import ia.battle.camp.ConfigurationManager;
import ia.battle.camp.FieldCell;
import ia.battle.camp.FieldCellType;
import ia.battle.camp.Warrior;
import ia.battle.camp.WarriorData;
import ia.battle.camp.actions.Action;
import ia.battle.camp.actions.Attack;
import ia.battle.camp.actions.Skip;
import ia.exceptions.OutOfMapException;
import ia.exceptions.RuleException;

public class FCWarrior extends Warrior {
	
	private AStar aStar;

    public FCWarrior(String name, int health, int defense, int strength, int speed, int range) throws RuleException {
        super(name, health, defense, strength, speed, range);

    }
    
    @Override
    public Action playTurn(long tick, int actionNumber) {

        BattleField battleField = BattleField.getInstance();
        WarriorData enemy = battleField.getEnemyData();
        FieldCell enemyField = enemy.getFieldCell();  
        FieldCell currentField = this.getPosition();
        
        if (enemy.getInRange()) {
            return new Attack(enemyField);
        }

        if (actionNumber == 0) {
        	this.aStar = new AStar(this.getMap());
        }
        
        if (actionNumber != 2) {
        	ArrayList<Node> siPath = this.getSpecialItemPath();
	        
	        if (siPath != null) {
	        	return new FCWarriorMove(siPath);
	        }
        
            ArrayList<Node> enemyPath = this.aStar.findPath(currentField.getX(), currentField.getY(), enemyField.getX(), enemyField.getY());
            int enemyCount = this.neareastEnemyCounter(enemyPath);        	            

        	return new FCWarriorMove(enemyPath, enemyCount);            	
        }

        ArrayList<Node> escapePath = getEscapePath();
        int movs = this.getSpeed() / 5;
        int escapeNodeIndex = Math.max(Math.min(movs, escapePath.size()) - 1, 0);
        Node escapeNode = escapePath.get(escapeNodeIndex);
        
        double escapeRange = this.getRangeFromCell(escapeNode.getX(), escapeNode.getY());
        double currentRange = this.getRangeFromCell(currentField.getX(), currentField.getY());

        if (escapeRange > currentRange) {
        	return new FCWarriorMove(escapePath);
        }
        
        return new Skip();
    }

    @Override
    public void wasAttacked(int damage, FieldCell source) {
    	
    	System.out.println("DAMAGE TO " + this.getName() + ": " + damage);
    }

    @Override
    public void enemyKilled() {
        // TODO Auto-generated method stub

    }
    
    private int[][] getMap() {
        int width = ConfigurationManager.getInstance().getMapWidth();
        int height = ConfigurationManager.getInstance().getMapHeight();        
        int[][] map = new int[width][height];
        
        for(int x = 0; x < width; x++) {
        	for(int y = 0; y < height; y++) {
        		try {
        			map[x][y] = BattleField.getInstance().getFieldCell(x, y).getFieldCellType() == FieldCellType.BLOCKED ? 1 : 0;
				} catch (OutOfMapException e) {
					e.printStackTrace();
				}
        	}
        }

        return map;
    }
    
    private double getRangeFromCell(int centerX, int centerY) {
		FieldCell enemyCell = BattleField.getInstance().getEnemyData().getFieldCell();
		int x = enemyCell.getX();
		int y = enemyCell.getY();

		return Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2);   	
    }

	private boolean isEnemyInRange(int centerX, int centerY) {
		int range = this.getRange();

		if (this.getRangeFromCell(centerX, centerY) <= Math.pow(range, 2)) {
			return true;
		}

		return false;
	}    
    
    private int neareastEnemyCounter(ArrayList<Node> path) {
        
    	int count = 1;
        
        for (Node node : path) {
        	if (this.isEnemyInRange(node.getX(), node.getY())) {
        		break;
        	}
    		count++;
        }
        
        return count;
    }
    
    private ArrayList<Node> getSpecialItemPath() {
        ArrayList<Node> siPath = null;
        int siCount = 0;
        ArrayList<FieldCell> specialItems = BattleField.getInstance().getSpecialItems();
        
        for (FieldCell specialItem : specialItems) {
        	ArrayList<Node> path = this.aStar.findPath(this.getPosition().getX(), this.getPosition().getY(), specialItem.getX(), specialItem.getY());

        	int specialItemCount = this.neareastEnemyCounter(path);
        	if (path.size() == (specialItemCount - 1) && (siCount == 0 || specialItemCount < siCount)) {
        		siPath = path;
        		siCount = specialItemCount;
        	}
        }

        return siPath;
    }
    
    private ArrayList<Node> getEscapePath() {
    	FieldCell enemyField = BattleField.getInstance().getEnemyData().getFieldCell();
        int diffX = this.getPosition().getX() - enemyField.getX();
        int diffY = this.getPosition().getY() - enemyField.getY();
        int toX = diffX > 0 ? ConfigurationManager.getInstance().getMapWidth() - 2 : 1;
        int toY = diffY > 0 ? ConfigurationManager.getInstance().getMapHeight() - 2 : 1;
        
        return this.aStar.findPath(this.getPosition().getX(), this.getPosition().getY(), toX, toY);
    }
    
}
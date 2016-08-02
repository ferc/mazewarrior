import ia.battle.camp.FieldCell;
import ia.battle.camp.BattleField;
import ia.battle.camp.actions.Move;
import ia.exceptions.OutOfMapException;

import java.util.ArrayList;

public class FCWarriorMove extends Move {
       
        private ArrayList<FieldCell> path;
       
        public FCWarriorMove(FieldCell dest) {
    		path = new ArrayList<>();
    		
    		path.add(dest);
        }
        
        public FCWarriorMove(ArrayList<Node> nodes) {
    		path = new ArrayList<>();
    		
    		for(Node node : nodes) {
				try {
					FieldCell cell = BattleField.getInstance().getFieldCell(node.getX(), node.getY());
        			path.add(cell);
				} catch (OutOfMapException e) {
					e.printStackTrace();
				}
    		}
        }

        public FCWarriorMove(ArrayList<Node> nodes, int limit) {
    		path = new ArrayList<>();
    		
    		for(int i = 0; i < limit; i++) {
				try {
					FieldCell cell = BattleField.getInstance().getFieldCell(nodes.get(i).getX(), nodes.get(i).getY());
        			path.add(cell);
				} catch (OutOfMapException e) {
					e.printStackTrace();
				}
    		}
        }
       
        @Override
        public ArrayList<FieldCell> move() {
            return path;
        }

}

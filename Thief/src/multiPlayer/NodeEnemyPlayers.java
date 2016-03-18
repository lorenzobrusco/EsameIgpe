package multiPlayer;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import singlePlayer.model.NodeCharacter;

public class NodeEnemyPlayers extends NodeCharacter {

    public NodeEnemyPlayers(String model, Vector3f dimensionControll, int life, int DAMAGE) {
	super(model, dimensionControll, life, DAMAGE);
    }

    public NodeEnemyPlayers(Spatial model, Vector3f dimensionControll, int life, int DAMAGE) {
	super(model, dimensionControll, life, DAMAGE);
    }
    
    public void setWalkDirection(Vector3f direction){
	this.characterControl.setWalkDirection(direction);
    }
    
    public void setViewDirection(Vector3f view){
	this.characterControl.setViewDirection(view);
    }
    
    @Override
    public void startAttack(){
	super.startAttack();
    }
    
    //TODO Implementare questa classe per i nemici del multiplayer
}

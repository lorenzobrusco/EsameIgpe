package logic.enemyIntelligence;

public class Node<T> {

    protected T id;

    public Node(T id) {
	this.id = id;
    }

    public T getId() {
	return id;
    }

    public void setId(T id) {
	this.id = id;
    }

}

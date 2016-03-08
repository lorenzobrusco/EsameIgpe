package logic.enemyIntelligence;

public class Edge<T> {

    protected T from;

    protected T to;

    public Edge(T from, T to) {
	this.from = from;
	this.to = to;
    }

    public T getFrom() {
	return from;
    }

    public void setFrom(T from) {
	this.from = from;
    }

    public T getTo() {
	return to;
    }

    public void setTo(T to) {
	this.to = to;
    }

}

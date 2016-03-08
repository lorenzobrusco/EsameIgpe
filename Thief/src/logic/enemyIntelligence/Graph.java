package logic.enemyIntelligence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph<N extends Node<T>, E extends Edge<T>, T> {

    protected List<N> nodeList;

    protected List<E> edgeList;

    private Map<T, Adjacency<N, T>> adjacency;

    protected boolean directionGraph;

    public Graph(boolean diGraph) {

	this.nodeList = new ArrayList<N>();
	this.edgeList = new ArrayList<E>();
	this.adjacency = new HashMap<T, Adjacency<N, T>>();
	this.directionGraph = diGraph;
    }

    public Graph(List<N> nodeList, List<E> edgeList, Map<T, Adjacency<N, T>> adjacency, boolean diGraph) {
	super();
	this.nodeList = nodeList;
	this.edgeList = edgeList;
	this.adjacency = adjacency;
	this.directionGraph = diGraph;
    }

    public void addNode(N node) {
	nodeList.add(node);
    }

    public void addEdge(E edge) {
	edgeList.add(edge);
    }

    public N getNode(T index) {
	for (N node : nodeList)
	    if (node.equals(index))
		return node;
	return null;
    }

    public E getEdge(T index) {
	for (E edge : edgeList)
	    if (edge.equals(index))
		return edge;
	return null;
    }

    public void addAdjacency(T key, Adjacency<N, T> adjacency) {
	this.adjacency.put(key, adjacency);
    }

    public Adjacency<N, T> getAdjacency(T key) {
	return this.getAdjacency(key);

    }
}

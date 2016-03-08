package thread;
//

//import java.awt.Point;
//import java.awt.geom.Point2D;
//import java.awt.geom.Point2D.Double;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Vector;
//
//import logic.World;
//import logic.character.ElementOfWorld;
//import logic.character.Enemy;

public class Way extends Thread {

    // Da rivedere tutto. La seguente idea è di utilizzare il backtracking per
    // il calcolo del percorso
    // da parte del nemico
    //
    // private final int PASSO = 50;
    // private final Enemy enemy;
    // private HashMap<Point2D.Double, Point2D.Double[]> pointsForPoint;
    // private Vector<ArrayList<Point2D.Double>> sol;
    // private ArrayList<Point2D.Double> wrongPoints;
    // private Point2D.Double normalize;
    // public static double x, y, x2, y2;
    //
    // public Way(Enemy enemy) {
    //
    // this.enemy = enemy;
    // sol = new Vector<ArrayList<Point2D.Double>>();
    // wrongPoints = new ArrayList<Point2D.Double>();
    // pointsForPoint = new HashMap<Point2D.Double, Point2D.Double[]>();
    // normalize = new Point2D.Double(
    // (enemy.getThief().getX() - enemy.getX()) / (double)
    // (distanceFloat(enemy.getX(), enemy.getY(),
    // enemy.getThief().getX(), enemy.getThief().getY())),
    // (enemy.getThief().getY() - enemy.getY()) / (double)
    // (distanceFloat(enemy.getX(), enemy.getY(),
    // enemy.getThief().getX(), enemy.getThief().getY())));
    // }
    //
    // public boolean solve() {
    //
    // Point2D.Double enemyPosition = new Point2D.Double(enemy.getX(),
    // enemy.getY());
    // boolean stop = false;
    // boolean existSolution = false;
    // while (!stop) {
    // if (!enemy.collition(enemy, enemy.getThief())) {
    // if (canAdd(/* prendo un punto dalla lista */)) {
    // // add(.....);
    // if (enemy.collition(enemy, enemy.getThief())) {
    // stop = true;
    // existSolution = true;
    // } else
    // ;
    // // next(enemyPosition,...);
    // } else {
    // if (enemy.collition(enemy, enemy.getThief())) {
    // stop = true;
    // existSolution = true;
    // } else {
    //
    // remove(enemyPosition);
    // // next(enemyPosition,....);
    // }
    // if (enemy.distance(enemy.getX(), enemy.getY(), enemy.getThief().getX(),
    // enemy.getThief().getY(),
    // enemy.getSizex(), enemy.getSizey(), enemy.getThief().getSizex(),
    // enemy.getThief().getSizey()) > (enemy.getENEMY_VIEW() * 2)) {
    // stop = true;
    // existSolution = false;
    // }
    // }
    // }
    // }
    // return existSolution;
    // }
    //
    // private void next(Point2D.Double currentPosition, Point2D.Double
    // nextPosition) {
    // currentPosition = nextPosition;
    // }
    //
    // private void remove(Double enemyPosition) {
    //
    // }
    //
    // private boolean canAdd(Point2D.Double point) {
    //
    // normalize = new Point2D.Double(
    // (point.x - enemy.getX()) / (double) (distanceFloat(enemy.getX(),
    // enemy.getY(), point.x, point.y)),
    // (point.y - enemy.getY()) / (double) (distanceFloat(enemy.getX(),
    // enemy.getY(), point.x, point.y)));
    // Enemy tmp = enemy;
    // while (!tmp.collition(tmp, enemy.getThief())) {
    // if (tmp.collition(tmp, enemy.getThief()))
    // return true;
    // for (Point2D.Double d :
    // World.getInstance().getElementsOfWorld().keySet()) {
    // if (((ElementOfWorld) tmp).collition((ElementOfWorld) tmp,
    // World.getInstance().getElementsOfWorld().get(d))) {
    // metodoDiagonale(new Point2D.Double(tmp.getX(), tmp.getY()));
    // return false;
    // // prendiamo i 4 punti di enemy dopo aver colliso
    // // System.out.println("collisione: " + tmp.getX() + " " +
    // // tmp.getY());
    //
    // }
    // }
    // wrongPoints.add(new Point2D.Double((tmp.getX() + normalize.x),
    // (tmp.getY() + normalize.y)));
    // tmp.setX(tmp.getX() + normalize.x);
    // tmp.setY(tmp.getY() + normalize.y);
    // }
    //
    // return true;
    // }
    //
    // private double distanceFloat(double x1, double y1, double x2, double y2)
    // {
    // return (double) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1),
    // 2));
    // }
    //
    // private void metodoDiagonale(Point2D.Double point) {
    //
    // Point2D.Double[] four_points = new Point2D.Double[4];
    // four_points[0] = new Point2D.Double((point.x + PASSO), (point.y +
    // PASSO));// alto
    // // destro
    // four_points[1] = new Point2D.Double((point.x + PASSO), (point.y -
    // PASSO));// basso
    // // destro
    // four_points[2] = new Point2D.Double((point.x - PASSO), (point.y +
    // PASSO));// alto
    // // sinistro
    // four_points[3] = new Point2D.Double((point.x - PASSO), (point.y -
    // PASSO));// basso
    // // sinistro
    // pointsForPoint.put(point, four_points);
    // }
    //
    // @Override
    // public void run() {
    //
    // canAdd();
    // for (Point2D.Double d : wrongPoints) {
    // try {
    // sleep((int) (Math.random() * 100));
    //
    // x = d.x;
    // y = d.y;
    //
    // x2 = enemy.getThief().getX();
    // y2 = enemy.getThief().getY();
    //
    // // enemy.setX(enemy.getX() + normalize.x);
    // // enemy.setY(enemy.getY() + normalize.y);
    // // enemy.repaint();
    // //
    // } catch (Exception exception) {
    // System.err.println("catch");
    // }
    // }
    //
    // }

}

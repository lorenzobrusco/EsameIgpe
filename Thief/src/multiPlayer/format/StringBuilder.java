package multiPlayer.format;

import com.jme3.math.Vector3f;

public class StringBuilder {

    private static final String DELIMITER = "&";
    private static final String SEPARATOR = "#";

    public String builderString(Vector3f walk, Vector3f view, Vector3f location, int life, boolean attack, String ip,
	    String model, int score) {

	String line = walk.x + DELIMITER + walk.y + DELIMITER + walk.z + SEPARATOR + view.x + DELIMITER + view.y
		+ DELIMITER + view.z + SEPARATOR + location.x + DELIMITER + location.y + DELIMITER + location.z
		+ SEPARATOR + life + SEPARATOR + attack + SEPARATOR + ip + model + SEPARATOR + score;
//	System.out.println(line);
//	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
//	System.out.println(split.length);
	return line;
    }
    
    public Vector3f builderWalk(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	Vector3f walk = new Vector3f(Float.parseFloat(split[0]), Float.parseFloat(split[1]),
		Float.parseFloat(split[2]));
	return walk;
    }

    public Vector3f builderView(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	Vector3f view = new Vector3f(Float.parseFloat(split[3]), Float.parseFloat(split[4]),
		Float.parseFloat(split[5]));
	return view;
    }

    public Vector3f builderPosition(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	Vector3f position = new Vector3f(Float.parseFloat(split[6]), Float.parseFloat(split[7]),
		Float.parseFloat(split[8]));
	return position;
    }

    public int builderLife(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	int life = Integer.parseInt(split[9]);
	return life;
    }

    public boolean builderAttack(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	boolean attack = Boolean.parseBoolean(split[10]);
	return attack;
    }

    public String builderKeyPlayer(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	String key = split[11];
	return key;
    }

    public int builderScore(String line){
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	int score = Integer.parseInt(split[12]);
	return score;
    }
    
}

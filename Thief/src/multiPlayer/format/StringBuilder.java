package multiPlayer.format;

import com.jme3.math.Vector3f;

/**
 * 
 * this class build a string to send for multiplayer
 *
 */

public class StringBuilder {

    /** delimiter between vector */
    private static final String DELIMITER = "&";
    /** separator between values */
    private static final String SEPARATOR = "#";
    /** number of values */
    private static final int MAXLENGTH = 15;

    /** constructor */
    public String builderString(Vector3f walk, Vector3f view, Vector3f location, int life, boolean attack, String ip,
	    String model, String name, int score) {

	String line = walk.x + DELIMITER + walk.y + DELIMITER + walk.z + SEPARATOR + view.x + DELIMITER + view.y
		+ DELIMITER + view.z + SEPARATOR + location.x + DELIMITER + location.y + DELIMITER + location.z
		+ SEPARATOR + life + SEPARATOR + attack + SEPARATOR + ip + SEPARATOR + model + SEPARATOR + name
		+ SEPARATOR + score;
	return line;
    }

    /** this method check if string is a instance of builderString */
    public boolean checkString(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	return split.length == MAXLENGTH;
    }

    /** this method builder walk's vector */
    public Vector3f builderWalk(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	try {
	    Vector3f walk = new Vector3f(Float.parseFloat(split[0]), Float.parseFloat(split[1]),
		    Float.parseFloat(split[2]));
	    return walk;
	} catch (NumberFormatException e) {
	    return new Vector3f(0, 0, 0);
	}
    }

    /** this method builder view's vector */
    public Vector3f builderView(String line) {
	try {
	    String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	    Vector3f view = new Vector3f(Float.parseFloat(split[3]), Float.parseFloat(split[4]),
		    Float.parseFloat(split[5]));
	    return view;

	} catch (NumberFormatException e) {
	    return new Vector3f(0, 0, 0);
	}
    }

    /** this method builder position's vector */
    public Vector3f builderPosition(String line) {
	try {
	    String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	    Vector3f position = new Vector3f(Float.parseFloat(split[6]), Float.parseFloat(split[7]),
		    Float.parseFloat(split[8]));
	    return position;
	} catch (NumberFormatException e) {
	    return new Vector3f(0, 0, 0);
	}
    }

    /** this method builder life */
    public int builderLife(String line) {
	try {
	    String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	    int life = Integer.parseInt(split[9]);
	    return life;
	} catch (NumberFormatException e) {
	    return Integer.MAX_VALUE;
	}
    }

    /** this method builder attack */
    public boolean builderAttack(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	boolean attack = split[10].equals("true");
	return attack;
    }

    /** this method builder key */
    public String builderKeyPlayer(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	String key = split[11] + split[12];
	return key;
    }

    /** this method builder model */
    public String builderModel(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	String model = split[12];
	return model;
    }

    /** this method builder address */
    public String builderAddress(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	String address = split[11];
	return address;
    }

    /** this method builder player's name */
    public String builderName(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	String address = split[13];
	return address;
    }

    /** this method builder score */
    public int builderScore(String line) {
	String[] split = line.split("[\\" + DELIMITER + "\\" + SEPARATOR + "]");
	try {
	    int score = Integer.parseInt(split[14]);
	    return score;
	} catch (NumberFormatException e) {
	    return 0;
	}
    }
}

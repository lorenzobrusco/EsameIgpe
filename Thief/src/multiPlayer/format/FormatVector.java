package multiPlayer.format;

import com.jme3.math.Vector3f;

public class FormatVector {

    public boolean equal(Vector3f one, Vector3f second) {
	return one.x == second.x && one.y == second.y && one.z == second.z;
    }

}

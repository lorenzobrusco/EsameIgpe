package multiPlayer.format;

import com.jme3.math.Vector3f;

public class FormatFloat {

    char[] numbers = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.', '+', '-' };

    public Vector3f formatVector(final String x, final String y, final String z) {
	try {
	    for (int i = 0; i < x.length(); i++) {
		int cont = 0;
		for (int j = 0; j < numbers.length; j++) {
		    if (x.charAt(i) != numbers[j]) {
			cont++;
		    }
		}
		if (cont >= numbers.length) {
		    return new Vector3f(0, 0, 0);
		}
	    }

	    return new Vector3f(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z));
	} catch (NumberFormatException e) {
	    return new Vector3f(0, 0, 0);
	}
    }

}

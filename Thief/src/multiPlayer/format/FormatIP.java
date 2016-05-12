package multiPlayer.format;

/**
 * 
 * this class check if string is a ip
 *
 */
public class FormatIP {

    /**string to check*/
    private final String ipAddress;

    /**builder*/
    public FormatIP(String ipAddress) {
	this.ipAddress = ipAddress;
    }

    /**this method return true if string is a ip*/
    public boolean itIsCorrectFormat() {
	final String[] address = this.ipAddress.split(".");
	for (int i = 0; i < address.length; i++)
	    if (Integer.parseInt(address[i]) < 0 || Integer.parseInt(address[i]) > 255)
		return false;
	return true;
    }

    /**this method get ip*/
    public String getIpAddress() {
	return ipAddress;
    }

}

package server.formatIP;

public class Format {

    private final String ipAddress;

    public Format(String ipAddress) {
	this.ipAddress = ipAddress;
    }

    public boolean itIsCorrectFormat() {
	final String[] address = this.ipAddress.split(".");
	for (int i = 0; i < address.length; i++)
	    if (Integer.parseInt(address[i]) < 0 || Integer.parseInt(address[i]) > 255)
		return false;
	return true;
    }

    public String getIpAddress() {
	return ipAddress;
    }

}

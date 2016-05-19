package multiPlayer.protocols;

/**
 * 
 * this interface is implemented for each class that has a connection
 *
 */

public interface CommunicationProtocol {

    public void startConnection();

    public void endConnection();

    public void communicationState();

    public String ipAddress();

}

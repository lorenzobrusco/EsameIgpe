package multiPlayer.protocols;

public interface CommunicationProtocol {

    public void startConnection();
    
    public void endConnection();
    
    public void communicationState();
    
    public String ipAddress();
    
}

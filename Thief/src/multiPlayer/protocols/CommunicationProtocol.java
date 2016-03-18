package multiPlayer.protocols;

public interface CommunicationProtocol {

    public void startConnection();

    public void endConnection();

    public void communicationState();

    public void communicationNewPlayer(String name, String model, String x, String y, String z);

    public String ipAddress();

}

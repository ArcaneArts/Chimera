package art.arcane.chimera.gateway.net;

public interface IClient
{
    public void send(GatewayMessage m);

    public void sendNow(GatewayMessage m);

    public void receiveMessage(GatewayMessage m);

    public boolean isRemote();
}

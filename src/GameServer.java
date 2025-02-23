import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GameServer<T extends ConnectedClient> extends Thread
{
    private DatagramSocket socket;
    private final int port = 1300;

    private List<T> connectedClients = new ArrayList<T>();

    public GameServer()
    {
        try {
            socket = new DatagramSocket(port);
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run()
    {
        while (true)
        {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

            try {
                socket.receive(packet);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    public void sendData(byte[] data, InetAddress client, int port)
    {
        DatagramPacket packet = new DatagramPacket(data, data.length, client, port);

        try {
            socket.send(packet);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data)
    {
        for(T p : connectedClients)
        {
            sendData(data, p.ipAddress, p.port);
        }
    }

    private void parsePacket(byte[] data, InetAddress ipAddress, int port)
    {
        String message = new String(data).trim();

        Packet packet = null;
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
        switch (type) {
            case INVALID:
                System.out.println("INVALID PACKET");
                break;
            case LOGIN:
                packet = new PacketLogin(data);
                System.out.println("[" + ipAddress.getHostAddress() + ":" + port + "]" + ((PacketLogin) packet).getMSG() + " has connected...");
                break;
        }
    }

    public void addConnection(T clientInstance, PacketLogin loginPacket)
    {
        boolean alreadyConnected = false;
        for (T connectedClient : connectedClients)
        {
            if (clientInstance.username.equalsIgnoreCase(connectedClient.username)) {
                if (connectedClient.ipAddress == null) {
                    connectedClient.ipAddress = clientInstance.ipAddress;
                }

                if (connectedClient.port == -1) {
                    connectedClient.port = clientInstance.port;
                }

                alreadyConnected = true;
            }
            else{
                sendData(loginPacket.getData(), connectedClient.ipAddress, connectedClient.port);
                Packet notifyClientsAboutOtherClientsPacket = new PacketLogin(connectedClient.username);
                sendData(notifyClientsAboutOtherClientsPacket.getData(), clientInstance.ipAddress, clientInstance.port);
            };
        }

        if (!alreadyConnected)
            connectedClients.add(clientInstance);
    }
}

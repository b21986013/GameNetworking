import java.io.IOException;
import java.net.*;

public class GameClient extends Thread
{
    private InetAddress host_ip;
    private DatagramSocket socket;
    private final int port = 1300;

    public GameClient(String ipAddress)
    {
        try{
            socket = new DatagramSocket();
            host_ip = InetAddress.getByName(ipAddress);
        }
        catch (SocketException | UnknownHostException e){
            e.printStackTrace();
        }

        sendData("ping".getBytes());
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

    public void sendData(byte[] data)
    {
        DatagramPacket packet = new DatagramPacket(data, data.length, host_ip, port);
        try {
            socket.send(packet);
        }
        catch (IOException e){
            e.printStackTrace();
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
                System.out.println("[" + ipAddress.getHostAddress() + ":" + port + "]" + ((PacketLogin) packet).getMSG() + " has joined...");
                break;
        }
    }
}

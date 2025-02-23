public class PacketLogin extends Packet{
    private byte[] data;
    private String msg_username;

    // Use this for parsing incoming packets.
    public PacketLogin(byte[] data)
    {
        super(00);
        this.msg_username = readMessage(data);
    }

    // Use this for sending packets.
    public PacketLogin(String msg)
    {
        super(00);
        this.msg_username = msg;
    }

    @Override
    public byte[] getData()
    {
        data = ("00" + msg_username).getBytes();
        return data;
    }

    public String getMSG()
    {
        return msg_username;
    }
}

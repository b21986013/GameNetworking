public abstract class Packet {
    public byte packetId;

    public Packet(int packetId)
    {
        this.packetId = (byte)packetId;
    }

    public static enum PacketTypes {
        INVALID(-1),
        LOGIN(00),
        DISCONNECT(01);

        private int packetId;
        private PacketTypes(int packetId)
        {
            this.packetId = packetId;
        }

        public int getId(){
            return packetId;
        }
    }

    public String readMessage(byte[] data)
    {
        String message = new String(data).trim();
        return message.substring(2);
    }

    public abstract byte[] getData();

    public static PacketTypes lookupPacket(String id)
    {
        try {
            return lookupPacket(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return PacketTypes.INVALID;
        }
    }

    public static PacketTypes lookupPacket(int id)
    {
        for(PacketTypes p : PacketTypes.values())
        {
            if(p.getId() == id)
                return p;
        }

        return PacketTypes.INVALID;
    }

}

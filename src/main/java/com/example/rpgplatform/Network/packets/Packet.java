package com.example.rpgplatform.Network.packets;

import com.example.rpgplatform.Network.GameClient;
import com.example.rpgplatform.Network.GameServer;

public abstract class Packet {
  public enum PacketTypes {
    INVALID(-1), LOGIN(00), DISCONNET(01), MOVE(02);
//    , PLACEBOMB(03);

    private final int packetId;

    PacketTypes(int packetId) {
      this.packetId = packetId;
    }

    public int getPacketId() {
      return packetId;
    }
  }

  public byte packetId;

  public Packet(int packetId) {
    this.packetId = (byte) packetId;
  }

  public abstract void writeData(GameClient client);

  public abstract void writeData(GameServer server);

  public String readData(byte[] data) {
    String message = new String(data).trim();
    return message.substring(2);
  }

  public abstract byte[] getData();

  public static PacketTypes lookupPacket(String packetId) {
    try {
      return lookupPacket(Integer.parseInt(packetId));
    } catch (NumberFormatException e) {
      return PacketTypes.INVALID;
    }
  }

  public static PacketTypes lookupPacket(int id) {
    for (PacketTypes p : PacketTypes.values()) {
      if (p.getPacketId() == id) {
        return p;
      }
    }
    return PacketTypes.INVALID;
  }
}

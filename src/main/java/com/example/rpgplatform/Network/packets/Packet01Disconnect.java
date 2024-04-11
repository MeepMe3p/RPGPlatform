package com.example.rpgplatform.Network.packets;

import com.example.rpgplatform.Network.GameClient;
import com.example.rpgplatform.Network.GameServer;

public class Packet01Disconnect extends Packet {
  private final String username;

  public Packet01Disconnect(byte[] data) {
    super(01);
    this.username = readData(data);
  }

  public Packet01Disconnect(String username) {
    super(01);
    this.username = username;
  }

  @Override
  public void writeData(GameClient client) {
    client.sendData(getData());
  }

  @Override
  public void writeData(GameServer server) {
    server.sendDataToAllClients(getData());
  }

  @Override
  public byte[] getData() {
    return ("01" + this.username).getBytes();
  }

  public String getUsername() {
    return username;
  }
}

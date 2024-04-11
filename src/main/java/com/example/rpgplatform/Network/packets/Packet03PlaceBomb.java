//package com.example.rpgplatform.net.packets;
//
//import com.example.rpgplatform.net.GameClient;
//import com.example.rpgplatform.net.GameServer;
//
//public class Packet03PlaceBomb extends Packet {
//  private final String username;
//  private final int state;
//  private final int bombType;
//
//  public Packet03PlaceBomb(byte[] data) {
//    super(03);
//    String[] dataArray = readData(data).split(",");
//    this.username = dataArray[0];
//    this.state = Integer.parseInt(dataArray[1]);
//    this.bombType = Integer.parseInt(dataArray[2]);
//  }
//
//  public Packet03PlaceBomb(String username, int state, int bombType) {
//    super(03);
//    this.username = username;
//    this.state = state;
//    this.bombType = bombType;
//  }
//
//  @Override
//  public void writeData(GameClient client) {
//    client.sendData(getData());
//  }
//
//  @Override
//  public void writeData(GameServer server) {
//    server.sendDataToAllClients(getData());
//  }
//
//  @Override
//  public byte[] getData() {
//    return ("03" + this.username + "," + this.state + "," + this.bombType).getBytes();
//  }
//
//  public String getUsername() {
//    return username;
//  }
//
//  public int getState() {
//    return state;
//  }
//
//  public int getBombType() {
//    return bombType;
//  }
//}

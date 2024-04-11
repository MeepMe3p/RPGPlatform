package com.example.rpgplatform.Network;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.example.rpgplatform.CharactersStuff.WarriorComponent;
import com.example.rpgplatform.Components.EntityType;
import javafx.geometry.Point2D;

import java.net.InetAddress;

public class PlayerMP {
  private final Entity player;
  private InetAddress ipAddress;
  private int port;
  private String username;

  public PlayerMP(double x, double y, String username, InetAddress ipAddress, int port) {
    this.username = username;
    this.ipAddress = ipAddress;
    this.port = port;

    this.player = FXGL.entityBuilder()
        .at(x, y)
        .type(EntityType.PLAYER)
        .bbox(new HitBox(new Point2D(30,55), BoundingShape.circle(16)))
        .bbox(new HitBox(new Point2D(30,91),BoundingShape.box(30,19)))
        .with(new WarriorComponent(username))
        .with(new CollidableComponent(true))
//        .with(new IrremovableComponent())
            // TODO: Study this and understand the "why?" https://github.com/AlmasB/FXGL/issues/373
        .build();
  }

  public String getUsername() {
    return this.username;
  }

  public Entity getEntity() {
    return player;
  }

  public InetAddress getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(InetAddress ipAddress) {
    this.ipAddress = ipAddress;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}

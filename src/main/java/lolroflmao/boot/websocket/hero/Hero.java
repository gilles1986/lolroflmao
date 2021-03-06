/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lolroflmao.boot.websocket.hero;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;


public class Hero {

  private static final int DEFAULT_LENGTH = 5;

  private final int id;
  private final WebSocketSession session;

  private Direction direction;
  private int length = DEFAULT_LENGTH;
  private Map head;
  private final String hexColor;
  private final String name;

  public Hero(int id, String name, WebSocketSession session) {
    this.id = id;
    this.session = session;
    this.name = name;
    this.hexColor = HeroUtils.getRandomHexColor();
    resetState();
  }

  private void resetState() {
    this.direction = Direction.NONE;
    this.head = HeroUtils.getRandomLocation();
    this.length = DEFAULT_LENGTH;
  }

  private synchronized void kill() throws Exception {
    resetState();
    sendMessage("{'type': 'dead'}");
  }

  private synchronized void reward() throws Exception {
    length++;
    sendMessage("{'type': 'kill'}");
  }


  protected void sendMessage(String msg) throws Exception {
    session.sendMessage(new TextMessage(msg));
  }

  public synchronized void update(Collection<Hero> snakes) throws Exception {
    Map nextLocation = head.getAdjacentLocation(direction);
    if (nextLocation.x >= HeroUtils.PLAYFIELD_WIDTH) {
      nextLocation.x = 0;
    }
    if (nextLocation.y >= HeroUtils.PLAYFIELD_HEIGHT) {
      nextLocation.y = 0;
    }
    if (nextLocation.x < 0) {
      nextLocation.x = HeroUtils.PLAYFIELD_WIDTH;
    }
    if (nextLocation.y < 0) {
      nextLocation.y = HeroUtils.PLAYFIELD_HEIGHT;
    }
    if (direction != Direction.NONE) {
      head = nextLocation;
    }

    handleCollisions(snakes);
  }

  private void handleCollisions(Collection<Hero> heroes) throws Exception {
    for (Hero hero : heroes) {
      boolean headCollision = id != hero.id && hero.getHead().equals(head);
      if (headCollision) {
        kill();
        if (id != hero.id) {
          hero.reward();
        }
      }
    }
  }

  public synchronized Map getHead() {
    return head;
  }

  public synchronized void setDirection(Direction direction) {
    this.direction = direction;
  }

  public synchronized void fire() {
    System.out.println("fire in the hole");
  }

  public synchronized String getLocationsJson() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("{x: %d, y: %d}",
        Integer.valueOf(head.x), Integer.valueOf(head.y)));
    return String.format("{'id':%d,'body':[%s],'direction':'%s'}",
        Integer.valueOf(id), sb.toString(),this.direction);
  }

  public int getId() {
    return id;
  }

  public String getHexColor() {
    return hexColor;
  }

  public String getName() {
    return name;
  }
}

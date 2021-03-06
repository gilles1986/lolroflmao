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

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.TextWebSocketHandlerAdapter;

import java.awt.Color;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class HeroWebSocketHandler extends TextWebSocketHandlerAdapter {

    public static final int PLAYFIELD_WIDTH = 640;
    public static final int PLAYFIELD_HEIGHT = 480;
    public static final int GRID_SIZE = 10;

    private static final AtomicInteger snakeIds = new AtomicInteger(0);
    private static final Random random = new Random();


    private final int id;
    private Hero hero;

    public static String getRandomHexColor() {
        float hue = random.nextFloat();
        // sat between 0.1 and 0.3
        float saturation = (random.nextInt(2000) + 1000) / 10000f;
        float luminance = 0.9f;
        Color color = Color.getHSBColor(hue, saturation, luminance);
        return '#' + Integer.toHexString(
                (color.getRGB() & 0xffffff) | 0x1000000).substring(1);
    }


    public static Map getRandomLocation() {
        int x = roundByGridSize(random.nextInt(PLAYFIELD_WIDTH));
        int y = roundByGridSize(random.nextInt(PLAYFIELD_HEIGHT));
        return new Map(x, y);
    }

    private String randomName() {
      String[] firstNames = {"Super", "Mega", "Fire", "Water"};
      String[] lastNames = {"man", "woman", "boy", "girl"};
      StringBuilder sb = new StringBuilder();
      sb.append(firstNames[random.nextInt(firstNames.length)])
          .append(lastNames[random.nextInt(lastNames.length)]);

      return sb.toString();
    }

    private static int roundByGridSize(int value) {
        value = value + (GRID_SIZE / 2);
        value = value / GRID_SIZE;
        value = value * GRID_SIZE;
        return value;
    }

    public HeroWebSocketHandler() {
        this.id = snakeIds.getAndIncrement();
    }


    @Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.hero = new Hero(id, randomName(), session);
        HeroTimer.addSnake(hero);
        StringBuilder sb = new StringBuilder();
        for (Iterator<Hero> iterator = HeroTimer.getHeroes().iterator();
                iterator.hasNext();) {
            Hero figure = iterator.next();
            sb.append(String.format("{id: %d, name: '%s'}",
                Integer.valueOf(figure.getId()), figure.getName()));
            if (iterator.hasNext()) {
                sb.append(',');
            }
        }
        HeroTimer.broadcast(String.format("{'type': 'join','data':[%s]}",
                sb.toString()));
    }


  @Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	String payload = message.getPayload();
        if ("west".equals(payload)) {
            hero.setDirection(Direction.WEST);
        } else if ("north".equals(payload)) {
            hero.setDirection(Direction.NORTH);
        } else if ("east".equals(payload)) {
            hero.setDirection(Direction.EAST);
        } else if ("south".equals(payload)) {
            hero.setDirection(Direction.SOUTH);
        } else if ("fire".equals(payload)) {
            hero.fire();
        }
    }


    @Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        HeroTimer.removeHero(hero);
        HeroTimer.broadcast(String.format("{'type': 'leave', 'id': %d}",
                Integer.valueOf(id)));
    }
}

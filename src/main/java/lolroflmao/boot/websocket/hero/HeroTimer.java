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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * Sets up the timer for the multi-player hero game WebSocket example.
 */
public class HeroTimer {

    private static final Log log =
            LogFactory.getLog(HeroTimer.class);

    private static Timer gameTimer = null;

    private static final long TICK_DELAY = 100;

    private static final ConcurrentHashMap<Integer, Hero> heroes =
            new ConcurrentHashMap<Integer, Hero>();

    private static final ConcurrentHashMap<Integer, Bullet> bullets =
            new ConcurrentHashMap<Integer, Bullet>();


    public static synchronized void addSnake(Hero snake) {
        if (heroes.size() == 0) {
            startTimer();
        }
        heroes.put(Integer.valueOf(snake.getId()), snake);
    }

    public static synchronized void addBullet(Bullet bullet) {
        bullets.put(Integer.valueOf(bullet.getId()), bullet);
    }


    public static Collection<Hero> getHeroes() {
        return Collections.unmodifiableCollection(heroes.values());
    }

    public static Collection<Bullet> getBullets() {
        return Collections.unmodifiableCollection(bullets.values());
    }


    public static synchronized void removeHero(Hero hero) {
        heroes.remove(Integer.valueOf(hero.getId()));
        if (heroes.size() == 0) {
            stopTimer();
        }
    }

    public static synchronized void removeBullet(Bullet bullet) {
        bullets.remove(Integer.valueOf(bullet.getId()));
    }


    public static void tick() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Hero> iterator = HeroTimer.getHeroes().iterator();
                iterator.hasNext();) {
            Hero hero = iterator.next();
            hero.update(HeroTimer.getHeroes());
            sb.append(hero.getLocationsJson());
            if (iterator.hasNext()) {
                sb.append(',');
            }
        }
        broadcast(String.format("{'type': 'update', 'data' : [%s]}",
                sb.toString()));
    }

    public static void broadcast(String message) throws Exception {
        for (Hero hero : HeroTimer.getHeroes()) {
            hero.sendMessage(message);
        }
    }


    public static void startTimer() {
        gameTimer = new Timer(HeroTimer.class.getSimpleName() + " Timer");
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    tick();
                } catch (Throwable e) {
                    log.error("Caught to prevent timer from shutting down", e);
                }
            }
        }, TICK_DELAY, TICK_DELAY);
    }


    public static void stopTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }
}

package lolroflmao.boot.websocket.hero;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * Created with IntelliJ IDEA.
 * User: robert
 * Date: 04.10.13
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class Bullet {

    private static final int DEFAULT_LENGTH = 1;

    private final int id;

    private Direction direction;
    private int length = DEFAULT_LENGTH;
    private Map head;
    private final String hexColor;

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

            }
        }
    }

    public int getId() {
        return id;
    }
}

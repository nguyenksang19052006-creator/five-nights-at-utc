package vn.utc.fnaf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DoorClosing {
    private Texture[] frames;
    private int currentFrame = 0;
    private float stateTimer = 0;
    private final float Frame_Duration = 0.03f;

    public DoorClosing() {
        frames = new Texture[8];
        for (int i = 0; i < 8; i++) {
            frames[i] = new Texture("Anh/door frames/door_" + (i + 1) + ".png");
        }
    }

    public void update(float delta, boolean isPressing) {
        stateTimer += delta;

        if (stateTimer >= Frame_Duration) {
            if (isPressing && currentFrame < 7) {
                currentFrame++;
                stateTimer = 0;
            } else if (!isPressing && currentFrame > 0) {
                currentFrame--;
                stateTimer = 0;
            }
        }
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(frames[currentFrame], x, y, width, height);
    }

    public boolean isFullyClosed() {
        return currentFrame == 7;
    }

    public void dispose() {
        for (Texture t : frames) {
            t.dispose();
        }
    }
}

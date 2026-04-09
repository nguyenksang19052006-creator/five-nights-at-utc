package vn.utc.fnaf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CameraGoingUp {
    private Texture[] frames;
    private int currentFrame = 0;
    private float stateTimer = 0;
    private final float frameDuration = 0.02f;

    public CameraGoingUp() {
        frames = new Texture[12];
        for (int i = 0; i < 12; i++) {
            frames[i] = new Texture("Anh/camera frames/cam_" + (i) + ".png");
        }
    }

    public void update(float delta, boolean isClicking) {
        stateTimer += delta;

        if (stateTimer >= frameDuration) {
            if (isClicking && currentFrame < 11) {
                currentFrame++;
                stateTimer = 0;
            } else if (!isClicking && currentFrame > 0) {
                currentFrame--;
                stateTimer = 0;
            }
        }
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(frames[currentFrame], x, y, width, height);
    }

    public boolean isFullyUp() {
        return currentFrame == 11;
    }

    public boolean isDown() {
        return currentFrame == 0;
    }

    public void dispose() {
        for (Texture t : frames) {
            t.dispose();
        }
    }
}

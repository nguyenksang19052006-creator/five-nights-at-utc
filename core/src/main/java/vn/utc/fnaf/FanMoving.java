package vn.utc.fnaf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FanMoving {
    private Texture[] frames;
    private int currentFrame = 0;
    private float stateTimer = 0f;
    private final float frameDuration = 0.05f;

    public FanMoving() {
        frames = new Texture[8];
        for (int i = 0; i < 8; i++) {
            frames[i] = new Texture("Anh/fan frames/Fan_" + (i + 1) + ".png");
        }
    }

    public void update(float delta) {
        stateTimer += delta;

        if (stateTimer >= frameDuration) {
            currentFrame++;
            stateTimer = 0;

            if (currentFrame >= 8) {
                currentFrame = 0;
            }
        }
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(frames[currentFrame], x, y, width, height);
    }

    public void dispose() {
        for (Texture tex : frames) {
            tex.dispose();
        }
    }
}

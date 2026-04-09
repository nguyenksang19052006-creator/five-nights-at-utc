package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WinScreen {
    private Texture[] frames;
    private int currentFrame = 0;
    private float stateTimer = 0;
    private final float frameDuration = 0.1f;
    private Sound winSound;
    private boolean soundPlayed = false;

    public WinScreen() {
        frames = new Texture[91];
        for (int i = 0; i < 91; i++) {
            String fileName = String.format("%03d", i + 1);
            frames[i] = new Texture("Anh/WINNER/ezgif-frame-" + fileName + ".png");
        }
        winSound = Gdx.audio.newSound(Gdx.files.internal("Nhac/6AM.mp3"));
    }

    public boolean update(float delta) {
        if (!soundPlayed) {
            winSound.play(1.0f);
            soundPlayed = true;
        }

        stateTimer += delta;
        if (stateTimer >= frameDuration) {
            stateTimer = 0;
            currentFrame++;
        }

        return currentFrame >= 91;
    }

    public void draw(SpriteBatch batch, float width, float height) {
        if (currentFrame < 91) {
            batch.draw(frames[currentFrame], 0, 0, width, height);
        }
    }

    public void dispose() {
        for (Texture t : frames) t.dispose();
        winSound.dispose();
    }
}

package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Sound;

public class WindowFlash {
    private Texture[] frames;
    private int currentFrame = 11;
    private float animationTimer = 0;
    private float cooldownTimer = 0;
    private Sound flashSound;

    private final float FrameTime = 0.04f;
    private final float CD = 0.7f;

    public WindowFlash() {
        frames = new Texture[12];
        for (int i = 0; i < 12; i++) {
            frames[i] = new Texture("Anh/Window/Window_Blackout" + (i + 1) + ".png");
        }
        flashSound = Gdx.audio.newSound(Gdx.files.internal("Nhac/FlashSound.mp3"));
    }

    public void update(float delta, boolean isMonitorOpen) {
        if (cooldownTimer > 0) cooldownTimer -= delta;

        if (!isMonitorOpen && Gdx.input.isKeyPressed(Input.Keys.SPACE) && cooldownTimer <= 0) {
            currentFrame = 0;
            animationTimer = 0;
            cooldownTimer = CD;
            flashSound.play(1f);
        }

        if (!isMonitorOpen) {
            animationTimer += delta;
            if (animationTimer >= FrameTime) {
                animationTimer = 0;
                if (currentFrame < 11) {
                    currentFrame++;
                }
            }
        }
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(frames[currentFrame], x, y, width, height);
    }

    public void trigger() {
        if (currentFrame >= 11 && cooldownTimer <= 0) {
            currentFrame = 0;
            animationTimer = 0;
            cooldownTimer = CD;
            if (flashSound != null) {
                flashSound.play(1f);
            }
        }
    }

    public void dispose() {
        for (Texture t : frames) {
            if (t != null) t.dispose();
        }
    }
}

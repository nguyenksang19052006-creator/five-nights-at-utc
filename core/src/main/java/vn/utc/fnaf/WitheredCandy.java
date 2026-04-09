package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;

public class WitheredCandy extends Animatronic {
    private HashMap<Integer, Texture> poses;
    private long soundId = -1;
    private float volume = 0.3f; // độ to âm thanh
    private float snowballTimer = 1f; // thời gian snowball
    public boolean stateChanged = false; // thực hiện blackout

    private final float baseMovement = 15f;
    private final float baseKill = 30f;
    private final float baseChance = 0.2f;

    private final float[][] candyPositions = {
        {-400f, 0f}, // Cam01
        {-150f, 0f}, // Cam02
        {-200f, 0f}, // Cam03
        {-200f, 0f} // Cam04
    };

    public WitheredCandy() {
        super("Withered Candy");
        poses = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            poses.put(i, new Texture("Anh/Withered Candy/Pose_" + i + ".png"));
        }
        presenceSound = Gdx.audio.newSound(Gdx.files.internal("Nhac/WitheredCandyPresence.mp3"));
        this.isActive = false;
    }

    @Override
    public void update(float delta, int playerViewCamera, boolean isMonitorUp) {
        if (AI_Level == 0 || gotYou) return;
        if (!isActive) {
            movementTimer += delta;
            float move = baseMovement - 0.41f * AI_Level;
            if (movementTimer >= move) {
                float success = baseChance + (0.04f * AI_Level);
                if (MathUtils.random() <= success) {
                    movementTimer = 0;
                    spawn();
                }
            }
        } else {
            float killTimer = baseKill - 0.75f * AI_Level;
            attackTimer += delta;
            if (attackTimer >= killTimer) {
                gotYou = true;
                despawn();
                return;
            }

            if (isMonitorUp && playerViewCamera == currentCamera) {
                repelTimer += delta;
                if (repelTimer >= snowballTimer + (0.015f * AI_Level)) {
                    snowballDespawn();
                }
            } else {
                repelTimer = 0;
            }

            updateVolume(playerViewCamera, isMonitorUp);
        }
    }

    private void spawn() {
        isActive = true;
        currentCamera = MathUtils.random(1, 4);
        attackTimer = 0;
        repelTimer = 0;
        soundId = presenceSound.loop(0.2f);
        stateChanged = true;
    }

    private void updateVolume(int playerViewCamera, boolean isMonitorUp) {
        if (soundId == -1) return;

        float targetVolume = 0.25f;
        if (isMonitorUp) {
            if (playerViewCamera == currentCamera) {
                targetVolume = 0.75f;
            } else {
                targetVolume = 0.5f;
            }
        }

        if (volume != targetVolume) {
            volume = targetVolume;
            presenceSound.setVolume(soundId, volume);
        }
    }

    private void snowballDespawn() {
        snowballTimer += 0.1f + (0.011f * AI_Level);
        despawn();
    }

    private void despawn() {
        isActive = false;
        currentCamera = -1;
        if (soundId != -1) {
            presenceSound.stop(soundId);
            soundId = -1;
        }
        stateChanged = true;
    }

    public void stopAudio() {
        if (presenceSound != null) {
            presenceSound.stop();
        }
    }

    public float getX() {
        return (currentCamera > 0) ? candyPositions[currentCamera - 1][0] : 0;
    }

    public float getY() {
        return (currentCamera > 0) ? candyPositions[currentCamera - 1][1] : 0;
    }

    public Texture getActiveTexture() {
        return poses.get(currentCamera);
    }

    @Override
    public void dispose() {
        presenceSound.dispose();
        for (Texture t : poses.values()) t.dispose();
    }
}

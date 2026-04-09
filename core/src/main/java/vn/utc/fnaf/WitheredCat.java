package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public class WitheredCat extends Animatronic {
    private Texture phases[];
    private int currentPhase = 0;
    public boolean stateChanged = false;
    private Sound bonkSound;

    private final float baseMovement = 10.0f;
    private final float baseChance = 0.2f;

    private final float[][] catPositions = {
        {1100f, 650f, 650f, 390f},  // Phase 1
        {700f, 350f, 1200f, 800f},  // Phase 2
        {500f, 300f, 1300f, 900f},  // Phase 3
        {1200f, 150f, 1500f, 1100f} // Phase 4 // tấn công
    };

    public WitheredCat() {
        super("Withered Cat");
        this.currentCamera = 1;
        bonkSound = Gdx.audio.newSound(Gdx.files.internal("Nhac/CatBonk.mp3"));
        phases = new Texture[4];
        for (int i = 0; i < 4; i++) {
            phases[i] = new Texture("Anh/Withered Cat/Pose_" + (i + 1) + ".png");
        }
        this.isActive = false;
    }

    private boolean isHoldingDoor;

    public void setHoldingDoor(boolean holding) {
        this.isHoldingDoor = holding;
    }

    @Override
    public void update(float delta, int playerViewCamera, boolean isMonitorUp) {
        if (AI_Level <= 0 || gotYou) return;

        if (currentPhase < 4) {
            movementTimer += delta;
            float interval = baseMovement - (0.35f * AI_Level);

            if (movementTimer >= interval) {
                movementTimer = 0;
                float success = baseChance + (0.04f * AI_Level);

                if (MathUtils.random() < success) {
                    currentPhase++;
                    isActive = true;

                    if (playerViewCamera == this.currentCamera && isMonitorUp) {
                        stateChanged = true;
                    }

                    if (currentPhase == 4) {
                        attackTimer = 0f;
                        repelTimer = 0f;
                    }
                }
            }
        }
        else if (currentPhase == 4) {
            if (!isHoldingDoor) {
                repelTimer = 0;
                attackTimer += delta;

                if (attackTimer >= 11.97f - (0.2f * AI_Level)) {
                    gotYou = true;
                    isActive = false;
                }
            } else {
                repelTimer += delta;

                if (repelTimer >= 1.0f + (0.1f * AI_Level)) {
                    bonkSound.play(1f);
                    despawn();
                }
            }
        }
    }

    private void despawn() {
        isActive = false;
        currentPhase = 0;
        attackTimer = 0;
        repelTimer = 0;
        movementTimer = 0;
        stateChanged = true;
    }

    public float getX()      { return catPositions[MathUtils.clamp(currentPhase - 1, 0, 3)][0]; }
    public float getY()      { return catPositions[MathUtils.clamp(currentPhase - 1, 0, 3)][1]; }
    public float getWidth()  { return catPositions[MathUtils.clamp(currentPhase - 1, 0, 3)][2]; }
    public float getHeight() { return catPositions[MathUtils.clamp(currentPhase - 1, 0, 3)][3]; }

    public Texture getActiveTexture() {
        return phases[MathUtils.clamp(currentPhase - 1, 0, 3)];
    }

    @Override
    public void dispose() {
        if (bonkSound != null) bonkSound.dispose();
        for (Texture t : phases) if (t != null) t.dispose();
    }
}

package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WitheredChester extends Animatronic {
    private Texture[] poses;

    private float totalProgressTimer = 0;
    private float stallTimer = 0;
    private float flashCooldown = 0;

    private final float StallDuration = 0.55f;
    private final float flashInterval = 0.70f;

    private WindowFlash windowFlash;

    public WitheredChester() {
        super("Withered Chester");
        poses = new Texture[3];
        for (int i = 0; i < 3; i++) {
            poses[i] = new Texture("Anh/Window/Chester_Pose" + (i + 1) + ".png");
        }
        this.isActive = true;
    }

    public void setWindowFlash(WindowFlash windowFlash) {
        this.windowFlash = windowFlash;
    }

    @Override
    public void update(float delta, int playerViewCamera, boolean isMonitorUp) {
        if (AI_Level <= 0 || gotYou) return;

        float effectiveDuration = 10.0f - (0.35f * AI_Level);

        if (flashCooldown > 0) flashCooldown -= delta;

        if (stallTimer > 0) {
            stallTimer -= delta;
            return;
        }

        if (!isMonitorUp && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (flashCooldown <= 0) {
                float pushbackAmount = 3.0f - (AI_Level * 0.1f);
                totalProgressTimer -= pushbackAmount;
                if (totalProgressTimer < 0) totalProgressTimer = 0;

                stallTimer = StallDuration;
                flashCooldown = flashInterval;

                if(windowFlash != null) windowFlash.trigger();

                return;
            }
        }

        totalProgressTimer += delta;

        if (totalProgressTimer >= (effectiveDuration * 3f)) {
            isActive = false;
            gotYou = true;
        }
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        if (isActive && !gotYou && AI_Level > 0) {
            float effectiveDuration = 10.0f - (0.35f * AI_Level);

            int index = (int) (totalProgressTimer / effectiveDuration);

            if (index < 0) index = 0;
            if (index > 2) index = 2;

            batch.draw(poses[index], x, y, width, height);
        }
    }

    @Override
    public void dispose() {
        for (Texture t : poses) {
            if (t != null) t.dispose();
        }
    }
}

package vn.utc.fnaf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class WitheredAstro {
    private Texture texture;
    public int AI_Level = 0;
    private boolean isActive = false; // đang active hay k
    private boolean nextFlipSafe = false; // lần tiếp theo đặt camera xuống sẽ không xuất hiện
    private float reactionTimer = 0; // thời gian react
    private float vanishTimer = 0; // biến mất sau x giây khi thực hiện thành công
    private boolean isVanishStarting = false;
    private boolean gotYou = false; // chết nếu true

    public WitheredAstro() {
        texture = new Texture("Anh/Withered Astro/AstroPose.png");
    }

    public void update(float delta, boolean isMonitorOpenIntent) {
        if (AI_Level <= 0 || gotYou) return;

        if (isActive) {
            reactionTimer += delta;
            float limit = 3.8f - (0.1f * AI_Level);
            if (reactionTimer >= limit) {
                gotYou = true;
                isActive = false;
                return;
            }

            if (isMonitorOpenIntent) {
                isVanishStarting = true;
            }

            if (isVanishStarting) {
                vanishTimer += delta;
                if (vanishTimer >= 0.24f) {
                    despawn();
                }
            }
        }
    }

    public void trySpawn() {
        if (AI_Level <= 0) return;
        if (nextFlipSafe) {
            nextFlipSafe = false;
            return;
        }

        float spawnChance = 0.1f + (0.03f * AI_Level);
        if (MathUtils.random() < spawnChance) {
            isActive = true;
            reactionTimer = 0;
            vanishTimer = 0;
            isVanishStarting = false;
            nextFlipSafe = true;
        }
    }

    private void despawn() {
        isActive = false;
        reactionTimer = 0;
        vanishTimer = 0;
        isVanishStarting = false;
    }

    public void draw(SpriteBatch batch) {
        if (isActive) {
            batch.draw(texture, 1300f, 0f, 2200f, 1400f);
        }
    }

    public boolean isJumpscareReady() { return gotYou; }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}

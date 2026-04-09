package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;

public class MonitorWatching {
    private Texture layout, crack, CameraUI;
    private HashMap<Integer, Texture> locations;
    private HashMap<String, Texture> buttonTextures;
    private Sound switchCam, signalLost;

    // Static
    private Texture[] staticFrames;
    private int currentStaticFrame = 0;
    private float staticTimer = 0;
    private final float FRAME_DURATION = 0.07f;

    // Blackout
    private Texture[] blackoutFrames;
    private boolean isBlackoutActive = false;
    private int currentBlackoutFrame = 0;
    private float blackoutTimer = 0;
    private final float BLACKOUT_FRAME_TIME = 0.06f;
    private final float BLACKOUT_STAY_TIME = 0.6f;
    private boolean isHoldingFinalFrame = false;

    private int currentCam = 1;
    private final int TOTAL_CAMS = 4;

    private final float[][] ButtonPositions = {
        {2000f, 100f}, {2100f, 400f}, {2400f, 370f}, {2220f, 310f}
    };

    private final float B_Width = 100f;
    private final float B_Height = 70f;

    public MonitorWatching() {
        layout = new Texture("Anh/Layout.png");
        CameraUI = new Texture("Anh/Camera_UI.png");
        crack = new Texture("Anh/MonitorCrack.png");
        locations = new HashMap<>();
        buttonTextures = new HashMap<>();

        switchCam = Gdx.audio.newSound(Gdx.files.internal("Nhac/CameraSwitch.mp3"));
        signalLost = Gdx.audio.newSound(Gdx.files.internal("Nhac/Signal_Lost.mp3"));

        staticFrames = new Texture[4];
        for (int i = 0; i < 4; i++) {
            staticFrames[i] = new Texture("Anh/static frames/StaticFrame_" + (i + 1) + ".png");
        }

        blackoutFrames = new Texture[5];
        for (int i = 0; i < 5; i++) {
            blackoutFrames[i] = new Texture("Anh/blackout frames/Blackout_" + (i + 1) + ".png");
        }

        for (int i = 1; i <= TOTAL_CAMS; i++) {
            locations.put(i, new Texture("Anh/camera locations/Location_" + i + ".png"));
            buttonTextures.put("Cam0" + i + "_Gray", new Texture("Buttons/Cam0" + i + "_Gray.png"));
            buttonTextures.put("Cam0" + i + "_Green", new Texture("Buttons/Cam0" + i + "_Green.png"));
        }
    }

    public void triggerBlackout(boolean isMonitorShowing) {
        if (!isMonitorShowing) return; // Silent exit if we aren't looking at the screen

        isBlackoutActive = true;
        currentBlackoutFrame = 0;
        blackoutTimer = 0;
        isHoldingFinalFrame = false;
        signalLost.play(1f);
    }

    private void updateBlackout(float delta) {
        if (!isBlackoutActive) return;

        blackoutTimer += delta;

        if (!isHoldingFinalFrame) {
            if (blackoutTimer >= BLACKOUT_FRAME_TIME) {
                blackoutTimer = 0;
                currentBlackoutFrame++;

                if (currentBlackoutFrame >= 4) {
                    currentBlackoutFrame = 4;
                    isHoldingFinalFrame = true;
                }
            }
        } else {
            if (blackoutTimer >= BLACKOUT_STAY_TIME) {
                isBlackoutActive = false;
                isHoldingFinalFrame = false;
            }
        }
    }

    public void updateInput(float touchX, float touchY, float screenX) {
        if (isBlackoutActive) return;

        for (int i = 0; i < TOTAL_CAMS; i++) {
            float xPos = screenX + ButtonPositions[i][0];
            float yPos = ButtonPositions[i][1];

            if (touchX >= xPos && touchX <= xPos + B_Width && touchY >= yPos && touchY <= yPos + B_Height) {
                int selectedCam = i + 1;
                if (currentCam != selectedCam) {
                    currentCam = selectedCam;
                    switchCam.play(0.6f);
                }
            }
        }
    }

    public void drawBackground(SpriteBatch batch, float screenX) {
        batch.draw(locations.get(currentCam), screenX, 0, 2600, 1462);
    }
    public void drawForeground(SpriteBatch batch, float screenX) {
        float delta = Gdx.graphics.getDeltaTime();
        updateBlackout(delta);


        if (isBlackoutActive) {
            batch.draw(blackoutFrames[currentBlackoutFrame], screenX, 0, 2600, 1462);
        }

        updateStaticAnimation(delta);
        batch.draw(staticFrames[currentStaticFrame], screenX, 0, 2600, 1462);

        batch.draw(CameraUI, screenX, -565, 2600, 2600);
        batch.draw(crack, screenX, 0, 2500, 2000);
        batch.draw(layout, screenX + 1700, -100, 1000, 1000);

        for (int i = 0; i < TOTAL_CAMS; i++) {
            float xPos = screenX + ButtonPositions[i][0];
            float yPos = ButtonPositions[i][1];
            int camNum = i + 1;
            String status = (camNum == currentCam) ? "_Green" : "_Gray";
            Texture btnTex = buttonTextures.get("Cam0" + camNum + status);
            if (btnTex != null) batch.draw(btnTex, xPos, yPos, B_Width, B_Height);
        }
    }

    private void updateStaticAnimation(float delta) {
        staticTimer += delta;
        if (staticTimer >= FRAME_DURATION) {
            currentStaticFrame = (currentStaticFrame + 1) % staticFrames.length;
            staticTimer = 0;
        }
    }

    public int getCurrentCam() {
        return currentCam;
    }

    public void dispose() {
        layout.dispose();
        crack.dispose();
        CameraUI.dispose();
        switchCam.dispose();
        signalLost.dispose();
        for (Texture t : staticFrames) t.dispose();
        for (Texture t : blackoutFrames) t.dispose();
        for (Texture t : locations.values()) t.dispose();
        for (Texture t : buttonTextures.values()) t.dispose();
    }
}

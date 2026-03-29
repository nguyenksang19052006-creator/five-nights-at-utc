package vn.utc.fnaf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;

public class MonitorWatching {
    private Texture layout;
    private HashMap<Integer, Texture> locations;
    private HashMap<String, Texture> buttonTextures;

    private int currentCam = 1;
    private final int TOTAL_CAMS = 4;

    private final float[][] btnPositions = {
        {2000f, 100f},
        {2100f, 400f},
        {2350f, 350f},
        {2250f, 300f}
    };

    private final float B_Width = 120f;
    private final float B_Height = 80f;

    public MonitorWatching() {
        layout = new Texture("Anh/Layout.png");
        locations = new HashMap<>();
        buttonTextures = new HashMap<>();

        for (int i = 1; i <= TOTAL_CAMS; i++) {
            locations.put(i, new Texture("Anh/camera locations/Location_" + i + ".png"));
            buttonTextures.put("Cam0" + i + "_Gray", new Texture("Buttons/Cam0" + i + "_Gray.png"));
            buttonTextures.put("Cam0" + i + "_Green", new Texture("Buttons/Cam0" + i + "_Green.png"));
        }
    }

    public void updateInput(float touchX, float touchY, float screenX) {
        for (int i = 0; i < TOTAL_CAMS; i++) {
            float xPos = screenX + btnPositions[i][0];
            float yPos = btnPositions[i][1];

            if (touchX >= xPos && touchX <= xPos + B_Width && touchY >= yPos && touchY <= yPos + B_Height) {
                currentCam = i + 1;
            }
        }
    }

    public void draw(SpriteBatch batch, float screenX, float y, float width, float height) {
        batch.draw(locations.get(currentCam), screenX, 0, 2600, 1462);

        batch.draw(layout, screenX + 1700, -100, 1000, 1000);

        for (int i = 0; i < TOTAL_CAMS; i++) {
            float xPos = screenX + btnPositions[i][0];
            float yPos = btnPositions[i][1];

            int camNum = i + 1;
            String status = (camNum == currentCam) ? "_Green" : "_Gray";
            Texture btnTex = buttonTextures.get("Cam0" + camNum + status);

            if (btnTex != null) {
                batch.draw(btnTex, xPos, yPos, B_Width, B_Height);
            }
        }
    }

    public void dispose() {
        layout.dispose();
        for (Texture t : locations.values()) t.dispose();
        for (Texture t : buttonTextures.values()) t.dispose();
    }
}

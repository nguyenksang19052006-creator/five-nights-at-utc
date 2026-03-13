package vn.utc.fnaf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MonitorWatching {
    private Texture location1;

    public MonitorWatching() {
        location1 = new Texture("Anh/camera locations/Location_1.png");
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(location1, x, y, width, height);
    }

    public void dispose() {
        if (location1 != null) location1.dispose();
    }
}

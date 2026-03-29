package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;

public class NightTimer {
    private final float motPhut = 60f;

    float thoiGianThuc = 0f;
    int soPhut = 0;
    private final int[] gio = {12, 1, 2, 3, 4, 5, 6};
    int currentHour = 12;

    public void render(float delta) {
        thoiGianThuc += delta;
        if (thoiGianThuc >= motPhut) {
            thoiGianThuc -= motPhut;
            soPhut++;

            if (soPhut <= 6) {
                currentHour = gio[soPhut];
            }
        }

        if (soPhut >= 6) {
            Gdx.app.exit();
        }
    }
    public String getTimeString() {
        return currentHour + "AM";
    }
}

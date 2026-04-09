package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class NightTimer {
    private final float motPhut = 60f;
    float thoiGianThuc = 0f;
    int soPhut = 0;
    private final int[] gio = {12, 1, 2, 3, 4, 5, 6};
    int currentHour = 12;

    private BitmapFont font;
    private ShaderProgram fontShader;

    public NightTimer() {
        Texture texture = new Texture(Gdx.files.internal("font.png"), true);
        texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);

        font = new BitmapFont(Gdx.files.internal("font.fnt"), new TextureRegion(texture), false);
        font.getData().setScale(2.5f);

        fontShader = new ShaderProgram(
            Gdx.files.internal("font.vert"),
            Gdx.files.internal("font.frag")
        );

        if (!fontShader.isCompiled()) {
            Gdx.app.error("ShaderError", fontShader.getLog());
        }
    }

    public boolean is6AM() {
        return soPhut >= 6;
    }

    public void render(float delta) {
        thoiGianThuc += delta;
        if (thoiGianThuc >= motPhut) {
            thoiGianThuc -= motPhut;
            soPhut++;

            if (soPhut <= 6) {
                currentHour = gio[soPhut];
            }
        }
    }

    public void draw(SpriteBatch batch, float x, float y) {
        batch.setShader(fontShader);
        font.draw(batch, getTimeString(), x, y);
        batch.setShader(null);
    }

    public String getTimeString() {
        return currentHour + " AM";
    }

    public void dispose() {
        if (font != null) font.dispose();
        if (fontShader != null) fontShader.dispose();
    }
}

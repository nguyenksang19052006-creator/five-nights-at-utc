package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Sound;

public class GameOver {
    private String killerName = "";
    private Texture background;
    private BitmapFont font;
    private final float btnX = 1100f, btnY = 200f, btnW = 400f, btnH = 100f;

    private Sound gameOverSound;

    public GameOver(BitmapFont sharedFont) {
        background = new Texture("Anh/Solid_black.png");
        this.font = sharedFont;
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("Nhac/GameOver.mp3"));
    }

    public void setKiller(String name) {
        this.killerName = "You got killed by " + name + "!";
        this.gameOverSound.play(1f);
    }

    public boolean update(Viewport uiViewport) {
        if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            uiViewport.unproject(touch);

            if (touch.x >= btnX && touch.x <= btnX + btnW && touch.y >= btnY && touch.y <= btnY + btnH) {
                return true;
            }
        }
        return false;
    }

    public void draw(SpriteBatch batch, Texture staticFrame, float width, float height) {
        batch.draw(background, 0, 0, width, height);

        batch.draw(staticFrame, 0, 0, width, height);

        font.getData().setScale(4.0f);
        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER", width / 2f - 400f, height / 2f + 100f);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, killerName, width / 2f - 340, height / 2f - 50f);
        font.draw(batch, "MENU", btnX + 100f, btnY + 70f);
    }

    public void dispose() {
        background.dispose();
    }
}

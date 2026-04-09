package vn.utc.fnaf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenu {
    private Texture background, playButton;
    private Texture leftArrow, rightArrow;
    private Texture candyIMG, catIMG, astroIMG, chesterIMG;
    private Texture candyM, catM, astroM, chesterM;
    private boolean startRequested = false;
    private Texture menuImage = null;

    private BitmapFont font;
    private ShaderProgram fontShader;

    private Texture[] staticFrames;
    private int currentStaticFrame = 0;
    private float staticTimer = 0;
    private final float FRAME_DURATION = 0.07f;

    // Audio
    private Sound confirmSound;

    // AI Levels
    private int chesterAI = 0;
    private int catAI = 0;
    private int candyAI = 0;
    private int astroAI = 0;
    private final int MAX_AI = 20;

    private final float chesterLArrowX = 350f, chesterRArrowX = 600f, chesterArrowY = 600f;
    private final float catLArrowX = 900f, catRArrowX = 1150f, catArrowY = 600f;
    private final float candyLArrowX = 1450f, candyRArrowX = 1700f, candyArrowY = 600f;
    private final float astroLArrowX = 2000f, astroRArrowX = 2250f, astroArrowY = 600f;

    private final float arrowSize = 80f;
    private final float btnX = 2300f, btnY = 100f, btnW = 150f, btnH = 150f;

    private String hoverText = "";

    private float fadeAlpha = 0f;
    private boolean isFading = false;
    private float stallTimer = 0f;

    private final float fadeSpeed = 0.5f;
    private final float stallDuration = 2.0f;

    public MainMenu() {
        background = new Texture("Anh/Solid_black.png");
        playButton = new Texture("Anh/PlayButton.png");
        leftArrow = new Texture("Anh/LeftArrow.png");
        rightArrow = new Texture("Anh/RightArrow.png");

        chesterIMG = new Texture("Anh/Menu Images/Chester.png");
        catIMG = new Texture("Anh/Menu Images/Cat.png");
        candyIMG = new Texture("Anh/Menu Images/Candy.png");
        astroIMG = new Texture("Anh/Menu Images/Astro.png");

        chesterM = new Texture("Anh/ChesterMenu.png");
        catM = new Texture("Anh/CatMenu.png");
        candyM = new Texture("Anh/CandyMenu.png");
        astroM = new Texture("Anh/AstroMenu.png");

        confirmSound = Gdx.audio.newSound(Gdx.files.internal("Nhac/Confirmed.mp3"));

        Texture fontTexture = new Texture(Gdx.files.internal("font.png"), true);
        fontTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("font.fnt"), new TextureRegion(fontTexture), false);
        font.getData().setScale(1.5f);

        fontShader = new ShaderProgram(Gdx.files.internal("font.vert"), Gdx.files.internal("font.frag"));

        staticFrames = new Texture[4];
        for (int i = 0; i < 4; i++) {
            staticFrames[i] = new Texture("Anh/static frames/StaticFrame_" + (i + 1) + ".png");
        }
    }

    public void update(Viewport uiViewport) {
        updateStaticAnimation(Gdx.graphics.getDeltaTime());

        if (isFading) {
            if (fadeAlpha < 1.0f) {
                fadeAlpha += Gdx.graphics.getDeltaTime() * fadeSpeed;
                if (fadeAlpha > 1.0f) fadeAlpha = 1.0f;
            } else {
                stallTimer += Gdx.graphics.getDeltaTime();
                if (stallTimer >= stallDuration) {
                    startRequested = true;
                }
            }
        }

        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        uiViewport.unproject(mousePos);

        hoverText = "";
        menuImage = null;

        if (mousePos.x >= 300 && mousePos.x <= 700 && mousePos.y >= 500 && mousePos.y <= 1000) {
            hoverText = "Chester: attacks from your office's window, flash using space to keep him at bay.";
            menuImage = chesterM;
        } else if (mousePos.x >= 850 && mousePos.x <= 1250 && mousePos.y >= 500 && mousePos.y <= 1000) {
            hoverText = "Cat: attacks from your office's left door, close the door accordingly.";
            menuImage = catM;
        } else if (mousePos.x >= 1400 && mousePos.x <= 1800 && mousePos.y >= 500 && mousePos.y <= 1000) {
            hoverText = "Candy: attacks in your camera, look at him to make him go away, each repel makes him stronger.";
            menuImage = candyM;
        } else if (mousePos.x >= 1950 && mousePos.x <= 2350 && mousePos.y >= 500 && mousePos.y <= 1000) {
            hoverText = "Astro: attacks randomly when you put your monitor down, flip it back up immediately before it's too late.";
            menuImage = astroM;
        }

        if (!isFading && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            handleAIClicks(mousePos);

            if (mousePos.x >= btnX && mousePos.x <= btnX + btnW && mousePos.y >= btnY && mousePos.y <= btnY + btnH) {
                isFading = true;
                confirmSound.play();
            }
        }
    }

    private void handleAIClicks(Vector3 mousePos) {
        // Chester
        if (mousePos.y >= chesterArrowY && mousePos.y <= chesterArrowY + arrowSize) {
            if (mousePos.x >= chesterLArrowX && mousePos.x <= chesterLArrowX + arrowSize && chesterAI > 0) chesterAI--;
            if (mousePos.x >= chesterRArrowX && mousePos.x <= chesterRArrowX + arrowSize && chesterAI < MAX_AI) chesterAI++;
        }
        // Cat
        if (mousePos.y >= catArrowY && mousePos.y <= catArrowY + arrowSize) {
            if (mousePos.x >= catLArrowX && mousePos.x <= catLArrowX + arrowSize && catAI > 0) catAI--;
            if (mousePos.x >= catRArrowX && mousePos.x <= catRArrowX + arrowSize && catAI < MAX_AI) catAI++;
        }
        // Candy
        if (mousePos.y >= candyArrowY && mousePos.y <= candyArrowY + arrowSize) {
            if (mousePos.x >= candyLArrowX && mousePos.x <= candyLArrowX + arrowSize && candyAI > 0) candyAI--;
            if (mousePos.x >= candyRArrowX && mousePos.x <= candyRArrowX + arrowSize && candyAI < MAX_AI) candyAI++;
        }
        // Astro
        if (mousePos.y >= astroArrowY && mousePos.y <= astroArrowY + arrowSize) {
            if (mousePos.x >= astroLArrowX && mousePos.x <= astroLArrowX + arrowSize && astroAI > 0) astroAI--;
            if (mousePos.x >= astroRArrowX && mousePos.x <= astroRArrowX + arrowSize && astroAI < MAX_AI) astroAI++;
        }
    }

    public void draw(SpriteBatch batch, float width, float height) {

        batch.draw(background, 0, 0, width, height);
        if (menuImage != null) {
            batch.draw(menuImage, 0, 0, width, height);
        }

        batch.draw(leftArrow, chesterLArrowX, chesterArrowY, arrowSize, arrowSize);
        batch.draw(rightArrow, chesterRArrowX, chesterArrowY, arrowSize, arrowSize);
        batch.draw(leftArrow, catLArrowX, catArrowY, arrowSize, arrowSize);
        batch.draw(rightArrow, catRArrowX, catArrowY, arrowSize, arrowSize);
        batch.draw(leftArrow, candyLArrowX, candyArrowY, arrowSize, arrowSize);
        batch.draw(rightArrow, candyRArrowX, candyArrowY, arrowSize, arrowSize);
        batch.draw(leftArrow, astroLArrowX, astroArrowY, arrowSize, arrowSize);
        batch.draw(rightArrow, astroRArrowX, astroArrowY, arrowSize, arrowSize);

        batch.draw(playButton, btnX, btnY, btnW, btnH);

        batch.draw(chesterIMG, 300, 700, 400, 300);
        batch.draw(catIMG, 850, 700, 400, 300);
        batch.draw(candyIMG, 1400, 700, 400, 300);
        batch.draw(astroIMG, 1950, 700, 400, 300);

        batch.setShader(fontShader);
        font.getData().setScale(1.5f);
        font.draw(batch, "" + chesterAI, chesterLArrowX + 130f, chesterArrowY + 35f);
        font.draw(batch, "" + catAI, catLArrowX + 130f, catArrowY + 35f);
        font.draw(batch, "" + candyAI, candyLArrowX + 130f, candyArrowY + 35f);
        font.draw(batch, "" + astroAI, astroLArrowX + 130f, astroArrowY + 35f);

        font.getData().setScale(3.5f);
        font.setColor(Color.WHITE);
        String title = "Custom Night";
        float titleX = 900f;
        float titleY = 1250f;
        font.draw(batch, title, titleX, titleY);

        font.getData().setScale(1.5f);
        font.setColor(Color.RED);
        String credits = "A FNaF-like horror game made by Sang                                                                   MSV: 243630602";
        float creditX = 100f;
        float creditY = 1100f;
        font.draw(batch, credits, creditX, creditY);

        font.getData().setScale(1.0f);
        font.setColor(Color.WHITE);
        String shortcut = "F2 to go back to the Menu (while in game).";
        float shortcutX = 100f;
        float shortcutY = 170f;
        font.draw(batch, shortcut, shortcutX, shortcutY);

        if (!hoverText.isEmpty()) {
            font.getData().setScale(1.0f);
            font.draw(batch, hoverText, 100, 100);
            font.getData().setScale(1.5f);
        }

        batch.setShader(null);

        batch.draw(staticFrames[currentStaticFrame], 0, 0, width, height);

        if (fadeAlpha > 0) {
            batch.setColor(1, 1, 1, fadeAlpha);
            batch.draw(background, 0, 0, width, height);
            batch.setColor(1, 1, 1, 1);
        }
    }

    private void updateStaticAnimation(float delta) {
        staticTimer += delta;
        if (staticTimer >= FRAME_DURATION) {
            currentStaticFrame = (currentStaticFrame + 1) % staticFrames.length;
            staticTimer = 0;
        }
    }

    public int getChesterAI() { return chesterAI; }
    public int getCatAI() { return catAI; }
    public int getCandyAI() { return candyAI; }
    public int getAstroAI() { return astroAI; }
    public boolean isStartRequested() { return startRequested; }
    public BitmapFont getFont() { return font; }

    public void resetStartRequest() {
        this.startRequested = false;
        this.isFading = false;
        this.fadeAlpha = 0f;
        this.stallTimer = 0f;
    }

    public Texture getCurrentStaticFrameTexture() {
        return staticFrames[currentStaticFrame];
    }

    public boolean isFading() {
        return isFading;
    }

    public void dispose() {
        background.dispose();
        playButton.dispose();
        leftArrow.dispose();
        rightArrow.dispose();
        chesterIMG.dispose();
        catIMG.dispose();
        candyIMG.dispose();
        astroIMG.dispose();
        font.dispose();
        confirmSound.dispose();
        if (fontShader != null) fontShader.dispose();
        for (Texture t : staticFrames) t.dispose();
    }
}

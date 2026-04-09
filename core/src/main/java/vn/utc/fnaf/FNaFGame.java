package vn.utc.fnaf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Sound;

public class FNaFGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture office;
    private Viewport viewport;
    private Viewport uiViewport;
    private OrthographicCamera camera;
    private WindowFlash windowSystem;
    private OrthographicCamera uiCamera;

    // Animatronics
    private WitheredCandy candy;
    private WitheredAstro astro;
    private WitheredCat cat;
    private WitheredChester chester;

    // Xoay Camera
    private final float OFFICE_WIDTH = 3000f;
    private final float VIEWPORT_WIDTH = 2600f;
    private final float VIEWPORT_HEIGHT = 1462f;
    private final float PAN_SPEED = 400f;
    private final float LERP_FACTOR = 20f;
    private float targetCameraX;

    // random sh
    private Music bgm, menuTheme;
    private Texture ButtonRed, ButtonGreen, MonitorButton;
    private Sound door, cam;
    private boolean cuaMo = true, amThanhDongCua = true;
    private boolean isMonitorOpen = false;
    private float monitorCD = 0;

    // other classes
    private MonitorWatching cameraSystem;
    private NightTimer nightTimer;
    private DoorClosing theDoor;
    private CameraGoingUp theCamera;
    private FanMoving theFan;
    private GameOver gameOverScreen;

    // menu, win, etc
    private enum State {MENU, PLAYING, GAME_OVER, WIN}

    private State gameState = State.MENU;
    private MainMenu mainMenu;
    private WinScreen winScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        office = new Texture("Anh/Office.png");

        ButtonRed = new Texture("Anh/ButtonRed.png");
        ButtonGreen = new Texture("Anh/ButtonGreen.png");
        MonitorButton = new Texture("Anh/MonitorButton.png");
        windowSystem = new WindowFlash();

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);

        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, uiCamera);

        camera.position.set(OFFICE_WIDTH / 2f, VIEWPORT_HEIGHT / 2f, 0);
        targetCameraX = camera.position.x;

        nightTimer = new NightTimer();
        theDoor = new DoorClosing();
        theCamera = new CameraGoingUp();
        cameraSystem = new MonitorWatching();
        theFan = new FanMoving();

        candy = new WitheredCandy();
        astro = new WitheredAstro();
        cat = new WitheredCat();
        chester = new WitheredChester();

        bgm = Gdx.audio.newMusic(Gdx.files.internal("Nhac/Ambient.mp3"));
        menuTheme = Gdx.audio.newMusic(Gdx.files.internal("Nhac/MenuTheme.mp3"));
        door = Gdx.audio.newSound(Gdx.files.internal("Nhac/OpenCloseDoor.mp3"));
        cam = Gdx.audio.newSound(Gdx.files.internal("Nhac/OpenCloseCamera.mp3"));

        menuTheme.setLooping(true);
        menuTheme.setVolume(0.6f);
        menuTheme.play();

        bgm.setLooping(true);
        bgm.setVolume(0.5f);

        mainMenu = new MainMenu();
        gameOverScreen = new GameOver(mainMenu.getFont());
        winScreen = new WinScreen();

        chester.setWindowFlash(windowSystem);
    }

    private void handleCameraMovement(float delta) {
        if (isMonitorOpen && theCamera.isFullyUp()) return;

        float mouseX = Gdx.input.getX();
        float screenWidth = Gdx.graphics.getWidth();

        float threshold = 0.2f;
        if (mouseX < screenWidth * threshold) {
            targetCameraX -= PAN_SPEED * delta;
        } else if (mouseX > screenWidth * (1 - threshold)) {
            targetCameraX += PAN_SPEED * delta;
        }

        float minX = VIEWPORT_WIDTH / 2f;
        float maxX = OFFICE_WIDTH - (VIEWPORT_WIDTH / 2f);
        targetCameraX = MathUtils.clamp(targetCameraX, minX, maxX);

        camera.position.x = MathUtils.lerp(camera.position.x, targetCameraX, LERP_FACTOR * delta);
        camera.update();
    }

    private void resetGame() {
        isMonitorOpen = false;
        monitorCD = 0;
        cuaMo = true;
        amThanhDongCua = true;
        targetCameraX = OFFICE_WIDTH / 2f;
        camera.position.x = targetCameraX;

        if (candy != null) {
            candy.stopAudio();
        }

        candy = new WitheredCandy();
        astro = new WitheredAstro();
        cat = new WitheredCat();
        chester = new WitheredChester();
        nightTimer = new NightTimer();
        theDoor = new DoorClosing();
        theCamera = new CameraGoingUp();

        chester.setWindowFlash(windowSystem);
        mainMenu.resetStartRequest();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        mainMenu.update(uiViewport);

        // =================Logic=================
        if (gameState == State.MENU) {
            if (mainMenu.isFading() && menuTheme.isPlaying()) {
                menuTheme.stop();
            }
            if (mainMenu.isStartRequested()) {
                chester.AI_Level = mainMenu.getChesterAI();
                cat.AI_Level = mainMenu.getCatAI();
                candy.AI_Level = mainMenu.getCandyAI();
                astro.AI_Level = mainMenu.getAstroAI();

                bgm.play();
                gameState = State.PLAYING;
            }
        } else if (gameState == State.PLAYING) {

            nightTimer.render(delta);

            if (nightTimer.is6AM()) {
                gameState = State.WIN;
                bgm.stop();
                candy.stopAudio();
                return;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
                resetGame();
                bgm.stop();
                candy.stopAudio();
                menuTheme.play();
                gameState = State.MENU;
                return;
            }

            theFan.update(delta);
            handleCameraMovement(delta);
            if (monitorCD > 0) monitorCD -= delta;

            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPoint);

            // Door
            boolean isTouchingDoorButton = (touchPoint.x >= 600 && touchPoint.x <= 700 &&
                touchPoint.y >= 600 && touchPoint.y <= 750);
            boolean isHoldingDoor = Gdx.input.isButtonPressed(Input.Buttons.LEFT) && isTouchingDoorButton;
            cuaMo = !isHoldingDoor;

            if (cuaMo != amThanhDongCua) {
                door.play(0.6f);
                amThanhDongCua = cuaMo;
            }
            theDoor.update(delta, isHoldingDoor);

            // Window and Animatronics
            windowSystem.update(delta, isMonitorOpen && theCamera.isFullyUp());
            cat.setHoldingDoor(isHoldingDoor);

            candy.update(delta, cameraSystem.getCurrentCam(), isMonitorOpen && theCamera.isFullyUp());
            cat.update(delta, cameraSystem.getCurrentCam(), isMonitorOpen && theCamera.isFullyUp());
            chester.update(delta, cameraSystem.getCurrentCam(), isMonitorOpen && theCamera.isFullyUp());
            astro.update(delta, isMonitorOpen);

            // Blackout
            if (candy.stateChanged) {
                cameraSystem.triggerBlackout(isMonitorOpen && theCamera.isFullyUp());
                candy.stateChanged = false;
            }
            if (cat.stateChanged) {
                if (cat.getCurrentCamera() == cameraSystem.getCurrentCam()) {
                    cameraSystem.triggerBlackout(isMonitorOpen && theCamera.isFullyUp());
                }
                cat.stateChanged = false;
            }

            // Monitor
            boolean wasMonitorOpenBefore = isMonitorOpen;
            boolean isOverMonitor = (touchPoint.x >= camera.position.x - 625 &&
                touchPoint.x <= camera.position.x + 625 &&
                touchPoint.y >= 100 && touchPoint.y <= 175);

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && isOverMonitor && monitorCD <= 0) {
                isMonitorOpen = !isMonitorOpen;
                monitorCD = 0.5f;
                cam.play(0.7f);
                if (wasMonitorOpenBefore && !isMonitorOpen) {
                    astro.trySpawn();
                }
            }
            theCamera.update(delta, isMonitorOpen);

            // Jumpscare
            if (candy.isJumpscareReady() || astro.isJumpscareReady() || cat.isJumpscareReady() || chester.isJumpscareReady()) {
                bgm.stop();
                candy.stopAudio();

                if (candy.isJumpscareReady()) gameOverScreen.setKiller("Candy");
                else if (astro.isJumpscareReady()) gameOverScreen.setKiller("Astro");
                else if (cat.isJumpscareReady()) gameOverScreen.setKiller("Cat");
                else if (chester.isJumpscareReady()) gameOverScreen.setKiller("Chester");

                gameState = State.GAME_OVER;
            }

        } else if (gameState == State.GAME_OVER) {
            if (gameOverScreen.update(uiViewport)) {
                resetGame();
                menuTheme.play();
                gameState = State.MENU;
            }
        } else if (gameState == State.WIN) {
            if (winScreen.update(delta)) {
                Gdx.app.exit();
            }
        }

        // =================Draw=================
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameState == State.MENU) {
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();
            mainMenu.draw(batch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
            batch.end();
        } else if (gameState == State.GAME_OVER) {
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();
            gameOverScreen.draw(batch, mainMenu.getCurrentStaticFrameTexture(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
            batch.end();
        } else if (gameState == State.WIN) {
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();
            winScreen.draw(batch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
            batch.end();
        } else {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            float screenXOffset = camera.position.x - VIEWPORT_WIDTH / 2;

            if (isMonitorOpen && theCamera.isFullyUp()) {
                Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                viewport.unproject(touchPoint);

                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    cameraSystem.updateInput(touchPoint.x, touchPoint.y, screenXOffset);
                }

                cameraSystem.drawBackground(batch, screenXOffset);

                if (cat.isActive() && cameraSystem.getCurrentCam() == 1) {
                    batch.draw(cat.getActiveTexture(), screenXOffset + cat.getX(), cat.getY(), cat.getWidth(), cat.getHeight());
                }
                if (candy.isActive() && candy.getCurrentCamera() == cameraSystem.getCurrentCam()) {
                    batch.draw(candy.getActiveTexture(), screenXOffset + candy.getX(), candy.getY(), 2600, 1462);
                }

                cameraSystem.drawForeground(batch, screenXOffset);
                bgm.setVolume(0.25f);
            } else {
                batch.draw(office, 0, 0, OFFICE_WIDTH, VIEWPORT_HEIGHT);
                theFan.draw(batch, 750, 495, 420, 340);

                chester.draw(batch, 1138, 600, 713, 570);
                windowSystem.draw(batch, 1138, 600, 713, 570);

                theDoor.draw(batch, -90, 50, 700, 1400);
                astro.draw(batch);

                bgm.setVolume(0.5f);

                if (cuaMo) batch.draw(ButtonRed, 600, 600, 100, 150);
                else batch.draw(ButtonGreen, 600, 600, 100, 150);

                if (!theCamera.isDown()) {
                    theCamera.draw(batch, screenXOffset, 0, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
                }
            }

            batch.draw(MonitorButton, camera.position.x - 625, 100, 1250, 75);
            batch.end();

            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();
            nightTimer.draw(batch, (int) (VIEWPORT_WIDTH - 330), (int) (VIEWPORT_HEIGHT - 100));
            batch.end();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        uiViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        office.dispose();
        door.dispose();
        cam.dispose();
        nightTimer.dispose();
        theDoor.dispose();
        theCamera.dispose();
        cameraSystem.dispose();
        candy.dispose();
        cat.dispose();
        chester.dispose();
        astro.dispose();
        gameOverScreen.dispose();
        winScreen.dispose();
        if (menuTheme != null) menuTheme.dispose();
        if (bgm != null) bgm.dispose();
    }
}

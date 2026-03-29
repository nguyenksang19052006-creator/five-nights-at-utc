package vn.utc.fnaf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private OrthographicCamera camera;

    // Xoay Camera
    private final float OFFICE_WIDTH = 3000f;
    private final float VIEWPORT_WIDTH = 2600f;
    private final float VIEWPORT_HEIGHT = 1462f;
    private final float PAN_SPEED = 400f;
    private final float LERP_FACTOR = 20f;
    private float targetCameraX;

    // Random sh idk man
    private Music bgm;
    private Texture ButtonRed, ButtonGreen, MonitorButton;
    private Sound door, cam;
    private boolean cuaMo = true, amThanhDongCua = true;
    private boolean isMonitorOpen = false;
    private float monitorCD = 0;

    // Các class khác
    private MonitorWatching cameraSystem;
    private BitmapFont font;
    private NightTimer nightTimer;
    private DoorClosing theDoor;
    private CameraGoingUp theCamera;
    private FanMoving theFan;

    @Override
    public void create() {
        batch = new SpriteBatch();
        office = new Texture("Anh/Office.png");

        ButtonRed = new Texture("Anh/ButtonRed.png");
        ButtonGreen = new Texture("Anh/ButtonGreen.png");
        MonitorButton = new Texture("Anh/MonitorButton.png");

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);

        camera.position.set(OFFICE_WIDTH / 2f, VIEWPORT_HEIGHT / 2f, 0);
        targetCameraX = camera.position.x;

        nightTimer = new NightTimer();
        theDoor = new DoorClosing();
        theCamera = new CameraGoingUp();
        cameraSystem = new MonitorWatching();
        theFan = new FanMoving();

        bgm = Gdx.audio.newMusic(Gdx.files.internal("Nhac/Ambient.mp3"));
        door = Gdx.audio.newSound(Gdx.files.internal("Nhac/OpenCloseDoor.mp3"));
        cam = Gdx.audio.newSound(Gdx.files.internal("Nhac/OpenCloseCamera.mp3"));

        bgm.setLooping(true);
        bgm.setVolume(0.5f);
        bgm.play();

        font = new BitmapFont();
        font.getData().setScale(5.0f);
    }

    private void handleCameraMovement(float delta) {
        if (isMonitorOpen) return;

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

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        nightTimer.render(delta);
        theFan.update(delta);

        handleCameraMovement(delta);

        if (monitorCD > 0) monitorCD -= delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPoint);

        boolean isTouchingDoorButton = (touchPoint.x >= 600 && touchPoint.x <= 700 &&
            touchPoint.y >= 600 && touchPoint.y <= 750);
        boolean isHoldingDoor = Gdx.input.isButtonPressed(Input.Buttons.LEFT) && isTouchingDoorButton;
        cuaMo = !isHoldingDoor;

        if (cuaMo != amThanhDongCua) {
            door.play();
            amThanhDongCua = cuaMo;
        }

        theDoor.update(delta, isHoldingDoor);

        boolean isOverMonitor = (touchPoint.x >= camera.position.x - 625 && touchPoint.x <= camera.position.x + 625 &&
            touchPoint.y >= 100 && touchPoint.y <= 175);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && isOverMonitor && monitorCD <= 0) {
            isMonitorOpen = !isMonitorOpen;
            monitorCD = 0.5f;
            cam.play();
        }

        theCamera.update(delta, isMonitorOpen);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (isMonitorOpen && theCamera.isFullyUp()) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                cameraSystem.updateInput(touchPoint.x, touchPoint.y, camera.position.x - VIEWPORT_WIDTH / 2);
            }
            cameraSystem.draw(batch, camera.position.x - VIEWPORT_WIDTH/2, 0, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
            bgm.setVolume(0.2f);
        } else {
            batch.draw(office, 0, 0, OFFICE_WIDTH, VIEWPORT_HEIGHT);
            theFan.draw(batch, 750, 495, 420, 340);
            theDoor.draw(batch, -90, 50, 700, 1400);
            bgm.setVolume(0.5f);

            if (cuaMo) batch.draw(ButtonRed, 600, 600, 100, 150);
            else batch.draw(ButtonGreen, 600, 600, 100, 150);

            if (!theCamera.isDown()) {
                theCamera.draw(batch, camera.position.x - VIEWPORT_WIDTH/2, 0, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
            }
        }

        font.draw(batch, nightTimer.getTimeString(), camera.position.x + 1100, 1400);
        batch.draw(MonitorButton, camera.position.x - 625, 100, 1250, 75);

        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void dispose() {
        batch.dispose();
        office.dispose();
        door.dispose();
        cam.dispose();
        font.dispose();
        theDoor.dispose();
        theCamera.dispose();
        cameraSystem.dispose();
        if (bgm != null) bgm.dispose();
    }
}

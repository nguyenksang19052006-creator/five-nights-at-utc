package vn.utc.fnaf;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

public class FNaFGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture office;
    private Viewport viewport;
    private Camera camera;
    private Music bgm;
    private Texture ButtonRed, ButtonGreen, MonitorButton;
    private Sound door, cam;
    private boolean cuaMo = true, amThanhDongCua = true;
    private boolean isMonitorOpen = false;
    private float monitorCD = 0;
    private MonitorWatching cameraSystem;
    private BitmapFont font;
    private NightTimer nightTimer;
    private DoorClosing theDoor;
    private CameraGoingUp theCamera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        office = new Texture("Anh/Office.png");
        ButtonRed = new Texture("Anh/ButtonRed.png");
        ButtonGreen = new Texture("Anh/ButtonGreen.png");
        MonitorButton = new Texture("Anh/MonitorButton.png");
        camera = new OrthographicCamera();
        viewport = new FitViewport(2600, 1462, camera);
        nightTimer = new NightTimer();
        theDoor = new DoorClosing();
        theCamera = new CameraGoingUp();
        cameraSystem = new MonitorWatching();

        bgm = Gdx.audio.newMusic(Gdx.files.internal("Nhac/Ambient.mp3"));
        door = Gdx.audio.newSound(Gdx.files.internal("Nhac/OpenCloseDoor.mp3"));
        cam = Gdx.audio.newSound(Gdx.files.internal("Nhac/OpenCloseCamera.mp3"));

        bgm.setLooping(true);
        bgm.setVolume(0.5f);
        bgm.play();

        font = new BitmapFont();
        font.getData().setScale(5.0f);
    }

    @Override
    public void render() {
            float delta = Gdx.graphics.getDeltaTime();
            nightTimer.render(delta);

            if (monitorCD > 0) {
                monitorCD -= delta;
            }

            batch.setProjectionMatrix(camera.combined);
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

            boolean isOverMonitor = (touchPoint.x >= 680 && touchPoint.x <= 1930 &&
                touchPoint.y >= 100 && touchPoint.y <= 175);

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && isOverMonitor && monitorCD <= 0) {
                isMonitorOpen = !isMonitorOpen;
                monitorCD = 0.5f;
                cam.play();
            }

            theCamera.update(delta, isMonitorOpen);

            viewport.apply();
            batch.begin();

            if (isMonitorOpen && theCamera.isFullyUp()) {
                cameraSystem.draw(batch, 0, 0, 2600, 1462);
            } else {
                batch.draw(office, 0, 0, 2600, 1462);
                theDoor.draw(batch, 95, -50, 530, 1500);
                    if (cuaMo) {
                        batch.draw(ButtonRed, 600, 600, 100, 150);
                    } else {
                        batch.draw(ButtonGreen, 600, 600, 100, 150);
                    }

                if (!theCamera.isDown()) {
                    theCamera.draw(batch, 0, 0, 2600, 1462);
                }
            }

            font.draw(batch, nightTimer.getTimeString(), 2400, 1400);
            batch.draw(MonitorButton, 680, 100, 1250, 75);
            batch.end();

            if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            }
        }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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

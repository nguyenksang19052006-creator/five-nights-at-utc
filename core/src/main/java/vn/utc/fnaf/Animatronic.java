package vn.utc.fnaf;

import com.badlogic.gdx.audio.Sound;

public abstract class Animatronic {
    public int AI_Level = 0;
    protected String name;
    protected int currentCamera = -1; // -1 = khong hoat dong
    protected boolean isActive = false;
    protected boolean gotYou = false;

    protected float movementTimer = 0;
    protected float attackTimer = 0;
    protected float repelTimer = 0;

    protected Sound presenceSound;

    public Animatronic(String name) {
        this.name = name;
    }

    public abstract void update(float delta, int playerViewCamera, boolean isMonitorUp);
    public abstract void dispose();

    // Getters
    public int getCurrentCamera() { return currentCamera; }
    public boolean isJumpscareReady() { return gotYou; }
    public boolean isActive() { return isActive; }
}

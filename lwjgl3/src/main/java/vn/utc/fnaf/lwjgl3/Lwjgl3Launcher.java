package vn.utc.fnaf.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import vn.utc.fnaf.FNaFGame;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new FNaFGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration =
            new Lwjgl3ApplicationConfiguration();

        configuration.setTitle("UTC's Custom Night");
        configuration.setWindowIcon("Anh/LogoGame.png");

        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        configuration.useVsync(true);
        configuration.setForegroundFPS(60);

        return configuration;
    }
}

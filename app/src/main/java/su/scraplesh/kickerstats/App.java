package su.scraplesh.kickerstats;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "5jQx2snnHDOgfGcUQ4fxD5TIvHfAiXONd1AOEQxG", "vxLNFgVrurDRfev4dzTr6RQqWP0F9mhNmkgsI7eM");
    }
}

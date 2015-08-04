package su.scraplesh.kickerstats;

import android.app.Activity;
import android.os.Bundle;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends Activity implements
        StartGameFragment.OnStartGameListener,
        FieldFragment.OnSelectGameRoleListener,
        PlayerListFragment.OnSelectPlayerListener {

    private String activeFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activeFragmentTag = StartGameFragment.TAG;
        getFragmentManager().beginTransaction()
                .add(R.id.container, new StartGameFragment(), activeFragmentTag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStartGame() {
        activeFragmentTag = FieldFragment.TAG;
        getFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left
                )
                .replace(R.id.container, new FieldFragment(), activeFragmentTag)
                .commit();
    }

    @Override
    public void onSelectGameRole(FieldFragment.GameRole gameRole) {
        FieldFragment fieldFragment = (FieldFragment) getFragmentManager().findFragmentByTag(FieldFragment.TAG);
        if (!fieldFragment.allRolesSet()) {
            activeFragmentTag = PlayerListFragment.TAG;

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.animator.slide_in_left,
                            R.animator.slide_out_right,
                            R.animator.slide_in_right,
                            R.animator.slide_out_left
                    )
                    .replace(R.id.container, new PlayerListFragment(), activeFragmentTag)
                    .commit();

            ParseQuery.getQuery("Player").findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> players, ParseException e) {
                    final PlayerListFragment playerListFragment = (PlayerListFragment) getFragmentManager().findFragmentByTag(PlayerListFragment.TAG);
                    playerListFragment.updatePlayers(players);
                }
            });

        }
    }

    @Override
    public void onSelectPlayer(ParseObject player) {

    }
}

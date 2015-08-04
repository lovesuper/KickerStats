package su.scraplesh.kickerstats;

import android.app.Activity;
import android.os.Bundle;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends Activity implements
        StartGameFragment.OnStartGameListener,
        FieldFragment.OnSelectGameRoleListener,
        PlayerListFragment.OnSelectPlayerListener {

    private ParseObject activeGame;
    private ParseObject redGoalkeeper;
    private ParseObject redForward;
    private ParseObject blueForward;
    private ParseObject blueGoalkeeper;

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
        final ParseObject newGame = new ParseObject("Game");
        newGame.put("active", true);
        newGame.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                activeGame = newGame;

                activeFragmentTag = FieldFragment.TAG;
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.animator.slide_in_left,
                                R.animator.slide_out_right,
                                R.animator.slide_in_right,
                                R.animator.slide_out_left
                        )
                        .replace(R.id.container, new FieldFragment(), activeFragmentTag)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onSelectGameRole(FieldFragment.GameRole gameRole) {
        activeFragmentTag = PlayerListFragment.TAG;

        getFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left
                )
                .replace(R.id.container, PlayerListFragment.newInstance(gameRole), activeFragmentTag)
                .addToBackStack(null)
                .commit();

        ParseQuery.getQuery("Player").findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> players, ParseException e) {
                final PlayerListFragment playerListFragment = (PlayerListFragment) getFragmentManager().findFragmentByTag(PlayerListFragment.TAG);
                playerListFragment.updatePlayers(players);
            }
        });
    }

    @Override
    public void onSelectPlayer(ParseObject player, FieldFragment.GameRole gameRole) {
        final FieldFragment fragment = (FieldFragment) getFragmentManager().findFragmentByTag(FieldFragment.TAG);

        ParseObject newRole = new ParseObject("GameRole");
        newRole.put("game", activeGame);
        newRole.put("player", player);

        switch (gameRole) {
            case RedGoalkeeper: {
                newRole.put("team", ParseObject.createWithoutData("Team", "aAKlFkJ3EL"));
                newRole.put("role", ParseObject.createWithoutData("Role", "3tMcNNXdyU"));
                redGoalkeeper = player;
                fragment.setRedGoalkeeper(player);
                break;
            }
            case RedForward: {
                newRole.put("team", ParseObject.createWithoutData("Team", "aAKlFkJ3EL"));
                newRole.put("role", ParseObject.createWithoutData("Role", "7unJgGmyAW"));
                redForward = player;
                fragment.setRedForward(player);
                break;
            }
            case BlueForward: {
                newRole.put("team", ParseObject.createWithoutData("Team", "bV5hMUx9Ge"));
                newRole.put("role", ParseObject.createWithoutData("Role", "7unJgGmyAW"));
                blueForward = player;
                fragment.setBlueForward(player);
                break;
            }
            case BlueGoalkeeper: {
                newRole.put("team", ParseObject.createWithoutData("Team", "bV5hMUx9Ge"));
                newRole.put("role", ParseObject.createWithoutData("Role", "3tMcNNXdyU"));
                blueGoalkeeper = player;
                fragment.setBlueGoalkeeper(player);
                break;
            }
        }

        getFragmentManager().popBackStack();
    }
}

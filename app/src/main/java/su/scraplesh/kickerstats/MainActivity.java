package su.scraplesh.kickerstats;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements
        FieldFragment.OnSelectGameRoleListener,
        PlayerListFragment.OnSelectPlayerListener,
        GoalFragment.OnGoalListener {

    @Bind(R.id.container) FrameLayout container;

    private ParseObject activeGame;
    private ParseObject redGoalkeeper;
    private String redGoalkeeperName;
    private ParseObject redForward;
    private String redForwardName;
    private ParseObject blueForward;
    private String blueForwardName;
    private ParseObject blueGoalkeeper;
    private String blueGoalkeeperName;
    private String activeFragmentTag;
    private int redGoals = 0;
    private int blueGoals = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        resetGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_start_game).setVisible(activeGame == null);
        menu.findItem(R.id.action_reset_game).setVisible(activeGame != null);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_start_game: {
                return onStartGame();
            }
            case R.id.action_reset_game: {
                return resetGame();
            }
            case android.R.id.home: {
                setResult(RESULT_OK);
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private boolean resetGame() {
        if (activeGame != null) {
            activeGame.put("active", false);
            activeGame.saveInBackground();
        }

        activeGame = null;
        redGoalkeeper = null;
        redGoalkeeperName = null;
        redForward = null;
        redForwardName = null;
        blueForward = null;
        blueForwardName = null;
        blueGoalkeeper = null;
        blueGoalkeeperName = null;

        redGoals = 0;
        blueGoals = 0;

        updateBar();

        container.removeAllViews();
        View.inflate(this, R.layout.nonactive_game, container);

        return true;
    }

    private boolean onStartGame() {
        final ParseObject newGame = new ParseObject("Game");
        newGame.put("active", true);
        newGame.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                activeGame = newGame;

                container.removeAllViews();

                activeFragmentTag = FieldFragment.TAG;
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new FieldFragment(), activeFragmentTag)
                        .addToBackStack(null)
                        .commit();

                updateBar();
            }
        });

        return true;
    }

    private void updateBar() {
        final ActionBar bar = getActionBar();
        if (bar != null && activeFragmentTag != null) {
            switch (activeFragmentTag) {
                case FieldFragment.TAG: {
                    if (activeGame == null) {
                        bar.setTitle("Начните игру");
                    } else {
                        bar.setTitle(isPlayersSet() ? "Кто забил?" : "Установите игроков...");
                    }
                    break;
                }
                case PlayerListFragment.TAG: {
                    bar.setTitle("Выберите игрока...");
                    break;
                }
                case GoalFragment.TAG: {
                    bar.setTitle("Кому забили?");
                    break;
                }
            }
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onSelectGameRole(Team team, Role role) {
        if (isPlayersSet()) {
            activeFragmentTag = GoalFragment.TAG;

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.animator.slide_in_left,
                            R.animator.slide_out_right,
                            R.animator.slide_in_right,
                            R.animator.slide_out_left
                    )
                    .replace(R.id.container, GoalFragment.newInstance(team, role), activeFragmentTag)
                    .addToBackStack(null)
                    .commit();
        } else {
            activeFragmentTag = PlayerListFragment.TAG;

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.animator.slide_in_left,
                            R.animator.slide_out_right,
                            R.animator.slide_in_right,
                            R.animator.slide_out_left
                    )
                    .replace(R.id.container, PlayerListFragment.newInstance(team, role), activeFragmentTag)
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

        updateBar();
    }

    @Override
    public void onSelectPlayer(ParseObject player, Team team, Role role) {
        ParseObject newRole = new ParseObject("GameRole");
        newRole.put("game", activeGame);
        newRole.put("player", player);

        switch (team) {
            case Red: {
                newRole.put("team", ParseObject.createWithoutData("Team", "aAKlFkJ3EL"));

                switch (role) {
                    case Goalkeeper: {
                        newRole.put("role", ParseObject.createWithoutData("Role", "3tMcNNXdyU"));

                        redGoalkeeper = newRole;
                        redGoalkeeperName = player.getString("name");
                        break;
                    }
                    case Forward: {
                        newRole.put("role", ParseObject.createWithoutData("Role", "7unJgGmyAW"));

                        redForward = newRole;
                        redForwardName = player.getString("name");
                        break;
                    }
                }
                break;
            }
            case Blue: {
                newRole.put("team", ParseObject.createWithoutData("Team", "bV5hMUx9Ge"));

                switch (role) {
                    case Goalkeeper: {
                        newRole.put("role", ParseObject.createWithoutData("Role", "3tMcNNXdyU"));

                        blueGoalkeeper = newRole;
                        blueGoalkeeperName = player.getString("name");
                        break;
                    }
                    case Forward: {
                        newRole.put("role", ParseObject.createWithoutData("Role", "7unJgGmyAW"));

                        blueForward = newRole;
                        blueForwardName = player.getString("name");
                        break;
                    }
                }
                break;
            }
        }

        newRole.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                activeFragmentTag = FieldFragment.TAG;

                getFragmentManager().popBackStack();
                updateBar();
            }
        });

    }

    @Override
    public void onGoal(Team whoTeam, Role who, Team whereTeam) {
        ParseObject newGoal = new ParseObject("Goal");

        switch (whoTeam) {
            case Red: {
                switch (who) {
                    case Goalkeeper: {
                        newGoal.put("who", redGoalkeeper);

                        break;
                    }
                    case Forward: {
                        newGoal.put("who", redForward);

                        break;
                    }
                }
                break;
            }
            case Blue: {
                switch (who) {
                    case Goalkeeper: {
                        newGoal.put("who", blueGoalkeeper);

                        break;
                    }
                    case Forward: {
                        newGoal.put("who", blueForward);

                        break;
                    }
                }
                break;
            }
        }

        switch (whereTeam) {
            case Red: {
                blueGoals += 1;
                newGoal.put("where", ParseObject.createWithoutData("Team", "aAKlFkJ3EL"));
                break;
            }
            case Blue: {
                redGoals += 1;
                newGoal.put("where", ParseObject.createWithoutData("Team", "bV5hMUx9Ge"));
                break;
            }
        }

        newGoal.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (redGoals == 10 || blueGoals == 10) {
                    resetGame();
                } else {
                    getFragmentManager().popBackStack();
                    activeFragmentTag = FieldFragment.TAG;
                    updateBar();
                }
            }
        });
    }

    @Override
    public String getPlayerName(Team team, Role role) {
        String name = getString(R.string.choose_player);

        switch (team) {
            case Red: {
                switch (role) {
                    case Goalkeeper: {
                        if (redGoalkeeperName != null) {
                            name = redGoalkeeperName;
                        }
                        break;
                    }
                    case Forward: {
                        if (redForwardName != null) {
                            name = redForwardName;
                        }
                        break;
                    }
                }
                break;
            }
            case Blue: {
                switch (role) {
                    case Goalkeeper: {
                        if (blueGoalkeeperName != null) {
                            name = blueGoalkeeperName;
                        }
                        break;
                    }
                    case Forward: {
                        if (blueForwardName != null) {
                            name = blueForwardName;
                        }
                        break;
                    }
                }
                break;
            }
        }

        return name;
    }

    @Override
    public boolean isPlayersSet() {
        return redGoalkeeper != null && redForward != null && blueForward != null && blueGoalkeeper != null;
    }

    @Override
    public int getRedGoals() {
        return redGoals;
    }

    @Override
    public int getBlueGoals() {
        return blueGoals;
    }

    public enum Team {
        Red,
        Blue
    }

    public enum Role {
        Goalkeeper,
        Forward,
    }
}

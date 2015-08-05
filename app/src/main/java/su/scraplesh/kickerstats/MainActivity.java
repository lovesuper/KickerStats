package su.scraplesh.kickerstats;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends Activity implements
        NoGameFragment.OnStartGameListener,
        FieldFragment.OnSelectGameRoleListener,
        PlayerListFragment.OnSelectPlayerListener,
        GoalFragment.OnGoalListener {

    public static final String PLAYERS_LIST_LABEL = "playersList";

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
        onGameEnded();
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
        menu.findItem(R.id.action_end_game).setVisible(isPlayersSet());
        menu.findItem(R.id.action_reset_game).setVisible(activeGame != null);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_end_game: {
                return onGameEnded();
            }
            case R.id.action_reset_game: {
                return resetGame();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private boolean onGameEnded() {
        activeFragmentTag = NoGameFragment.TAG;
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new NoGameFragment(), activeFragmentTag)
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left
                )
                .addToBackStack(null)
                .commit();

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

        return true;
    }

    private boolean resetGame() {
        if (activeGame != null) {
            activeGame.put("isDeleted", true);
            activeGame.saveEventually();
        }
        onGameEnded();

        return true;
    }

    @Override
    public void onStartGame() {
        activeGame = new ParseObject("Game");
        activeGame.put("isDeleted", false);
        activeGame.saveEventually();

        activeFragmentTag = FieldFragment.TAG;
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new FieldFragment(), activeFragmentTag)
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left
                )
                .addToBackStack(null)
                .commit();

        updateBar();
    }

    private void updateBar() {
        final ActionBar bar = getActionBar();
        if (bar != null && activeFragmentTag != null) {
            switch (activeFragmentTag) {
                case FieldFragment.TAG: {
                    bar.setTitle(isPlayersSet() ? "Кто забил?" : "Установите игроков...");
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
                default: {
                    bar.setTitle(getString(R.string.app_name));
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


            ParseQuery.getQuery("Player")
                    .fromLocalDatastore()
                    .orderByAscending("name")
                    .findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(final List<ParseObject> players, ParseException e) {
                            if (players.isEmpty()) {
                                ParseQuery.getQuery("Player")
                                        .orderByAscending("name")
                                        .findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(final List<ParseObject> players, ParseException e) {
                                                // Release any objects previously pinned for this query.
                                                ParseObject.unpinAllInBackground(PLAYERS_LIST_LABEL, players, new DeleteCallback() {
                                                    public void done(ParseException e) {
                                                        if (e != null) {
                                                            // There was some error.
                                                            return;
                                                        }

                                                        // Add the latest results for this query to the cache.
                                                        ParseObject.pinAllInBackground(PLAYERS_LIST_LABEL, players);
                                                        updatePlayers(players);
                                                    }
                                                });
                                            }
                                        });
                            } else {
                                updatePlayers(players);
                            }
                        }
                    });
        }

        updateBar();
    }

    private void updatePlayers(final List<ParseObject> players) {
        final PlayerListFragment playerListFragment = (
                (PlayerListFragment) getFragmentManager().findFragmentByTag(
                        PlayerListFragment.TAG
                )
        );
        playerListFragment.updatePlayers(players);
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

        newRole.saveEventually();

        activeFragmentTag = FieldFragment.TAG;
        getFragmentManager().popBackStack();
        updateBar();
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

        newGoal.saveEventually();

        if (redGoals == 10 || blueGoals == 10) {
            onGameEnded();
        } else {
            getFragmentManager().popBackStack();
            activeFragmentTag = FieldFragment.TAG;
            updateBar();
        }
    }

    @Override
    public String getPlayerName(Team team, Role role) {
        String name = getString(R.string.choose_player);

        switch (team) {
            case Red: {
                switch (role) {
                    case Goalkeeper: {
                        name = redGoalkeeperName != null ? redGoalkeeperName : getString(R.string.choose_goalkeeper);
                        break;
                    }
                    case Forward: {
                        name = redForwardName != null ? redForwardName : getString(R.string.choose_forward);
                        break;
                    }
                }
                break;
            }
            case Blue: {
                switch (role) {
                    case Goalkeeper: {
                        name = blueGoalkeeperName != null ? blueGoalkeeperName : getString(R.string.choose_goalkeeper);
                        break;
                    }
                    case Forward: {
                        name = blueForwardName != null ? blueForwardName : getString(R.string.choose_forward);
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

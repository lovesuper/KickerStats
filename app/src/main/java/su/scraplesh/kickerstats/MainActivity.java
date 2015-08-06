package su.scraplesh.kickerstats;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends Activity implements
        NoGameFragment.OnStartGameListener,
        FieldFragment.OnSelectGameRoleListener,
        FragmentManager.OnBackStackChangedListener {

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
    private ParseObject lastGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onGameEnded(true);

        getFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();
    }

    private void shouldDisplayHomeUp() {
        final ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(!activeFragmentTag.equals(NoGameFragment.TAG));
        }
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        switch (activeFragmentTag) {
            case FieldFragment.TAG: {
                resetGame();
                break;
            }
            case NoGameFragment.TAG: {
                break;
            }
            default: {
                activeFragmentTag = FieldFragment.TAG;
                updateBar();
                super.onBackPressed();
            }
        }
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
        menu.findItem(R.id.action_end_game).setVisible(arePlayersSet());
        menu.findItem(R.id.action_undo_goal).setVisible(lastGoal != null);
        menu.findItem(R.id.action_reset_game).setVisible(activeGame != null);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_end_game: {
                return onGameEnded(false);
            }
            case R.id.action_undo_goal: {
                if (lastGoal != null) {
                    lastGoal.getParseObject("where").fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (parseObject.getObjectId().equals("aAKlFkJ3EL")) {
                                blueGoals -= 1;
                            } else {
                                redGoals -= 1;
                            }

                            final FieldFragment fragment = (FieldFragment) getFragmentManager().findFragmentByTag(FieldFragment.TAG);
                            fragment.updateView();
                        }
                    });

                    lastGoal.put("is_deleted", true);
                    lastGoal.saveEventually();
                    lastGoal = null;
                }
                return true;
            }
            case R.id.action_reset_game: {
                return resetGame();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private boolean onGameEnded(boolean isReseted) {
        activeFragmentTag = NoGameFragment.TAG;
        final NoGameFragment fragment = isReseted ? new NoGameFragment() : NoGameFragment.gameEnded(redGoals, blueGoals);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, activeFragmentTag)
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

        lastGoal = null;

        redGoals = 0;
        blueGoals = 0;

        updateBar();

        return true;
    }

    private boolean resetGame() {
        new AlertDialog.Builder(this)
                .setTitle("Сброс игры")
                .setMessage("Вы уверены, что хотите сбросить игру?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (activeGame != null) {
                            activeGame.put("isDeleted", true);
                            activeGame.saveEventually();
                        }
                        onGameEnded(true);
                    }
                })
                .setNegativeButton("Нет", null)
                .create()
                .show();

        return true;
    }

    @Override
    public void onStartGame() {
        activeGame = new ParseObject("Game");
        activeGame.put("isDeleted", false);
        activeGame.saveEventually();

        lastGoal = null;

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
                    bar.setTitle(arePlayersSet() ? "Кто забил?" : "Установите игроков...");
                    break;
                }
                case PlayerListFragment.TAG: {
                    bar.setTitle("Выберите игрока...");
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

    private void onSelectPlayer(ParseObject player, Team team, Role role) {
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
    public void onSelectGameRole(final Team team, final Role role) {
        activeFragmentTag = PlayerListFragment.TAG;

        final PlayerListFragment fragment = new PlayerListFragment();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left
                )
                .replace(R.id.container, fragment, activeFragmentTag)
                .addToBackStack(null)
                .commit();

        fragment.setOnSelectPlayerListener(new PlayerListFragment.OnSelectPlayerListener() {
            @Override
            public void onSelectPlayer(ParseObject player) {
                MainActivity.this.onSelectPlayer(player, team, role);
            }
        });

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
                                                    fragment.updatePlayers(players);
                                                }
                                            });
                                        }
                                    });
                        } else {
                            fragment.updatePlayers(players);
                        }
                    }
                });

        updateBar();
    }

    @Override
    public void onGoal(Team whoTeam, Role who, boolean isAutoGoal) {
        lastGoal = new ParseObject("Goal");
        lastGoal.put("is_deleted", false);

        switch (whoTeam) {
            case Red: {
                if (isAutoGoal) {
                    lastGoal.put("where", ParseObject.createWithoutData("Team", "aAKlFkJ3EL"));
                    blueGoals += 1;
                } else {
                    lastGoal.put("where", ParseObject.createWithoutData("Team", "bV5hMUx9Ge"));
                    redGoals += 1;
                }

                switch (who) {
                    case Goalkeeper: {
                        lastGoal.put("who", redGoalkeeper);
                        break;
                    }
                    case Forward: {
                        lastGoal.put("who", redForward);
                        break;
                    }
                }
                break;
            }
            case Blue: {
                if (isAutoGoal) {
                    lastGoal.put("where", ParseObject.createWithoutData("Team", "bV5hMUx9Ge"));
                    redGoals += 1;
                } else {
                    lastGoal.put("where", ParseObject.createWithoutData("Team", "aAKlFkJ3EL"));
                    blueGoals += 1;
                }

                switch (who) {
                    case Goalkeeper: {
                        lastGoal.put("who", blueGoalkeeper);
                        break;
                    }
                    case Forward: {
                        lastGoal.put("who", blueForward);
                        break;
                    }
                }
                break;
            }
        }

        lastGoal.saveEventually();

        if (redGoals == 10 || blueGoals == 10) {
            onGameEnded(false);
        } else {
            final FieldFragment fragment = (FieldFragment) getFragmentManager().findFragmentByTag(FieldFragment.TAG);
            fragment.updateView();
        }
    }

    @Override
    public String getPlayerName(Team team, Role role) {
        String name = null;

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
    public boolean arePlayersSet() {
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

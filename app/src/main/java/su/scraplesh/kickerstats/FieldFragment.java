package su.scraplesh.kickerstats;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FieldFragment extends Fragment {

    public static final String TAG = "field";

    private OnSelectGameRoleListener mListener;

    @Bind(R.id.button_red_goalkeeper) Button buttonRedGoalkeeper;
    @Bind(R.id.button_red_goalkeeper_autogoal) Button buttonRedGoalkeeperAutoGoal;
    @Bind(R.id.button_red_forward) Button buttonRedForward;
    @Bind(R.id.button_red_forward_autogoal) Button buttonRedForwardAutogoal;
    @Bind(R.id.button_blue_forward) Button buttonBlueForward;
    @Bind(R.id.button_blue_forward_autogoal) Button buttonBlueForwardAutoGoal;
    @Bind(R.id.button_blue_goalkeeper) Button buttonBlueGoalkeeper;
    @Bind(R.id.button_blue_goalkeeper_autogoal) Button buttonBlueGoalkeeperAutoGoal;
    @Bind(R.id.text_red_goals) TextView textRedGoals;
    @Bind(R.id.text_blue_goals) TextView textBlueGoals;

    public FieldFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_field, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectGameRoleListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    public void updateView() {
        final String redGoalkeeperName = mListener.getPlayerName(MainActivity.Team.Red, MainActivity.Role.Goalkeeper);
        if (redGoalkeeperName != null) {
            buttonRedGoalkeeper.setText(redGoalkeeperName);
            buttonRedGoalkeeperAutoGoal.setVisibility(View.VISIBLE);
        } else {
            buttonRedGoalkeeper.setText(getString(R.string.choose_goalkeeper));
            buttonRedGoalkeeperAutoGoal.setVisibility(View.GONE);
        }
        final String redForwardName = mListener.getPlayerName(MainActivity.Team.Red, MainActivity.Role.Forward);
        if (redForwardName != null) {
            buttonRedForward.setText(redForwardName);
            buttonRedForwardAutogoal.setVisibility(View.VISIBLE);
        } else {
            buttonRedForward.setText(getString(R.string.choose_forward));
            buttonRedForwardAutogoal.setVisibility(View.GONE);
        }
        final String blueForwardName = mListener.getPlayerName(MainActivity.Team.Blue, MainActivity.Role.Forward);
        if (blueForwardName != null) {
            buttonBlueForward.setText(blueForwardName);
            buttonBlueForwardAutoGoal.setVisibility(View.VISIBLE);
        } else {
            buttonBlueForward.setText(getString(R.string.choose_forward));
            buttonBlueForwardAutoGoal.setVisibility(View.GONE);
        }
        final String blueGoalkeeperName = mListener.getPlayerName(MainActivity.Team.Blue, MainActivity.Role.Goalkeeper);
        if (blueGoalkeeperName != null) {
            buttonBlueGoalkeeper.setText(blueGoalkeeperName);
            buttonBlueGoalkeeperAutoGoal.setVisibility(View.VISIBLE);
        } else {
            buttonBlueGoalkeeper.setText(getString(R.string.choose_goalkeeper));
            buttonBlueGoalkeeperAutoGoal.setVisibility(View.GONE);
        }

        if (mListener.arePlayersSet()){
            textRedGoals.setVisibility(View.VISIBLE);
            textRedGoals.setText(String.valueOf(mListener.getRedGoals()));
            textBlueGoals.setVisibility(View.VISIBLE);
            textBlueGoals.setText(String.valueOf(mListener.getBlueGoals()));
        } else {
            textRedGoals.setVisibility(View.INVISIBLE);
            textRedGoals.setText("0");
            textBlueGoals.setVisibility(View.INVISIBLE);
            textBlueGoals.setText("0");
        }
    }

    @OnClick(R.id.button_red_goalkeeper)
    public void onSelectRedGoalkeeper() {
        if (mListener.arePlayersSet()) {
            mListener.onGoal(MainActivity.Team.Red, MainActivity.Role.Goalkeeper, false);
        } else {
            mListener.onSelectGameRole(MainActivity.Team.Red, MainActivity.Role.Goalkeeper);
        }
    }

    @OnClick(R.id.button_red_goalkeeper_autogoal)
    public void onSelectRedGoalkeeperAutogoal() {
        mListener.onGoal(MainActivity.Team.Red, MainActivity.Role.Goalkeeper, true);
    }

    @OnClick(R.id.button_red_forward)
    public void onSelectRedForward() {
        if (mListener.arePlayersSet()) {
            mListener.onGoal(MainActivity.Team.Red, MainActivity.Role.Forward, false);
        } else {
            mListener.onSelectGameRole(MainActivity.Team.Red, MainActivity.Role.Forward);
        }
    }

    @OnClick(R.id.button_red_forward_autogoal)
    public void onSelectRedForwardAutogoal() {
        mListener.onGoal(MainActivity.Team.Red, MainActivity.Role.Forward, true);
    }

    @OnClick(R.id.button_blue_forward)
    public void onSelectBlueForward() {
        if (mListener.arePlayersSet()) {
            mListener.onGoal(MainActivity.Team.Blue, MainActivity.Role.Forward, false);
        } else {
            mListener.onSelectGameRole(MainActivity.Team.Blue, MainActivity.Role.Forward);
        }
    }

    @OnClick(R.id.button_blue_forward_autogoal)
    public void onSelectBlueForwardAutogoal() {
        mListener.onGoal(MainActivity.Team.Blue, MainActivity.Role.Forward, true);
    }

    @OnClick(R.id.button_blue_goalkeeper)
    public void onSelectBlueGoalkeeper() {
        if (mListener.arePlayersSet()) {
            mListener.onGoal(MainActivity.Team.Blue, MainActivity.Role.Goalkeeper, false);
        } else {
            mListener.onSelectGameRole(MainActivity.Team.Blue, MainActivity.Role.Goalkeeper);
        }
    }

    @OnClick(R.id.button_blue_goalkeeper_autogoal)
    public void onSelectBlueGoalkeeperAutogoal() {
        mListener.onGoal(MainActivity.Team.Blue, MainActivity.Role.Goalkeeper, true);
    }

    public interface OnSelectGameRoleListener {
        void onSelectGameRole(MainActivity.Team team, MainActivity.Role role);
        String getPlayerName(MainActivity.Team team, MainActivity.Role role);
        boolean arePlayersSet();
        int getRedGoals();
        int getBlueGoals();
        void onGoal(MainActivity.Team whoTeam, MainActivity.Role who, boolean isAutoGoal);
    }

}

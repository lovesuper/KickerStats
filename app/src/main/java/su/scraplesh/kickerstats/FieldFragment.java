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
    @Bind(R.id.button_red_forward) Button buttonRedForward;
    @Bind(R.id.button_blue_forward) Button buttonBlueForward;
    @Bind(R.id.button_blue_goalkeeper) Button buttonBlueGoalkeeper;
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
        buttonRedGoalkeeper.setText(mListener.getPlayerName(MainActivity.Team.Red, MainActivity.Role.Goalkeeper));
        buttonRedForward.setText(mListener.getPlayerName(MainActivity.Team.Red, MainActivity.Role.Forward));
        buttonBlueForward.setText(mListener.getPlayerName(MainActivity.Team.Blue, MainActivity.Role.Forward));
        buttonBlueGoalkeeper.setText(mListener.getPlayerName(MainActivity.Team.Blue, MainActivity.Role.Goalkeeper));

        if (mListener.isPlayersSet()){
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
        mListener.onSelectGameRole(MainActivity.Team.Red, MainActivity.Role.Goalkeeper);
    }

    @OnClick(R.id.button_red_forward)
    public void onSelectRedForward() {
        mListener.onSelectGameRole(MainActivity.Team.Red, MainActivity.Role.Forward);
    }

    @OnClick(R.id.button_blue_forward)
    public void onSelectBlueForward() {
        mListener.onSelectGameRole(MainActivity.Team.Blue, MainActivity.Role.Forward);
    }

    @OnClick(R.id.button_blue_goalkeeper)
    public void onSelectBlueGoalkeeper() {
        mListener.onSelectGameRole(MainActivity.Team.Blue, MainActivity.Role.Goalkeeper);
    }

    public interface OnSelectGameRoleListener {
        void onSelectGameRole(MainActivity.Team team, MainActivity.Role role);
        String getPlayerName(MainActivity.Team team, MainActivity.Role role);
        boolean isPlayersSet();
        int getRedGoals();
        int getBlueGoals();
    }

}

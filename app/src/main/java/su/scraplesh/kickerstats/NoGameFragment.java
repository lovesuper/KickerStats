package su.scraplesh.kickerstats;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoGameFragment extends Fragment {

    public static final String TAG = "no_game";
    public static final String ARG_RED_GOALS = "red_goals";
    public static final String ARG_BLUE_GOALS = "blue_goals";

    @Bind(R.id.container_end_game) LinearLayout containerEndGame;
    @Bind(R.id.text_red_goals) TextView textRedGoals;
    @Bind(R.id.text_blue_goals) TextView textBlueGoals;

    private OnStartGameListener mListener;
    private int redGoals = -1;
    private int blueGoals = -1;

    public static NoGameFragment gameEnded(int redGoals, int blueGoals) {
        final NoGameFragment fragment = new NoGameFragment();

        final Bundle args = new Bundle();
        args.putInt(ARG_RED_GOALS, redGoals);
        args.putInt(ARG_BLUE_GOALS, blueGoals);
        fragment.setArguments(args);

        return fragment;
    }

    public NoGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            redGoals = getArguments().getInt(ARG_RED_GOALS, -1);
            blueGoals = getArguments().getInt(ARG_BLUE_GOALS, -1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (redGoals > -1 && blueGoals > -1) {
            containerEndGame.setVisibility(View.VISIBLE);
            textRedGoals.setText(String.valueOf(redGoals));
            textBlueGoals.setText(String.valueOf(blueGoals));
        } else {
            containerEndGame.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_no_game, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.button_start_game)
    public void onButtonPressed() {
        mListener.onStartGame();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStartGameListener) activity;
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

    public interface OnStartGameListener {
        void onStartGame();
    }

}

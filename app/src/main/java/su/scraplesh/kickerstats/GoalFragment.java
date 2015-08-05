package su.scraplesh.kickerstats;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnGoalListener} interface
 * to handle interaction events.
 * Use the {@link GoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoalFragment extends Fragment {

    public static final String TAG = "goal";

    private static final String ARG_TEAM = "team";
    private static final String ARG_ROLE = "role";

    private MainActivity.Team team;
    private MainActivity.Role role;
    private OnGoalListener mListener;

    public static GoalFragment newInstance(MainActivity.Team team, MainActivity.Role role) {
        GoalFragment fragment = new GoalFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_TEAM, team);
        args.putSerializable(ARG_ROLE, role);
        fragment.setArguments(args);

        return fragment;
    }

    public GoalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            team = (MainActivity.Team) getArguments().getSerializable(ARG_TEAM);
            role = (MainActivity.Role) getArguments().getSerializable(ARG_ROLE);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_goal, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.button_to_red)
    public void onGoalToRed() {
        if (mListener != null) {
            mListener.onGoal(team, role, MainActivity.Team.Red);
        }
    }

    @OnClick(R.id.button_to_blue)
    public void onGoalToBlue() {
        if (mListener != null) {
            mListener.onGoal(team, role, MainActivity.Team.Blue);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGoalListener) activity;
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

    public interface OnGoalListener {

       void onGoal(MainActivity.Team whoTeam, MainActivity.Role who, MainActivity.Team whereTeam);
    }

}

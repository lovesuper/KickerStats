package su.scraplesh.kickerstats;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.ParseObject;

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

    private ParseObject redGoalkeeper;
    private ParseObject redForward;
    private ParseObject blueForward;
    private ParseObject blueGoalkeeper;

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

        initView();

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

    @OnClick(R.id.button_red_goalkeeper)
    public void onSelectRedGoalkeeper() {
        if (mListener != null) {
            mListener.onSelectGameRole(GameRole.RedGoalkeeper);
        }
    }

    @OnClick(R.id.button_red_forward)
    public void onSelectRedForward() {
        if (mListener != null) {
            mListener.onSelectGameRole(GameRole.RedForward);
        }
    }

    @OnClick(R.id.button_blue_forward)
    public void onSelectBlueForward() {
        if (mListener != null) {
            mListener.onSelectGameRole(GameRole.BlueForward);
        }
    }

    @OnClick(R.id.button_blue_goalkeeper)
    public void onSelectBlueGoalkeeper() {
        if (mListener != null) {
            mListener.onSelectGameRole(GameRole.BlueGoalkeeper);
        }
    }

    private void initView() {
        if (redGoalkeeper == null) {
            buttonRedForward.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            buttonBlueForward.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            buttonBlueGoalkeeper.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    public void setRedGoalkeeper(ParseObject redGoalkeeper) {
        this.redGoalkeeper = redGoalkeeper;
        buttonRedForward.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    public void setRedForward(ParseObject redForward) {
        this.redForward = redForward;
        buttonBlueForward.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
    }

    public void setBlueForward(ParseObject blueForward) {
        this.blueForward = blueForward;
        buttonBlueGoalkeeper.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
    }

    public void setBlueGoalkeeper(ParseObject blueGoalkeeper) {
        this.blueGoalkeeper = blueGoalkeeper;
    }

    public boolean allRolesSet() {
        return redGoalkeeper != null && redForward != null && blueForward != null && blueGoalkeeper != null;
    }

    public enum GameRole {
        RedGoalkeeper,
        RedForward,
        BlueForward,
        BlueGoalkeeper
    }

    public interface OnSelectGameRoleListener {

        void onSelectGameRole(GameRole gameRole);

    }

}

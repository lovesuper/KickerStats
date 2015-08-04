package su.scraplesh.kickerstats;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class PlayerListFragment extends ListFragment {

    public static final String TAG = "player_list";

    private OnSelectPlayerListener mListener;
    private List<ParseObject> players = new ArrayList<>();

    public PlayerListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1
        ));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectPlayerListener) activity;
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (mListener != null) {
            mListener.onSelectPlayer(players.get(position));
        }
    }

    public void updatePlayers(List<ParseObject> players) {
        this.players = players;

        ArrayAdapter adapter = (ArrayAdapter) getListAdapter();
        adapter.clear();

        for (ParseObject player : players) {
            //noinspection unchecked
            adapter.add(player.getString("name"));
        }

        adapter.notifyDataSetChanged();
    }

    public interface OnSelectPlayerListener {

        void onSelectPlayer(ParseObject player);

    }

}

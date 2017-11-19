package xyz.imxqd.ta.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.imxqd.ta.R;
import xyz.imxqd.ta.models.ChatMessage;

public class ChatFragment extends Fragment {
    private OnChatMessageInteractionListener mListener;

    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChatMessageInteractionListener) {
            mListener = (OnChatMessageInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChatMessageInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnChatMessageInteractionListener {
        void onListFragmentInteraction(ChatMessage message);
    }
}

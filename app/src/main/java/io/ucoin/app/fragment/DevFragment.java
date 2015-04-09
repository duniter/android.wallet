package io.ucoin.app.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.ucoin.app.Fragment;
import io.ucoin.app.R;
import io.ucoin.app.activity.MainActivity;

public class DevFragment extends Fragment{

    private TextView resultText;
    private TextView uid;
    private TextView public_key;

    public static DevFragment newInstance() {
       return new DevFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_dev,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultText = (TextView) view.findViewById(R.id.resultText);
        uid = (TextView) view.findViewById(R.id.uid);
        public_key = (TextView) view.findViewById(R.id.public_key);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_dev, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        getActivity().setTitle(R.string.dev);
        ((MainActivity)getActivity()).setBackButtonEnabled(false);
    }


    private void test() {
        Fragment fragment = PinFragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fade_in,
                        R.animator.fade_out,
                        R.animator.fade_in,
                        R.animator.fade_out)
                .replace(R.id.frame_content, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    protected static boolean isEquals(byte[] expectedData, byte[] actualData) {
        if (expectedData == null && actualData != null) {
            return false;
        }

        if (expectedData != null && actualData == null) {
            return false;
        }

        return expectedData.equals(actualData);
    }

    protected static boolean isEquals(String expectedData, String actualData) {
        if (expectedData == null && actualData != null) {
            return false;
        }

        if (expectedData != null && actualData == null) {
            return false;
        }

        return expectedData.equals(actualData);
    }
}

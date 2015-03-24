package io.ucoin.app.fragment.identity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import io.ucoin.app.ListFragment;
import io.ucoin.app.R;
import io.ucoin.app.adapter.CertificationCursorAdapter;
import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.enums.CertificationType;
import io.ucoin.app.model.http_api.WotCertification;
import io.ucoin.app.sqlite.Contract;


public class CertificationListFragment extends ListFragment
implements LoaderManager.LoaderCallbacks<Cursor>    {

    static public CertificationListFragment newInstance(UcoinIdentity identity, CertificationType type) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putParcelable(UcoinIdentity.class.getSimpleName(), identity);
        newInstanceArgs.putSerializable(CertificationType.class.getSimpleName(), type);
        CertificationListFragment fragment = new CertificationListFragment();
        fragment.setArguments(newInstanceArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_certification_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CertificationCursorAdapter certificationCursorAdapter
                = new CertificationCursorAdapter(getActivity(), null, 0);
        setListAdapter(certificationCursorAdapter);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        UcoinIdentity identity = args.getParcelable(UcoinIdentity.class.getSimpleName());
        CertificationType type = (CertificationType) args.getSerializable(CertificationType.class.getSimpleName());

        String selection = Contract.Certification.IDENTITY_ID + "=? AND " + Contract.Certification.TYPE + "=?";
        String selectionArgs[] = new String[]{
                identity.id().toString(),
                type.name()
        };

        return new CursorLoader(
                getActivity(),
                Provider.CERTIFICATION_URI,
                null, selection, selectionArgs,
                Contract.Certification.BLOCK +" DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String[] indexes = data.getColumnNames();
        for(String index : indexes) {
            Log.d("INDEX", index);
        }
        data.getColumnIndex("_ID");
        Log.d("Certification._ID", Integer.toString(data.getColumnIndex(Contract.Certification._ID)));
        data.getColumnIndex(Contract.Member._ID);
        Log.d("Member._ID", Integer.toString(data.getColumnIndex(Contract.Member._ID)));

        ((CertificationCursorAdapter)this.getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((CertificationCursorAdapter)this.getListAdapter()).swapCursor(null);
    }
}

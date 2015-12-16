package io.ucoin.app.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.adapter.CurrencyCursorAdapter;
import io.ucoin.app.fragment.dialog.AddCurrencyDialogFragment;

public class CurrencyListActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        ImageButton.OnClickListener,
        ListView.OnItemClickListener,
        DialogInterface.OnDismissListener {

    private ListView mList;
    private Button mSelectAll;
    private ImageButton mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_currency_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mList = (ListView) findViewById(R.id.list);

        CurrencyCursorAdapter adapter = new CurrencyCursorAdapter(this, null, 0);
        mList.setAdapter(adapter);
        mList.setEmptyView(findViewById(R.id.empty));
        mList.setOnItemClickListener(this);

        mSelectAll = (Button) findViewById(R.id.all);
        mSelectAll.setOnClickListener(this);

        mButton = (ImageButton) findViewById(R.id.add_currency_button);
        mButton.setOnClickListener(this);

        try {
            setSupportActionBar(toolbar);
        } catch (Throwable t) {
            Log.w("setSupportActionBar", t.getMessage());
        }
        getLoaderManager().initLoader(0, getIntent().getExtras(), this);
    }

    //handle click on add currency floating button
    @Override
    public void onClick(View v) {
        if(v instanceof ImageButton) {
            //block screen rotation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

            //animate button
            RotateAnimation animation = new RotateAnimation(0, 145, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(200);
            animation.setFillAfter(true);
            mButton.startAnimation(animation);

            //show dialog
            AddCurrencyDialogFragment fragment = AddCurrencyDialogFragment.newInstance();
            fragment.setOnDismissListener(this);
            fragment.show(getFragmentManager(), fragment.getClass().getSimpleName());
        }else if(v instanceof Button){
            Intent intent = new Intent(this, CurrencyActivity.class);
            intent.putExtra(Application.EXTRA_CURRENCY_ID, new Long(-1));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    //handles click on currency list item
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, CurrencyActivity.class);
        intent.putExtra(Application.EXTRA_CURRENCY_ID, id);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        //unlock screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
        //animate button
        RotateAnimation animation = new RotateAnimation(145, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        mButton.startAnimation(animation);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                UcoinUris.CURRENCY_URI,
                null, null, null,
                BaseColumns._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (data.getCount()){
            case 1:
                mSelectAll.setVisibility(View.GONE);
                break;
            default:
                mSelectAll.setVisibility(View.VISIBLE);
        }
        ((CurrencyCursorAdapter) mList.getAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((CurrencyCursorAdapter) mList.getAdapter()).swapCursor(null);
    }
}

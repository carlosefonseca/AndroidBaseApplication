package co.base.androidbaseapplication.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.base.androidbaseapplication.R;
import co.base.androidbaseapplication.data.Events;
import co.base.androidbaseapplication.services.SyncService;
import co.base.androidbaseapplication.data.model.Country;
import co.base.androidbaseapplication.ui.base.BaseActivity;
import co.base.androidbaseapplication.ui.details.CountryDetailActivity;
import co.base.androidbaseapplication.util.DialogFactory;


public class MainActivity extends BaseActivity implements MainMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "co.base.androidbaseapplication.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject MainPresenter mMainPresenter;
    @Inject CountriesAdapter mCountriesAdapter;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.my_toolbar)
    Toolbar mToolbar;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        mCountriesAdapter.setOnItemClickListener(mOnItemClickListener);

        mRecyclerView.setAdapter(mCountriesAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMainPresenter.attachView(this);
        mMainPresenter.loadCountries();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Events.SYNC_COMPLETED.toString()));

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(SyncService.getStartIntent(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    /*****
     * MVP View methods implementation
     *****/

    @Override
    public void showCountries(List<Country> countries) {
        mCountriesAdapter.setCountries(countries);
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_loading_countries))
                .show();
    }

    @Override
    public void countryItemClicked(Country country) {
        Intent intentToLaunch = CountryDetailActivity.getCallingIntent(getApplicationContext(),
                country.getmCountryCode(), country.getmLat(), country.getmLng());
        startActivity(intentToLaunch);
    }

    @Override
    public void showCountriesEmpty() {
        mCountriesAdapter.setCountries(Collections.<Country>emptyList());
        mCountriesAdapter.notifyDataSetChanged();
        Toast.makeText(this, R.string.empty_countries, Toast.LENGTH_LONG).show();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mMainPresenter.loadCountries();
        }
    };

    private CountriesAdapter.OnItemClickListener mOnItemClickListener =
            new CountriesAdapter.OnItemClickListener() {
                @Override
                public void onCountryItemClicked(Country country) {
                    if (MainActivity.this.mMainPresenter != null && country != null) {
                        MainActivity.this.mMainPresenter.onCountryClicked(country);
                    }
                }
            };
}

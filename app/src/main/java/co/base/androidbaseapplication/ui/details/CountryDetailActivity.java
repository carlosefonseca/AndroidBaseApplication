package co.base.androidbaseapplication.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.base.androidbaseapplication.R;
import co.base.androidbaseapplication.ui.base.BaseActivity;

public class CountryDetailActivity extends BaseActivity implements CountryDetailMvpView {

    private static final String INSTANCE_EXTRA_PARAM_COUNTRY_CODE
            = "STATE_PARAM_COUNTRY_CODE";

    private static final String INSTANCE_STATE_PARAM_COUNTRY_CODE
            = "STATE_SAVE_COUNTRY_CODE";

    private static final String INSTANCE_EXTRA_PARAM_COUNTRY_LAT
            = "STATE_PARAM_COUNTRY_LAT";

    private static final String INSTANCE_STATE_PARAM_COUNTRY_LAT
            = "STATE_SAVE_COUNTRY_LAT";

    private static final String INSTANCE_EXTRA_PARAM_COUNTRY_LNG
            = "STATE_PARAM_COUNTRY_LNG";

    private static final String INSTANCE_STATE_PARAM_COUNTRY_LNG
            = "STATE_SAVE_COUNTRY_LNG";

    private String mCountryCode;
    private Double mLat;
    private Double mLng;

    @Bind(R.id.webView) WebView mCountryDetailsView;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.my_toolbar) Toolbar mToolbar;

    @Inject
    CountryDetailPresenter mCountryDetailPresenter;

    public static Intent getCallingIntent(Context context, String mCountryCode, Double lat,
                                          Double lng) {
        Intent callingIntent = new Intent(context, CountryDetailActivity.class);
        callingIntent.putExtra(INSTANCE_EXTRA_PARAM_COUNTRY_CODE, mCountryCode);
        return callingIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_country_detail);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        mCountryDetailPresenter.attachView(this);

        if (savedInstanceState == null) {
            this.mCountryCode = getIntent().getStringExtra(INSTANCE_EXTRA_PARAM_COUNTRY_CODE);
            this.mLat = getIntent().getDoubleExtra(INSTANCE_EXTRA_PARAM_COUNTRY_LAT, 0);
            this.mLng = getIntent().getDoubleExtra(INSTANCE_EXTRA_PARAM_COUNTRY_LNG, 0);
        } else {
            this.mCountryCode = savedInstanceState.getString(INSTANCE_STATE_PARAM_COUNTRY_CODE);
            this.mLat = savedInstanceState.getDouble(INSTANCE_STATE_PARAM_COUNTRY_LAT);
            this.mLng = savedInstanceState.getDouble(INSTANCE_STATE_PARAM_COUNTRY_LNG);
        }

        mCountryDetailsView.getSettings().setJavaScriptEnabled(true);

        mCountryDetailsView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                setProgress(progress * 1000);
                if (progress == 100) {
                    mCountryDetailPresenter.hideLoading();
                }
            }
        });

        mCountryDetailPresenter.showLoading();

        mCountryDetailsView.loadUrl("http://www.geognos.com/geo/en/cc/"
                + this.mCountryCode.toLowerCase() + ".html");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString(INSTANCE_STATE_PARAM_COUNTRY_CODE, this.mCountryCode);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountryDetailPresenter.detachView();
    }

    @Override
    public void showLoadingView() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingView() {
        mProgressBar.setVisibility(View.GONE);
    }
}

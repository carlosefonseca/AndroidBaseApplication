package co.base.androidbaseapplication.ui.main;

import java.util.List;

import javax.inject.Inject;

import co.base.androidbaseapplication.data.DataManager;
import co.base.androidbaseapplication.data.model.Country;
import co.base.androidbaseapplication.ui.base.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainPresenter extends BasePresenter<MainMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public MainPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadCountries() {
        checkViewAttached();
        mSubscription = mDataManager.getCountries()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Country>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the ribots.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(List<Country> countries) {
                        if (countries.isEmpty()) {
                            getMvpView().showCountriesEmpty();
                        } else {
                            getMvpView().showCountries(countries);
                        }
                    }
                });
    }

    public void onCountryClicked(Country country) {
        getMvpView().countryItemClicked(country);
    }

}

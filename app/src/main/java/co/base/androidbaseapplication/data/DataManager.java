package co.base.androidbaseapplication.data;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.base.androidbaseapplication.data.local.DatabaseHelper;
import co.base.androidbaseapplication.data.local.EventPosterHelper;
import co.base.androidbaseapplication.data.local.PreferencesHelper;
import co.base.androidbaseapplication.data.model.Country;
import co.base.androidbaseapplication.data.model.mapper.CountryItemMapper;
import co.base.androidbaseapplication.data.model.rest.CountryItemResponse;
import co.base.androidbaseapplication.data.remote.CountriesService;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;


@Singleton
public class DataManager {

    private final CountriesService mCountriesService;
    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;
    private final EventPosterHelper mEventPosterHelper;

    @Inject
    public DataManager(CountriesService countriesService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper, EventPosterHelper eventPosterHelper) {
        mCountriesService = countriesService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
        mEventPosterHelper = eventPosterHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<Country> syncCountries() {
        return mCountriesService.getCountries()
                .concatMap(new Func1<List<CountryItemResponse>, Observable<Country>>() {
                    @Override
                    public Observable<Country> call(List<CountryItemResponse> countries) {
                        Collection<Country> countryList = CountryItemMapper.transform(countries);
                        return mDatabaseHelper.setCountries(countryList);
                    }
                }).doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        mEventPosterHelper.postEvent(Events.SYNC_COMPLETED);
                    }
                });
    }

    public Observable<List<Country>> getCountries() {
        return mDatabaseHelper.getCountries().distinct();
    }
}

package co.base.androidbaseapplication.model.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.base.androidbaseapplication.events.EventPosterHelper;
import co.base.androidbaseapplication.events.Events;
import co.base.androidbaseapplication.model.entities.Country;
import co.base.androidbaseapplication.model.entities.mapper.CountryItemMapper;
import co.base.androidbaseapplication.model.entities.rest.CountryItemResponse;
import co.base.androidbaseapplication.model.local.CountryDataStore;
import co.base.androidbaseapplication.model.repository.CountryRepository;
import co.base.androidbaseapplication.model.rest.exceptions.ServerErrorException;
import co.base.androidbaseapplication.model.rest.exceptions.UnknownErrorException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

@Singleton
public class RestDataSource implements CountryRepository {

    final String ENDPOINT = "https://restcountries.eu/rest/v1/";

    private final CountryApi mCountryApi;
    private final CountryDataStore mCountryDataStore;
    private final EventPosterHelper mEventPosterHelper;

    @Inject
    public RestDataSource(CountryDataStore countryDataStore, EventPosterHelper eventPosterHelper) {

        //TODO Custom HttpClient Interceptor

        /*OkHttpClient client = new OkHttpClient();

        CountryInterceptor countryInterceptor =
                new CountryInterceptor(
                        "AUTH_TOKEN");

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        client.interceptors().add(countryInterceptor);*/

        Gson gson = new GsonBuilder()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //.client(client)
                .build();
        mCountryApi = retrofit.create(CountryApi.class);

        mCountryDataStore = countryDataStore;

        mEventPosterHelper = eventPosterHelper;
    }


    @Override
    public Observable<List<Country>> getCountries() {
        return mCountryApi.getCountries()
                .concatMap(new Func1<List<CountryItemResponse>, Observable<List<Country>>>() {
                    @Override
                    public Observable<List<Country>> call(List<CountryItemResponse> countries) {
                        List<Country> countryList = CountryItemMapper.transform(countries);
                        mCountryDataStore.setCountries(countryList);
                        return Observable.just(countryList);
                    }
                }).doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        mEventPosterHelper.postEvent(Events.SYNC_COMPLETED);
                    }
                }).onErrorResumeNext(new Func1<Throwable, Observable<? extends List<Country>>>() {
                    @Override
                    public Observable<? extends List<Country>> call(Throwable throwable) {
                        boolean serverError = throwable.getMessage()
                                .equals(HttpErrors.SERVER_ERROR);
                        return Observable.error(
                                (serverError) ? new ServerErrorException() :
                                        new UnknownErrorException());
                    }
                });
    }
}

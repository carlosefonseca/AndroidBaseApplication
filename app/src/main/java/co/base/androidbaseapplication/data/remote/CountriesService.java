package co.base.androidbaseapplication.data.remote;

import java.util.List;

import co.base.androidbaseapplication.data.model.rest.CountryItemResponse;
import retrofit2.http.GET;
import rx.Observable;

public interface CountriesService {

    String ENDPOINT = "https://restcountries.eu/rest/v1/";

    @GET("region/europe")
    Observable<List<CountryItemResponse>> getCountries();
}

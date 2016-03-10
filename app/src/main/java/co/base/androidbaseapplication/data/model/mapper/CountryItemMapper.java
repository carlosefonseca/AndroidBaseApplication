package co.base.androidbaseapplication.data.model.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.base.androidbaseapplication.data.model.Country;
import co.base.androidbaseapplication.data.model.rest.CountryItemResponse;

public class CountryItemMapper {

    public static Collection<Country> transform(Collection<CountryItemResponse> countries) {
        List<Country> countriesList = new ArrayList<>();
        for (CountryItemResponse country : countries) {
            Country countryItem = transform(country);
            countriesList.add(countryItem);
        }
        return countriesList;
    }

    public static Country transform(CountryItemResponse countryItem) {
        Country country = new Country();
        country.setmCountryCode(countryItem.getAlpha2Code());
        country.setmCountryName(countryItem.getName());
        country.setmLat(countryItem.getLatlng().get(0));
        country.setmLng(countryItem.getLatlng().get(1));
        return country;
    }
}

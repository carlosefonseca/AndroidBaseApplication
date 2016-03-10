package co.base.androidbaseapplication.ui.main;

import java.util.List;

import co.base.androidbaseapplication.data.model.Country;
import co.base.androidbaseapplication.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void showCountries(List<Country> countries);

    void showCountriesEmpty();

    void showError();

    void countryItemClicked(Country country);
}

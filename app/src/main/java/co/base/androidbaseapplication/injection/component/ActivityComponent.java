package co.base.androidbaseapplication.injection.component;

import co.base.androidbaseapplication.injection.PerActivity;
import co.base.androidbaseapplication.injection.module.ActivityModule;
import co.base.androidbaseapplication.ui.details.CountryDetailActivity;
import co.base.androidbaseapplication.ui.main.MainActivity;
import dagger.Component;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);
    void inject(CountryDetailActivity countryDetailActivity);
}

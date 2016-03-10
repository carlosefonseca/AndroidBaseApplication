package co.base.androidbaseapplication.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import co.base.androidbaseapplication.data.DataManager;
import co.base.androidbaseapplication.data.local.EventPosterHelper;
import co.base.androidbaseapplication.services.SyncService;
import co.base.androidbaseapplication.data.local.DatabaseHelper;
import co.base.androidbaseapplication.data.local.PreferencesHelper;
import co.base.androidbaseapplication.data.remote.CountriesService;
import co.base.androidbaseapplication.injection.ApplicationContext;
import co.base.androidbaseapplication.injection.module.ApplicationModule;
import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SyncService syncService);

    @ApplicationContext Context context();
    Application application();
    CountriesService ribotsService();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    EventPosterHelper eventPosterHelper();
    DataManager dataManager();

}

package co.base.androidbaseapplication.data.local;

import android.content.Context;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.base.androidbaseapplication.data.model.Country;
import co.base.androidbaseapplication.injection.ApplicationContext;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import rx.Observable;
import rx.Subscriber;

@Singleton
public class DatabaseHelper {

    private final RealmConfiguration mRealmConfiguration;

    @Inject
    public DatabaseHelper(@ApplicationContext Context context) {
        // Create a RealmConfiguration which is to locate Realm file in package's "files" directory.
        mRealmConfiguration = new RealmConfiguration.Builder(context).build();

    }

    public Observable<Country> setCountries(final Collection<Country> newCountries) {
        return Observable.create(new Observable.OnSubscribe<Country>() {
            @Override
            public void call(final Subscriber<? super Country> subscriber) {
                if (subscriber.isUnsubscribed()) return;

                // Get a Realm instance for this thread
                Realm mDb = Realm.getInstance(mRealmConfiguration);
                // Persist your data easily
                mDb.beginTransaction();
                for (Country country : newCountries) {
                    RealmObject result = mDb.copyToRealm(country);
                    if (result != null)
                        subscriber.onNext(country);
                }

                mDb.commitTransaction();

                subscriber.onCompleted();
            }
        });
    }

    public Observable<List<Country>> getCountries() {
        Realm mDb = Realm.getInstance(mRealmConfiguration);
        return Observable.just(
                mDb.copyFromRealm(mDb.where(Country.class).findAll()));
    }

}

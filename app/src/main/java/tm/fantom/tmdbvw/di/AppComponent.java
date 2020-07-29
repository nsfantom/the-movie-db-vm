package tm.fantom.tmdbvw.di;

import android.app.Application;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import tm.fantom.tmdbvw.TmApp;
import tm.fantom.tmdbvw.repo.SharedStorage;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                ActivityBuildersModule.class,
                AppModule.class,
                ViewModelFactoryModule.class,
        }
)
public interface AppComponent extends AndroidInjector<TmApp> {

    SharedStorage sharedStorage();

    Resources resources();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
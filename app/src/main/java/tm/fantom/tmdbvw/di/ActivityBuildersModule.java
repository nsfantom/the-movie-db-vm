package tm.fantom.tmdbvw.di;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import tm.fantom.tmdbvw.di.fragment.MainFragmentBuildersModule;
import tm.fantom.tmdbvw.di.fragment.MainModule;
import tm.fantom.tmdbvw.di.fragment.MainScope;
import tm.fantom.tmdbvw.di.fragment.MainViewModelsModule;
import tm.fantom.tmdbvw.ui.MainActivity;

@Module
public abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(
            modules = {MainFragmentBuildersModule.class, MainViewModelsModule.class, MainModule.class}
    )
    abstract MainActivity contributeMainActivity();

}

package tm.fantom.tmdbvw.di.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import tm.fantom.tmdbvw.ui.dashboard.DashboardFragment;
import tm.fantom.tmdbvw.ui.detail.MovieDetailFragment;

@Module
public abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract DashboardFragment contributeDashboardFragment();

    @ContributesAndroidInjector
    abstract MovieDetailFragment contributeMovieDetailFragment();
}

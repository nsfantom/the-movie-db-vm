package tm.fantom.tmdbvw.di.fragment;


import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import tm.fantom.tmdbvw.di.ViewModelKey;
import tm.fantom.tmdbvw.ui.dashboard.DashboardViewModel;
import tm.fantom.tmdbvw.ui.detail.MovieDetailsViewModel;

@Module
public abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel.class)
    public abstract ViewModel bindDashboardViewModel(DashboardViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailsViewModel.class)
    public abstract ViewModel bindMovieDetailsViewModel(MovieDetailsViewModel viewModel);
}





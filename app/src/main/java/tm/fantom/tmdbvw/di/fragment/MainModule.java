package tm.fantom.tmdbvw.di.fragment;


import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import tm.fantom.tmdbvw.api.req.SimpleApi;
import tm.fantom.tmdbvw.ui.dashboard.MovieSearchAdapter;

@Module
public class MainModule {

    @MainScope
    @Provides
    static MovieSearchAdapter provideAdapter() {
        return new MovieSearchAdapter();
    }

    @MainScope
    @Provides
    static SimpleApi provideMainApi(Retrofit retrofit) {
        return retrofit.create(SimpleApi.class);
    }
}

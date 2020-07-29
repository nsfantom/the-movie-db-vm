package tm.fantom.tmdbvw.ui.dashboard;

import android.content.res.Resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import tm.fantom.tmdbvw.R;
import tm.fantom.tmdbvw.api.netmodel.MovieListObject;
import tm.fantom.tmdbvw.api.netmodel.MoviesResponse;
import tm.fantom.tmdbvw.api.req.SimpleApi;
import tm.fantom.tmdbvw.repo.MovieId;
import tm.fantom.tmdbvw.repo.SharedStorage;
import tm.fantom.tmdbvw.ui.Resource;

public class DashboardViewModel extends ViewModel {

    private final SharedStorage sharedStorage;
    private final SimpleApi simpleApi;
    private final Resources resources;
    private MediatorLiveData<Resource<Movies>> movies;
    private int currentPage = 0;
    private int totalRecords = 0;

    @Inject
    public DashboardViewModel(SharedStorage sharedStorage, SimpleApi simpleApi, Resources resources) {
        this.sharedStorage = sharedStorage;
        this.simpleApi = simpleApi;
        this.resources = resources;
    }

    public boolean isDarkModeEnabled() {
        return sharedStorage.isDarkMode();
    }

    public LiveData<Resource<Movies>> getMovies() {
        if (movies == null) {
            movies = new MediatorLiveData<>();
            movies.setValue(Resource.loading((Movies) null));

            iterateMovies();
        }
        return movies;
    }

    @NotNull
    private LiveData<Resource<Movies>> getResourceLiveData() {
        return LiveDataReactiveStreams.fromPublisher(

                simpleApi.getMovieNowPlaying(currentPage + 1)

                        .onErrorReturn(throwable -> {
                            Timber.e("apply: %s", throwable.getMessage());
                            return new MoviesResponse().setError(true);
                        })

                        .map(response -> {
                            if (response.isError()) {
                                return new Movies(new ArrayList<>());
                            }
                            currentPage = response.getPage();
                            totalRecords = response.getTotalResults();
                            return new Movies(parseMovies(response.getResults())).setAppend(currentPage > 1);
                        })

                        .map((Function<Movies, Resource<Movies>>) movies -> {

                            if (movies.getMovies().size() == 0) {
                                return Resource.error(resources.getString(R.string.error_default), null);
                            }

                            return Resource.success(movies);
                        })

                        .subscribeOn(Schedulers.io())
        );
    }

    private List<MovieModel> parseMovies(List<MovieListObject> results) {
        List<MovieModel> movies = new ArrayList<>();
        for (MovieListObject mlo : results) {
            movies.add(MovieModel.fromNetwork(mlo).setPoster(resources.getString(R.string.base_url_poster, mlo.getPosterPath())));
        }
        return movies;
    }

    public void onLoadMore(int count) {
        Timber.d("load more count: %s, total: %s", count, totalRecords);
        if (!(totalRecords <= count - 1)) {
            iterateMovies();
        }
    }

    public void forceRefresh() {
        currentPage = 0;
        iterateMovies();
    }

    private void iterateMovies() {
        final LiveData<Resource<Movies>> source = getResourceLiveData();
        movies.addSource(source, resource -> {
            movies.setValue(resource);
            movies.removeSource(source);
        });
    }

    public void onMovieClick(int id, String name) {
        sharedStorage.getMovieIdSubject().onNext(new MovieId(id).setMovieName(name));
    }
}




















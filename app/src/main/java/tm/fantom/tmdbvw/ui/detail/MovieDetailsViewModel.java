package tm.fantom.tmdbvw.ui.detail;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import tm.fantom.tmdbvw.R;
import tm.fantom.tmdbvw.api.netmodel.GenresResult;
import tm.fantom.tmdbvw.api.netmodel.MovieDetailResponse;
import tm.fantom.tmdbvw.api.req.SimpleApi;
import tm.fantom.tmdbvw.repo.MovieId;
import tm.fantom.tmdbvw.repo.SharedStorage;
import tm.fantom.tmdbvw.ui.Resource;
import tm.fantom.tmdbvw.util.DateUtils;

public class MovieDetailsViewModel extends ViewModel {

    private final SharedStorage sharedStorage;
    private final SimpleApi simpleApi;
    private final Resources resources;
    private MediatorLiveData<Resource<String>> title;
    private MediatorLiveData<Resource<MovieDetails>> movie;

    @Inject
    public MovieDetailsViewModel(SharedStorage sharedStorage, SimpleApi simpleApi, Resources resources) {
        this.sharedStorage = sharedStorage;
        this.simpleApi = simpleApi;
        this.resources = resources;
    }

    public LiveData<Resource<String>> getTitle() {
        if (title == null) {
            title = new MediatorLiveData<>();
            title.setValue(Resource.loading((String) null));

            final LiveData<Resource<String>> source = LiveDataReactiveStreams.fromPublisher(
                    sharedStorage.getMovieIdSubject().toFlowable(BackpressureStrategy.LATEST)
                            .onErrorReturn(throwable -> new MovieId(-1))
                            .doOnNext(movieId -> {
                                if (movieId.getMovieId() != -1)
                                    getDetails(movieId);
                            })
                            .map(MovieId::getMovieName)
                            .map((Function<String, Resource<String>>) movie -> {

                                if (TextUtils.isEmpty(movie)) {
                                    return Resource.error(resources.getString(R.string.error_default), null);
                                }

                                return Resource.success(movie);
                            })
                            .subscribeOn(Schedulers.io())
            );
            title.addSource(source, resource -> {
                title.setValue(resource);
                title.removeSource(source);
            });


        }
        return title;
    }

    public LiveData<Resource<MovieDetails>> getMovie() {
        if (movie == null) {
            movie = new MediatorLiveData<>();
            movie.setValue(Resource.loading((MovieDetails) null));
        }
        return movie;
    }

    private void getDetails(MovieId movieId) {
        final LiveData<Resource<MovieDetails>> source = LiveDataReactiveStreams.fromPublisher(

                simpleApi.getMovieMovieId(movieId.getMovieId())

                        .onErrorReturn(throwable -> {
                            Timber.e("apply: %s", throwable.getMessage());
                            return new MovieDetailResponse();
                        })
                        .map(this::parseMovie)
                        .map((Function<MovieDetails, Resource<MovieDetails>>) movie -> {

                            if (movie.isError()) {
                                return Resource.error(resources.getString(R.string.error_default), null);
                            }


                            return Resource.success(movie);
                        })

                        .subscribeOn(Schedulers.io())
        );
        new Handler(Looper.getMainLooper()).post(() ->
                movie.addSource(source, resource -> {
                    movie.setValue(resource);
                    movie.removeSource(source);
                }));
    }

    private MovieDetails parseMovie(MovieDetailResponse mdr) {
        if (mdr.getId() == null) return new MovieDetails().setError(true);
        return new MovieDetails()
                .setPoster(resources.getString(R.string.base_url_poster, mdr.getPosterPath()))
                .setCategory(parseCategory(mdr.getGenres()))
                .setRelease(resources.getString(R.string.release, DateUtils.parseRelease(mdr.getReleaseDate())))
                .setBudget(resources.getString(R.string.budget, mdr.getBudget()))
                .setTag(mdr.getTagLine())
                .setRating(resources.getString(R.string.rating, mdr.getVoteAverage()))
                .setDescription(mdr.getOverview());
    }

    private String parseCategory(List<GenresResult> genres) {
        String cat = "";
        for (GenresResult gs : genres) {
            if (!TextUtils.isEmpty(cat))
                cat = cat.concat(", ");
            cat = cat.concat(gs.getName());
        }
        return cat;
    }
}




















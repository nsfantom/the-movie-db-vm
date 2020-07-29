package tm.fantom.tmdbvw.api.req;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tm.fantom.tmdbvw.api.netmodel.MovieDetailResponse;
import tm.fantom.tmdbvw.api.netmodel.MoviesResponse;

public interface SimpleApi {

    @GET("movie/now_playing")
    Flowable<MoviesResponse> getMovieNowPlaying(@Query("page") int page);

    @GET("movie/{movie_id}")
    Flowable<MovieDetailResponse> getMovieMovieId(
            @retrofit2.http.Path("movie_id") int movieId
    );
}

package tm.fantom.tmdbvw.ui.dashboard;

import java.util.List;

public class Movies {
    private boolean append = false;
    private List<MovieModel> movies;

    public Movies(List<MovieModel> movies) {
        this.movies = movies;
    }

    public boolean isAppend() {
        return append;
    }

    public Movies setAppend(boolean append) {
        this.append = append;
        return this;
    }

    public List<MovieModel> getMovies() {
        return movies;
    }
}

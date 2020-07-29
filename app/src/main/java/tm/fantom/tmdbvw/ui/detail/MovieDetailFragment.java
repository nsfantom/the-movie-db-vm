package tm.fantom.tmdbvw.ui.detail;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import tm.fantom.tmdbvw.databinding.FragmentMovieDetailBinding;
import tm.fantom.tmdbvw.ui.base.BaseFragment;
import tm.fantom.tmdbvw.viewmodels.ViewModelProviderFactory;


public class MovieDetailFragment extends BaseFragment {
    private FragmentMovieDetailBinding layout;
    private MovieDetailsViewModel viewModel;

    @Inject
    ViewModelProviderFactory providerFactory;

    public static MovieDetailFragment newInstance() {
        return new MovieDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        layout = FragmentMovieDetailBinding.inflate(inflater);
        return attachToBaseView(inflater, container, layout.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this, providerFactory).get(MovieDetailsViewModel.class);
        initClickListeners();
        subscribeObservers();
    }


    private void initClickListeners() {
        layout.ivBack.setOnClickListener(v -> getParentActivity().onBackPressed());
    }

    private void subscribeObservers() {
        viewModel.getTitle().removeObservers(getViewLifecycleOwner());
        viewModel.getTitle().observe(getViewLifecycleOwner(), stringResource -> {
            if (stringResource != null) {
                switch (stringResource.status) {

                    case LOADING: {
                        showProgress();
                        break;
                    }

                    case SUCCESS: {
                        if (stringResource.data != null) {
                            layout.tvTitle.setText(stringResource.data);
                        }
                        break;
                    }

                    case ERROR: {
                        showError(stringResource.message);
                        break;
                    }
                }
            }
        });

        viewModel.getMovie().removeObservers(getViewLifecycleOwner());
        viewModel.getMovie().observe(getViewLifecycleOwner(), dataResource -> {
            if (dataResource != null) {
                switch (dataResource.status) {

                    case LOADING: {
                        showProgress();
                        break;
                    }

                    case SUCCESS: {
                        hideProgress();
                        if (dataResource.data != null) {
                            showData(dataResource.data);
                        }
                        break;
                    }

                    case ERROR: {
                        showError(dataResource.message);
                        break;
                    }
                }
            }
        });
    }

    private void showData(MovieDetails movieDetails) {
        hideProgress();
        layout.sdvPreview.setImageRequest(
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(movieDetails.getPoster()))
                        .build()
        );
        layout.tvCategory.setText(movieDetails.getCategory());
        layout.tvReleaseDate.setText(movieDetails.getRelease());
        layout.tvBudget.setText(movieDetails.getBudget());
        layout.tvRating.setText(movieDetails.getRating());
        layout.tvTag.setText(movieDetails.getTag());
        layout.tvOverview.setText(movieDetails.getDescription());
    }

}

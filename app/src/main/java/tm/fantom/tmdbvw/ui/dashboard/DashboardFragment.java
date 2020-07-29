package tm.fantom.tmdbvw.ui.dashboard;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import tm.fantom.tmdbvw.R;
import tm.fantom.tmdbvw.databinding.FragmentDashboardBinding;
import tm.fantom.tmdbvw.ui.Fragments;
import tm.fantom.tmdbvw.ui.GridSpacingItemDecoration;
import tm.fantom.tmdbvw.ui.base.BaseFragment;
import tm.fantom.tmdbvw.viewmodels.ViewModelProviderFactory;

public class DashboardFragment extends BaseFragment {
    private FragmentDashboardBinding layout;
    private int scrollPosition;
    private DashboardViewModel viewModel;

    @Inject
    MovieSearchAdapter adapter;

    @Inject
    ViewModelProviderFactory providerFactory;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        layout = FragmentDashboardBinding.inflate(inflater);
        return attachToBaseView(inflater, container, layout.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this, providerFactory).get(DashboardViewModel.class);
        initViews();
//        initSkeleton();
        subscribeObservers();
    }

    @Override
    public void onResume() {
        super.onResume();
        restorePosition();
    }

    @Override
    public void onPause() {
        scrollPosition = layout.rvItems.getScrollY();
        super.onPause();
    }

    private void initViews() {
        restoreToggle(viewModel.isDarkModeEnabled());

        layout.rvItems.setAdapter(adapter);
//        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        layout.rvItems.setLayoutManager(glm);
        adapter.setLastListener(c -> viewModel.onLoadMore(c));
        adapter.setClickListener((m, n) -> {
            viewModel.onMovieClick(m, n);
            getParentActivity().navigateTo(Fragments.DETAILS);
        });
        layout.rvItems.addItemDecoration(
                new GridSpacingItemDecoration(2,
                        getResources().getDimensionPixelOffset(R.dimen.default_margin),
                        true
                )
        );
        layout.swipeToRefresh.setEnabled(true);
        layout.swipeToRefresh.setColorSchemeResources(R.color.control_color_state);
        layout.swipeToRefresh.setOnRefreshListener(() -> {
            layout.swipeToRefresh.setRefreshing(true);
            viewModel.forceRefresh();
        });
    }

    private void initSkeleton() {
        if (adapter.getItemCount() == 0) {
            adapter.addItem(new MovieModel(-1).setSkeleton(true));
            adapter.addItem(new MovieModel(-1).setSkeleton(true));
            adapter.addItem(new MovieModel(-1).setSkeleton(true));
            adapter.addItem(new MovieModel(-1).setSkeleton(true));
        }
    }

    private void subscribeObservers() {
        viewModel.getMovies().removeObservers(getViewLifecycleOwner());
        viewModel.getMovies().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource != null) {
                switch (listResource.status) {

                    case LOADING: {
                        initSkeleton();
                        break;
                    }

                    case SUCCESS: {
                        if (listResource.data != null) {
                            hideProgress();
                            if (listResource.data.isAppend()) {
                                adapter.appendItems(listResource.data.getMovies());
                            } else {
                                adapter.setItems(listResource.data.getMovies());
                                layout.rvItems.scrollToPosition(0);
                            }
                        }
                        break;
                    }

                    case ERROR: {
                        showError(listResource.message);
                        break;
                    }
                }
            }
        });
    }

    public void restoreToggle(boolean enabled) {
        layout.toggleButton.setChecked(enabled);
        layout.toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> getParentActivity().setThemeMode(isChecked));
    }

    public void restorePosition() {
        layout.rvItems.setScrollY(scrollPosition);
    }

    @Override
    public void hideProgress() {
        super.hideProgress();
        if (layout.swipeToRefresh.isRefreshing()) layout.swipeToRefresh.setRefreshing(false);
    }
}

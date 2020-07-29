package tm.fantom.tmdbvw.ui.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import dagger.android.support.DaggerFragment;
import tm.fantom.tmdbvw.databinding.FragmentBaseBinding;
import tm.fantom.tmdbvw.ui.MainActivity;

public class BaseFragment extends DaggerFragment implements StandardDialogs {
    private FragmentBaseBinding layout;
    protected Context context;

    private boolean shouldOverrideCurrentFragment = true;


    private boolean backStackRequired = true;

    public String getFragmentTag() {
        return getClass().getSimpleName();
    }

    public void setShouldOverrideCurrentFragment(boolean shouldOverrideCurrentFragment) {
        this.shouldOverrideCurrentFragment = shouldOverrideCurrentFragment;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    public MainActivity getParentActivity() {
        return (MainActivity) getActivity();
    }

    public boolean needBackStack() {
        return backStackRequired;
    }

    protected View attachToBaseView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, View view) {
        layout = FragmentBaseBinding.inflate(inflater);
        layout.flBaseContainer.addView(view, 0);

        return layout.getRoot();
    }

    @Override
    public void onStart() {
        if (shouldOverrideCurrentFragment) {
            getParentActivity().setCurrentFragment(this);
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        if (shouldOverrideCurrentFragment) {
            getParentActivity().setCurrentFragment(this);
        }
        super.onResume();
    }

    protected String getTextFromEditText(@NonNull final EditText editText) {
        return editText.getEditableText().toString();
    }

    @Override
    public void showProgress() {
        if (layout != null) {
            layout.pbBaseProgress.show();
        } else {
            getParentActivity().showProgress();
        }
    }

    @Override
    public void hideProgress() {
        MainActivity activity = getParentActivity();
        if (activity == null || activity.isDestroyed() || activity.isFinishing())
            return;
        if (layout != null) {
            layout.pbBaseProgress.hide();
        } else {
            activity.hideProgress();
        }
    }

    @Override
    public void showToast(String message, int length) {
        MainActivity activity = getParentActivity();
        if (activity != null) {
            getParentActivity().showToast(message, length);
        }
    }

    @Override
    public void showToast(String message, int yOffset, int length) {
        MainActivity activity = getParentActivity();
        if (activity != null) {
            getParentActivity().showToast(message, yOffset, length);
        }
    }

    public void showError(String message) {
        hideProgress();
        if (!TextUtils.isEmpty(message)) showToast(message, Toast.LENGTH_SHORT);
    }
}
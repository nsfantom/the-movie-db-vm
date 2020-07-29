package tm.fantom.tmdbvw.ui.base;

import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;
import tm.fantom.tmdbvw.R;
import tm.fantom.tmdbvw.databinding.ToastCustomViewBinding;
import tm.fantom.tmdbvw.repo.SharedStorage;
import tm.fantom.tmdbvw.ui.Fragments;

public abstract class BaseActivity extends DaggerAppCompatActivity implements StandardDialogs {

    private WeakReference<Dialog> progressDialog;

    protected CompositeDisposable disposables;

    @Inject
    protected SharedStorage sharedStorage;

    public abstract void navigateTo(@Fragments int fragments);

    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new WeakReference<>(new Dialog(this));
            Window decor = progressDialog.get().getWindow();
            if (decor != null) decor.getDecorView().setBackground(null);
            progressDialog.get().setContentView(R.layout.custom_progressbar);
            progressDialog.get().setCancelable(false);
            progressDialog.get().setCanceledOnTouchOutside(false);
        }

        progressDialog.get().show();
    }

    public void hideProgress() {
        if (this.isDestroyed() || this.isFinishing()) return;
        if (progressDialog != null && progressDialog.get() != null && progressDialog.get().isShowing()) {
            progressDialog.get().dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void showToast(String message, final int length) {
        if (!this.isFinishing()) {
            ToastCustomViewBinding toastLayout = ToastCustomViewBinding.inflate(getLayoutInflater());
            toastLayout.customText.setText(message);
            Toast toast = new Toast(this);
            toast.setView(toastLayout.getRoot());
            toast.setDuration(length);
            toast.show();
        }
    }

    @Override
    public void showToast(String message, final int yOffset, final int length) {
        if (!this.isFinishing()) {
            ToastCustomViewBinding toastLayout = ToastCustomViewBinding.inflate(getLayoutInflater());
            toastLayout.customText.setText(message);
            Toast toast = new Toast(this);
            toast.setView(toastLayout.getRoot());
            toast.setDuration(length);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, yOffset);
            toast.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getCompositeDisposable().add(sharedStorage.getMovieIdSubject()
//                .doOnNext(i -> navigateTo(Fragments.DETAILS))
//                .subscribe());
    }

    @Override
    protected void onPause() {
        clearDisposables();
        super.onPause();
    }

    protected void clearDisposables() {
        if (null != disposables && !disposables.isDisposed()) {
            disposables.dispose();
            disposables = null;
        }
    }

    protected CompositeDisposable getCompositeDisposable() {
        if (disposables == null || disposables.isDisposed()) {
            disposables = new CompositeDisposable();
        }
        return disposables;
    }
}

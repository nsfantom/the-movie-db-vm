package tm.fantom.tmdbvw.ui.base;

public interface StandardDialogs {

    void showProgress();

    void hideProgress();

    void showToast(String message, final int length);

    void showToast(String message, final int yOffset, final int length);
}

package tm.fantom.tmdbvw;

import androidx.appcompat.app.AppCompatDelegate;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;
import tm.fantom.tmdbvw.di.DaggerAppComponent;


public final class TmApp extends DaggerApplication {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
//        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        Fresco.initialize(this, ImagePipelineConfig.newBuilder(this)
                .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                .setDiskCacheEnabled(true)
                .build());

        RxJavaPlugins.setErrorHandler(e -> Timber.e(e, "unhandled exception: %s", e.getMessage()));
    }

}

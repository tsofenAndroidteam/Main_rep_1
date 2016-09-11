package com.example.misho.login.Item;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.misho.login.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.example.misho.login.Item.facebook.samples.zoomable.ZoomableDraweeView;



public class ZoomableActivity extends AppCompatActivity {

    public static final String KEY_TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (null == data) {
            throw new IllegalArgumentException("No data to display");
        }

        String title = getIntent().getStringExtra(KEY_TITLE);
        if (title != null) {
            setTitle(title);
        }

        Fresco.initialize(this);
        setContentView(R.layout.activity_zoomable);

        ZoomableDraweeView view = (ZoomableDraweeView) findViewById(R.id.zoomable);

        DraweeController ctrl = Fresco.newDraweeControllerBuilder().setUri(
                data).setTapToRetryEnabled(true).build();
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();

        view.setController(ctrl);
        view.setHierarchy(hierarchy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Fresco.shutDown();
    }
}

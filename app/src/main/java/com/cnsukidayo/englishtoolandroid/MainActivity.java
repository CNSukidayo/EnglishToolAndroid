package com.cnsukidayo.englishtoolandroid;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cnsukidayo.englishtoolandroid.context.EnglishToolProperties;
import com.cnsukidayo.englishtoolandroid.core.home.HomeListViewAdapter;
import com.cnsukidayo.englishtoolandroid.utils.GetPathUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String basePath = GetPathUtils.getStoragePath(this, true);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ListView homeJsonListView = findViewById(R.id.homeJsonListView);
        homeJsonListView.setAdapter(new HomeListViewAdapter(this));
        File file = new File(basePath + File.separator + EnglishToolProperties.sourcePath + File.separator + EnglishToolProperties.json);

    }

}

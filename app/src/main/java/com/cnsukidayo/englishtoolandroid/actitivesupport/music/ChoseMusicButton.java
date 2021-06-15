package com.cnsukidayo.englishtoolandroid.actitivesupport.music;

import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.function.Consumer;

public class ChoseMusicButton {
    // 每个歌曲独有的ID
    private int id;
    private TextView nameTextView;
    private TextView authorTextView;
    private File musicFile;
    private LinearLayout linearLayout;
    private Consumer<File> consumer;
    public ChoseMusicButton() {
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public void setNameTextView(TextView nameTextView) {
        this.nameTextView = nameTextView;
    }

    public TextView getAuthorTextView() {
        return authorTextView;
    }

    public void setAuthorTextView(TextView authorTextView) {
        this.authorTextView = authorTextView;
    }

    public File getMusicFile() {
        return musicFile;
    }

    public void setMusicFile(File musicFile) {
        this.musicFile = musicFile;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
        this.linearLayout.setOnClickListener(v -> consumer.accept(musicFile));
    }

    public Consumer<File> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<File> consumer) {
        this.consumer = consumer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

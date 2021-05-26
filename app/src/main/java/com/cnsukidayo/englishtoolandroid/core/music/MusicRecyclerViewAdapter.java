package com.cnsukidayo.englishtoolandroid.core.music;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<MusicRecyclerViewAdapter.LinearViewHolder> {

    private LayoutInflater layoutInflater;
    private final File[] allMusic;
    private Map<Integer, ChoseMusicButton> allChooseDaysButton;
    private Consumer<File> consumer;
    public MusicRecyclerViewAdapter(File[] allMusic, Context context,Consumer<File> consumer) {
        layoutInflater = LayoutInflater.from(context);
        this.allMusic = allMusic;
        allChooseDaysButton = new HashMap<>(allMusic.length);
        this.consumer = consumer;
    }

    public MusicRecyclerViewAdapter(List<File> allMusic, Context context, Consumer<File> consumer) {
        this((File[]) allMusic.toArray(),context,consumer);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(layoutInflater.inflate(R.layout.activity_chose_music_button, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        String fileName = allMusic[position].getName().substring(0, allMusic[position].getName().indexOf('.'));
        holder.choseMusicButton.getNameTextView().setText(fileName.substring(0, fileName.indexOf('-')));
        holder.choseMusicButton.getAuthorTextView().setText(fileName.substring(fileName.indexOf("-") + 2));
        holder.choseMusicButton.setMusicFile(allMusic[position]);
        holder.choseMusicButton.setConsumer(consumer);
        allChooseDaysButton.put(position, holder.choseMusicButton);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.allMusic.length;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {


        private ChoseMusicButton choseMusicButton;

        @RequiresApi(api = Build.VERSION_CODES.N)
        LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            choseMusicButton = new ChoseMusicButton();
            choseMusicButton.setNameTextView(itemView.findViewById(R.id.musicName));
            choseMusicButton.setAuthorTextView(itemView.findViewById(R.id.musicAuthor));
            choseMusicButton.setLinearLayout(itemView.findViewById(R.id.musicButton));
        }

    }
}

package com.cnsukidayo.englishtoolandroid.core.music;

import android.annotation.SuppressLint;
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
    private Consumer<File> consumer;
    private Map<Integer, ChoseMusicButton> allChooseDaysButton;

    @SuppressLint("UseSparseArrays")
    private MusicRecyclerViewAdapter(File[] allMusic, Context context, Consumer<File> consumer) {
        layoutInflater = LayoutInflater.from(context);
        this.allMusic = allMusic;
        allChooseDaysButton = new HashMap<>(allMusic.length);
        this.consumer = consumer;
    }

    public MusicRecyclerViewAdapter(List<File> allMusic, Context context, Consumer<File> consumer) {
        this((File[]) allMusic.toArray(), context, consumer);
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
        int lastIndex = fileName.indexOf('-');
        if (lastIndex != -1) {
            holder.choseMusicButton.getNameTextView().setText(fileName.substring(0, lastIndex) + "**" + position);
            holder.choseMusicButton.getAuthorTextView().setText(fileName.substring(lastIndex + 2));
        } else {
            holder.choseMusicButton.getNameTextView().setText(fileName);
        }
        holder.choseMusicButton.setMusicFile(allMusic[position]);
        holder.choseMusicButton.setConsumer(consumer);
        holder.choseMusicButton.setId(position);
        allChooseDaysButton.put(position, holder.choseMusicButton);
        if (position == preMusicID) {
            holder.choseMusicButton.getNameTextView().setTextColor(layoutInflater.getContext().getResources().getColor(R.color.colorDottedLine));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.allMusic.length;
    }

    /**
     * 根据音乐ID修改当前正在播放的音乐的背景框,表明当前音乐正在被播放.
     *
     * @param id 音乐ID
     */
    private int preMusicID = -1;

    public void changeBackGroundByID(int id) {
        if (preMusicID != -1) {
            ChoseMusicButton choseMusicButton = allChooseDaysButton.get(preMusicID);
            if (choseMusicButton != null) {
                choseMusicButton.getNameTextView().setTextColor(layoutInflater.getContext().getResources().getColor(R.color.colorBlack));
                choseMusicButton.getAuthorTextView().setTextColor(layoutInflater.getContext().getResources().getColor(R.color.colorGray));
            }
        }
        preMusicID = id;
        ChoseMusicButton choseMusicButton = allChooseDaysButton.get(preMusicID);
        if (choseMusicButton != null) {
            choseMusicButton.getNameTextView().setTextColor(layoutInflater.getContext().getResources().getColor(R.color.colorDottedLine));
            choseMusicButton.getAuthorTextView().setTextColor(layoutInflater.getContext().getResources().getColor(R.color.colorDarkBlue));
        }
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

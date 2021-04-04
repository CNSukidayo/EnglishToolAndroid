package com.cnsukidayo.englishtoolandroid.core.learn;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.cnsukidayo.englishtoolandroid.R;
import com.cnsukidayo.englishtoolandroid.core.entitys.Word;

import java.util.List;
import java.util.function.Consumer;

public class SearchPoPRecyclerViewAdapter extends RecyclerView.Adapter<SearchPoPRecyclerViewAdapter.LinearViewHolder> {

    private final List<Word> matchFirstCharListWord;
    private LayoutInflater layoutInflater;
    private Consumer<Word> consumer;
    public SearchPoPRecyclerViewAdapter(Context context, List<Word> matchFirstCharListWord, Consumer<Word> consumer) {
        layoutInflater = LayoutInflater.from(context);
        this.matchFirstCharListWord = matchFirstCharListWord;
        this.consumer = consumer;
    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(layoutInflater.inflate(R.layout.layout_search_pop, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        holder.searchSelectTextView.setText(matchFirstCharListWord.get(position).getEnglish());
        holder.searchSelectTextView.setOnClickListener(v -> consumer.accept(matchFirstCharListWord.get(position)));
    }

    @Override
    public int getItemCount() {
        return matchFirstCharListWord.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        //
        private TextView searchSelectTextView;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            searchSelectTextView = itemView.findViewById(R.id.selectEnglish);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}

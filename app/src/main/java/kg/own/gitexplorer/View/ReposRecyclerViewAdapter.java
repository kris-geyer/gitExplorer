package kg.own.gitexplorer.View;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kg.own.gitexplorer.Model.Data.SingleRepos;
import kg.own.gitexplorer.R;

public class ReposRecyclerViewAdapter extends RecyclerView.Adapter<ReposRecyclerViewAdapter.ViewHolder> {

    private final List<SingleRepos> reposList;
    private final ReposViewInterface reposViewInterface;

    public ReposRecyclerViewAdapter(List<SingleRepos> singleRepos, ReposViewInterface reposViewInterface) {
        this.reposList = singleRepos;
        this.reposViewInterface = reposViewInterface;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv;

        ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
            .inflate(R.layout.viewholder_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        StringBuilder text = new StringBuilder();
        text.append(reposList.get(position).getName());
        if(!reposList.get(position).getType().equals("dir")){
            text.append(" - size: ")
                .append((int)reposList.get(position).getSize())
                .append(" Bytes");
        }


        holder.tv.setText(text);

        if(reposList.get(position).getType().equals("dir")){
            holder.tv.setPaintFlags(holder.tv.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);

            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reposViewInterface.filDirPressed(reposList.get(position).getName(), reposList.get(position).getPath());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return reposList.size();
    }

}

package edu.upc.arnaubeltra.tfgquibot.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.models.Experiment;

public class ExperimentsAdapter extends RecyclerView.Adapter<ExperimentsAdapter.ExperimentsViewHolder> {

    public interface IExperimentsRCVItemClicked {
        void onExperimentClicked(int index);
    }

    private IExperimentsRCVItemClicked listener;

    private ArrayList<Experiment> experimentsList;

    public ExperimentsAdapter(IExperimentsRCVItemClicked listener) {
        this.listener = listener;
    }

    public void updateExperiments(ArrayList<Experiment> experimentData) {
        experimentsList = experimentData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExperimentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_experiments, parent, false);
        return new ExperimentsViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperimentsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return experimentsList.size();
    }

    public class ExperimentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        IExperimentsRCVItemClicked listener;

        public ExperimentsViewHolder(@NonNull View itemView, IExperimentsRCVItemClicked itemClickedListener) {
            super(itemView);

            listener = itemClickedListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onExperimentClicked(getAdapterPosition());
        }
    }
}

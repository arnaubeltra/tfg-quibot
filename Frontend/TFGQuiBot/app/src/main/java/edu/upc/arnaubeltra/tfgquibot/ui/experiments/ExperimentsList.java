package edu.upc.arnaubeltra.tfgquibot.ui.experiments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.adapters.ExperimentsAdapter;
import edu.upc.arnaubeltra.tfgquibot.models.Experiment;


public class ExperimentsList extends Fragment {

    private ArrayList<Experiment> experimentsData = new ArrayList<>();

    private RecyclerView rcvListExperiments;
    public static ExperimentsAdapter experimentsAdapter;

    private int experimentIndex;

    // Required empty public constructor
    public ExperimentsList() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_experiments, container, false);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
package edu.upc.arnaubeltra.tfgquibot.adapters.customProgram;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import edu.upc.arnaubeltra.tfgquibot.R;
import edu.upc.arnaubeltra.tfgquibot.UserNavigation;
import edu.upc.arnaubeltra.tfgquibot.adapters.LoggedUsersListAdapter;
import edu.upc.arnaubeltra.tfgquibot.models.User;
import edu.upc.arnaubeltra.tfgquibot.ui.customProgram.CustomProgram;

public class CustomProgramAdapter extends RecyclerView.Adapter<CustomProgramAdapter.CustomProgramViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    public interface ICustomProgramRCVItemClicked {
        void onUserClicked(int index);
    }

    private ICustomProgramRCVItemClicked listener;

    private ArrayList<String> actions = new ArrayList<>();

    public CustomProgramAdapter(ICustomProgramRCVItemClicked listener) {
        this.listener = listener;
    }

    public void updateActionsList(ArrayList<String> actions) {
        this.actions = actions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomProgramAdapter.CustomProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(UserNavigation.getContext()).inflate(R.layout.layout_custom_program, parent, false);
        return new CustomProgramViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomProgramAdapter.CustomProgramViewHolder holder, int position) {
        holder.iconAction.setImageResource(getIcon(actions.get(position)));
        holder.iconEdit.setImageResource(R.drawable.icon_edit);
        holder.iconDelete.setImageResource(R.drawable.icon_delete);
        holder.txtAction.setText(actions.get(position));
    }

    private int getIcon(String action) {
        switch (action) {
            case "Endavant":
                return R.drawable.ic_arrow_up_2;
            case "Enrere":
                return R.drawable.ic_arrow_down_2;
            case "Dreta":
                return R.drawable.ic_arrow_right_2;
            case "Esquerra":
                return R.drawable.ic_arrow_left_2;
            case "Baixar xeringa":
                return R.drawable.ic_lower_pipette;
            case "Pujar xeringa":
                return R.drawable.ic_raise_pipette;
            case "Accionar xeringa":
                return R.drawable.icon_suck_2;
        }
        return R.drawable.ic_arrow_up_2;
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(actions, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(actions, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(CustomProgramViewHolder myViewHolder) {

    }

    @Override
    public void onRowClear(CustomProgramViewHolder myViewHolder) {

    }

    public class CustomProgramViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iconAction, iconEdit, iconDelete;
        private TextView txtAction;

        View rowView;

        public CustomProgramViewHolder(@NonNull View itemView, ICustomProgramRCVItemClicked itemClickedListener) {
            super(itemView);

            rowView = itemView;
            listener = itemClickedListener;

            iconAction = itemView.findViewById(R.id.iconActionCustomProgram);
            iconEdit = itemView.findViewById(R.id.iconEditCustomProgram);
            iconDelete = itemView.findViewById(R.id.iconDeleteCustomProgram);
            txtAction = itemView.findViewById(R.id.txtActionCustomProgram);

            iconEdit.setOnClickListener(view -> onEditAction(getAdapterPosition()));
            iconDelete.setOnClickListener(view -> onDeleteAction(getAdapterPosition()));
        }

        @Override
        public void onClick(View view) {
            listener.onUserClicked(getAdapterPosition());
            Log.d("TAG", "onClick: " + getAdapterPosition());
        }
    }

    private void onEditAction(int index) {
        CustomProgram.getInstance().openDialogNewAction(index, actions.get(index));
    }

    private void onDeleteAction(int index) {
        actions.remove(index);
        updateActionsList(actions);
        CustomProgram.getInstance().checkListIsEmpty();
    }
}

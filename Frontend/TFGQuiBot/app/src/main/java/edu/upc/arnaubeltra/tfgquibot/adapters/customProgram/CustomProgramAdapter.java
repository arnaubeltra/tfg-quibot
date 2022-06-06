package edu.upc.arnaubeltra.tfgquibot.adapters.customProgram;

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
import edu.upc.arnaubeltra.tfgquibot.models.Action;
import edu.upc.arnaubeltra.tfgquibot.ui.customProgram.CustomProgram;

public class CustomProgramAdapter extends RecyclerView.Adapter<CustomProgramAdapter.CustomProgramViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    public interface ICustomProgramRCVItemClicked {
        void onUserClicked(int index);
    }

    private ICustomProgramRCVItemClicked listener;

    private ArrayList<Action> actions = new ArrayList<>();

    private CustomProgram customProgram = CustomProgram.getInstance();

    public CustomProgramAdapter(ICustomProgramRCVItemClicked listener) {
        this.listener = listener;
    }

    public void updateActionsList(ArrayList<Action> actions) {
        this.actions = actions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomProgramAdapter.CustomProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(customProgram.getCustomProgramContext()).inflate(R.layout.layout_custom_program, parent, false);
        return new CustomProgramViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomProgramAdapter.CustomProgramViewHolder holder, int position) {
        holder.iconAction.setImageResource(getIcon(actions.get(position).getName()));
        holder.iconEdit.setImageResource(R.drawable.icon_edit);
        holder.iconDelete.setImageResource(R.drawable.icon_delete);
        if (actions.get(position).getQuantity() != 0) {
            holder.txtAction.setMaxLines(2);
            holder.txtAction.setText(actions.get(position).getName() + "\n" + actions.get(position).getQuantity() + "ml");
        } else if (actions.get(position).getRepetitions() != 0) {
            holder.txtAction.setMaxLines(2);
            holder.txtAction.setText(actions.get(position).getName() + " " + actions.get(position).getRepetitions() + " " + CustomProgram.getInstance().getResources().getString(R.string.txtTimes) + "\n" +
                    CustomProgram.getInstance().getResources().getString(R.string.txtLast2) + " " + actions.get(position).getLastNInstructions() + " " + CustomProgram.getInstance().getResources().getString(R.string.txtActions));

        }else {
            holder.txtAction.setMaxLines(1);
            holder.txtAction.setText(actions.get(position).getName() + "\n");
        }
    }

    private int getIcon(String action) {
        if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtForward)))
            return R.drawable.icon_arrow_up_2;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtBackwards)))
            return R.drawable.icon_arrow_down_2;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtRight)))
            return R.drawable.icon_arrow_right_2;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtLeft)))
            return R.drawable.icon_arrow_left_2;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtLowerPipette)))
            return R.drawable.icon_lower_pipette;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtRaisePipette)))
            return R.drawable.icon_raise_pipette;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtSuck)))
            return R.drawable.icon_suck_2;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtUnsuck)))
            return R.drawable.icon_suck_2;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtSuckXMl)))
            return R.drawable.icon_suck_2;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtUnsuckXMl)))
            return R.drawable.icon_suck_2;
        else if (action.equals(CustomProgram.getInstance().getResources().getString(R.string.txtRepeatPreviousActions)))
            return R.drawable.icon_reload;
        else
            return R.drawable.icon_arrow_up_2;
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
        CustomProgram.getInstance().openDialogNewAction(index, "edit_action");
    }

    private void onDeleteAction(int index) {
        actions.remove(index);
        updateActionsList(actions);
        CustomProgram.getInstance().checkListIsEmpty();
    }
}

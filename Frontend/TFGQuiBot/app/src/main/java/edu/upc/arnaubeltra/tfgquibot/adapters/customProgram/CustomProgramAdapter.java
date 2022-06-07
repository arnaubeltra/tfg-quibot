package edu.upc.arnaubeltra.tfgquibot.adapters.customProgram;

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


// Adapter used in the Recycler View of the CustomProgram class.
public class CustomProgramAdapter extends RecyclerView.Adapter<CustomProgramAdapter.CustomProgramViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    // Interface used to handle element in list clicked.
    public interface ICustomProgramRCVItemClicked {
        void onUserClicked(int index);
    }

    // Listener of the element clicked.
    private ICustomProgramRCVItemClicked listener;
    // ArrayList where are stored the elements displayed in the RecyclerView.
    private ArrayList<Action> actions = new ArrayList<>();
    // Instance of the CustomProgram class
    private CustomProgram customProgram = CustomProgram.getInstance();

    // Constructor
    public CustomProgramAdapter(ICustomProgramRCVItemClicked listener) {
        this.listener = listener;
    }

    // Method called when Recycler View needs to be updated as new data has ben updated or data has been removed.
    public void updateActionsList(ArrayList<Action> actions) {
        this.actions = actions;
        notifyDataSetChanged();
    }

    // Inflate the layout of each element of the Recycler View. This layout represent each individual list element.
    @NonNull
    @Override
    public CustomProgramAdapter.CustomProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(customProgram.getCustomProgramContext()).inflate(R.layout.layout_custom_program, parent, false);
        return new CustomProgramViewHolder(itemView, listener);
    }

    // Configure each new element in the Recycler View, adding all the content needed.
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
            holder.txtAction.setText(actions.get(position).getName() + " " + actions.get(position).getRepetitions() + " " + CustomProgram.getInstance().getResources().getString(R.string.txtTimes) + "\n" + CustomProgram.getInstance().getResources().getString(R.string.txtLast2) + " " + actions.get(position).getLastNInstructions() + " " + CustomProgram.getInstance().getResources().getString(R.string.txtActions));
        } else {
            holder.txtAction.setMaxLines(1);
            holder.txtAction.setText(actions.get(position).getName() + "\n");
        }
    }

    // Method that returns an icon according to an action. Switch-case statement not possible to use as does not accept resources as return values.
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

    // Method that returns the number of items of the list.
    @Override
    public int getItemCount() {
        return actions.size();
    }

    // Method that handles when a row is moved up or down in the Recycler View (re-organizing). It basically updates the ArrayList with all the elements of the list.
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(actions, i, i + 1);
        } else {
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(actions, i, i - 1);
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    // ViewHolder class, that defines the structure of each element of the Recycler View.
    public class CustomProgramViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iconAction, iconEdit, iconDelete;
        private TextView txtAction;

        View rowView;

        // Definition of the elements of the view and call to methods if they have to perform any action.
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

        // Handler when clicking one of the Recycler View list elements.
        @Override
        public void onClick(View view) {
            listener.onUserClicked(getAdapterPosition());
        }
    }

    // When edit icon is pressed, call to openDialogNewAction method of the CustomProgram class.
    private void onEditAction(int index) {
        CustomProgram.getInstance().openDialogNewAction(index, "edit_action");
    }

    // Handler of what has to be done, when an element is deleted from the Recycler View.
    private void onDeleteAction(int index) {
        actions.remove(index);
        updateActionsList(actions);
        CustomProgram.getInstance().checkListIsEmpty();
    }
}

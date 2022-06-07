package edu.upc.arnaubeltra.tfgquibot.adapters.customProgram;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


// Class that handles movement (up or down) of the items inside the Recycler View.
public class ItemMoveCallback extends ItemTouchHelper.Callback {

    // Interface that defines method implemented by the CustomProgramAdapter class.
    public interface ItemTouchHelperContract {
        void onRowMoved(int fromPosition, int toPosition);
    }

    private final ItemTouchHelperContract mAdapter;

    // Constructor
    public ItemMoveCallback(ItemTouchHelperContract adapter) {
        this.mAdapter = adapter;
    }

    // Set longPressDrag to enabled (to be able to drag items)
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    // Set itemViewSwipe to not enabled, to avoid moving elements to left or right.
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }

    // Method that handles if item is being moved up or down.
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    // Method that handles movement of an item, sending initial position and end position.
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
}

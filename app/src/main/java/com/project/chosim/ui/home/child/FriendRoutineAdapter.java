package com.project.chosim.ui.home.child;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.project.chosim.data.model.Routine;
import com.project.chosim.databinding.ItemFriendRoutineBinding;

public class FriendRoutineAdapter extends ListAdapter<Routine, RecyclerView.ViewHolder> {

    protected FriendRoutineAdapter() {
        super(new DiffUtil.ItemCallback<Routine>() {
            @Override
            public boolean areItemsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
                return TextUtils.equals(oldItem.getDocumentId(), newItem.getDocumentId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
                return oldItem.isYourDone() == newItem.isYourDone() &&
                        TextUtils.equals(oldItem.getTitle(), newItem.getTitle()) &&
                        oldItem.isPrivateRoutine() == newItem.isPrivateRoutine() &&
                        oldItem.isAlarm() == newItem.isAlarm();
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFriendRoutineBinding binding = ItemFriendRoutineBinding.inflate(layoutInflater, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Routine routine = getItem(position);
            ItemFriendRoutineBinding binding = ((ItemViewHolder) holder).binding;

            if (routine.isPrivateRoutine()) {
                binding.titleTextView.setText("비공개 루틴");
                binding.titleTextView.setAlpha(0.38f);

            } else {
                binding.titleTextView.setText(routine.getTitle());
                binding.titleTextView.setAlpha(1f);
            }

            binding.notificationIndicator.setVisibility(routine.isAlarm() ? View.VISIBLE : View.GONE);
        }
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemFriendRoutineBinding binding;

        public ItemViewHolder(@NonNull ItemFriendRoutineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

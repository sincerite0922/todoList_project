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
import com.project.chosim.databinding.ItemRoutineBinding;

public class RoutineAdapter extends ListAdapter<Routine, RoutineAdapter.ItemViewHolder> {

    private OnItemClickListener onItemClickListener;

    protected RoutineAdapter() {
        super(new DiffUtil.ItemCallback<Routine>() {
            @Override
            public boolean areItemsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
                return TextUtils.equals(oldItem.getDocumentId(), newItem.getDocumentId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
                for (String key : oldItem.getCycle().keySet()) {
                    if (oldItem.getCycle().get(key) != newItem.getCycle().get(key)) {
                        return false;
                    }
                }

                return TextUtils.equals(oldItem.getTitle(), newItem.getTitle()) &&
                        oldItem.isAlarm() == newItem.isAlarm() &&
                        TextUtils.equals(oldItem.getAlarmTime(), newItem.getAlarmTime()) &&
                        oldItem.isRepeat() == newItem.isRepeat() &&
                        oldItem.isPrivateRoutine() == newItem.isPrivateRoutine() &&
                        oldItem.isMyDone() == newItem.isMyDone() &&
                        oldItem.isYourDone() == newItem.isYourDone() &&
                        ((int) oldItem.getTag()) == ((int) newItem.getTag());
            }

            @Override
            public Object getChangePayload(@NonNull Routine oldItem, @NonNull Routine newItem) {
                return new Object();
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemRoutineBinding binding = ItemRoutineBinding.inflate(layoutInflater, parent, false);
        binding.yourNameContainer.setVisibility(viewType == 0 ? View.GONE : View.VISIBLE);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Routine routine = getItem(position);
        ItemRoutineBinding binding = holder.binding;

        binding.yourNameTextView.setText(routine.getYourName() + "와(과)의 루틴");

        binding.myDoneCheckBox.setOnCheckedChangeListener(null);
        binding.myDoneCheckBox.setChecked(routine.isMyDone());
        binding.myDoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onItemClickListener != null) {
                onItemClickListener.onDoneCheckChanged(routine, isChecked);
            }
        });

        binding.titleTextView.setText(routine.getTitle());
        binding.notificationIndicator.setVisibility(routine.isAlarm() ? View.VISIBLE : View.GONE);
        binding.editButton.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onEditClicked(routine);
            }
        });

        if (routine.isTogether()) {
            binding.yourDoneCheckBox.setVisibility(View.VISIBLE);
            binding.yourDoneCheckBox.setChecked(routine.isYourDone());

        } else {
            binding.yourDoneCheckBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (int) getItem(position).getTag();
    }

    interface OnItemClickListener {
        void onDoneCheckChanged(Routine routine, boolean isChecked);

        void onEditClicked(Routine routine);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemRoutineBinding binding;

        public ItemViewHolder(@NonNull ItemRoutineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

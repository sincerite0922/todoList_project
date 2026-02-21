package com.project.chosim.ui.home.child;

import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.project.chosim.databinding.ItemFriendBinding;

public class FriendAdapter extends ListAdapter<Pair<String, String>, FriendAdapter.ItemViewHolder> {

    private OnItemClickListener onItemClickListener;

    protected FriendAdapter() {
        super(new DiffUtil.ItemCallback<Pair<String, String>>() {
            @Override
            public boolean areItemsTheSame(@NonNull Pair<String, String> oldItem, @NonNull Pair<String, String> newItem) {
                return TextUtils.equals(oldItem.first, newItem.first);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Pair<String, String> oldItem, @NonNull Pair<String, String> newItem) {
                return TextUtils.equals(oldItem.second, newItem.second);
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
        ItemFriendBinding binding = ItemFriendBinding.inflate(layoutInflater, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Pair<String, String> friend = getItem(position);
        ItemFriendBinding binding = holder.binding;

        String uid = friend.first;
        String userName = friend.second;

        binding.nameTextView.setText(userName);

        binding.getRoot().setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClicked(uid, userName);
            }
        });

        binding.getRoot().setOnLongClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onLongClicked(uid, userName);
            }

            return true;
        });
    }

    interface OnItemClickListener {
        void onClicked(String uid, String userName);

        void onLongClicked(String uid, String userName);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemFriendBinding binding;

        public ItemViewHolder(@NonNull ItemFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

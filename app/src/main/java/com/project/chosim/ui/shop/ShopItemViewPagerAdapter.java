package com.project.chosim.ui.shop;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.project.chosim.Constants;
import com.project.chosim.data.model.ShopItem;
import com.project.chosim.databinding.ItemShopBinding;

import java.util.ArrayList;
import java.util.List;


public class ShopItemViewPagerAdapter extends RecyclerView.Adapter<ShopItemViewPagerAdapter.ShopItemViewHolder> {

    private final List<ShopItem> dataSet;
    private final ShopFragment.Type type;
    private Consumer<ShopItem> onItemClickListener = null;


    public ShopItemViewPagerAdapter(List<ShopItem> items, ShopFragment.Type type) {
        this.dataSet = items;
        this.type = type;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOnItemClickListener(Consumer<ShopItem> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemShopBinding binding = ItemShopBinding.inflate(inflater, parent, false);
        return new ShopItemViewHolder(type, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopItemViewHolder holder, int position) {
        int startIndex = position * Constants.SEARCH_RESULT_ITEM_COUNT_PER_PAGE;
        int endIndex = Math.min((position + 1) * Constants.SEARCH_RESULT_ITEM_COUNT_PER_PAGE, dataSet.size());

        List<ShopItem> subList = dataSet.subList(startIndex, endIndex);

        for (int i = 0; i < holder.containers.size(); i++) {
            if (i < subList.size()) {
                ShopItem item = subList.get(i);

                holder.containers.get(i).setVisibility(View.VISIBLE);
                holder.containers.get(i).setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.accept(item);
                    }
                });

                if (item.getDrawableResId() != -1) {
                    holder.imageViews.get(i).setImageResource(item.getDrawableResId());

                } else {
                    holder.imageViews.get(i).setImageDrawable(null);
                }

                holder.textViews.get(i).setText(item.getTitle());

            } else {
                holder.containers.get(i).setVisibility(View.INVISIBLE);
                holder.containers.get(i).setOnClickListener(null);

                holder.imageViews.get(i).setImageDrawable(null);
                holder.textViews.get(i).setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        // 페이지 갯수는 전체 데이터 수 / 각 페이지에서 표시할 데이터 수의 올림값
        return (int) Math.ceil(dataSet.size() / (double) Constants.SEARCH_RESULT_ITEM_COUNT_PER_PAGE);
    }


    protected static class ShopItemViewHolder extends RecyclerView.ViewHolder {

        private final List<MaterialCardView> containers = new ArrayList<>();
        private final List<ImageView> imageViews = new ArrayList<>();
        private final List<TextView> textViews = new ArrayList<>();


        public ShopItemViewHolder(ShopFragment.Type type, @NonNull ItemShopBinding binding) {
            super(binding.getRoot());

            containers.add(binding.imageView1Container);
            containers.add(binding.imageView2Container);
            containers.add(binding.imageView3Container);
            containers.add(binding.imageView4Container);
            containers.add(binding.imageView5Container);
            containers.add(binding.imageView6Container);
            containers.add(binding.imageView7Container);
            containers.add(binding.imageView8Container);
            containers.add(binding.imageView9Container);

            imageViews.add(binding.imageView1);
            imageViews.add(binding.imageView2);
            imageViews.add(binding.imageView3);
            imageViews.add(binding.imageView4);
            imageViews.add(binding.imageView5);
            imageViews.add(binding.imageView6);
            imageViews.add(binding.imageView7);
            imageViews.add(binding.imageView8);
            imageViews.add(binding.imageView9);

            textViews.add(binding.textView1);
            textViews.add(binding.textView2);
            textViews.add(binding.textView3);
            textViews.add(binding.textView4);
            textViews.add(binding.textView5);
            textViews.add(binding.textView6);
            textViews.add(binding.textView7);
            textViews.add(binding.textView8);
            textViews.add(binding.textView9);

            if (type == ShopFragment.Type.WALLPAPER) {
                for (MaterialCardView container : containers) {
                    container.setContentPadding(0, 0, 0, 0);
                }
            }
        }
    }
}

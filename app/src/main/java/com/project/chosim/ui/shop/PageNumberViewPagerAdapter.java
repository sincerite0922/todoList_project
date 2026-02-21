package com.project.chosim.ui.shop;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.project.chosim.Constants;
import com.project.chosim.R;
import com.project.chosim.data.model.ShopItem;

import java.util.ArrayList;
import java.util.List;


public class PageNumberViewPagerAdapter extends RecyclerView.Adapter<PageNumberViewPagerAdapter.PageNumberItemViewHolder> {

    private final ViewPager2 itemViewPager;
    private final int pageCount;
    private Consumer<Integer> itemClickListener = null;


    public PageNumberViewPagerAdapter(ViewPager2 itemViewPager, List<ShopItem> searchResults) {
        this.itemViewPager = itemViewPager;
        pageCount = (int) Math.ceil(searchResults.size() / (double) Constants.SEARCH_RESULT_ITEM_COUNT_PER_PAGE);
    }

    public void setItemClickListener(Consumer<Integer> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public PageNumberItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);

        for (int i = 0; i < Constants.SEARCH_RESULT_MAXIMUM_PAGE_NUMBER_COUNT; i++) {
            View button = inflater.inflate(R.layout.item_page_number, layout, false);
            layout.addView(button);

            if (i != 0) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
                params.setMargins(context.getResources().getDimensionPixelSize(R.dimen.page_number_spacing), 0, 0, 0);
                button.setLayoutParams(params);
            }
        }

        return new PageNumberItemViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull PageNumberItemViewHolder holder, int position) {
        int startNumber = (position * Constants.SEARCH_RESULT_MAXIMUM_PAGE_NUMBER_COUNT) + 1;   // 1, 6, 11, ...
        int endNumber = Math.min(startNumber + Constants.SEARCH_RESULT_MAXIMUM_PAGE_NUMBER_COUNT - 1, pageCount);   // 5, 10, 15, ...
        int count = endNumber - startNumber + 1;

        for (Button button : holder.buttons) {
            button.setVisibility(getItemCount() == 1 ? View.GONE : View.INVISIBLE);
        }

        for (int i = 0; i < count; i++) {
            int number = startNumber + i;
            Button button = holder.buttons.get(i);

            button.setVisibility(View.VISIBLE);
            button.setText("" + number);
            button.setSelected(itemViewPager.getCurrentItem() == (number - 1));
            button.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    try {
                        Button b = (Button) v;
                        itemClickListener.accept(Integer.parseInt(b.getText().toString()));

                    } catch (Exception ignore) {
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // 갯수는 페이지 수 / 한 화면에 표시할 페이지 번호 수의 올림값
        return (int) Math.ceil(pageCount / (double) Constants.SEARCH_RESULT_MAXIMUM_PAGE_NUMBER_COUNT);
    }

    protected static class PageNumberItemViewHolder extends RecyclerView.ViewHolder {

        List<Button> buttons;

        public PageNumberItemViewHolder(@NonNull View itemView) {
            super(itemView);

            ArrayList<Button> buttons = new ArrayList<>();
            LinearLayout layout = (LinearLayout) itemView;

            for (int i = 0; i < layout.getChildCount(); i++) {
                buttons.add((Button) layout.getChildAt(i));
            }

            this.buttons = buttons;
        }
    }
}

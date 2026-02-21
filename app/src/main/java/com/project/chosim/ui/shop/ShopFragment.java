package com.project.chosim.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.project.chosim.Constants;
import com.project.chosim.R;
import com.project.chosim.data.model.ShopItem;
import com.project.chosim.databinding.FragmentWallpaperShopBinding;
import com.project.chosim.ui.BaseFragment;
import com.project.chosim.ui.MainViewModel;

import java.util.ArrayList;

public class ShopFragment extends BaseFragment {

    public enum Type {
        WALLPAPER, COSTUME
    }

    public static ShopFragment getInstance(Type type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type.name());

        ShopFragment fragment = new ShopFragment();
        fragment.setArguments(bundle);

        return fragment;
    }


    private Type type = Type.WALLPAPER;
    private FragmentWallpaperShopBinding binding;
    private MainViewModel mainViewModel;

    private PageNumberViewPagerAdapter pageNumberViewPagerAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().getString("type") != null) {
            type = Type.valueOf(getArguments().getString("type"));
        }

        if (savedInstanceState != null) {
            type = Type.valueOf(savedInstanceState.getString("type"));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("type", type.name());
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWallpaperShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getAppUser().observe(getViewLifecycleOwner(), appUser -> {
            if (appUser != null) {
                binding.pointTextView.setText("point: " + appUser.getPoint());
            }
        });

        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (type == Type.WALLPAPER) {
            binding.toolbar.setTitle("루티니 벽지");

        } else {
            binding.toolbar.setTitle("루티니 의상");
        }

        ArrayList<ShopItem> items = new ArrayList<>();

        // 더미 아이템 생성
        // 벽지 ID 는 0 부터, 의상 ID 는 1000 부터로 설정
        int i = type == Type.WALLPAPER ? 0 : 1000;
        int size = i + Constants.SEARCH_RESULT_ITEM_COUNT_PER_PAGE * 10;
        for (; i < size; i++) {
            items.add(new ShopItem(i));
        }

        {
            ShopItemViewPagerAdapter adapter = new ShopItemViewPagerAdapter(items, type);
            adapter.setOnItemClickListener(item -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", item);
                requireActivity().getSupportFragmentManager().setFragmentResult("shop", bundle);
                onBackPressed();
            });

            binding.itemViewPager.setAdapter(adapter);
            binding.itemViewPager.setUserInputEnabled(false);
        }

        {
            pageNumberViewPagerAdapter = new PageNumberViewPagerAdapter(binding.itemViewPager, items);
            pageNumberViewPagerAdapter.setItemClickListener(number -> {
                int prevItem = binding.itemViewPager.getCurrentItem();
                int nextItem = number - 1;

                int prevPositionInPageNumberViewPager = prevItem / Constants.SEARCH_RESULT_MAXIMUM_PAGE_NUMBER_COUNT;
                int nextPositionInPageNumberViewPager = nextItem / Constants.SEARCH_RESULT_MAXIMUM_PAGE_NUMBER_COUNT;

                binding.itemViewPager.setCurrentItem(nextItem, false);

                pageNumberViewPagerAdapter.notifyItemChanged(prevPositionInPageNumberViewPager);

                if (prevPositionInPageNumberViewPager != nextPositionInPageNumberViewPager) {
                    pageNumberViewPagerAdapter.notifyItemChanged(nextPositionInPageNumberViewPager);
                }
            });

            binding.pageNumberViewPager.setAdapter(pageNumberViewPagerAdapter);
            binding.pageNumberViewPager.setUserInputEnabled(false);

            // ViewPager 의 폭을 미리 정해놔야 함. 폭의 크기는 (각 페이지 번호 버튼의 폭 * 번호 최대 갯수) + (버튼 간 간격 * (번호 최대 갯수 - 1))
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.pageNumberViewPager.getLayoutParams();
            params.width = (getResources().getDimensionPixelSize(R.dimen.page_number_size) * Constants.SEARCH_RESULT_MAXIMUM_PAGE_NUMBER_COUNT) +
                    (getResources().getDimensionPixelSize(R.dimen.page_number_spacing) * (Constants.SEARCH_RESULT_MAXIMUM_PAGE_NUMBER_COUNT - 1));
            binding.pageNumberViewPager.setLayoutParams(params);

            ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    int lastPosition = binding.pageNumberViewPager.getAdapter().getItemCount() - 1;

                    if (position == lastPosition) {
                        binding.nextButton.setVisibility(View.INVISIBLE);

                    } else {
                        binding.nextButton.setVisibility(View.VISIBLE);
                    }

                    if (position == 0) {
                        binding.preButton.setVisibility(View.INVISIBLE);

                    } else {
                        binding.preButton.setVisibility(View.VISIBLE);
                    }
                }
            };

            binding.pageNumberViewPager.registerOnPageChangeCallback(pageChangeCallback);
            pageChangeCallback.onPageSelected(0);
        }

        binding.preButton.setOnClickListener(v -> {
            binding.pageNumberViewPager.setCurrentItem(binding.pageNumberViewPager.getCurrentItem() - 1, false);
        });

        binding.nextButton.setOnClickListener(v -> {
            binding.pageNumberViewPager.setCurrentItem(binding.pageNumberViewPager.getCurrentItem() + 1, false);
        });
    }
}

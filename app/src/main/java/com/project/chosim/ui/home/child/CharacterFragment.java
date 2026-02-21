package com.project.chosim.ui.home.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.project.chosim.data.model.ShopItem;
import com.project.chosim.databinding.FragmentCharacterBinding;
import com.project.chosim.ui.BaseFragment;
import com.project.chosim.ui.MainViewModel;
import com.project.chosim.ui.shop.ShopFragment;

public class CharacterFragment extends BaseFragment {

    private FragmentCharacterBinding binding;
    private MainViewModel mainViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCharacterBinding.inflate(inflater, container, false);
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

        binding.wallpaperButton.setOnClickListener(v ->
                startFragment(ShopFragment.getInstance(ShopFragment.Type.WALLPAPER), "shop"));

        binding.costumeButton.setOnClickListener(v ->
                startFragment(ShopFragment.getInstance(ShopFragment.Type.COSTUME), "shop"));

        requireActivity().getSupportFragmentManager().setFragmentResultListener(
                "shop",
                this,
                (requestKey, result) -> {
                    ShopItem item = result.getParcelable("item");
                    if (item == null) return;

                    if (item.getId() < 1000) { // 벽지
                        if (item.getDrawableResId() != -1) {
                            binding.wallpaperImageView.setImageResource(item.getDrawableResId());
                        } else {
                            binding.wallpaperImageView.setImageDrawable(null);
                        }
                    } else { // 의상

                    }
                });
    }
}

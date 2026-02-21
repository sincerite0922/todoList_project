package com.project.chosim.ui.home.child;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.project.chosim.Constants;
import com.project.chosim.data.model.Routine;
import com.project.chosim.databinding.FragmentFriendsBinding;
import com.project.chosim.ui.BaseFragment;
import com.project.chosim.ui.MainViewModel;
import com.project.chosim.ui.friend.AddFriendFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class FriendFragment extends BaseFragment {

    private final FriendAdapter friendAdapter = new FriendAdapter();
    private final FriendRoutineAdapter routineAdapter = new FriendRoutineAdapter();
    private FragmentFriendsBinding binding;
    private MainViewModel mainViewModel;
    private FriendViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel = new ViewModelProvider(this).get(FriendViewModel.class);

        // binding.toolbar.setNavigationOnClickListener(v -> startSingleTopActivity(SettingsFragment.class));
        binding.dateTextView.setText(Constants.dateFormat.format(new Date()));
        binding.addButton.setOnClickListener(v -> startFragment(new AddFriendFragment(), "AddFriend"));
        binding.addButton.hide();

        binding.routineRecyclerView.setAdapter(routineAdapter);
        binding.friendsRecyclerView.setAdapter(friendAdapter);

        friendAdapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
            @Override
            public void onClicked(String uid, String userName) {
                viewModel.selectFriend(uid, userName);
            }

            @Override
            public void onLongClicked(String uid, String userName) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setMessage(userName + "을 삭제하시겠습니까? 친구와의 루틴도 전부 삭제되며 복원할 수 없습니다.")
                        .setPositiveButton("확인", (dialog, which) -> {
                            if (binding.progressView.getVisibility() == View.VISIBLE) return;

                            binding.progressView.setVisibility(View.VISIBLE);
                            viewModel.removeFriend(uid, result -> {
                                binding.progressView.setVisibility(View.GONE);

                                if (result) {
                                    showToastMessage("삭제되었습니다.");
                                } else {
                                    showToastMessage("오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.");
                                }
                            });

                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });

        mainViewModel.getAppUser().observe(getViewLifecycleOwner(), appUser -> {
            if (appUser == null) return;

            binding.pointTextView.setText("point: " + appUser.getPoint());

            ArrayList<Pair<String, String>> items = new ArrayList<>();
            for (Map.Entry<String, String> entry : appUser.getFriends().entrySet()) {
                items.add(Pair.create(entry.getKey(), entry.getValue()));
            }

            Collections.sort(items, (o1, o2) -> o1.second.compareTo(o2.second));

            friendAdapter.submitList(items);

            if (items.size() < 10 && binding.addButton.isOrWillBeHidden()) {
                binding.addButton.show();

            } else if (items.size() == 10 && binding.addButton.isOrWillBeShown()) {
                binding.addButton.hide();
            }

            String uid = viewModel.getSelectedFriendUidValue();
            if (!TextUtils.isEmpty(uid) && !appUser.getFriends().containsKey(uid)) {
                viewModel.clearFriendSelection();
            }
        });

        viewModel.getFriendName().observe(getViewLifecycleOwner(), friendName -> {
            if (TextUtils.isEmpty(friendName)) {
                binding.toolbar.setTitle("");

            } else {
                binding.toolbar.setTitle(friendName + "'s routine");
            }
        });

        viewModel.getRoutines().observe(getViewLifecycleOwner(), routines -> {
            if (routines == null) return;

            int doneCount = 0;
            for (Routine routine : routines) {
                if (routine.isMyDone()) {
                    doneCount += 1;
                }
            }

            binding.greateIndicator.setVisibility(View.GONE);
            binding.goodIndicator.setVisibility(View.GONE);
            binding.badIndicator.setVisibility(View.GONE);

            if (routines.size() > 0) {
                if (doneCount == routines.size()) {
                    binding.greateIndicator.setVisibility(View.VISIBLE);

                } else if (doneCount == 0) {
                    binding.badIndicator.setVisibility(View.VISIBLE);

                } else {
                    binding.goodIndicator.setVisibility(View.VISIBLE);
                }
            }

            ArrayList<Routine> items = new ArrayList<>(routines);
            routineAdapter.submitList(items);
        });
    }
}

package com.project.chosim.ui.routine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.project.chosim.R;
import com.project.chosim.data.model.Routine;
import com.project.chosim.databinding.FragmentRoutineEditorBinding;
import com.project.chosim.ui.BaseFragment;
import com.project.chosim.ui.MainViewModel;
import com.project.chosim.receiver.AlarmRegistrationReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RoutineEditorFragment extends BaseFragment {

    private static final String ARG_ROUTINE = "ARG_ROUTINE";
    private final HashMap<Integer, FrameLayout> weekButtons = new HashMap<>();
    private final ArrayList<String> friends = new ArrayList<>();
    private final ArrayList<String> uidList = new ArrayList<>();
    private Routine routine;
    private FragmentRoutineEditorBinding binding;
    private MainViewModel mainViewModel;
    private RoutineEditorViewModel viewModel;

    public static RoutineEditorFragment getInstance(Routine routine) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_ROUTINE, routine);

        RoutineEditorFragment fragment = new RoutineEditorFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            routine = getArguments().getParcelable(ARG_ROUTINE);
        }

        if (savedInstanceState != null) {
            routine = savedInstanceState.getParcelable(ARG_ROUTINE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_ROUTINE, routine);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRoutineEditorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel = new ViewModelProvider(this, new RoutineEditorViewModelFactory(routine))
                .get(RoutineEditorViewModel.class);

        initUi();
        bindRoutine();
    }

    private void initUi() {
        mainViewModel.getAppUser().observe(getViewLifecycleOwner(), appUser -> {
            friends.clear();
            uidList.clear();

            friends.add("개인 루틴");
            uidList.add(null);

            if (appUser != null) {
                for (Map.Entry<String, String> entry : appUser.getFriends().entrySet()) {
                    uidList.add(entry.getKey());
                    friends.add(entry.getValue());
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_dropdown, friends);
            ((AutoCompleteTextView) binding.friendsTextField.getEditText()).setAdapter(adapter);

            if (routine != null && routine.isTogether()) {
                ((AutoCompleteTextView) binding.friendsTextField.getEditText()).setText(routine.getYourName(), false);
            } else {
                ((AutoCompleteTextView) binding.friendsTextField.getEditText()).setText("개인 루틴", false);
            }
        });

        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.nameTextField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.doneButton.setEnabled(checkFieldsValidation());
            }
        });

        weekButtons.put(Calendar.MONDAY, binding.mondayButton);
        weekButtons.put(Calendar.TUESDAY, binding.tuesdayButton);
        weekButtons.put(Calendar.WEDNESDAY, binding.wednesdayButton);
        weekButtons.put(Calendar.THURSDAY, binding.thursdayButton);
        weekButtons.put(Calendar.FRIDAY, binding.fridayButton);
        weekButtons.put(Calendar.SATURDAY, binding.saturdayButton);
        weekButtons.put(Calendar.SUNDAY, binding.sundayButton);

        for (FrameLayout button : weekButtons.values()) {
            button.setSelected(false);

            button.setOnClickListener(v -> {
                v.setSelected(!v.isSelected());
                binding.doneButton.setEnabled(checkFieldsValidation());
            });
        }
    }

    private void bindRoutine() {
        if (routine == null) {
            // 루틴 추가
            binding.toolbar.getMenu().findItem(R.id.action_delete).setVisible(false);
            binding.doneButton.setOnClickListener(v -> create());

        } else {
            // 루틴 수정
            viewModel.getRoutine().observe(getViewLifecycleOwner(), r -> {
                if (r == null) {
                    showToastMessage(routine.getTitle() + " 루틴이 삭제되었습니다.");
                    onBackPressed();
                }
            });

            binding.titleTextView.setText("루틴 설정");
            binding.toolbar.getMenu().findItem(R.id.action_delete).setVisible(true);
            binding.toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_delete) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setMessage("해당 루틴을 삭제하시겠습니까?")
                            .setPositiveButton("확인", (dialog, which) -> {
                                viewModel.delete();
                                requireContext().sendBroadcast(new Intent(requireContext(), AlarmRegistrationReceiver.class));
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }

                return true;
            });

            binding.doneButton.setText("수정완료");
            binding.doneButton.setOnClickListener(v -> update());

            binding.nameTextField.getEditText().append(routine.getTitle());
            binding.alarmSwitch.setChecked(routine.isAlarm());

            String[] token = routine.getAlarmTime().split(":");
            int hour = Integer.parseInt(token[0]);
            int minute = Integer.parseInt(token[1]);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                binding.timePicker.setHour(hour);
                binding.timePicker.setMinute(minute);
            } else {
                binding.timePicker.setCurrentHour(hour);
                binding.timePicker.setCurrentMinute(minute);
            }

            for (Map.Entry<String, Boolean> entry : routine.getCycle().entrySet()) {
                int week = Integer.parseInt(entry.getKey());
                weekButtons.get(week).setSelected(entry.getValue());
            }

            binding.repeatSwitch.setChecked(routine.isRepeat());
            binding.privateSwitch.setChecked(routine.isPrivateRoutine());

            if (routine.isTogether()) {
                ((AutoCompleteTextView) binding.friendsTextField.getEditText()).setText(routine.getYourName(), false);
            } else {
                ((AutoCompleteTextView) binding.friendsTextField.getEditText()).setText("개인 루틴", false);
            }

            binding.friendsTextField.setEnabled(false);

            binding.doneButton.setEnabled(checkFieldsValidation());
        }
    }

    private void create() {
        String title = binding.nameTextField.getEditText().getText().toString().trim();
        boolean alarm = binding.alarmSwitch.isChecked();

        String alarmTime;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmTime = String.format(Locale.US, "%02d:%02d", binding.timePicker.getHour(), binding.timePicker.getMinute());
        } else {
            alarmTime = String.format(Locale.US, "%02d:%02d", binding.timePicker.getCurrentHour(), binding.timePicker.getCurrentMinute());
        }

        HashMap<String, Boolean> cycle = new HashMap<>();
        for (int week : weekButtons.keySet()) {
            cycle.put(String.valueOf(week), weekButtons.get(week).isSelected());
        }

        boolean repeat = binding.repeatSwitch.isChecked();
        boolean privateRoutine = binding.privateSwitch.isChecked();

        String friendName = binding.friendsTextField.getEditText().getText().toString();
        int index = friends.indexOf(friendName);

        Pair<String, String> friend = null;
        if (index > 0) {
            friend = Pair.create(uidList.get(index), friends.get(index));
        }

        viewModel.create(title, alarm, alarmTime, cycle, repeat, privateRoutine, friend);
        requireContext().sendBroadcast(new Intent(requireContext(), AlarmRegistrationReceiver.class));

        showToastMessage(title + " 루틴이 추가되었습니다.");
        onBackPressed();
    }

    private void update() {
        String title = binding.nameTextField.getEditText().getText().toString().trim();
        boolean alarm = binding.alarmSwitch.isChecked();

        String alarmTime;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmTime = String.format(Locale.US, "%02d:%02d", binding.timePicker.getHour(), binding.timePicker.getMinute());
        } else {
            alarmTime = String.format(Locale.US, "%02d:%02d", binding.timePicker.getCurrentHour(), binding.timePicker.getCurrentMinute());
        }

        HashMap<String, Boolean> cycle = new HashMap<>();
        for (int week : weekButtons.keySet()) {
            cycle.put(String.valueOf(week), weekButtons.get(week).isSelected());
        }

        boolean repeat = binding.repeatSwitch.isChecked();
        boolean privateRoutine = binding.privateSwitch.isChecked();

        viewModel.update(title, alarm, alarmTime, cycle, repeat, privateRoutine);
        requireContext().sendBroadcast(new Intent(requireContext(), AlarmRegistrationReceiver.class));

        showToastMessage(title + " 루틴이 수정되었습니다.");
        onBackPressed();
    }

    private boolean checkFieldsValidation() {
        boolean hasCheckedCycle = false;
        for (FrameLayout button : weekButtons.values()) {
            if (button.isSelected()) {
                hasCheckedCycle = true;
                break;
            }
        }

        return !binding.nameTextField.getEditText().getText().toString().trim().isEmpty() && hasCheckedCycle;
    }
}

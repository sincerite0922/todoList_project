package com.project.chosim.ui.home.child;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;
import com.kizitonwose.calendarview.utils.Size;
import com.project.chosim.R;
import com.project.chosim.databinding.FragmentEvaluationBinding;
import com.project.chosim.databinding.ItemCalendarDayBinding;
import com.project.chosim.ui.BaseFragment;
import com.project.chosim.ui.MainViewModel;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EvaluationFragment extends BaseFragment {

    private FragmentEvaluationBinding binding;
    private MainViewModel mainViewModel;
    private EvaluationViewModel viewModel;

    private Calendar accountCreationDate;

    private YearMonth currentMonth = YearMonth.now();
    private final YearMonth firstMonth = YearMonth.of(2000, 1);
    private final YearMonth lastMonth = YearMonth.now();

    private Map<Integer, Integer> rates;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEvaluationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel = new ViewModelProvider(this).get(EvaluationViewModel.class);

        mainViewModel.getAppUser().observe(getViewLifecycleOwner(), appUser -> {
            if (appUser != null) {
                Date timestamp = appUser.getTimestamp();

                if (timestamp != null && accountCreationDate == null) {
                    accountCreationDate = Calendar.getInstance();
                    accountCreationDate.setTime(timestamp);
                    accountCreationDate.set(Calendar.HOUR_OF_DAY, 0);
                    accountCreationDate.set(Calendar.MINUTE, 0);
                    accountCreationDate.set(Calendar.SECOND, 0);
                    accountCreationDate.set(Calendar.MILLISECOND, 0);

                    if (rates != null) {
                        binding.calendarView.notifyMonthChanged(currentMonth);
                        setChartData();
                    }
                }
            }
        });

        View cell = getLayoutInflater().inflate(R.layout.item_calendar_day, null);
        cell.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int height = cell.getMeasuredHeight();

        binding.calendarView.setDaySize(new Size(Integer.MIN_VALUE, height));

        binding.calendarView.setMonthScrollListener(calendarMonth -> {
            currentMonth = calendarMonth.getYearMonth();

            String yearMonth = String.format(Locale.KOREA, "%04d년 %02d월", calendarMonth.getYear(), calendarMonth.getMonth());
            binding.monthTextView.setText(yearMonth);

            binding.previousMonthButton.setEnabled(currentMonth.isAfter(firstMonth));
            binding.nextMonthButton.setEnabled(currentMonth.isBefore(lastMonth));

            viewModel.selectMonth(currentMonth);

            return null;
        });

        binding.previousMonthButton.setOnClickListener(v -> {
            binding.calendarView.scrollToMonth(currentMonth.minusMonths(1));
        });

        binding.nextMonthButton.setOnClickListener(v -> {
            binding.calendarView.scrollToMonth(currentMonth.plusMonths(1));
        });

        binding.calendarView.setDayBinder(new DayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer viewContainer, @NonNull CalendarDay day) {
                int textColor;
                boolean isThisMonth = day.getOwner() == DayOwner.THIS_MONTH;
                float alpha = isThisMonth ? 1f : 0.38f;

                DayOfWeek dayOfWeek = day.getDate().getDayOfWeek();

                if (dayOfWeek == DayOfWeek.SUNDAY) {
                    textColor = ContextCompat.getColor(requireContext(), R.color.colorSunday);
                } else if (dayOfWeek == DayOfWeek.SATURDAY) {
                    textColor = ContextCompat.getColor(requireContext(), R.color.colorSaturday);
                } else {
                    textColor = getThemeColor(com.google.android.material.R.attr.colorOnSurface);
                }

                viewContainer.binding.calendarDayText.setTextColor(textColor);
                viewContainer.binding.calendarDayText.setText(String.valueOf(day.getDate().getDayOfMonth()));
                viewContainer.binding.calendarDayText.setAlpha(alpha);

                if (isThisMonth && rates != null) {
                    if (rates.containsKey(day.getDate().getDayOfMonth())) {
                        int rate = rates.get(day.getDate().getDayOfMonth());

                        if (rate == 100) {
                            viewContainer.binding.rateIndicator.setCardBackgroundColor(
                                    ContextCompat.getColor(requireContext(), R.color.evaluate_great));

                        } else if (rate == 0) {
                            viewContainer.binding.rateIndicator.setCardBackgroundColor(
                                    ContextCompat.getColor(requireContext(), R.color.evaluate_bad));

                        } else {
                            viewContainer.binding.rateIndicator.setCardBackgroundColor(
                                    ContextCompat.getColor(requireContext(), R.color.evaluate_good));
                        }

                        return;
                    }
                }

                viewContainer.binding.rateIndicator.setCardBackgroundColor(Color.WHITE);
                viewContainer.binding.getRoot().setOnClickListener(null);
            }
        });

        binding.calendarView.setup(firstMonth, lastMonth, DayOfWeek.SUNDAY);
        binding.calendarView.scrollToMonth(currentMonth);

        binding.chart.getDescription().setEnabled(false);
        binding.chart.getLegend().setEnabled(false);
        binding.chart.getXAxis().setAxisMinimum(1f);
        binding.chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.chart.getXAxis().setDrawGridLines(false);

        binding.chart.getAxisLeft().setAxisMinimum(0f);
        binding.chart.getAxisLeft().setAxisMaximum(100f);
        binding.chart.getAxisLeft().setDrawGridLines(false);
        binding.chart.getAxisLeft().setLabelCount(6);
        binding.chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return "" + ((int) value) + "%";
            }
        });

        binding.chart.getAxisRight().setEnabled(false);

        setChartData();

        viewModel.getRates().observe(getViewLifecycleOwner(), _rates -> {
            if (accountCreationDate == null) return;

            Calendar currentMonth = Calendar.getInstance();
            currentMonth.set(this.currentMonth.getYear(), this.currentMonth.getMonthValue() - 1, 1);

            HashMap<Integer, Integer> rates = new HashMap<>();

            if (currentMonth.before(accountCreationDate)) {
                for (Integer day : _rates.keySet()) {
                    currentMonth.set(Calendar.DAY_OF_MONTH, day);

                    if (!currentMonth.before(accountCreationDate)) {
                        rates.put(day, _rates.get(day));
                    }
                }
            }

            this.rates = rates;

            binding.calendarView.notifyMonthChanged(this.currentMonth);
            setChartData();
        });
    }

    private int getThemeColor(@AttrRes int resId) {
        final TypedValue value = new TypedValue();
        requireContext().getTheme().resolveAttribute(resId, value, true);
        return value.data;
    }

    private void setChartData() {
        ArrayList<Entry> values = new ArrayList<Entry>();

        int day = 1;
        while (currentMonth.isValidDay(day)) {
            int rate = 0;

            if (rates != null && rates.containsKey(day)) {
                rate = rates.get(day);
            }

            values.add(new Entry(day, rate));

            day += 1;
        }

        LineDataSet set;

        if (binding.chart.getData() != null && binding.chart.getData().getDataSetCount() > 0) {
            set = (LineDataSet) binding.chart.getData().getDataSetByIndex(0);
            set.setValues(values);
            set.notifyDataSetChanged();
            binding.chart.getData().getDataSetCount();
            binding.chart.notifyDataSetChanged();
            binding.chart.invalidate();

        } else {
            set = new LineDataSet(values, "Rates");
            set.setDrawCircles(false);
            set.setColor(Color.BLACK);
            set.setLineWidth(1f);
            set.setDrawValues(false);
            set.setHighlightEnabled(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);

            LineData data = new LineData(dataSets);

            binding.chart.setData(data);
        }
    }


    private static class DayViewContainer extends ViewContainer {
        ItemCalendarDayBinding binding;

        public DayViewContainer(View view) {
            super(view);

            binding = ItemCalendarDayBinding.bind(view);
        }
    }
}

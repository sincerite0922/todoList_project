package com.project.chosim.ui;

import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.project.chosim.R;

public abstract class BaseFragment extends Fragment {

    protected void startSingleTopActivity(Class<?> cls) {
        if (isDetached()) return;

        Intent intent = new Intent(requireContext(), cls);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void showToastMessage(String message) {
        if (isDetached()) return;

        Toast.makeText(requireContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void onBackPressed() {
        if (isDetached()) return;

        requireActivity().onBackPressed();
    }

    protected void startFragment(Fragment fragment, String tag) {
        if (isDetached()) return;

        if (requireActivity().getSupportFragmentManager().findFragmentByTag(tag) != null) return;

        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment, tag)
                .addToBackStack(null)
                .commit();
    }
}

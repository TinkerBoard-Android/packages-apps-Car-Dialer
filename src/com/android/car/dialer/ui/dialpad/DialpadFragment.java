/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.car.dialer.ui.dialpad;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.car.apps.common.FabDrawable;
import com.android.car.dialer.R;
import com.android.car.dialer.log.L;
import com.android.car.dialer.telecom.TelecomUtils;
import com.android.car.dialer.telecom.UiCallManager;
import com.android.car.dialer.ui.common.DialerBaseFragment;

/**
 * Fragment that controls the dialpad.
 */
public class DialpadFragment extends DialerBaseFragment implements
        KeypadFragment.KeypadCallback {
    private static final String TAG = "CD.DialpadFragment";
    private static final String DIAL_NUMBER_KEY = "DIAL_NUMBER_KEY";
    private static final String DIALPAD_MODE_KEY = "DIALPAD_MODE_KEY";
    private static final String PLUS_DIGIT = "+";
    private static final int MAX_DIAL_NUMBER = 20;

    /**
     * Shows the dialpad for an active phone call.
     */
    private static final int MODE_IN_CALL = 1;

    /**
     * Shows dialpad for dialing.
     */
    private static final int MODE_DIAL = 2;

    private TextView mTitleView;
    private int mMode;
    private StringBuffer mNumber = new StringBuffer(MAX_DIAL_NUMBER);

    /**
     * Creates a new instance of the {@link DialpadFragment} which is used for dialing a number.
     *
     * @param dialNumber The given number as the one to dial.
     */
    public static DialpadFragment newPlaceCallDialpad(@Nullable String dialNumber) {
        DialpadFragment fragment = new DialpadFragment();

        Bundle args = new Bundle();
        args.putInt(DIALPAD_MODE_KEY, MODE_DIAL);
        if (!TextUtils.isEmpty(dialNumber)) {
            args.putString(DIAL_NUMBER_KEY, dialNumber);
        }
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Returns a new instance of the {@link DialpadFragment} which runs in an active call for
     * dialing extension number, etc.
     */
    public static DialpadFragment newInCallDialpad() {
        DialpadFragment fragment = new DialpadFragment();

        Bundle args = new Bundle();
        args.putInt(DIALPAD_MODE_KEY, MODE_IN_CALL);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mMode = getArguments().getInt(DIALPAD_MODE_KEY);
        L.d(TAG, "onCreateView mode: " + mMode);
        View rootView = inflater.inflate(R.layout.dialpad_fragment, container, false);
        Fragment keypadFragment = KeypadFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.dialpad_fragment_container, keypadFragment)
                .commit();

        if (getArguments() != null && getArguments().containsKey(DIAL_NUMBER_KEY)) {
            appendDialedNumber(getArguments().getString(DIAL_NUMBER_KEY));
        }

        mTitleView = rootView.findViewById(R.id.title);
        ImageButton callButton = rootView.findViewById(R.id.call_button);
        ImageButton deleteButton = rootView.findViewById(R.id.delete_button);

        if (mMode == MODE_IN_CALL) {
            mTitleView.setText("");
            deleteButton.setVisibility(View.GONE);
            callButton.setVisibility(View.GONE);
        } else {
            mTitleView.setText(getContext().getString(R.string.dial_a_number));
            callButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            Context context = getContext();
            FabDrawable callDrawable = new FabDrawable(context);
            callDrawable.setFabAndStrokeColor(context.getColor(R.color.phone_call));
            callButton.setBackground(callDrawable);
            callButton.setOnClickListener((unusedView) -> {
                if (!TextUtils.isEmpty(mNumber.toString()) && mMode == MODE_DIAL) {
                    UiCallManager.get().safePlaceCall(mNumber.toString(), false);
                }
            });
            deleteButton.setOnClickListener(v -> removeLastDigit());
            deleteButton.setOnLongClickListener(v -> {
                clearDialedNumber();
                return true;
            });
        }

        return rootView;
    }

    @Override
    public void onDialVoiceMail() {
        UiCallManager.get().callVoicemail();
    }

    @Override
    public void onAppendDigit(String digit) {
        if (PLUS_DIGIT.equals(digit)) {
            removeLastDigit();
        }
        appendDialedNumber(digit);
    }

    private String getFormattedNumber(String number) {
        return TelecomUtils.getFormattedNumber(getContext(), number);
    }

    private void clearDialedNumber() {
        mNumber.setLength(0);
        mTitleView.setText(getContext().getString(R.string.dial_a_number));
    }

    private void removeLastDigit() {
        if (mNumber.length() != 0) {
            mNumber.deleteCharAt(mNumber.length() - 1);
            mTitleView.setText(getFormattedNumber(mNumber.toString()));
        }

        if (mNumber.length() == 0 && mMode == MODE_DIAL) {
            mTitleView.setText(R.string.dial_a_number);
        }
    }

    private void appendDialedNumber(String number) {
        mNumber.append(number);
        if (mMode == MODE_DIAL && mNumber.length() < MAX_DIAL_NUMBER) {
            mTitleView.setText(getFormattedNumber(mNumber.toString()));
        } else {
            mTitleView.setText(mNumber.toString());
        }
    }
}
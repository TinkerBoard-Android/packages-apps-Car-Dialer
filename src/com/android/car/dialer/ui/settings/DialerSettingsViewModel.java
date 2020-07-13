/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.car.dialer.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.android.car.dialer.livedata.HfpDeviceListLiveData;

/**
 * ViewModel for {@link DialerSettingsFragment}
 */
public class DialerSettingsViewModel extends AndroidViewModel {
    private static final String EMPTY_STRING = "";
    private final HfpDeviceListLiveData mHfpDeviceListLiveData;

    public DialerSettingsViewModel(@NonNull Application application) {
        super(application);
        mHfpDeviceListLiveData = new HfpDeviceListLiveData(application.getApplicationContext());
    }

    /**
     * Returns the LiveData for the first HFP device's name.  Returns an empty string if there's no
     * device connected.
     */
    public LiveData<String> getFirstHfpConnectedDeviceName() {
        return Transformations.map(mHfpDeviceListLiveData, (devices) ->
                devices != null && !devices.isEmpty()
                        ? devices.get(0).getName()
                        : EMPTY_STRING);
    }
}

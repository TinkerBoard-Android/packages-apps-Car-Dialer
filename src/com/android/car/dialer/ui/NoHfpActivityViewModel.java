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

package com.android.car.dialer.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.car.dialer.bluetooth.UiBluetoothMonitor;
import com.android.car.dialer.livedata.BluetoothErrorStringLiveData;

/** View model for {@link NoHfpFragment} */
public class NoHfpActivityViewModel extends AndroidViewModel {

    private final LiveData<Boolean> mHasHfpDeviceConnectedLiveData;
    private final LiveData<String> mBluetoothErrorStringLiveData;

    public NoHfpActivityViewModel(@NonNull Application application) {
        super(application);
        mHasHfpDeviceConnectedLiveData = UiBluetoothMonitor.get().hasHfpDeviceConnected();
        mBluetoothErrorStringLiveData = BluetoothErrorStringLiveData.get(application);
    }

    public LiveData<String> getBluetoothErrorStringLiveData() {
        return mBluetoothErrorStringLiveData;
    }

    /** Returns a {@link LiveData} which monitors if there are any connected HFP devices. */
    public LiveData<Boolean> hasHfpDeviceConnected() {
        return mHasHfpDeviceConnectedLiveData;
    }
}

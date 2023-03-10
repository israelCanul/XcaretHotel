// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.xcaret.inappmessaging_display.display.internal.injection.components;

import android.app.Application;
import android.util.DisplayMetrics;

import com.xcaret.inappmessaging_display.display.internal.BindingWrapperFactory;
import com.xcaret.inappmessaging_display.display.internal.FiamWindowManager;
import com.xcaret.inappmessaging_display.display.internal.InAppMessageLayoutConfig;
import com.xcaret.inappmessaging_display.display.internal.injection.modules.ApplicationModule;
import com.xcaret.inappmessaging_display.display.internal.injection.modules.InflaterConfigModule;

import dagger.Component;
import java.util.Map;
import javax.inject.Provider;
import javax.inject.Singleton;

/** @hide */
@Singleton
@Component(modules = {ApplicationModule.class, InflaterConfigModule.class})
public interface UniversalComponent {
  Application providesApplication();

  DisplayMetrics displayMetrics();

  FiamWindowManager fiamWindowManager();

  BindingWrapperFactory inflaterClient();

  Map<String, Provider<InAppMessageLayoutConfig>> myKeyStringMap();
}

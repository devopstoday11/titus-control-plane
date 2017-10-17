/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.netflix.titus.common.util.proxy;

import com.google.common.base.Preconditions;
import io.netflix.titus.common.util.proxy.annotation.NoIntercept;
import io.netflix.titus.common.util.proxy.annotation.ObservableResult;
import rx.Observable;

@ObservableResult
public interface MyApi {

    String echo(String message);

    @NoIntercept
    String notInterceptedEcho(String message);

    @ObservableResult
    Observable<String> observableEcho(String message);

    @ObservableResult(enabled = false)
    Observable<String> untrackedObservableEcho(String message);

    class MyApiImpl implements MyApi {
        @Override
        public String echo(String message) {
            return buildReply(message);
        }

        @Override
        public String notInterceptedEcho(String message) {
            return echo(message);
        }

        @Override
        public Observable<String> observableEcho(String message) {
            return Observable.create(subscriber -> {
                subscriber.onNext(buildReply(message));
                subscriber.onCompleted();
            });
        }

        @Override
        public Observable<String> untrackedObservableEcho(String message) {
            return observableEcho(message);
        }

        String buildReply(String message) {
            Preconditions.checkNotNull(message);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                sb.append(message);
            }
            return sb.toString();
        }
    }
}

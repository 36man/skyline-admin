/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.skyline.admin.server.support.k8s;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

@Slf4j
public class WatcherWrapper<T,R> implements Watcher<T> {

    private CompletableFuture<R> completableFuture = new CompletableFuture<>();

    private BiFunction<Action, T,R> callback;

    public WatcherWrapper(BiFunction<Action, T, R> callback) {
        this.callback = callback;
    }

    @Override
    public void eventReceived(Action action, T resource) {
        R value = callback.apply(action, resource);
        if (value != null) {
            completableFuture.complete(value);
        }
    }

    @Override
    public void onClose(WatcherException e) {
        if (e != null) {
            completableFuture.completeExceptionally(e);
        }
    }

    public void setValue(R value){
        completableFuture.complete(value);
    }

    public R get(){
        try {
            return completableFuture.get();
        } catch (Exception ex) {
            log.error("watcher error", ex);
            return null;
        }
    }

    public  R get(long timeout){
        try{
            return completableFuture.get(timeout, TimeUnit.MILLISECONDS);
        }catch(Exception ex){
            log.error("watcher error", ex);
            return null;
        }
    }
}

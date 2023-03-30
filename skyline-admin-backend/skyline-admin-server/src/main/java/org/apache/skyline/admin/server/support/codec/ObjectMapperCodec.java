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

package org.apache.skyline.admin.server.support.codec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ObjectMapperCodec {

    private final ObjectMapper objectMapper;

    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return doDeserialize(bytes, new Action<T>() {
            @Override
            public <T> T get() throws Exception {
                return (T)objectMapper.readValue(bytes, clazz);
            }
        });
    }


    public <T> T deserialize(byte[] bytes, TypeReference<T> tTypeReference) {
        return doDeserialize(bytes, new Action<T>() {
            @Override
            public <T> T get() throws Exception {
                return (T)objectMapper.readValue(bytes, tTypeReference);
            }
        });
    }

    public <T> T deserialize(String content, Class<T> clazz) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return deserialize(content.getBytes(), clazz);
    }

    private <T> T doDeserialize(byte [] bytes, Action<T> action) {
        try {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return (T)action.get();
        } catch (Exception exception) {
            throw new RuntimeException(
                    String.format("objectMapper! deserialize error %s", exception));
        }
    }

    public String serialize(Object object) {
        try {

            if (object == null) {
                return null;
            }

            return objectMapper.writeValueAsString(object);

        } catch (Exception ex) {
            throw new RuntimeException(String.format("objectMapper! serialize error %s", ex));
        }
    }

    public <T> T serialize(Object object, Class<T> clazz){
        try {

            if (object == null) {
                return null;
            }
            return (T)objectMapper.convertValue(object,clazz);

        } catch (Exception ex) {
            throw new RuntimeException(String.format("objectMapper! serialize error %s", ex));
        }
    }

    public interface Action<T>{

        <T> T get()throws Exception;

    }

}

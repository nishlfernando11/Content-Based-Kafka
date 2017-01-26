/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.kafka.common.requests;

import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.protocol.ProtoUtils;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;

import java.nio.ByteBuffer;

public class SendInterestsRequest extends AbstractRequest {

    private static final Schema CURRENT_SCHEMA = ProtoUtils.currentRequestSchema(ApiKeys.SEND_INTERESTS.id);
    private static final String GROUP_ID_KEY_NAME = "group_id";
    private static final String INTERESTS_KEY_NAME = "interests";

    private final String groupId;
    private final String interests;

    public SendInterestsRequest(String groupId, String interests) {
        super(new Struct(CURRENT_SCHEMA));
        struct.set(GROUP_ID_KEY_NAME, groupId);
        struct.set(INTERESTS_KEY_NAME, interests);
        this.groupId = groupId;
        this.interests = interests;
    }

    public SendInterestsRequest(Struct struct) {
        super(struct);
        groupId = struct.getString(GROUP_ID_KEY_NAME);
        interests = struct.getString(INTERESTS_KEY_NAME);
    }

    @Override
    public AbstractResponse getErrorResponse(int versionId, Throwable e) {
        switch (versionId) {
            case 0:
                return new LeaveGroupResponse(Errors.forException(e).code());
            default:
                throw new IllegalArgumentException(String.format("Version %d is not valid. Valid versions for %s are 0 to %d",
                        versionId, this.getClass().getSimpleName(), ProtoUtils.latestVersion(ApiKeys.SEND_INTERESTS.id)));
        }
    }

    public String groupId() {
        return groupId;
    }


    //get interests
    public String interests(){
        return interests;
    }

    public static SendInterestsRequest parse(ByteBuffer buffer, int versionId) {
        return new SendInterestsRequest(ProtoUtils.parseRequest(ApiKeys.SEND_INTERESTS.id, versionId, buffer));
    }

    public static SendInterestsRequest parse(ByteBuffer buffer) {
        return new SendInterestsRequest(CURRENT_SCHEMA.read(buffer));
    }
}

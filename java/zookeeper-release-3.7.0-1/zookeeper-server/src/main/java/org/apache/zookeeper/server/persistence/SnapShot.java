/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper.server.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.zookeeper.server.DataTree;

/**
 * snapshot interface for the persistence layer.
 * implement this interface for implementing
 * snapshots.
 *
 * 定义了四个方法：反序列化，序列化，查找最新的snapshot文件，释放资源。
 *
 * 所有的数据，在内存中有一份，都使用{@link org.apache.zookeeper.server.DataNode}来进行树形结构的组织，最后形成一个 {@link DataTree}对象
 *
 * {@link DataTree} 每隔一段时间把快照从内存持久化到磁盘
 *
 * 理论上来说，数据在磁盘也有一份。（快照 + 未拍摄快照的已提交事务日志组成）
 *
 * 理论上来说，内存拥有完整的一份{@link DataTree}
 */
public interface SnapShot {

    /**
     * deserialize a data tree from the last valid snapshot and
     * return the last zxid that was deserialized
     * @param dt the datatree to be deserialized into
     * @param sessions the sessions to be deserialized into
     * @return the last zxid that was deserialized from the snapshot
     * @throws IOException
     *
     * 从磁盘恢复日志到内存
     */
    long deserialize(DataTree dt, Map<Long, Integer> sessions) throws IOException;

    /**
     * persist the datatree and the sessions into a persistence storage
     * @param dt the datatree to be serialized
     * @param sessions the session timeouts to be serialized
     * @param name the object name to store snapshot into
     * @param fsync sync the snapshot immediately after write
     * @throws IOException
     *
     * 内存中的数据持久化到磁盘
     */
    void serialize(DataTree dt, Map<Long, Integer> sessions, File name, boolean fsync) throws IOException;

    /**
     * find the most recent snapshot file
     * @return the most recent snapshot file
     * @throws IOException
     */
    File findMostRecentSnapshot() throws IOException;

    /**
     * get information of the last saved/restored snapshot
     * @return info of last snapshot
     */
    SnapshotInfo getLastSnapshotInfo();

    /**
     * free resources from this snapshot immediately
     * @throws IOException
     */
    void close() throws IOException;

}

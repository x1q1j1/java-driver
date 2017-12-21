/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.driver.internal.core.metadata.token;

import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.token.Token;
import com.google.common.base.Preconditions;
import com.google.common.collect.SetMultimap;
import java.util.List;
import java.util.Map;

interface ReplicationStrategy {
  static ReplicationStrategy newInstance(Map<String, String> replicationConfig, String logPrefix) {
    String strategyClass = replicationConfig.get("class");
    Preconditions.checkNotNull(
        strategyClass, "Missing replication strategy class in " + replicationConfig);
    switch (strategyClass) {
      case "org.apache.cassandra.locator.LocalStrategy":
        return new LocalReplicationStrategy();
      case "org.apache.cassandra.locator.SimpleStrategy":
        return new SimpleReplicationStrategy(replicationConfig);
      case "org.apache.cassandra.locator.NetworkTopologyStrategy":
        return new NetworkTopologyReplicationStrategy(replicationConfig, logPrefix);
      case "org.apache.cassandra.locator.EverywhereStrategy":
        return new EverywhereStrategy();
      default:
        throw new IllegalArgumentException("Unsupported replication strategy: " + strategyClass);
    }
  }

  SetMultimap<Token, Node> computeReplicasByToken(
      Map<Token, Node> tokenToPrimary, List<Token> ring);
}

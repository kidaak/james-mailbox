/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mailbox.cassandra;

import java.util.UUID;

import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.cassandra.mail.CassandraMailboxMapper;
import org.apache.james.mailbox.cassandra.mail.CassandraMessageMapper;
import org.apache.james.mailbox.cassandra.user.CassandraSubscriptionMapper;
import org.apache.james.mailbox.store.MailboxSessionMapperFactory;
import org.apache.james.mailbox.store.mail.MailboxMapper;
import org.apache.james.mailbox.store.mail.ModSeqProvider;
import org.apache.james.mailbox.store.mail.UidProvider;
import org.apache.james.mailbox.store.user.SubscriptionMapper;

import com.datastax.driver.core.Session;

/**
 * Cassandra implementation of {@link MailboxSessionMapperFactory}
 * 
 */
public class CassandraMailboxSessionMapperFactory extends MailboxSessionMapperFactory<UUID> {
    private static final int DEFAULT_MAX_RETRY = 1000;

    private final Session session;
    private final UidProvider<UUID> uidProvider;
    private final ModSeqProvider<UUID> modSeqProvider;
    private final CassandraTypesProvider typesProvider;
    private int maxRetry;

    public CassandraMailboxSessionMapperFactory(UidProvider<UUID> uidProvider, ModSeqProvider<UUID> modSeqProvider, Session session, CassandraTypesProvider typesProvider) {
        this.uidProvider = uidProvider;
        this.modSeqProvider = modSeqProvider;
        this.session = session;
        this.maxRetry = DEFAULT_MAX_RETRY;
        this.typesProvider = typesProvider;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public CassandraMessageMapper createMessageMapper(MailboxSession mailboxSession) {
        return new CassandraMessageMapper(session, uidProvider, modSeqProvider, null, maxRetry, typesProvider);
    }

    @Override
    public MailboxMapper<UUID> createMailboxMapper(MailboxSession mailboxSession) {
        return new CassandraMailboxMapper(session, typesProvider, maxRetry);
    }

    @Override
    public SubscriptionMapper createSubscriptionMapper(MailboxSession mailboxSession) {
        return new CassandraSubscriptionMapper(session);
    }

    public ModSeqProvider<UUID> getModSeqProvider() {
        return modSeqProvider;
    }

    public UidProvider<UUID> getUidProvider() {
        return uidProvider;
    }

    Session getSession() {
        return session;
    }
}

package org.apache.kafka.common.contentfilter.ahocorasick.trie.handler;

import org.apache.kafka.common.contentfilter.ahocorasick.trie.Emit;

public interface EmitHandler {
    void emit(Emit emit);
    void emitNoneDuplicate(Emit emit);
}

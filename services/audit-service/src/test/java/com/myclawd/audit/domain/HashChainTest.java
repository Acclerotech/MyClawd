package com.myclawd.audit.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class HashChainTest {

    private static AuditEvent event(String what) {
        return new AuditEvent("work-order-service", "John Operator (JOHN3003)", what, "evidence", "corr-1",
                "2026-10-30T05:00:00Z");
    }

    private static List<AuditEvent> chainOf(String... events) {
        List<AuditEvent> chain = new ArrayList<>();
        String tip = HashChain.GENESIS;
        for (String e : events) {
            AuditEvent ev = event(e);
            HashChain.link(tip, ev);
            tip = ev.getHash();
            chain.add(ev);
        }
        return chain;
    }

    @Test
    void firstEventLinksToGenesis() {
        AuditEvent first = chainOf("Step 1 completed").get(0);

        assertThat(first.getPrevHash()).isEqualTo(HashChain.GENESIS);
        assertThat(first.getHash()).hasSize(64).isNotEqualTo(HashChain.GENESIS);
    }

    @Test
    void intactChainVerifies() {
        assertThat(HashChain.firstBroken(chainOf("Step 1 completed", "Step 2 completed", "Step 3 completed")))
                .isNull();
    }

    @Test
    void tamperingWithAPastEventIsDetected() {
        List<AuditEvent> chain = chainOf("Step 1 completed", "Step 2 completed", "Step 3 completed");

        // Rewrite history: change event 2's evidence without re-hashing.
        AuditEvent tampered = new AuditEvent("work-order-service", "John Operator (JOHN3003)", "Step 2 completed",
                "edited evidence", "corr-1", "2026-10-30T05:00:00Z");
        tampered.setPrevHash(chain.get(1).getPrevHash());
        tampered.setHash(chain.get(1).getHash());
        chain.set(1, tampered);

        assertThat(HashChain.firstBroken(chain)).isNotNull();
    }

    @Test
    void sameContentOnDifferentTipGivesDifferentHash() {
        AuditEvent a = event("Step 1 completed");
        AuditEvent b = event("Step 1 completed");
        HashChain.link(HashChain.GENESIS, a);
        HashChain.link(a.getHash(), b);

        assertThat(a.getHash()).isNotEqualTo(b.getHash());
    }
}

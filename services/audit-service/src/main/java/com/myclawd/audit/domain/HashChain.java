package com.myclawd.audit.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

/**
 * Append-only, hash-chained audit log: each event's hash covers its own content plus the
 * previous event's hash, so editing or deleting any past event breaks every hash after it.
 */
public final class HashChain {

    public static final String GENESIS = "0".repeat(64);
    private static final char SEP = '\u001f';

    private HashChain() {
    }

    public static String hash(String prevHash, AuditEvent e) {
        String material = String.join(String.valueOf(SEP),
                prevHash,
                nz(e.getSource()), nz(e.getActor()), nz(e.getEvent()),
                nz(e.getEvidence()), nz(e.getCorrelationId()), nz(e.getOccurredAt()));
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(sha.digest(material.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /** Links {@code next} onto the chain whose current tip hash is {@code tipHash}. */
    public static void link(String tipHash, AuditEvent next) {
        next.setPrevHash(tipHash);
        next.setHash(hash(tipHash, next));
    }

    /**
     * Verifies events in insertion order.
     *
     * @return the id of the first event that does not verify, or {@code null} if the chain is intact
     */
    public static Long firstBroken(List<AuditEvent> eventsInOrder) {
        String expectedPrev = GENESIS;
        for (AuditEvent e : eventsInOrder) {
            if (!Objects.equals(e.getPrevHash(), expectedPrev) || !Objects.equals(e.getHash(), hash(expectedPrev, e))) {
                return e.getId() == null ? -1L : e.getId();
            }
            expectedPrev = e.getHash();
        }
        return null;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}

package com.adven.concordion.extensions.exam.kafka.check.verify;

import com.adven.concordion.extensions.exam.kafka.Event;
import com.adven.concordion.extensions.exam.kafka.protobuf.ProtoEntity;
import org.apache.kafka.common.utils.Bytes;


public final class MockVerifier implements Verifier {

    private final boolean resultToReturn;

    private MockVerifier(final boolean resultToReturn) {
        this.resultToReturn = resultToReturn;
    }

    public static MockVerifier returningTrue() {
        return returning(true);
    }

    public static MockVerifier returningFalse() {
        return returning(false);
    }

    public static MockVerifier returning(final boolean resultToReturn) {
        return new MockVerifier(resultToReturn);
    }

    @Override
    public boolean verify(final Event<Bytes> first, final Event<ProtoEntity> second) {
        return resultToReturn;
    }

}

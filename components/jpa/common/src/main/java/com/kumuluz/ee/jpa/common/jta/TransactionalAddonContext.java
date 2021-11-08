package com.kumuluz.ee.jpa.common.jta;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.transaction.TransactionScoped;
import java.io.Serializable;
import java.util.Optional;

@TransactionScoped
public class TransactionalAddonContext implements Serializable {

    public static Optional<TransactionalAddonContext> current() {
        final Instance<TransactionalAddonContext> transactionalAddonCtx = CDI.current().select(TransactionalAddonContext.class);
        if (transactionalAddonCtx.isResolvable()) {
            return Optional.ofNullable(transactionalAddonCtx.get());
        }
        return Optional.empty();
    }

    private boolean readOnly;

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}

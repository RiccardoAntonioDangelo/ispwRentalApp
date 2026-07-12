package org.example.view.cli.state;

import org.example.view.cli.context.CliContext;

public abstract class AbstractCliScreen implements CliScreenState {
    protected final CliContext context;

    protected AbstractCliScreen(CliContext context) {
        this.context = context;
    }

    @Override
    public CliContext getCliContext() {
        return this.context;
    }
}
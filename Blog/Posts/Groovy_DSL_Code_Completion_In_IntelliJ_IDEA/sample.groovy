...
    /**
     * Registers the closure used to build the handler chain of the application.
     *
     * @param configurer The configuration closure, delegating to {@link GroovyChain}
     */
    void handlers(@DelegatesTo(value = GroovyChain.class, strategy = Closure.DELEGATE_FIRST) Closure&lt;?&gt; configurer);

...

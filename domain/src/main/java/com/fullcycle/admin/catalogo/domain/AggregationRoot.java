package com.fullcycle.admin.catalogo.domain;

public abstract class AggregationRoot<ID extends Identifier> extends Entity<ID> {

    protected AggregationRoot(final ID id){
        super(id);
    }
}

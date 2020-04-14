package com.wutian.maxy.jdom.entity;

public abstract class BaseEntity {
    private EntityType entityType;

    public BaseEntity(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public abstract boolean isNeedTranslate();

    public abstract String getLineText();

    public static enum EntityType {
        Comment,
        Element,
        LineBreak,
        ProcessingInstruction,
        EntityRef,
        Text,
        CDATA,
        DocType;

        private EntityType() {
        }
    }
}

package com.wutian.maxy.jdom.entity;

import org.jdom.Content;

public class LineBreak extends BaseEntity {
    private Content content;
    public LineBreak(Content content) {
        super(EntityType.LineBreak);
        this.content = content;
    }

    @Override
    public boolean isNeedTranslate() {
        return false;
    }

    @Override
    public String getLineText() {
        return content.getValue();
    }
}

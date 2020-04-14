package com.wutian.maxy.jdom.entity;

import org.jdom.Comment;
import org.jdom.output.XMLOutputter2;

public class CommentEntity extends BaseEntity {
    private Comment comment;
    private String lineText;

    public CommentEntity(Comment comment) {
        super(EntityType.Comment);
        this.comment = comment;
        lineText = new XMLOutputter2().outputString(comment);
    }

    @Override
    public boolean isNeedTranslate() {
        return false;
    }

    @Override
    public String getLineText() {
        return lineText;
    }

    @Override
    public String toString() {
        return lineText;
    }
}

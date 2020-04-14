package com.wutian.maxy.jdom.entity;

import org.jdom.Attribute;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrEntity extends BaseEntity {
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_TRANSLATABLE = "translatable";
    public static final String ATTRIBUTE_TRANSLATE = "translate";

    public static final String FORMAT_STRING = "<%s name=\"%s\">%s</%s>";
    public static final String FORMAT_STRING_NOT_TRANSLATE = "<%s name=\"%s\" translatable=\"false\">%s</%s>";

    private Element element;
    private String tagName;
    private String stringKey;
    private String value;
    private boolean isNeedTranslate;

    private Map<String, String> attributeMap;
    private boolean hasSubElement = false;

    public StrEntity(Element element) {
        super(EntityType.Element);
        this.element = element;
        tagName = element.getName();
        value = parseElementValue(element);

        attributeMap = new HashMap<>();
        for (Attribute attribute : element.getAttributes()) {
            attributeMap.put(attribute.getName(), attribute.getValue());
        }

        stringKey = attributeMap.get(ATTRIBUTE_NAME);
        isNeedTranslate = checkNeedTranslate();
    }

    private String parseElementValue(Element element) {
        List<Element> children = element.getChildren();
        if (children == null || children.isEmpty()) {
            return element.getValue();
        }

        hasSubElement = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < children.size(); i++) {
            Element elementChild = children.get(i);
            sb.append("        <" + elementChild.getName());
            List<Attribute> attributes = elementChild.getAttributes();
            for (Attribute attribute : attributes) {
                sb.append(" " + attribute.getName() + "=\"" + attribute.getValue() + "\"");
            }
            sb.append(">");
            sb.append(elementChild.getValue() + "</" + elementChild.getName() + ">");
            if (i != children.size() - 1)
                sb.append("\n");

        }
        return sb.toString();
    }

    public Element getElement() {
        return element;
    }

    public String getTagName() {
        return tagName;
    }

    public String getStringKey() {
        return stringKey;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean isNeedTranslate() {
        return isNeedTranslate;
    }

    private boolean checkNeedTranslate() {
        if (value.contains("@string/"))
            return false;

        if (stringKey.equals("app_name"))
            return false;

        return !attributeMap.containsKey(ATTRIBUTE_TRANSLATABLE) && !attributeMap.containsKey(ATTRIBUTE_TRANSLATE);
    }

    @Override
    public String getLineText() {
        if (isNeedTranslate())
            return String.format(FORMAT_STRING, tagName, stringKey, hasSubElement ? "\n" + value + "\n    " : value, tagName);
        else
            return String.format(FORMAT_STRING_NOT_TRANSLATE, tagName, stringKey, hasSubElement ? "\n" + value + "\n    " : value, tagName);
    }

    @Override
    public String toString() {
        return getLineText();
    }
}

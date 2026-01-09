package core.healing.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines semantic groups of attributes that serve similar purposes.
 * Used for cross-attribute matching in healing strategies.
 */
public enum AttributeGroup {

    /**
     * Identity attributes - uniquely identify an element
     * Examples: id, name, data-id, data-testid, formcontrol
     */
    IDENTITY(new String[] {
            "id",
            "name",
            "data-id",
            "data-testid",
            "data-test-id",
            "formcontrol",
            "formcontrolname",
            "ng-reflect-name"
    }),

    /**
     * Label attributes - describe the element to users
     * Examples: aria-label, placeholder, title, label
     */
    LABEL(new String[] {
            "aria-label",
            "aria-labelledby",
            "label",
            "title",
            "placeholder",
            "alt"
    }),

    /**
     * Role attributes - define the element's purpose
     * Examples: role, type, data-role
     */
    ROLE(new String[] {
            "role",
            "type",
            "data-role"
    }),

    /**
     * Visual attributes - styling and appearance
     * Examples: class, className
     */
    VISUAL(new String[] {
            "class",
            "classname"
    });

    private final Set<String> attributes;

    AttributeGroup(String[] attributes) {
        this.attributes = new HashSet<>(Arrays.asList(attributes));
    }

    /**
     * Check if an attribute belongs to this group
     */
    public boolean contains(String attribute) {
        if (attribute == null)
            return false;
        return attributes.contains(attribute.toLowerCase());
    }

    /**
     * Get all attributes in this group
     */
    public Set<String> getAttributes() {
        return new HashSet<>(attributes);
    }

    /**
     * Find which group an attribute belongs to
     * 
     * @return AttributeGroup or null if not found
     */
    public static AttributeGroup findGroup(String attribute) {
        if (attribute == null)
            return null;

        for (AttributeGroup group : values()) {
            if (group.contains(attribute)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Check if two attributes are in the same semantic group
     */
    public static boolean areSameGroup(String attr1, String attr2) {
        if (attr1 == null || attr2 == null)
            return false;

        AttributeGroup group1 = findGroup(attr1);
        AttributeGroup group2 = findGroup(attr2);

        return group1 != null && group1 == group2;
    }
}

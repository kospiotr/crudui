package org.vaadin.crudui;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;

import java.util.stream.StreamSupport;

/**
 * @author Alejandro Duarte
 */
public abstract class AbstractAutoGeneratedCrudFormFactory<T> implements CrudFormFactory<T> {

    protected FieldGroupFieldFactory fieldFactory = DefaultFieldGroupFieldFactory.get();

    protected void addFields(CrudFormConfiguration configuration, FieldGroup fieldGroup, ComponentContainer destLayout) {

        for (CrudFieldConfiguration fieldConfig : configuration.getCrudFieldConfigurations()) {
            Object propertyId = fieldConfig.getPropertyId();

            Field<?> field = fieldGroup.buildAndBind(DefaultFieldFactory.createCaptionByPropertyId(propertyId), propertyId, fieldConfig.getFieldType());
            field.setWidth("100%");
            field.setCaption(fieldConfig.getCaption());
            field.setReadOnly(fieldConfig.isReadOnly());
            field.setEnabled(fieldConfig.isEnabled());

            fieldConfig.getValidators().forEach(field::addValidator);
            fixNullRepresentation(field);
            destLayout.addComponent(field);
            fieldConfig.getCreationListener().accept(field);
        }

        focusFirstField(destLayout);
    }

    public static void fixNullRepresentation(Field<?> field) {
        if (AbstractTextField.class.isAssignableFrom(field.getClass())) {
            ((AbstractTextField) field).setNullRepresentation("");
        }
    }

    public static void focusFirstField(ComponentContainer destLayout) {
        StreamSupport.stream(destLayout.spliterator(), false)
                .filter(component -> Field.class.isAssignableFrom(component.getClass()))
                .map(component -> (Field) component)
                .filter(field -> field.isEnabled() && !field.isReadOnly())
                .findFirst()
                .ifPresent(field -> field.focus());
    }

}

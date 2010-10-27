package com.opengamma.engine.value;

import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicAPI;

/**
 * An immutable representation of the metadata that describes an actual value. This may be a value
 * a function is capable of producing, or the describe resolved value passed into a function to
 * satisfy a {@link ValueRequirement}.
 * 
 * For example the {@code ValueRequirement} for an FX function may state a constraint such as
 * "any currency" on a value. After the graph has been built, the actual value will be specified
 * including the specific currency. Similarly a constraint on a {@link ValueRequirement} might restrict
 * the function to be used (or the default of omission allows any) whereas the {@link ValueSpecification}
 * will indicate which function was used to compute that value.
 */
@PublicAPI
public class ValueSpecification implements java.io.Serializable {

  /**
   * The value being requested - matches that of a {@link ValueRequirement} satisfied by this specification.
   */
  private final String _valueName;

  /**
   * The specification of the object that the value refers to - matches that of a {@link ValueRequirement} satisfied by this specification.
   */
  private final ComputationTargetSpecification _targetSpecification;

  /**
   * The properties of the value described. This property set will satisfy the constraints of all {@link ValueRequirement}s satisfied by this specification.
   */
  private final ValueProperties _properties;

  public ValueSpecification(ValueRequirement requirementSpecification, String functionIdentifier) {
    ArgumentChecker.notNull(requirementSpecification, "requirementSpecification");
    ArgumentChecker.notNull(functionIdentifier, "functionIdentifier");
    // requirement specification interns its valueName
    _valueName = requirementSpecification.getValueName();
    _targetSpecification = requirementSpecification.getTargetSpecification();
    _properties = requirementSpecification.getConstraints().copy().with(ValuePropertyNames.FUNCTION, functionIdentifier).get();
  }

  public ValueSpecification(ValueRequirement requirementSpecification, ValueProperties properties) {
    ArgumentChecker.notNull(requirementSpecification, "requirementSpecification");
    ArgumentChecker.notNull(properties, "properties");
    ArgumentChecker.notNull(properties.getValues(ValuePropertyNames.FUNCTION), "properties.FUNCTION");
    assert requirementSpecification.getConstraints().isSatisfiedBy(properties);
    // requirement specification interns its valueName
    _valueName = requirementSpecification.getValueName();
    _targetSpecification = requirementSpecification.getTargetSpecification();
    _properties = properties;
  }

  public ValueSpecification(final String valueName, final ComputationTargetSpecification targetSpecification, final ValueProperties properties) {
    ArgumentChecker.notNull(valueName, "valueName");
    ArgumentChecker.notNull(targetSpecification, "targetSpecification");
    ArgumentChecker.notNull(properties, "properties");
    ArgumentChecker.notNull(properties.getValues(ValuePropertyNames.FUNCTION), "properties.FUNCTION");
    _valueName = valueName.intern();
    _targetSpecification = targetSpecification;
    _properties = properties;
  }

  public String getValueName() {
    return _valueName;
  }

  public ComputationTargetSpecification getTargetSpecification() {
    return _targetSpecification;
  }

  public ValueProperties getProperties() {
    return _properties;
  }

  public String getProperty(final String propertyName) {
    final Set<String> values = _properties.getValues(propertyName);
    if (values == null) {
      return null;
    } else if (values.isEmpty()) {
      throw new IllegalArgumentException("property " + propertyName + " contains only wild-card values");
    } else {
      return values.iterator().next();
    }
  }

  /**
   * Creates the maximal {@link ValueRequirement} that would be satisfied by this value specification.
   * 
   * @return the value requirement
   */
  public ValueRequirement toRequirementSpecification() {
    return new ValueRequirement(_valueName, _targetSpecification, _properties);
  }

  /**
   * Gets the identifier of the function that calculates this value.
   * @return the function identifier
   **/
  public String getFunctionUniqueId() {
    return getProperty(ValuePropertyNames.FUNCTION);
  }

  /**
   * Respecifies the properties to match a tighter requirement. Requires {@code requirement.isSatisfiedBy(this) == true}.
   * 
   * @param requirement additional requirement to reduce properties against
   * @return the new value specification, or this object if the composition is equal
   */
  public ValueSpecification compose(final ValueRequirement requirement) {
    assert requirement.isSatisfiedBy(this);
    final ValueProperties oldProperties = getProperties();
    final ValueProperties newProperties = oldProperties.compose(requirement.getConstraints());
    if (newProperties == oldProperties) {
      return this;
    } else {
      return new ValueSpecification(getValueName(), getTargetSpecification(), newProperties);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ValueSpecification)) {
      return false;
    }
    final ValueSpecification other = (ValueSpecification) obj;
    // valueName is interned
    return (_valueName == other._valueName) && ObjectUtils.equals(_targetSpecification, other._targetSpecification) && ObjectUtils.equals(_properties, other._properties);
  }

  @Override
  public int hashCode() {
    final int prime = 37;
    int result = 1;
    result = (result * prime) + _valueName.hashCode();
    result = (result * prime) + _targetSpecification.hashCode();
    result = (result * prime) + _properties.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}

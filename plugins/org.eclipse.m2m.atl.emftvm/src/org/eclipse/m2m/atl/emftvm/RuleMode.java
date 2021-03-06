/*******************************************************************************
 * Copyright (c) 2011 Vrije Universiteit Brussel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Wagelaar, Vrije Universiteit Brussel - initial API and
 *         implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.m2m.atl.emftvm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Rule Mode</b></em>',
 * and utility methods for working with them.
 * @author <a href="mailto:dennis.wagelaar@vub.ac.be">Dennis Wagelaar</a>
 * <!-- end-user-doc -->
 * @see org.eclipse.m2m.atl.emftvm.EmftvmPackage#getRuleMode()
 * @model
 * @generated
 */
public enum RuleMode implements Enumerator {
	/**
	 * The '<em><b>Manual</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #MANUAL_VALUE
	 * @generated
	 * @ordered
	 */
	MANUAL(0, "manual", "manual"),

	/**
	 * The '<em><b>Automatic Single</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #AUTOMATIC_SINGLE_VALUE
	 * @generated
	 * @ordered
	 */
	AUTOMATIC_SINGLE(1, "automaticSingle", "automaticSingle"),

	/**
	 * The '<em><b>Automatic Recursive</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #AUTOMATIC_RECURSIVE_VALUE
	 * @generated
	 * @ordered
	 */
	AUTOMATIC_RECURSIVE(2, "automaticRecursive", "automaticRecursive");

	/**
	 * The '<em><b>Manual</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Manual</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #MANUAL
	 * @model name="manual"
	 * @generated
	 * @ordered
	 */
	public static final int MANUAL_VALUE = 0;

	/**
	 * The '<em><b>Automatic Single</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Automatic Single</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #AUTOMATIC_SINGLE
	 * @model name="automaticSingle"
	 * @generated
	 * @ordered
	 */
	public static final int AUTOMATIC_SINGLE_VALUE = 1;

	/**
	 * The '<em><b>Automatic Recursive</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Automatic Recursive</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #AUTOMATIC_RECURSIVE
	 * @model name="automaticRecursive"
	 * @generated
	 * @ordered
	 */
	public static final int AUTOMATIC_RECURSIVE_VALUE = 2;

	/**
	 * An array of all the '<em><b>Rule Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final RuleMode[] VALUES_ARRAY =
		new RuleMode[] {
			MANUAL,
			AUTOMATIC_SINGLE,
			AUTOMATIC_RECURSIVE,
		};

	/**
	 * A public read-only list of all the '<em><b>Rule Mode</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<RuleMode> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Rule Mode</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * @param literal the literal string value
	 * @return the '<em><b>Rule Mode</b></em>' literal with the specified literal value.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RuleMode get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			RuleMode result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Rule Mode</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * @param name the literal name
	 * @return the '<em><b>Rule Mode</b></em>' literal with the specified name.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RuleMode getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			RuleMode result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Rule Mode</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * @param value the literal integer value
	 * @return the '<em><b>Rule Mode</b></em>' literal with the specified integer value.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RuleMode get(int value) {
		switch (value) {
			case MANUAL_VALUE: return MANUAL;
			case AUTOMATIC_SINGLE_VALUE: return AUTOMATIC_SINGLE;
			case AUTOMATIC_RECURSIVE_VALUE: return AUTOMATIC_RECURSIVE;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * The literal integer value.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * The literal name.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * The literal string value.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private RuleMode(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Returns the literal integer value.
	 * @return the literal integer value.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Returns the literal name.
	 * @return the literal name.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Returns the literal string value.
	 * @return the literal string value.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * @return the literal value of the enumerator, which is its string representation.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //RuleMode

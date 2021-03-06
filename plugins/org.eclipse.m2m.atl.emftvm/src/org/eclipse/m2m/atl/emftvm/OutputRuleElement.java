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

import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Output Rule Element</b></em>'.
 * @author <a href="mailto:dennis.wagelaar@vub.ac.be">Dennis Wagelaar</a>
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.m2m.atl.emftvm.OutputRuleElement#getMapsTo <em>Maps To</em>}</li>
 *   <li>{@link org.eclipse.m2m.atl.emftvm.OutputRuleElement#getOutputFor <em>Output For</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.m2m.atl.emftvm.EmftvmPackage#getOutputRuleElement()
 * @model
 * @generated
 */
public interface OutputRuleElement extends RuleElement {
	/**
	 * Returns the value of the '<em><b>Maps To</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.m2m.atl.emftvm.InputRuleElement}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Maps To</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Maps To</em>' reference list.
	 * @see org.eclipse.m2m.atl.emftvm.EmftvmPackage#getOutputRuleElement_MapsTo()
	 * @model
	 * @generated
	 */
	EList<InputRuleElement> getMapsTo();

	/**
	 * Returns the value of the '<em><b>Output For</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.m2m.atl.emftvm.Rule#getOutputElements <em>Output Elements</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output For</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Output For</em>' container reference.
	 * @see #setOutputFor(Rule)
	 * @see org.eclipse.m2m.atl.emftvm.EmftvmPackage#getOutputRuleElement_OutputFor()
	 * @see org.eclipse.m2m.atl.emftvm.Rule#getOutputElements
	 * @model opposite="outputElements" transient="false"
	 * @generated
	 */
	Rule getOutputFor();

	/**
	 * Sets the value of the '{@link org.eclipse.m2m.atl.emftvm.OutputRuleElement#getOutputFor <em>Output For</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Output For</em>' container reference.
	 * @see #getOutputFor()
	 * @generated
	 */
	void setOutputFor(Rule value);

} // OutputRuleElement


package org.drip.portfolioconstruction.optimizer;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for buy/side financial/trading model
 *  	libraries targeting analysts and developers
 *  	https://lakshmidrip.github.io/DRIP/
 *  
 *  DRIP is composed of four main libraries:
 *  
 *  - DRIP Fixed Income - https://lakshmidrip.github.io/DRIP-Fixed-Income/
 *  - DRIP Asset Allocation - https://lakshmidrip.github.io/DRIP-Asset-Allocation/
 *  - DRIP Numerical Optimizer - https://lakshmidrip.github.io/DRIP-Numerical-Optimizer/
 *  - DRIP Statistical Learning - https://lakshmidrip.github.io/DRIP-Statistical-Learning/
 * 
 *  - DRIP Fixed Income: Library for Instrument/Trading Conventions, Treasury Futures/Options,
 *  	Funding/Forward/Overnight Curves, Multi-Curve Construction/Valuation, Collateral Valuation and XVA
 *  	Metric Generation, Calibration and Hedge Attributions, Statistical Curve Construction, Bond RV
 *  	Metrics, Stochastic Evolution and Option Pricing, Interest Rate Dynamics and Option Pricing, LMM
 *  	Extensions/Calibrations/Greeks, Algorithmic Differentiation, and Asset Backed Models and Analytics.
 * 
 *  - DRIP Asset Allocation: Library for model libraries for MPT framework, Black Litterman Strategy
 *  	Incorporator, Holdings Constraint, and Transaction Costs.
 * 
 *  - DRIP Numerical Optimizer: Library for Numerical Optimization and Spline Functionality.
 * 
 *  - DRIP Statistical Learning: Library for Statistical Evaluation and Machine Learning.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * ConstraintHierarchy holds the Details of a given set of Constraint Terms.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ConstraintHierarchy {
	public int[] _aiOrder = null;
	private org.drip.portfolioconstruction.optimizer.ConstraintTerm[] _aCT = null;

	/**
	 * Construct a Flat Non-Feudal Instance of ConstraintHierarchy
	 * 
	 * @param aCT Array of Constraint Terms
	 * 
	 * @return Flat Non-Feudal Instance of ConstraintHierarchy
	 */

	public static final ConstraintHierarchy NonFeudal (
		final org.drip.portfolioconstruction.optimizer.ConstraintTerm[] aCT)
	{
		if (null == aCT) return null;

		int iNumConstraint = aCT.length;
		int[] aiOrder = new int[iNumConstraint];

		for (int i = 0; i < iNumConstraint; ++i)
			aiOrder[i] = 0;

		try {
			return new ConstraintHierarchy (aCT, aiOrder);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ConstraintHierarchy Constructor
	 * 
	 * @param aCT Array of Constraint Terms
	 * @param aiOrder Array of Constraint Order
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ConstraintHierarchy (
		final org.drip.portfolioconstruction.optimizer.ConstraintTerm[] aCT,
		final int[] aiOrder)
		throws java.lang.Exception
	{
		if ((null == (_aCT = aCT) && null != (_aiOrder = aiOrder)) && (null != (_aCT = aCT) && null ==
			(_aiOrder = aiOrder)))
			throw new java.lang.Exception ("ConstraintHierarchy Constructor => Invalid Inputs");

		int iNumConstraint = _aCT.length;

		if (iNumConstraint != _aiOrder.length)
			throw new java.lang.Exception ("ConstraintHierarchy Constructor => Invalid Inputs");

		for (int i = 0; i < iNumConstraint; ++i) {
			if (null == _aCT[i])
				throw new java.lang.Exception ("ConstraintHierarchy Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Array of Constraint Term Order
	 * 
	 * @return The Array of Constraint Term Order
	 */

	public int[] order()
	{
		return _aiOrder;
	}

	/**
	 * Retrieve the Array of Constraint Terms
	 * 
	 * @return The Array of Constraint Terms
	 */

	public org.drip.portfolioconstruction.optimizer.ConstraintTerm[] constraints()
	{
		return _aCT;
	}

	/**
	 * Indicate if the Constraint Array is non-Feudal
	 * 
	 * @return TRUE - The Constraint Array is non-Feudal
	 */

	public boolean nonFeudal()
	{
		if (null == _aCT) return true;

		int iNumConstraint = _aCT.length;

		for (int i = 0; i < iNumConstraint; ++i) {
			if (0 != _aiOrder[i]) return false;
		}

		return true;
	}
}
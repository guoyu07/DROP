
package org.drip.portfolioconstruction.constraint;

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
 * LimitExposureTermIssuerLong holds the Details of a Limit Issuer Long Exposure Constraint Term.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LimitExposureTermIssuerLong extends
	org.drip.portfolioconstruction.constraint.LimitExposureTermIssuer
{

	/**
	 * LimitExposureTermIssuerLong Constructor
	 * 
	 * @param strName Name of the Constraint
	 * @param scope Scope of the Constraint - ACCOUNT/ASSET/SET
	 * @param unit Unit of the Constraint
	 * @param dblMinimum Minimum Value of the Constraint
	 * @param dblMaximum Maximum Value of the Constraint
	 * @param adblPrice Array of the Asset Prices
	 * @param adblIssuerSelection Array of Issuer Selection
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LimitExposureTermIssuerLong (
		final java.lang.String strName,
		final org.drip.portfolioconstruction.optimizer.Scope scope,
		final org.drip.portfolioconstruction.optimizer.Unit unit,
		final double dblMinimum,
		final double dblMaximum,
		final double[] adblPrice,
		final double[] adblIssuerSelection)
		throws java.lang.Exception
	{
		super (
			strName,
			"CT_LIMIT_ISSUER_LONG_EXPOSURE",
			"Constrains the Issuer Long Exposure",
			scope,
			unit,
			dblMinimum,
			dblMaximum,
			adblPrice,
			adblIssuerSelection
		);
	}

	@Override public org.drip.function.definition.RdToR1 rdtoR1()
	{
		return new org.drip.function.definition.RdToR1 (null)
		{
			@Override public int dimension()
			{
				return price().length;
			}

			@Override public double evaluate (
				final double[] adblFinalHoldings)
				throws java.lang.Exception
			{
				double[] adblPrice = price();

				double dblConstraintValue = 0.;
				int iNumAsset = adblPrice.length;

				double[] adblIssuerSelection = issuerSelection();

				if (null == adblFinalHoldings || !org.drip.quant.common.NumberUtil.IsValid
					(adblFinalHoldings) || adblFinalHoldings.length != iNumAsset)
					throw new java.lang.Exception
						("LimitExposureTermIssuerLong::rdToR1::evaluate => Invalid Variate Dimension");

				for (int i = 0; i < iNumAsset; ++i)
				{
					if (adblFinalHoldings[i] > 0.)
						dblConstraintValue += adblIssuerSelection[i] * adblPrice[i] * adblFinalHoldings[i];
				}

				return dblConstraintValue;
			}
		};
	}
}

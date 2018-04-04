
package org.drip.xva.gross;

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
 * ExposureAdjustmentAggregator aggregates across Multiple Exposure/Adjustment Paths belonging to the Counter
 *  Party. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
 *  
 *  - Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk, Risk 20 (2) 86-90.
 *  
 *  - Li, B., and Y. Tang (2007): Quantitative Analysis, Derivatives Modeling, and Trading Strategies in the
 *  	Presence of Counter-party Credit Risk for the Fixed Income Market, World Scientific Publishing,
 *  	Singapore.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ExposureAdjustmentAggregator
{
	private org.drip.xva.gross.PathExposureAdjustment[] _pathExposureAdjustmentArray = null;

	/**
	 * ExposureAdjustmentAggregator Constructor
	 * 
	 * @param pathExposureAdjustmentArray Array of the Counter Party Group Paths
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ExposureAdjustmentAggregator (
		final org.drip.xva.gross.PathExposureAdjustment[] pathExposureAdjustmentArray)
		throws java.lang.Exception
	{
		if (null == (_pathExposureAdjustmentArray = pathExposureAdjustmentArray) ||
			0 == _pathExposureAdjustmentArray.length)
		{
			throw new java.lang.Exception ("ExposureAdjustmentAggregator Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Array of Group Path Exposure Adjustments
	 * 
	 * @return Array of Group Path Exposure Adjustments
	 */

	public org.drip.xva.gross.PathExposureAdjustment[] pathExposureAdjustmentArray()
	{
		return _pathExposureAdjustmentArray;
	}

	/**
	 * Retrieve the Array of the Vertex Anchor Dates
	 * 
	 * @return The Array of the Vertex Anchor Dates
	 */

	public org.drip.analytics.date.JulianDate[] anchorDates()
	{
		return _pathExposureAdjustmentArray[0].anchorDates();
	}

	/**
	 * Retrieve the Array of Collateralized Exposures
	 * 
	 * @return The Array of Collateralized Exposures
	 */

	public double[] collateralizedExposure()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] collateralizedExposure = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedExposure[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathCollateralizedExposure =
				_pathExposureAdjustmentArray[pathIndex].collateralizedExposure();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				collateralizedExposure[vertexIndex] += pathCollateralizedExposure[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedExposure[vertexIndex] /= pathCount;
		}

		return collateralizedExposure;
	}

	/**
	 * Retrieve the Array of Collateralized Exposure PV's
	 * 
	 * @return The Array of Collateralized Exposure PV's
	 */

	public double[] collateralizedExposurePV()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] collateralizedExposurePV = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedExposurePV[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathCollateralizedExposurePV =
				_pathExposureAdjustmentArray[pathIndex].collateralizedExposurePV();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				collateralizedExposurePV[vertexIndex] += pathCollateralizedExposurePV[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedExposurePV[vertexIndex] /= pathCount;
		}

		return collateralizedExposurePV;
	}

	/**
	 * Retrieve the Array of Uncollateralized Exposures
	 * 
	 * @return The Array of Uncollateralized Exposures
	 */

	public double[] uncollateralizedExposure()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] uncollateralizedExposure = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedExposure[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathUncollateralizedExposure =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedExposure();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				uncollateralizedExposure[vertexIndex] += pathUncollateralizedExposure[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			uncollateralizedExposure[vertexIndex] /= pathCount;

		return uncollateralizedExposure;
	}

	/**
	 * Retrieve the Array of Uncollateralized Exposure PV's
	 * 
	 * @return The Array of Uncollateralized Exposure PV's
	 */

	public double[] uncollateralizedExposurePV()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] uncollateralizedExposurePV = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedExposurePV[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathUncollateralizedExposurePV =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedExposurePV();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				uncollateralizedExposurePV[vertexIndex] +=
					pathUncollateralizedExposurePV[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedExposurePV[vertexIndex] /= pathCount;
		}

		return uncollateralizedExposurePV;
	}

	/**
	 * Retrieve the Array of Collateralized Positive Exposures
	 * 
	 * @return The Array of Collateralized Positive Exposures
	 */

	public double[] collateralizedPositiveExposure()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] collateralizedPositiveExposure = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedPositiveExposure[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathCollateralizedPositiveExposure =
				_pathExposureAdjustmentArray[pathIndex].collateralizedPositiveExposure();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				collateralizedPositiveExposure[vertexIndex] +=
					pathCollateralizedPositiveExposure[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedPositiveExposure[vertexIndex] /= pathCount;
		}

		return collateralizedPositiveExposure;
	}

	/**
	 * Retrieve the Array of Collateralized Positive Exposure PV
	 * 
	 * @return The Array of Collateralized Positive Exposure PV
	 */

	public double[] collateralizedPositiveExposurePV()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] collateralizedPositiveExposurePV = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedPositiveExposurePV[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathCollateralizedPositiveExposurePV =
				_pathExposureAdjustmentArray[pathIndex].collateralizedPositiveExposurePV();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				collateralizedPositiveExposurePV[vertexIndex] +=
					pathCollateralizedPositiveExposurePV[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedPositiveExposurePV[vertexIndex] /= pathCount;
		}

		return collateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Array of Uncollateralized Positive Exposures
	 * 
	 * @return The Array of Uncollateralized Positive Exposures
	 */

	public double[] uncollateralizedPositiveExposure()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] uncollateralizedPositiveExposure = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedPositiveExposure[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathUncollateralizedPositiveExposure =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedPositiveExposure();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				uncollateralizedPositiveExposure[vertexIndex] +=
					pathUncollateralizedPositiveExposure[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedPositiveExposure[vertexIndex] /= pathCount;
		}

		return uncollateralizedPositiveExposure;
	}

	/**
	 * Retrieve the Array of Uncollateralized Positive Exposure PV
	 * 
	 * @return The Array of Uncollateralized Positive Exposure PV
	 */

	public double[] uncollateralizedPositiveExposurePV()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] uncollateralizedPositiveExposurePV = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedPositiveExposurePV[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathUncollateralizedPositiveExposurePV =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedPositiveExposurePV();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				uncollateralizedPositiveExposurePV[vertexIndex] +=
					pathUncollateralizedPositiveExposurePV[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedPositiveExposurePV[vertexIndex] /= pathCount;
		}

		return uncollateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Array of Collateralized Negative Exposures
	 * 
	 * @return The Array of Collateralized Negative Exposures
	 */

	public double[] collateralizedNegativeExposure()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] collateralizedNegativeExposure = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedNegativeExposure[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathCollateralizedNegativeExposure =
				_pathExposureAdjustmentArray[pathIndex].collateralizedNegativeExposure();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				collateralizedNegativeExposure[vertexIndex] +=
					pathCollateralizedNegativeExposure[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedNegativeExposure[vertexIndex] /= pathCount;
		}

		return collateralizedNegativeExposure;
	}

	/**
	 * Retrieve the Array of Collateralized Negative Exposure PV
	 * 
	 * @return The Array of Collateralized Negative Exposure PV
	 */

	public double[] collateralizedNegativeExposurePV()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] collateralizedNegativeExposurePV = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedNegativeExposurePV[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathCollateralizedNegativeExposurePV =
				_pathExposureAdjustmentArray[pathIndex].collateralizedNegativeExposurePV();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				collateralizedNegativeExposurePV[vertexIndex] +=
					pathCollateralizedNegativeExposurePV[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			collateralizedNegativeExposurePV[vertexIndex] /= pathCount;
		}

		return collateralizedNegativeExposurePV;
	}

	/**
	 * Retrieve the Array of Uncollateralized Negative Exposures
	 * 
	 * @return The Array of Uncollateralized Negative Exposures
	 */

	public double[] uncollateralizedNegativeExposure()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] uncollateralizedNegativeExposure = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedNegativeExposure[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathUncollateralizedNegativeExposure =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedNegativeExposure();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				uncollateralizedNegativeExposure[vertexIndex] +=
					pathUncollateralizedNegativeExposure[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedNegativeExposure[vertexIndex] /= pathCount;
		}

		return uncollateralizedNegativeExposure;
	}

	/**
	 * Retrieve the Array of Uncollateralized Negative Exposure PV
	 * 
	 * @return The Array of Uncollateralized Negative Exposure PV
	 */

	public double[] uncollateralizedNegativeExposurePV()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] uncollateralizedNegativeExposurePV = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedNegativeExposurePV[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathUncollateralizedNegativeExposurePV =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedNegativeExposurePV();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				uncollateralizedNegativeExposurePV[vertexIndex] +=
					pathUncollateralizedNegativeExposurePV[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			uncollateralizedNegativeExposurePV[vertexIndex] /= pathCount;
		}

		return uncollateralizedNegativeExposurePV;
	}

	/**
	 * Retrieve the Array of Credit Exposures
	 * 
	 * @return The Array of Credit Exposures
	 */

	public double[] creditExposure()
	{
		int vertexCount = anchorDates().length;

		double[] creditExposure = new double[vertexCount];
		int pathCount = _pathExposureAdjustmentArray.length;

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			creditExposure[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathCreditExposure = _pathExposureAdjustmentArray[pathIndex].creditExposure();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				creditExposure[vertexIndex] += pathCreditExposure[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			creditExposure[vertexIndex] /= pathCount;
		}

		return creditExposure;
	}

	/**
	 * Retrieve the Array of Credit Exposure PV
	 * 
	 * @return The Array of Credit Exposure PV
	 */

	public double[] creditExposurePV()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] creditExposurePV = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			creditExposurePV[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathCreditExposurePV = _pathExposureAdjustmentArray[pathIndex].creditExposurePV();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				creditExposurePV[vertexIndex] += pathCreditExposurePV[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			creditExposurePV[vertexIndex] /= pathCount;
		}

		return creditExposurePV;
	}

	/**
	 * Retrieve the Array of Debt Exposures
	 * 
	 * @return The Array of Debt Exposures
	 */

	public double[] debtExposure()
	{
		int vertexCount = anchorDates().length;

		double[] debtExposure = new double[vertexCount];
		int pathCount = _pathExposureAdjustmentArray.length;

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			debtExposure[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathDebtExposure = _pathExposureAdjustmentArray[pathIndex].debtExposure();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				debtExposure[vertexIndex] += pathDebtExposure[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			debtExposure[vertexIndex] /= pathCount;
		}

		return debtExposure;
	}

	/**
	 * Retrieve the Array of Debt Exposure PV
	 * 
	 * @return The Array of Debt Exposure PV
	 */

	public double[] debtExposurePV()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] debtExposurePV = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			debtExposurePV[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathDebtExposurePV = _pathExposureAdjustmentArray[pathIndex].debtExposurePV();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				debtExposurePV[vertexIndex] += pathDebtExposurePV[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			debtExposurePV[vertexIndex] /= pathCount;
		}

		return debtExposurePV;
	}

	/**
	 * Retrieve the Array of Funding Exposures
	 * 
	 * @return The Array of Funding Exposures
	 */

	public double[] fundingExposure()
	{
		int vertexCount = anchorDates().length;

		double[] fundingExposure = new double[vertexCount];
		int pathCount = _pathExposureAdjustmentArray.length;

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			fundingExposure[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathFundingExposure = _pathExposureAdjustmentArray[pathIndex].fundingExposure();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				fundingExposure[vertexIndex] += pathFundingExposure[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			fundingExposure[vertexIndex] /= pathCount;
		}

		return fundingExposure;
	}

	/**
	 * Retrieve the Array of Funding Exposure PV
	 * 
	 * @return The Array of Funding Exposure PV
	 */

	public double[] fundingExposurePV()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] fundingExposurePV = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			fundingExposurePV[vertexIndex] = 0.;
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathFundingExposurePV = _pathExposureAdjustmentArray[pathIndex].fundingExposurePV();

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				fundingExposurePV[vertexIndex] += pathFundingExposurePV[vertexIndex];
			}
		}

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			fundingExposurePV[vertexIndex] /= pathCount;
		}

		return fundingExposurePV;
	}

	/**
	 * Retrieve the Expected Bilateral Collateral VA
	 * 
	 * @return The Expected Bilateral Collateral VA
	 */

	public org.drip.xva.basel.ValueAdjustment ftdcolva()
	{
		double ftdcolva = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try
		{
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				ftdcolva += _pathExposureAdjustmentArray[pathIndex].bilateralCollateralAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.COLVA (ftdcolva / pathCount);
	}

	/**
	 * Retrieve the Expected Collateral VA
	 * 
	 * @return The Expected Collateral VA
	 */

	public org.drip.xva.basel.ValueAdjustment colva()
	{
		double colva = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try
		{
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				colva += _pathExposureAdjustmentArray[pathIndex].bilateralCollateralAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.COLVA (colva / pathCount);
	}

	/**
	 * Retrieve the Expected Unilateral CVA
	 * 
	 * @return The Expected Unilateral CVA
	 */

	public org.drip.xva.basel.ValueAdjustment ucva()
	{
		double ucva = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try
		{
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				ucva += _pathExposureAdjustmentArray[pathIndex].unilateralCreditAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.UCVA (ucva / pathCount);
	}

	/**
	 * Retrieve the Expected Bilateral/FTD CVA
	 * 
	 * @return The Expected Bilateral/FTD CVA
	 */

	public org.drip.xva.basel.ValueAdjustment ftdcva()
	{
		double ftdcva = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try
		{
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				ftdcva += _pathExposureAdjustmentArray[pathIndex].bilateralCreditAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.FTDCVA (ftdcva / pathCount);
	}

	/**
	 * Retrieve the Expected CVA
	 * 
	 * @return The Expected CVA
	 */

	public org.drip.xva.basel.ValueAdjustment cva()
	{
		return ftdcva();
	}

	/**
	 * Retrieve the Expected CVA Contra-Liability
	 * 
	 * @return The Expected CVA Contra-Liability
	 */

	public org.drip.xva.basel.ValueAdjustment cvacl()
	{
		double cvacl = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try
		{
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				cvacl += _pathExposureAdjustmentArray[pathIndex].contraLiabilityCreditAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.CVACL (cvacl / pathCount);
	}

	/**
	 * Retrieve the Expected Unilateral DVA
	 * 
	 * @return The Expected Unilateral DVA
	 */

	public org.drip.xva.basel.ValueAdjustment udva()
	{
		double udva = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try {
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				udva += _pathExposureAdjustmentArray[pathIndex].unilateralDebtAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.DVA (udva / pathCount);
	}

	/**
	 * Retrieve the Expected Bilateral DVA
	 * 
	 * @return The Expected Bilateral DVA
	 */

	public org.drip.xva.basel.ValueAdjustment ftddva()
	{
		double ftddva = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try {
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				ftddva += _pathExposureAdjustmentArray[pathIndex].bilateralDebtAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.DVA (ftddva / pathCount);
	}

	/**
	 * Retrieve the Expected DVA
	 * 
	 * @return The Expected DVA
	 */

	public org.drip.xva.basel.ValueAdjustment dva()
	{
		double dva = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try {
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				dva += _pathExposureAdjustmentArray[pathIndex].debtAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.DVA (dva / pathCount);
	}

	/**
	 * Retrieve the Expected FVA
	 * 
	 * @return The Expected FVA
	 */

	public org.drip.xva.basel.ValueAdjustment fva()
	{
		double fva = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try
		{
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				fva += _pathExposureAdjustmentArray[pathIndex].fundingValueAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.FVA (fva / pathCount);
	}

	/**
	 * Retrieve the Expected FDA
	 * 
	 * @return The Expected FDA
	 */

	public org.drip.xva.basel.ValueAdjustment fda()
	{
		double fda = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		try
		{
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				fda += _pathExposureAdjustmentArray[pathIndex].fundingDebtAdjustment();
			}
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return org.drip.xva.basel.ValueAdjustment.FDA (fda / pathCount);
	}

	/**
	 * Retrieve the Expected DVA2
	 * 
	 * @return The Expected DVA2
	 */

	public org.drip.xva.basel.ValueAdjustment dva2()
	{
		return fda();
	}

	/**
	 * Retrieve the Expected FCA
	 * 
	 * @return The Expected FCA
	 */

	public org.drip.xva.basel.ValueAdjustment fca()
	{
		double fca = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			fca += _pathExposureAdjustmentArray[pathIndex].fundingCostAdjustment();
		}

		return org.drip.xva.basel.ValueAdjustment.HYBRID (fca / pathCount);
	}

	/**
	 * Retrieve the Expected FBA
	 * 
	 * @return The Expected FBA
	 */

	public org.drip.xva.basel.ValueAdjustment fba()
	{
		double fba = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			fba += _pathExposureAdjustmentArray[pathIndex].fundingBenefitAdjustment();
		}

		return org.drip.xva.basel.ValueAdjustment.HYBRID (fba / pathCount);
	}

	/**
	 * Retrieve the Expected SFVA
	 * 
	 * @return The Expected SFVA
	 */

	public org.drip.xva.basel.ValueAdjustment sfva()
	{
		double sfva = 0.;
		int pathCount = _pathExposureAdjustmentArray.length;

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			sfva += _pathExposureAdjustmentArray[pathIndex].symmetricFundingValueAdjustment();
		}

		return org.drip.xva.basel.ValueAdjustment.HYBRID (sfva / pathCount);
	}

	/**
	 * Retrieve the Total VA
	 * 
	 * @return The Total VA
	 */

	public double total()
	{
		return cva().amount() + dva().amount() + fva().amount() + colva().amount();
	}

	/**
	 * Generate the "Digest" containing the "Thin" Path Statistics
	 * 
	 * @return The "Digest" containing the "Thin" Path Statistics
	 */

	public org.drip.xva.gross.ExposureAdjustmentDigest digest()
	{
		int vertexCount = anchorDates().length;

		int pathCount = _pathExposureAdjustmentArray.length;
		double[] pathCVA = new double[pathCount];
		double[] pathDVA = new double[pathCount];
		double[] pathFBA = new double[pathCount];
		double[] pathFCA = new double[pathCount];
		double[] pathFDA = new double[pathCount];
		double[] pathFVA = new double[pathCount];
		double[] pathUCVA = new double[pathCount];
		double[] pathSFVA = new double[pathCount];
		double[] pathCVACL = new double[pathCount];
		double[] pathFTDCVA = new double[pathCount];
		double[] pathCOLVA = new double[pathCount];
		double[] pathTotalVA = new double[pathCount];
		double[] pathFTDCOLVA = new double[pathCount];
		double[][] debtExposure = new double[vertexCount][pathCount];
		double[][] creditExposure = new double[vertexCount][pathCount];
		double[][] debtExposurePV = new double[vertexCount][pathCount];
		double[][] fundingExposure = new double[vertexCount][pathCount];
		double[][] creditExposurePV = new double[vertexCount][pathCount];
		double[][] fundingExposurePV = new double[vertexCount][pathCount];
		double[][] collateralizedExposure = new double[vertexCount][pathCount];
		double[][] uncollateralizedExposure = new double[vertexCount][pathCount];
		double[][] collateralizedExposurePV = new double[vertexCount][pathCount];
		double[][] uncollateralizedExposurePV = new double[vertexCount][pathCount];
		double[][] collateralizedPositiveExposure = new double[vertexCount][pathCount];
		double[][] collateralizedNegativeExposure = new double[vertexCount][pathCount];
		double[][] uncollateralizedPositiveExposure = new double[vertexCount][pathCount];
		double[][] uncollateralizedNegativeExposure = new double[vertexCount][pathCount];
		double[][] collateralizedPositiveExposurePV = new double[vertexCount][pathCount];
		double[][] collateralizedNegativeExposurePV = new double[vertexCount][pathCount];
		double[][] uncollateralizedPositiveExposurePV = new double[vertexCount][pathCount];
		double[][] uncollateralizedNegativeExposurePV = new double[vertexCount][pathCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
			{
				collateralizedExposure[vertexIndex][pathIndex] = 0.;
				uncollateralizedExposure[vertexIndex][pathIndex] = 0.;
				collateralizedExposurePV[vertexIndex][pathIndex] = 0.;
				uncollateralizedExposurePV[vertexIndex][pathIndex] = 0.;
				collateralizedPositiveExposure[vertexIndex][pathIndex] = 0.;
				collateralizedNegativeExposure[vertexIndex][pathIndex] = 0.;
				uncollateralizedPositiveExposure[vertexIndex][pathIndex] = 0.;
				uncollateralizedNegativeExposure[vertexIndex][pathIndex] = 0.;
				collateralizedPositiveExposurePV[vertexIndex][pathIndex] = 0.;
				collateralizedNegativeExposurePV[vertexIndex][pathIndex] = 0.;
				uncollateralizedPositiveExposurePV[vertexIndex][pathIndex] = 0.;
				uncollateralizedNegativeExposurePV[vertexIndex][pathIndex] = 0.;
			}
		}

		for (int pathIndex = 0; pathIndex < pathCount; ++pathIndex)
		{
			double[] pathCollateralizedExposure =
				_pathExposureAdjustmentArray[pathIndex].collateralizedExposure();

			double[] pathCollateralizedExposurePV =
				_pathExposureAdjustmentArray[pathIndex].collateralizedExposurePV();

			double[] pathCollateralizedPositiveExposure =
				_pathExposureAdjustmentArray[pathIndex].collateralizedPositiveExposure();

			double[] pathCollateralizedPositiveExposurePV =
				_pathExposureAdjustmentArray[pathIndex].collateralizedPositiveExposurePV();

			double[] pathCollateralizedNegativeExposure =
				_pathExposureAdjustmentArray[pathIndex].collateralizedNegativeExposure();

			double[] pathCollateralizedNegativeExposurePV =
				_pathExposureAdjustmentArray[pathIndex].collateralizedNegativeExposurePV();

			double[] pathUncollateralizedExposure =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedExposure();

			double[] pathUncollateralizedExposurePV =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedExposurePV();

			double[] pathUncollateralizedPositiveExposure =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedPositiveExposure();

			double[] pathUncollateralizedPositiveExposurePV =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedPositiveExposurePV();

			double[] pathUncollateralizedNegativeExposure =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedNegativeExposure();

			double[] pathUncollateralizedNegativeExposurePV =
				_pathExposureAdjustmentArray[pathIndex].uncollateralizedNegativeExposurePV();

			double[] pathCreditExposure = _pathExposureAdjustmentArray[pathIndex].creditExposure();

			double[] pathCreditExposurePV = _pathExposureAdjustmentArray[pathIndex].creditExposurePV();

			double[] pathDebtExposure = _pathExposureAdjustmentArray[pathIndex].debtExposure();

			double[] pathDebtExposurePV = _pathExposureAdjustmentArray[pathIndex].debtExposurePV();

			double[] pathFundingExposure = _pathExposureAdjustmentArray[pathIndex].fundingExposure();

			double[] pathFundingExposurePV = _pathExposureAdjustmentArray[pathIndex].fundingExposurePV();

			try
			{
				pathCVA[pathIndex] = _pathExposureAdjustmentArray[pathIndex].creditAdjustment();

				pathDVA[pathIndex] = _pathExposureAdjustmentArray[pathIndex].debtAdjustment();

				pathFCA[pathIndex] = _pathExposureAdjustmentArray[pathIndex].fundingCostAdjustment();

				pathFDA[pathIndex] = _pathExposureAdjustmentArray[pathIndex].fundingDebtAdjustment();

				pathFVA[pathIndex] = _pathExposureAdjustmentArray[pathIndex].fundingValueAdjustment();

				pathFBA[pathIndex] = _pathExposureAdjustmentArray[pathIndex].fundingBenefitAdjustment();

				pathUCVA[pathIndex] = _pathExposureAdjustmentArray[pathIndex].unilateralCreditAdjustment();

				pathSFVA[pathIndex] =
					_pathExposureAdjustmentArray[pathIndex].symmetricFundingValueAdjustment();

				pathCVACL[pathIndex] =
					_pathExposureAdjustmentArray[pathIndex].contraLiabilityCreditAdjustment();

				pathFTDCVA[pathIndex] = _pathExposureAdjustmentArray[pathIndex].bilateralCreditAdjustment();

				pathCOLVA[pathIndex] =
					_pathExposureAdjustmentArray[pathIndex].bilateralCollateralAdjustment();

				pathFTDCOLVA[pathIndex] =
					_pathExposureAdjustmentArray[pathIndex].bilateralCollateralAdjustment();

				pathTotalVA[pathIndex] = _pathExposureAdjustmentArray[pathIndex].totalAdjustment();
			}
			catch (java.lang.Exception e)
			{
				e.printStackTrace();

				return null;
			}

			for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
			{
				collateralizedExposure[vertexIndex][pathIndex] =
					pathCollateralizedExposure[vertexIndex];
				collateralizedExposurePV[vertexIndex][pathIndex] =
					pathCollateralizedExposurePV[vertexIndex];
				collateralizedPositiveExposure[vertexIndex][pathIndex] =
					pathCollateralizedPositiveExposure[vertexIndex];
				collateralizedPositiveExposurePV[vertexIndex][pathIndex] =
					pathCollateralizedPositiveExposurePV[vertexIndex];
				collateralizedNegativeExposure[vertexIndex][pathIndex] =
					pathCollateralizedNegativeExposure[vertexIndex];
				collateralizedNegativeExposurePV[vertexIndex][pathIndex] =
					pathCollateralizedNegativeExposurePV[vertexIndex];
				uncollateralizedExposure[vertexIndex][pathIndex] =
					pathUncollateralizedExposure[vertexIndex];
				uncollateralizedExposurePV[vertexIndex][pathIndex] =
					pathUncollateralizedExposurePV[vertexIndex];
				uncollateralizedPositiveExposure[vertexIndex][pathIndex] =
					pathUncollateralizedPositiveExposure[vertexIndex];
				uncollateralizedPositiveExposurePV[vertexIndex][pathIndex] =
					pathUncollateralizedPositiveExposurePV[vertexIndex];
				uncollateralizedNegativeExposure[vertexIndex][pathIndex] =
					pathUncollateralizedNegativeExposure[vertexIndex];
				uncollateralizedNegativeExposurePV[vertexIndex][pathIndex] =
					pathUncollateralizedNegativeExposurePV[vertexIndex];
				creditExposure[vertexIndex][pathIndex] = pathCreditExposure[vertexIndex];
				creditExposurePV[vertexIndex][pathIndex] = pathCreditExposurePV[vertexIndex];
				debtExposure[vertexIndex][pathIndex] = pathDebtExposure[vertexIndex];
				debtExposurePV[vertexIndex][pathIndex] = pathDebtExposurePV[vertexIndex];
				fundingExposure[vertexIndex][pathIndex] = pathFundingExposure[vertexIndex];
				fundingExposurePV[vertexIndex][pathIndex] = pathFundingExposurePV[vertexIndex];
			}
		}

		try
		{
			return new org.drip.xva.gross.ExposureAdjustmentDigest (
				pathCOLVA,
				pathFTDCOLVA,
				pathUCVA,
				pathFTDCVA,
				pathCVA,
				pathCVACL,
				pathDVA,
				pathFVA,
				pathFDA,
				pathFCA,
				pathFBA,
				pathSFVA,
				pathTotalVA,
				collateralizedExposure,
				collateralizedExposurePV,
				collateralizedPositiveExposure,
				collateralizedPositiveExposurePV,
				collateralizedNegativeExposure,
				collateralizedNegativeExposurePV,
				uncollateralizedExposure,
				uncollateralizedExposurePV,
				uncollateralizedPositiveExposure,
				uncollateralizedPositiveExposurePV,
				uncollateralizedNegativeExposure,
				uncollateralizedNegativeExposurePV,
				creditExposure,
				creditExposurePV,
				debtExposure,
				debtExposurePV,
				fundingExposure,
				fundingExposurePV
			);
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
}

package org.drip.xva.csatimeline;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2018 Lakshmi Krishnamurthy
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
 * LastFlowDates holds the Last Client/Dealer Margin Flow and Trade Flow Dates using the Parameterization
 *  laid out in Andersen, Pykhtin, and Sokol (2017). The References are:
 *  
 *  - Andersen, L. B. G., M. Pykhtin, and A. Sokol (2017): Re-thinking Margin Period of Risk,
 *  	https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2902737, eSSRN.
 *  
 *  - Andersen, L. B. G., M. Pykhtin, and A. Sokol (2017): Credit Exposure in the Presence of Initial Margin,
 *  	https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2806156, eSSRN.
 *  
 *  - Anfuso, F., D. Aziz, P. Giltinan, and K Loukopoulus (2017): A Sound Modeling and Back-testing Framework
 *  	for Forecasting Initial Margin Requirements,
 *  	https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2716279, eSSRN.
 *  
 *  - BCBS (2015): Margin Requirements for Non-centrally Cleared Derivatives,
 *  	https://www.bis.org/bcbs/publ/d317.pdf.
 *  
 *  - Pykhtin, M. (2009): Modeling Credit Exposure for Collateralized Counter-parties, Journal of Credit
 *  	Risk, 5 (4) 3-27.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class LastFlowDates
{
	private org.drip.analytics.date.JulianDate _spot = null;
	private org.drip.analytics.date.JulianDate _valuation = null;
	private org.drip.analytics.date.JulianDate _clientTrade = null;
	private org.drip.analytics.date.JulianDate _dealerTrade = null;
	private org.drip.analytics.date.JulianDate _clientMargin = null;
	private org.drip.analytics.date.JulianDate _dealerMargin = null;

	/**
	 * Generate a LastFlowDates Instance from the Spot Date and the AndersenPykhtinSokolLag
	 * 
	 * @param spot The Spot Date
	 * @param andersenPykhtinSokolLag AndersenPykhtinSokolLag Instance
	 * @param calendarSet The Business Day Calendar Set
	 * 
	 * @return The LastFlowDates Instance
	 */

	public static final LastFlowDates SpotStandard (
		final org.drip.analytics.date.JulianDate spot,
		final org.drip.xva.csatimeline.AndersenPykhtinSokolLag andersenPykhtinSokolLag,
		final java.lang.String calendarSet)
	{
		if (null == spot || null == andersenPykhtinSokolLag)
		{
			return null;
		}

		org.drip.analytics.date.JulianDate clientMarginFlowDate = spot.subtractBusDays (
			andersenPykhtinSokolLag.clientMarginFlowGap(),
			calendarSet
		);

		try
		{
			return new LastFlowDates (
				clientMarginFlowDate.subtractBusDays (
					1,
					calendarSet
				),
				clientMarginFlowDate,
				spot.subtractBusDays (
					andersenPykhtinSokolLag.dealerMarginFlowGap(),
					calendarSet
				),
				spot.subtractBusDays (
					andersenPykhtinSokolLag.clientTradeFlowGap(),
					calendarSet
				),
				spot.subtractBusDays (
					andersenPykhtinSokolLag.dealerTradeFlowGap(),
					calendarSet
				),
				spot
			);
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * LastFlowDates Constructor
	 * 
	 * @param valuation The Margin Collateral Valuation Date
	 * @param clientMargin The Last Client Margin Flow (Observation) Date
	 * @param dealerMargin The Last Dealer Margin Flow (Observation) Date
	 * @param clientTrade The Last Client Trade Flow (Settlement) Date
	 * @param dealerTrade The Last Dealer Trade Flow (Settlement) Date
	 * @param spot TheSpot Date
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LastFlowDates (
		final org.drip.analytics.date.JulianDate valuation,
		final org.drip.analytics.date.JulianDate clientMargin,
		final org.drip.analytics.date.JulianDate dealerMargin,
		final org.drip.analytics.date.JulianDate clientTrade,
		final org.drip.analytics.date.JulianDate dealerTrade,
		final org.drip.analytics.date.JulianDate spot)
		throws java.lang.Exception
	{
		if (null == (_valuation = valuation) ||
			null == (_clientMargin = clientMargin) ||
			null == (_dealerMargin = dealerMargin) ||
			null == (_clientTrade = clientTrade) ||
			null == (_dealerTrade = dealerTrade) ||
			null == (_spot = spot))
		{
			throw new java.lang.Exception ("LastFlowDates Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Valuation Date
	 * 
	 * @return The Valuation Date
	 */

	public org.drip.analytics.date.JulianDate valuation()
	{
		return _valuation;
	}

	/**
	 * Retrieve the Last Client Margin Flow (Observation) Date
	 * 
	 * @return The Last Client Margin Flow (Observation) Date
	 */

	public org.drip.analytics.date.JulianDate clientMargin()
	{
		return _clientMargin;
	}

	/**
	 * Retrieve the Last Dealer Margin Flow (Observation) Date
	 * 
	 * @return The Last Dealer Margin Flow (Observation) Date
	 */

	public org.drip.analytics.date.JulianDate dealerMargin()
	{
		return _dealerMargin;
	}

	/**
	 * Retrieve the Last Client Trade Flow (Settlement) Date
	 * 
	 * @return The Last Client Trade Flow (Settlement) Date
	 */

	public org.drip.analytics.date.JulianDate clientTrade()
	{
		return _clientTrade;
	}

	/**
	 * Retrieve the Last Dealer Trade Flow (Settlement) Date
	 * 
	 * @return The Last Dealer Trade Flow (Settlement) Date
	 */

	public org.drip.analytics.date.JulianDate dealerTrade()
	{
		return _dealerTrade;
	}

	/**
	 * Retrieve the Spot Date
	 * 
	 * @return The Spot Date
	 */

	public org.drip.analytics.date.JulianDate spot()
	{
		return _spot;
	}

	/**
	 * Retrieve the ETD
	 * 
	 * @return The ETD
	 */

	public org.drip.analytics.date.JulianDate etd()
	{
		return _spot;
	}
}
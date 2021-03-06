package Portfolio;

import Utility.PortfolioUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static Utility.PortfolioUtils.getRandomWeights;

/**
 * @author Luke K. Rose <psylr5@nottingham.ac.uk>
 * @version 1.0
 * @since 0.3
 */

class SAOptimiser {

    /**
     * Optimises portfolio asset weighting, based on the Simulated Annealing method
     *
     * @param em                   Evaluation Method to use (e.g. Maximise Return, Balance Risk and Return)
     * @param initialTemperature   Starting "temperature" value use in Simulated Annealing
     * @param minimumTemperature   Final "temperature" value to end the algorithm
     * @param coolRate             Rate at which the current temperature is decreased (higher values slow the process down)
     * @param iterations           Number of iterations to run at each temperature
     * @param expectedReturns      List of average (mean) returns, one for each stock
     * @param riskCovarianceMatrix Covariance matrix of covariance between the various stocks
     * @param showDebug            True if debug information is to be printed, False if otherwise
     * @return An optimal portfolio, containing asset weightings
     */
    static Map<String, Double> optimise(ArrayList<String> stocks, PortfolioManager.EvaluationMethod em, double initialTemperature, double minimumTemperature, double coolRate, int iterations, Map<String, Double> originalPortfolio, Map<String, Double> stockPrices, Map<String, Double> expectedReturns, Map<String, Map<String, Double>> riskCovarianceMatrix, boolean showDebug) {
        double t = initialTemperature;

        Map<String, Double> solution = getRandomWeights(stocks);

        Map<String, Double> buyCost = new TreeMap<>(stockPrices);
        for (String stock : buyCost.keySet()) buyCost.put(stock, buyCost.get(stock) * 0.1);

        Map<String, Double> sellCost = new TreeMap<>(stockPrices);
        for (String stock : sellCost.keySet()) sellCost.put(stock, sellCost.get(stock) * 0.1);

        double bestFitness;
        if(em == PortfolioManager.EvaluationMethod.MAXIMISE_RETURN_MINIMISE_RISK)
            bestFitness = EvaluationFunction.getReturnToRiskRatio(solution, expectedReturns, riskCovarianceMatrix);
        else
            bestFitness = EvaluationFunction.getReturn(solution,expectedReturns);

        bestFitness -= Constraint.getTransactionCosts(originalPortfolio, solution, buyCost, sellCost);

        double currentFitness = bestFitness;
        Map<String, Double> currentSolution = new TreeMap<>(solution);

        while (t > minimumTemperature) {
            for (int i = 0; i < iterations; i++) {
                Map<String, Double> candidateSolution = PortfolioUtils.mutate(currentSolution, .5);
                double fitness;

                if (em == PortfolioManager.EvaluationMethod.MAXIMISE_RETURN_MINIMISE_RISK)
                    fitness = EvaluationFunction.getReturnToRiskRatio(candidateSolution, expectedReturns, riskCovarianceMatrix);
                else
                    fitness = EvaluationFunction.getReturn(candidateSolution, expectedReturns);

                fitness -= Constraint.getTransactionCosts(originalPortfolio, candidateSolution, buyCost, sellCost);

                if (showDebug)
                    System.out.println("ITERATION " + i + " - CURRENT FITNESS: " + currentFitness + " (RETURN: " + (EvaluationFunction.getReturn(currentSolution, expectedReturns) * 100.0) + "%) CANDIDATE FITNESS: " + fitness + " (RETURN: " + (EvaluationFunction.getReturn(candidateSolution, expectedReturns) * 100.0) + "%) BEST FITNESS: " + bestFitness + " (RETURN: " + (EvaluationFunction.getReturn(solution, expectedReturns) * 100) + "%)");

                double delta = currentFitness - fitness;
                if (delta < 0 || Math.exp(-delta / t) > Math.random()) {
                    currentFitness = fitness;
                    currentSolution = new TreeMap<>(candidateSolution);
                    if (fitness >= bestFitness) {
                        bestFitness = fitness;
                        solution = new TreeMap<>(candidateSolution);
                    }
                }
            }
            t *= coolRate;
        }
        return solution;
    }
}

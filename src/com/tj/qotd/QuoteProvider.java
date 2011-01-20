package com.tj.qotd;

import java.util.Random;

public class QuoteProvider {

	public static String[] quotations = new String[] {
		"First quotation — Me",
		"Second quotation — Me",
		"Another quotation — Me",
		"This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines This is a very long quotation running over several lines — Still Me"
	};
	public static int quoteId;

	public QuoteProvider() {
		// TODO Auto-generated constructor stub
	}

	public String getRandomQuote() {
		int nbStrings = quotations.length;
		Random rand = new Random();
        int stringIndex = rand.nextInt(nbStrings);

        quoteId = stringIndex;
        return quotations[stringIndex];
	}
	
	public String getCurrentQuote() {
		return quotations[quoteId];
	}
}

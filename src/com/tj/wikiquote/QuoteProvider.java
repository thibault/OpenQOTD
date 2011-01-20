package com.tj.wikiquote;

public class QuoteProvider {

	String[] quotations = new String[] {
		"First quotation — Me",
		"Second quotation — Me",
		"Another quotation — Met"
	};

	public QuoteProvider() {
		// TODO Auto-generated constructor stub
	}

	public String getQuote() {
		int nbStrings = quotations.length;
        int stringIndex = (int)Math.random() * nbStrings;

        return quotations[stringIndex];
	}

}

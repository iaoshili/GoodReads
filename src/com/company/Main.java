package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        String shelf = "fantasy";
        //Note that resource direct is where the html page is saved. But not properly defined;
        GoodReads books = new GoodReads(1,10,3.8,shelf);
        books.rankToHere("/Users/Greyjoy/Downloads/");
    }
}

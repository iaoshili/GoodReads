package com.company;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Greyjoy on 8/13/15.
 */
public class GoodReads {
    class Book implements Comparable<Book> {
        String bookLink;
        String title;
        String picLink;
        int ratings;
        double avgRating;

        @Override
        public String toString() {
            String result = String.format("BookLink is: %s; number of ratings is: %s", bookLink, ratings);
            return result;
        }

        public int compareTo(Book book){
            return this.ratings - book.ratings;
        }
    }

    private String shelf;
    private int startPage;
    private int endPage;
    private double scoreFloor;
    public GoodReads(int startPage, int endPage, double scoreFloor, String shelf) {
        this.startPage = startPage;
        this.endPage = endPage;
        this.scoreFloor = scoreFloor;
        this.shelf = shelf;
    }

    public void rankToHere(String savePath){
        ArrayList<Book> books = getListOfBooks();
        savePath += shelf+".html";
        StringBuffer htmlContent = new StringBuffer();

        htmlContent.append("<!DOCTYPE html>\n<html>\n<head>\n");

        htmlContent.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
        htmlContent.append("</head>\n\n<body>\n");
        htmlContent.append("<h1>GoodReads rankings</h1>" + " " + "<h3>First edition</h3>");

        int count = 0;
        for (Book book : books) {
            if (book.avgRating < scoreFloor) {
                continue;
            }
            count++;
            htmlContent.append("<p>"+String.valueOf(count)+". "
                    +"<img src=\""+book.picLink+"\">"
                    +"<a href=\""+book.bookLink+"\">"+book.title+"</a>"
                    +"得分："+String.valueOf(book.avgRating)+"分；"
                    +"喜欢："+String.valueOf(book.ratings)+"\n");
        }

        htmlContent.append("</body>");

        ProcessFile.writeToFile(htmlContent.toString(), savePath);
    }

    private void wait(int seconds){
        try {
            Thread.sleep(seconds*1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private ArrayList<Book> getListOfBooks(){
        ArrayList<Book> books = new ArrayList<Book>();
        int i = startPage;
        while (i <= endPage) {
            String url = String.format("https://www.goodreads.com/shelf/show/%s?page=%s",shelf,i);
            System.out.println("Fetching page "+i);
            ArrayList<Book> booksOfCurPage = new ArrayList<Book>();
            try {
                booksOfCurPage = getBooksOfCurPage(url,i);
                for (Book book : booksOfCurPage) {
                    books.add(book);
                }
                i++;
            }
            catch (IOException e){
                System.out.println(e.toString());
            }
            catch (Exception e){
                System.out.println(e.toString());
                System.out.println("Well, I crawl too often? Let's stop here");
                break;
            }

        }
        Collections.sort(books);
        Collections.reverse(books);
        return books;
    }

    private ArrayList<Book> getBooksOfCurPage(String url,int page) throws IOException{
        ArrayList<Book> books = new ArrayList<Book>();
        Document doc = null;
        String resourceDir = "/Users/Greyjoy/Documents/Book/Resource/";
        String filePath = resourceDir+shelf+page+".html";
        File f = new File(filePath);
        if (f.exists()) doc = Jsoup.parse(f, "UTF-8", "");
        else {
            doc = Jsoup.connect(url).get();
            ProcessFile.writeToFile(doc.outerHtml(),filePath);
        }
        Elements titleLinks = doc.select("a[href][title].leftAlignedImage");
        for (Element titleLink: titleLinks) {
            Book book = new Book();
            book.title = titleLink.attr("title");
            book.bookLink = "https://www.goodreads.com/"+titleLink.attr("href");
            book.picLink = "";
            books.add(book);
        }

        Elements ratingLinks = doc.select("span.greyText.smallText:contains(ratings)");
        for (int i = 0; i < titleLinks.size(); i++) {
            Element ratingLink = ratingLinks.get(i);
            Book book = books.get(i);
            String str = ratingLink.text();
            List ratingInfos = Arrays.asList(str.trim().split("—"));

            String avgRatingStr = (String) ratingInfos.get(0);
            avgRatingStr = avgRatingStr.replaceAll("[^0-9.]", " ");
            book.avgRating = Double.parseDouble(avgRatingStr.trim());

            String ratingStr = (String) ratingInfos.get(1);
            ratingStr = ratingStr.replaceAll("[^0-9,]", " ");
            ratingStr = ratingStr.replaceAll(",", "");
            book.ratings = Integer.parseInt(ratingStr.trim());
        }
        return books;
    }
}

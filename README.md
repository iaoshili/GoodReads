# GoodReads
GoodReads ranking

Programming language: Java.
Dependency: http://jsoup.org/ (You do need this dependency to get this work).

The purpose of this program is to crawl the data from Amazon goodreads.com based on any bookshelf you like. (Change the bookshelf name in main.java) And produce a result in html. Some examples are put into the example output folder.

Ranking based on the following rules:

1 Book with score less than a certain threshold is throwed away. (I prefer 7.6)

2 Books are ranked according to the number of views.

That's it, have fun!

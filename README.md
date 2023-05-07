# Read Me First

This API allows to find the most related questions to your question, and get the top of questions by their length.

Question Search API uses the Java 17, Spring Boot 3, Open API 3.0, H2 database. 

For starting the method to find the most related questions to your question,
you need to write your question and write the count of questions, which will be displayed.
The app find questions, which start by the first your word, and find the most related by using Levenshtein Distance.
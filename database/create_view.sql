-- Creating a view for the top 20 rated movies --
CREATE VIEW top20_movies AS SELECT m.*, r.rating FROM movies m INNER JOIN ratings r ON r.movieId = m.id ORDER BY r.rating DESC LIMIT 20;

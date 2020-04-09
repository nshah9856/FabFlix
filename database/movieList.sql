-- Query for movies table information
SELECT m.*, s.name as star, g.name as genre
FROM top20_movies m
INNER JOIN stars_in_movies sm ON sm.movieId = m.id
INNER JOIN genres_in_movies gm ON gm.movieId = m.id
INNER JOIN stars s ON s.id = sm.starId
INNER JOIN genres g ON  g.id = gm.genreId
;

-- Creating a view for the top 20 rated movies
-- CREATE VIEW top20_movies AS SELECT m.*, r.rating FROM movies m INNER JOIN ratings r ON r.movieId = m.id ORDER BY r.rating DESC LIMIT 20;
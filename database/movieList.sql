-- Query for movies table information
SELECT m.*, s.name as star, g.name as genre
FROM top20_movies m
INNER JOIN stars_in_movies sm ON sm.movieId = m.id
INNER JOIN genres_in_movies gm ON gm.movieId = m.id
INNER JOIN stars s ON s.id = sm.starId
INNER JOIN genres g ON  g.id = gm.genreId
;

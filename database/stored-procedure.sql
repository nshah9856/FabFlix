USE moviedb; 
-- TODO: change database later ^
DROP procedure IF EXISTS add_movie;

-- Change DELIMITER to $$ 
DELIMITER $$ 
USE moviedb $$
CREATE PROCEDURE add_movie (
    IN movieTitle VARCHAR(100),
    IN movieYear INTEGER,
    IN movieDirector VARCHAR(100), 
    IN  starName VARCHAR(100), 
    IN genreName VARCHAR(32))
    am: BEGIN
        -- movie exists: no changes made
        IF ((SELECT COUNT(*) FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDirector) > 0) THEN
            SELECT 'FAILURE: Movie already exists. No changes have been made.' as message;
            LEAVE am;
        END IF;
        -- generate movie id
        SET @movieId = concat("tt",(select max(substring(id, 3)) from movies) + 1);
        INSERT INTO movies(id, title, year, director, price) VALUES (@movieId, movieTitle, movieYear, movieDirector, ROUND(RAND()*9 + 1,2));
        
-- genre
        -- if genre does not exist
        IF ((SELECT COUNT(*) FROM genres WHERE name = genreName) = 0) THEN
        -- create genre
            SET @genreId = (SELECT MAX(id) FROM genres) + 1;
            INSERT INTO genres(id, name) VALUES (@genreId, genreName);
            -- SELECT CONCAT("Added new genre ", genreName) as message;
        END IF;

        -- link genre to movie
        INSERT INTO genres_in_movies(genreId, movieId) VALUES ((SELECT id FROM genres WHERE name = genreName), @movieId);
        -- SELECT CONCAT("Added genre ", genreName, " with movieId ", @movieId, " to genres_in_movies table.") as message;
 
-- star
        -- if star does not exist
        IF ((SELECT COUNT(*) FROM stars WHERE name = starName) = 0) THEN
        -- create star  *TODO*: star ID
            -- create star and add to stars table
            SET @starId = concat("nm",(select max(substring(id, 3)) from stars) + 1);
            INSERT INTO stars(id, name, birthYear) VALUES (@starId, starName, null);
            -- SELECT CONCAT("Added new star ", starName) as message;
        END IF;

        -- link star to movie; limit 1 in case more than 1 star is found with same name
          INSERT INTO stars_in_movies(starId, movieId) VALUES ((SELECT id FROM stars WHERE name = starName LIMIT 1), @movieId);

    SET @movieId = (SELECT id FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDirector LIMIT 1);
    SET @starId = (SELECT id FROM stars WHERE name = starName LIMIT 1);
    SET @genreId = (SELECT id FROM genres WHERE name = genreName LIMIT 1);
    SELECT concat('SUCCESS: MovieId : ',@movieId, ', StarId : ', @starId, ' Genre Id : ', @genreId) as message;
END
$$
-- Change back DELIMITER to ; 
DELIMITER ;

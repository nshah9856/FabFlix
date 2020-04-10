const fetchMovies = async () => {
    const data = await fetch(
        "movies",
        {
            method:"GET",
            headers:{
                'Content-Type': 'application/json'
            }
        }
    )

    const json = await data.json()

    return json
}

const handleMoviesResult = data => {
    const movie_data = {}
    data.forEach(
        ({movie_id, movie_director, movie_year, movie_title, movie_genre, movie_star, movie_rating}) => {
            const genres = movie_data[movie_id] ? [...movie_data[movie_id].movie_genre, movie_genre] : [movie_genre]
            const stars = movie_data[movie_id] ? [...movie_data[movie_id].movie_star, movie_star] : [movie_star]

            movie_data[movie_id] = {
                movie_id,
                movie_director,
                movie_year,
                movie_title,
                movie_genre: Array.from(new Set(genres)),
                movie_star: Array.from(new Set(stars)),
                movie_rating
            }
        }
    )

    const rows = Object.values(movie_data).map(
        ({movie_id, movie_director, movie_year, movie_title, movie_genre, movie_star, movie_rating}) => {
            const aTag = `<a href="movie.html?id=${movie_id}">${movie_title}</a>`
            return `
                <tr>
                    <th>${aTag}</th>
                    <th>${movie_year}</th>
                    <th>${movie_director}</th>
                    <th>
                    ${
                        movie_genre.slice(0,3).map(genre => `${genre}`).join(', ')
                    }
                    </th>
                    <th>
                    ${
                        movie_star.slice(0,3).map(star => `${star}`).join(', ')
                    }
                    </th>
                    <th>${movie_rating}</th>
                </tr>
            `
        }
    )

    document.getElementById('movies_body').innerHTML = rows.join('')
}


fetchMovies().then(handleMoviesResult)

window.onscroll = e => {
  console.log("Whatever")
  const floatingButton = document.getElementById('floating-button')
  e.target.documentElement.scrollTop > document.getElementById('header').scrollHeight ?
    floatingButton.style.opacity = 1 :
    floatingButton.style.opacity = 0
}
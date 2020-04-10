const getParameters = () => {
    const currLocation = window.location.href
    const searchQuery = currLocation.split('?')[1].split('&')
    const queries = {}
    searchQuery.forEach(
        val => {
            const [left, right] = val.split('=')
            queries[left] = right
        }
    )
    return queries
}

const fetchMovieDetail = async () => {
    const id = getParameters()["id"]
    const data = await fetch(
        `api/movie?id=${id}`,
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
    console.log(data)
    const movie_data = {}
    data.forEach(
        ({movie_id, movie_director, movie_year, movie_title, movie_genres, movie_stars, movie_star_id, movie_ratings}) => {
            const genres = movie_data[movie_id] ? [...movie_data[movie_id].movie_genre, movie_genres] : [movie_genres]
            const stars = movie_data[movie_id] ? [...movie_data[movie_id].movie_star, {name: movie_stars, id: movie_star_id}] : [{name: movie_stars, id: movie_star_id}]

            movie_data[movie_id] = {
                movie_id,
                movie_director,
                movie_year,
                movie_title,
                movie_genre: Array.from(new Set(genres)),
                movie_star: Array.from(new Set(stars)),
                movie_ratings
            }
        }
    )

    const rows = Object.values(movie_data).map(
        ({movie_director, movie_year, movie_title, movie_genre, movie_star, movie_ratings}) => {
            return `
                <tr>
                    <th>${movie_title}</th>
                    <th>${movie_year}</th>
                    <th>${movie_director}</th>
                    <th>
                    ${
                movie_genre.slice(0,3).map(genre => `${genre}`).join(', ')
            }
                    </th>
                    <th>
                    ${
                movie_star.slice(0,3).map(({name,id}) => `<a href=star?id=${id}>${name}</a>`).join(', ')
            }
                    </th>
                    <th>${movie_ratings}</th>
                </tr>
            `
        }
    )

    document.getElementById('movie_body').innerHTML = rows.join('')
}


fetchMovieDetail()
    .then(handleMoviesResult)
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
    const rows = data.map(
        ({movie_id, movie_director, movie_year, movie_title}) => {
            const aTag = `<a href="movie?id=${movie_id}">${movie_title}</a>`
            return `
                <tr>
                    <th>${aTag}</th>
                    <th>${movie_year}</th>
                    <th>${movie_director}</th>
                </tr>
            `
        }
    )

    document.getElementById('movies_body').innerHTML = rows.join('')
}


fetchMovies().then(handleMoviesResult)
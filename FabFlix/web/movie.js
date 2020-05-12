const fetchGenres = async () => {
    const data = await fetch(
        `api/genres`,
        {
            method:"GET",
            headers:{
                'Content-Type': 'application/json'
            }
        }
    )
    const json = await data.json()

    const ele = document.getElementById('genres')
    const titleEle = document.getElementById('titles-numeric')
    const titleAlphaEle = document.getElementById('titles-alpha')

    json.forEach(({genre_id, genre_name}, index) => {
        ele.innerHTML += `<li style="display: inline; padding: 10px; cursor:pointer;"><a onclick="genreClick(${genre_id})" name="genre" style="color: inherit;">${genre_name}</a></li>`
    });

    [...Array(10)].forEach( (l, i) => {
        titleEle.innerHTML += `<li style="display: inline; padding: 10px; cursor: pointer"><a onclick="titleClick(${i})" name="title" style="color: inherit;">${i}</a></li>`
    })

    for (let i = 65; i <= 90; i++) {
        const s = String.fromCharCode(i)
        titleAlphaEle.innerHTML += `<li style="display: inline; padding: 10px; cursor: pointer"><a onclick="titleClick('${s}')" name="title" style="color: inherit;">${s}</a></li>`
    }
    titleAlphaEle.innerHTML += `<li style="display: inline; padding: 10px; cursor: pointer"><a onclick="titleClick('*')" name="title" style="color: inherit;">*</a></li>`

}

const genreClick = genre_id => {
    forwardSearch({genre: genre_id, genre_search: true})
}

const titleClick = title => {
    forwardSearch({title: title, title_search: true})
}

const forwardSearch = search => {
    const queryString = Object.keys(search).map(key => key + '=' + search[key]).join('&');
    console.log(queryString)
    window.location = `movieList.html?${queryString}`
}

const handleSearch = event => {
    event.preventDefault();
    const FormValues = event.target.elements
    const title = FormValues['title']
    const year = FormValues['year']
    const director = FormValues['director']
    const star = FormValues['star']

    const search = {}

    if (title && title.value.length > 0){
        search['title'] = title.value
    }
    if(year && year.value.length > 0){
        search['year'] = year.value
    }
    if(director && director.value.length > 0){
        search['director'] = director.value
    }
    if(star && star.value.length > 0){
        search['star'] = star.value
    }

    // console.log(search)
    forwardSearch(search)
    // console.log("Title", title.value, "Year", year.value, "Director", director.value, "Star", star.value)
}

document.getElementById('title-search').addEventListener('submit', handleSearch)
document.getElementById('advance-search').addEventListener('submit', handleSearch)

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
    console.log(data)
    const json = await data.json()
    console.log(json)
    return json
}

const handleMoviesResult = data => {
    const movie_data = {}
    data.forEach(
        ({movie_id, movie_director, movie_year, movie_title, movie_price, movie_genres,movie_genre_ids, movie_stars, movie_star_id, movie_ratings}) => {
            // const genres = movie_data[movie_id] ? [...movie_data[movie_id].movie_genre, movie_genres] : [movie_genres]
            // const stars = movie_data[movie_id] ? [...movie_data[movie_id].movie_star, {name: movie_stars, id: movie_star_id}] : [{name: movie_stars, id: movie_star_id}]
            const titles = movie_stars.split(',')
            const genres = movie_genres.split(',')
            movie_data[movie_id] = {
                movie_id,
                movie_director,
                movie_year,
                movie_title,
                movie_price,
                movie_genre: movie_genre_ids.split(',').map((id,index) => ({id:id, name:genres[index]})),
                movie_star: movie_star_id.split(',').map((id,index) => ({id:id, name:titles[index]})),
                movie_ratings: movie_ratings ? movie_ratings : 'N/A'

            }
        }
    )

    const rows = Object.values(movie_data).map(
        ({movie_id, movie_director, movie_year, movie_title, movie_price, movie_genre, movie_star, movie_ratings}) => {
            return `
                <tr>
                    <th>
                        ${movie_title}
                        <a class="search_icon" onclick="addButtonClick('${movie_id}', '${movie_title}', '${movie_price}')" style="height: 25px;width: 25px"><i class="fas fa-plus" style="color: #e74c3c"></i></a>
                    </th>
                    <th>${movie_year}</th>
                    <th>${movie_director}</th>
                    <th>
                    ${
              movie_genre.map(({name,id}) => `<a href=movieList.html?genre=${id}&genre_search=true>${name}</a>`).join(', ')
            }
                    </th>
                    <th>
                    ${
                movie_star.map(({name,id}) => `<a href=star.html?id=${id}>${name}</a>`).join(', ')
            }
                    </th>
                    <th>${movie_ratings}</th>
                </tr>
            `
        }
    )

    document.getElementById('movie_body').innerHTML = rows.join('')
}

const addButtonClick = (movie_id,movie_title, movie_price) => {
    window.location.href = `addItem.html?id=${movie_id}&title=${movie_title}&price=${movie_price}`
}


window.onload = async event => {
    await fetchGenres()
    fetchMovieDetail()
        .then(handleMoviesResult)
    document.getElementById('movieListPage').href = "movieList.html?" + localStorage.getItem("searchParameter") + '&single_page=1'
}
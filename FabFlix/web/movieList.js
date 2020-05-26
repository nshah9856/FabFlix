let params;
let page;
let total_pages;

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

    // ////console.log(search)
    forwardSearch(search)
    // ////console.log("Title", title.value, "Year", year.value, "Director", director.value, "Star", star.value)
}

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
    ////console.log(queryString)
    window.location = `movieList.html?${queryString}`
}

const fetchMovies = async () => {
    const is_genre = params.get('genre_search')
    const only_title = params.get('title_search')
    let data;
    if(is_genre === 'true' || only_title === 'true'){
        data = await fetch(
            `api/search?${params.toString()}`,
            {
                method:"GET",
                headers:{
                    'Content-Type': 'application/json'
                }
            }
        )
    }
    else{
        data = await fetch(
            `api/search?${params.toString()}`,
            {
                method:"GET",
                headers:{
                    'Content-Type': 'application/json'
                }
            }
        )
    }

    const json = await data.json()
    ////console.log("Result! ")
    ////console.log(json)
    return json
}
const handleLookup = (query, doneCallback) => {
    //console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here
    if(query.length < 3) return;
    console.log("autocomplete initiated")
    const data = localStorage.getItem(query)
    if(data) {
        console.log("using cached results")
        handleLookupAjaxSuccess(JSON.parse(data),query,doneCallback)
        return;
    }
    console.log("sending AJAX request to backend Java Servlet")

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/autocomplete?query=" + escape(query),
        "success": data => {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            //console.log("lookup ajax error")
            //console.log(errorData)
        }
    })
}

const handleLookupAjaxSuccess = (data, query, doneCallback) => {
    //console.log("lookup ajax successful")

    //console.log(typeof data)
    // parse the string into JSON
    var jsonData = $.parseJSON(JSON.stringify(data));
    //console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here
    localStorage.setItem(query,JSON.stringify(data))
    console.log("Suggestion list")
    console.log(data)
    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}

const handleSelectSuggestion = (suggestion) => {
    // TODO: jump to the specific result page based on the selected suggestion

    //console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["id"])
    window.location.href = `movie.html?id=${suggestion["data"]["id"]}`
}


document.getElementById('title-search').addEventListener('submit', handleSearch)
document.getElementById('advance-search').addEventListener('submit', handleSearch)
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: (query, doneCallback) => {
        handleLookup(query, doneCallback)
    },
    onSelect: (suggestion) => {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});

$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        forwardSearch({title: $('#autocomplete').val(), filter_search: true})
    }
})

const handleMoviesResult = data => {
    const movie_data = {}
    //console.log(data)
    data.forEach(
        ({movie_id, movie_director, movie_year, movie_title, movie_genres, movie_price, movie_stars_name, movie_stars_id, movie_rating}) => {
            movie_data[movie_id] = {
                movie_id,
                movie_director,
                movie_year,
                movie_title,
                movie_price,
                movie_genres: movie_genres ? movie_genres.split(',') : [],
                movie_stars: movie_stars_name ? movie_stars_name.split(',') : [],
                movie_stars_id: movie_stars_id ? movie_stars_id.split(','): [],
                movie_rating: movie_rating ? movie_rating : 'N/A'
            }
        }
    )

    const rows = Object.values(movie_data).map(
        ({movie_id, movie_director, movie_year, movie_title, movie_price, movie_genres, movie_stars, movie_stars_id, movie_rating}) => {
            const aTag = `<div>
                            <a href="movie.html?id=${movie_id}" style="color:#86a9c7">${movie_title}</a>
                            <a class="search_icon" onclick="addButtonClick('${movie_id}', '${movie_title}', '${movie_price}')" style="height: 25px;width: 25px"><i class="fas fa-plus" style="color: #e74c3c"></i></a>
                        </div>
             `
            return `
                <tr>
                    <th>${aTag}</th>
                    <th>${movie_year}</th>
                    <th>${movie_director}</th>
                    <th>
                    ${
                movie_genres.slice(0,3).map(genre => `${genre}`).join(', ')
            }
                    </th>
                    <th>
                    ${
                movie_stars.slice(0,3).map((name,index) => `<a href="star.html?id=${movie_stars_id[index]}" style="color:#86a9c7">${name}</a>`).join(', ')
            }
                    </th>
                    <th>${movie_rating}</th>
                </tr>
            `
        }
    )
    //console.log("rows", rows.length)

    document.getElementById('movies_body').innerHTML = rows.join('')
    localStorage.setItem("searchParameter", params.toString())
}

const addButtonClick = (movie_id,movie_title, movie_price) => {
    window.location.href = `addItem.html?id=${movie_id}&title=${movie_title}&price=${movie_price}`
}

const displayTitleSort = () => {
    const sortDirection = params.get('title_sort')
    const descSort = document.getElementById('title_desc')
    const ascSort = document.getElementById('title_asc')

    if(!sortDirection){
        ascSort.style.color = 'inherit';
        descSort.style.color = 'inherit';
    }

    else if(sortDirection === 'desc'){
        descSort.style.color = '#e74c3c';
        ascSort.style.color = 'inherit';
    }

    else{
        ascSort.style.color = '#e74c3c';
        descSort.style.color = 'inherit';
    }
}

const displayRatingSort = () => {
    const sortDirection = params.get('rating_sort')
    const descSort = document.getElementById('rating_desc')
    const ascSort = document.getElementById('rating_asc')

    if(!sortDirection){
        ascSort.style.color = 'inherit';
        descSort.style.color = 'inherit';
    }

    else if(sortDirection === 'desc'){
        descSort.style.color = '#e74c3c';
        ascSort.style.color = 'inherit';
    }

    else{
        ascSort.style.color = '#e74c3c';
        descSort.style.color = 'inherit';
    }
}

const handleTitleFirst = () => {
    if(params.get('rating_first')){
       params.delete('rating_first')
        document.getElementById('title_first').style.color = "rgb(60, 231, 106)"
        document.getElementById('rating_first').style.color = "inherit"

    }else{
        document.getElementById('title_first').style.color = "inherit"
        document.getElementById('rating_first').style.color = "rgb(60, 231, 106)"
    }
    window.location.search = params.toString()
}
const handleTitleSort = (newSort) => {
    params.set('title_sort', newSort)
    window.location.search = params.toString()
}

const handleRatingFirst = () => {
    if(!params.get('rating_first')){
        params.set('rating_first', "1")
        document.getElementById('rating_first').style.color = "rgb(60, 231, 106)"
        document.getElementById('title_first').style.color = "inherit"
    }else{
        document.getElementById('rating_first').style.color = "inherit"
        document.getElementById('title_first').style.color = "rgb(60, 231, 106)"
    }
    window.location.search = params.toString()
}
const handleRatingSort = (newSort) => {
    params.set('rating_sort', newSort)
    window.location.search = params.toString()
}

const handleLimit = (event) => {
    params.set('limit', event.target.value)
    params.delete('offset') //reset offset when setting new limit
    //recalculate offset?
    window.location.search = params.toString()
}

const handlePagination = async () => {
    const data = await fetch(
      `api/pagecount?${params.toString()}`,
      {
          method:"GET",
          headers:{
              'Content-Type': 'application/json'
          }
      }
    )
    const json = await data.json()

    const ele = document.getElementById('page')

    const limit = params.get('limit')
    total_pages = Math.ceil(parseInt(json.title_count) / limit)

    //console.log("Total rows", json.title_count)
    //console.log("Pages", total_pages)

    const pages = []
    pages.push(`<li class="page-item"><a class="page-link" id="previousButton" onclick="previousClick()" style="display:none; color: #e74c3c">Previous</a></li>`)
    for(let i = 1; i <= total_pages; ++i){
        pages.push(`
                <li class="page-item"><a class="page-link" style="color: #e74c3c" onclick="pageClick(${i})">${i}</a></li>
        `)
    }
    pages.push(`<li class="page-item"><a class="page-link" id = "nextButton" onclick="nextClick()" style="display:none; color: #e74c3c">Next</a></li>`)

    ele.innerHTML += pages.join(' ')
}

const previousClick = () => {
    pageClick(page-1)
}

const nextClick = () => {
    //console.log("current ", page)
    pageClick(page+1)
}

const pageClick = p => {
    page = p;
    //console.log("after", page)
    const limit = parseInt(params.get("limit"))
    params.set("offset", (page-1)*limit)
    window.location.search = params.toString()
}

document.getElementById("title_first").addEventListener("click", handleTitleFirst)
document.getElementById("title_desc").addEventListener("click", () => handleTitleSort('desc'))
document.getElementById("title_asc").addEventListener("click", () => handleTitleSort('asc'))
document.getElementById("rating_first").addEventListener("click", handleRatingFirst)
document.getElementById("rating_desc").addEventListener("click", () => handleRatingSort('desc'))
document.getElementById("rating_asc").addEventListener("click", () => handleRatingSort('asc'))
document.getElementById('title-search').addEventListener('submit', handleSearch)
document.getElementById('advance-search').addEventListener('submit', handleSearch)
document.getElementById('page').addEventListener('change', handlePagination)
document.getElementById('limitSelector').addEventListener('change', handleLimit)


window.onload = async event => {
    //put prev/next fetchPageCount or something
    params = (new URL(document.location)).searchParams;
    document.getElementById('limitSelector').value = params.get("limit")

    await new Promise((resolve) => {
        if(!params.get("limit")){
            params.set("limit", "25") //default limit to 25!
            document.getElementById('limitSelector').value = "25"
        }
        if(!params.get("offset")){
            params.set("offset",0)
        }
        if(params.get("rating_first")){
            document.getElementById('rating_first').style.color = "rgb(60, 231, 106)"
        }else{
            document.getElementById('title_first').style.color = "rgb(60, 231, 106)"
        }
        resolve()
    })

    fetchMovies().then(handleMoviesResult)
    fetchGenres()
    handlePagination().then(
        () => {
            const offset = parseInt(params.get("offset"))
            if(offset > 0)
                document.getElementById("previousButton").style.display = "block";
            else{
                document.getElementById("previousButton").style.display = "none";
            }
            if(offset < ((total_pages -1)*parseInt(params.get("limit"))))
                document.getElementById("nextButton").style.display = "block";
            else{
                document.getElementById("nextButton").style.display = "none";
            }
            page =  (parseInt(params.get("offset")) / parseInt(params.get("limit"))) + 1
        }
    )
    displayTitleSort()
    displayRatingSort()
    if(params.get("single_page")){
        params.delete("single_page")
    }
}
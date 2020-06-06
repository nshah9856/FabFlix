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

const genreClick = genre_id => {
    forwardSearch({genre: genre_id, genre_search: true})
}

const titleClick = title => {
    forwardSearch({title: title, title_search: true})
}

const forwardSearch = search => {
    const queryString = Object.keys(search).map(key => key + '=' + search[key]).join('&');
    //console.log(queryString)
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

    // //console.log(search)
    forwardSearch(search)
    // //console.log("Title", title.value, "Year", year.value, "Director", director.value, "Star", star.value)
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

const fetchStarDetail = async () => {
  const id = getParameters()["id"]
  const data = await fetch(
    `api/star?id=${id}`,
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

const hadleStarResult = data => {
  const star_data = {}
  data.forEach(
    ({movie_ids, movie_titles, star_name, star_year}) => {
      // const movies = star_data[star_name] ? [...star_data[star_name].movies, {name: movie_name, id: movie_id}] : [{name: movie_name, id: movie_id}]
      const titles = movie_titles.split(',')
      star_data[star_name] = {
        star_name,
        star_year,
        movies: movie_ids.split(',').map((id,index) => ({id:id, name:titles[index]})),
      }
    }
  )

  const rows = Object.values(star_data).map(
    ({star_name, star_year, movies}) => {
      return `
                <tr>
                    <th>${star_name}</th>
                    <th>${star_year ? star_year : 'N/A'}</th>
                    <th>
                    ${
        movies.map(({name,id}) => `<a href=movie.html?id=${id}>${name}</a>`).join(', ')
        }
                    </th>
                </tr>
            `
    }
  )

  document.getElementById('star_body').innerHTML = rows.join('')
}

window.onload = async event => {
    await fetchGenres()
    fetchStarDetail()
        .then(hadleStarResult)
    document.getElementById('movieListPage').href = "movieList.html?" + localStorage.getItem("searchParameter") + '&single_page=1'
}
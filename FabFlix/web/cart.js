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

const fetchCart = async () => {
    const data = await fetch(
        `api/cart`,
        {
            method: "GET",
            headers: {
                'Content-Type': 'application/json'
            }
        }
    )
    return data.json()
}

document.getElementById('title-search').addEventListener('submit', handleSearch)
document.getElementById('advance-search').addEventListener('submit', handleSearch)

const displayCart = (json) => {
    const cart = document.getElementById("cart")

    const rows = json.map(
        ({movie_id, movie_title, movie_price, movie_quantity}) => {
            return `
                <tr>
                    <th style="width: 1em">
                        <a class="search_icon" onclick="removeItem('${movie_id}')" style="height: 30px; width: 30px; float: left">
                            <i class="far fa-trash-alt" style="color: #e74c3c"></i>
                         </a>
                    </th>
                    <th>
                        ${movie_title}
                        <input type="number" onchange="quantityChange(this,this.value, '${movie_id}', '${movie_price}')" value="${movie_quantity}" style="float: right; width: 3em;"/>
                    </th>
                    <th id="price_${movie_id}">${parseFloat(movie_price) * parseInt(movie_quantity)}</th>
                </tr>
            `
        }
    )
    cart.innerHTML += rows.join(' ')
}

const updateItem = async (id, newQuantity) => {
    await fetch(
        `api/cart?id=${id}&quantity=${newQuantity}`,
        {
            method:"POST",
            headers:{
                'Content-Type': 'application/json'
            }
        }
    )
}

const removeItem = async (id) => {
    await fetch(
        `api/cart?id=${id}&remove=1`,
        {
            method:"POST",
            headers:{
                'Content-Type': 'application/json'
            }
        }
    )
    window.location.reload()
}

const quantityChange = (ref, newQuantity, id, price) => {
    if(newQuantity <= 0){
        alert("Quantity must be at least 1")
        ref.value = 1
        updateItem(id,1)
    }
    else{
        updateItem(id,newQuantity)
    }
    document.getElementById(`price_${id}`).innerHTML = (parseFloat(price) * parseInt(newQuantity)).toString()
}

window.onload = async event => {
    await fetchGenres()
    const json = await fetchCart()
    //console.log(json)
    if(json.length === 0){
        document.getElementById("message").innerHTML = 'Empty Cart';
    }
    else{
        document.getElementById("table").style.display = 'table';
        document.getElementById("payment").style.display = 'block';
        displayCart(json)
    }
    document.getElementById('movieListPage').href = "movieList.html?" + localStorage.getItem("searchParameter") + '&single_page=1'
}
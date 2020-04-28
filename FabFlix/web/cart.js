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
    console.log(json)
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
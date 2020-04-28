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

const displaySales = (list) => {
    const sales = document.getElementById("sales")

    const rows = Object.values(list).map(
        (arr) => {
            const {sale_id, sale_date, movie_id, movie_quantity} = arr[0]
            return `
                <tr>
                    <th>${sale_id}</th>
                    <th>
                        ${movie_id}
                    </th>
                    <th>${movie_quantity}</th>
                    <th>${sale_date}</th>
                </tr>
            `
        }
    )
    sales.innerHTML += rows.join(' ')
}


window.onload = async event => {
    await fetchGenres()
    const sales = localStorage.getItem("order")
    if(!sales){
        document.getElementById("message").innerHTML = "No order to place!"
        return;
    }
    document.getElementById("table").style.display = "table"
    displaySales(JSON.parse(sales))
    localStorage.removeItem("order")
}
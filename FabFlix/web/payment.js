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

const submitPaymentForm = (event) => {
  event.preventDefault();
  const id = document.getElementById('cardNumber').value.trim()
  const firstName = document.getElementById('firstName').value.trim()
  const lastName = document.getElementById('lastName').value.trim()
  const expiration = document.getElementById('expiration').value.trim()

  const sendData = {
    id, firstName, lastName, expiration
  }

  $.ajax(
    "api/payment", {
      method: "POST",
      // Serialize the login form to the data sent by POST request
      data:  sendData,
      success: handlePaymentResult
    }
  );
}

const handlePaymentResult = (resultDataString) => {
  const json = JSON.parse(resultDataString);

  if (json["status"] === "success") {
    placeOrder() // Conduct the order!
  } else {
    $("#payment_error_message").text(json["message"] + "!");
  }
}

const placeOrder = async () => {
  const data = await fetch(
      "api/sessions?customer_id&cart",
      {
        method: "GET"
      }
  )

  const {cart, customer_id} = await data.json()

  const cartItems = JSON.parse(cart)

  const ret = {}
  const results = await Promise.all(cartItems.map(async ({movie_id, movie_quantity},index) => {
    //console.log("movie id:", movie_id)

    const d = await fetch(
        `api/order?customer_id=${customer_id}&movie_id=${movie_id}&movie_quantity=${movie_quantity}`,
        {
          method: "GET"
        }
    )
    const j = await d.json()
    ret[index] = j
    return j
  }))

  //console.log(cartItems, customer_id)
  //console.log(results)

  localStorage.setItem("order", JSON.stringify(ret))
  window.location.href = "order.html"
}

document.getElementById("paymentForm").addEventListener('submit', submitPaymentForm)
document.getElementById('title-search').addEventListener('submit', handleSearch)
document.getElementById('advance-search').addEventListener('submit', handleSearch)


window.onload = async event => {
  await fetchGenres()
}
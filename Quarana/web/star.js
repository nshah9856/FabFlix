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
    ({movie_id, movie_name, star_name, star_year}) => {
      const movies = star_data[star_name] ? [...star_data[star_name].movies, {name: movie_name, id: movie_id}] : [{name: movie_name, id: movie_id}]

      star_data[star_name] = {
        star_name,
        star_year,
        movies,
      }
    }
  )

  const rows = Object.values(star_data).map(
    ({star_name, star_year, movies}) => {
      return `
                <tr>
                    <th>${star_name}</th>
                    <th>${star_year}</th>
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


fetchStarDetail()
  .then(hadleStarResult)
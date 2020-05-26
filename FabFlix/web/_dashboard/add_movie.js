const handleSubmit = async (event) => {
    event.preventDefault()

    const title = document.getElementById("title").value
    const director = document.getElementById("director").value
    const year = document.getElementById("year").value
    const genre = document.getElementById("genre").value
    const star = document.getElementById("star").value

    const errorMessage = document.getElementById("add_movie_error_message")

    //Error checking
    errorMessage.innerHTML = !title || !director || !year || !genre || !star ? "All fields are required!" : ""

    //console.log(title, director, year, genre, star)

    const data = await fetch(
        `api/addMovie?title=${title}&director=${director}&year=${year}&genre=${genre}&star=${star}`,
        {
            method:"POST",
            headers:{
                'Content-Type': 'application/json'
            }
        }
    )

    const json = await data.json()

    //console.log(json)
    if(json['message'].startsWith("SUCCESS")){ // we inserted it!
        document.getElementById('message').innerHTML = json['message']
        document.getElementById("body").style.display = 'none';
        document.getElementById('ok').style.display = 'block';

        // window.location.href = "/FabFlix/_dashboard"
    }
    else{
        errorMessage.innerHTML = json['message']
    }
};

document.getElementById("addMovieForm").addEventListener("submit", handleSubmit)
document.getElementById('ok').style.display = 'none';

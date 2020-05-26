const handleSubmit = async (event) => {
    event.preventDefault()

    const name = document.getElementById("name").value
    const year = document.getElementById("year").value
    const errorMessage = document.getElementById("add_star_error_message")

    //Error checking
    errorMessage.innerHTML = !name ? "Name is required!" : ""

    //console.log(name, year)

    const data = await fetch(
        `api/addStar?name=${name}` + `${!year ? '' : '&year='+year}`,
    {
            method:"POST",
                headers:{
            'Content-Type': 'application/json'
            }
        }
    )

    const json = await data.json()

    if(json['success']){ // we inserted it!
        document.getElementById('message').innerHTML = "New Star Id : " + json['starId'];
        document.getElementById("body").style.display = 'none';
        document.getElementById('ok').style.display = 'block';

        // window.location.href = "/FabFlix/_dashboard"
    }
    else{
        errorMessage.innerHTML = "Could not add the star in the database : ("
    }
};

document.getElementById("addStarForm").addEventListener("submit", handleSubmit)
document.getElementById('ok').style.display = 'none';

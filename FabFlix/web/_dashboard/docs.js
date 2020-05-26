const fetchMetaData = async () => {
    const data = await fetch(
        `api/metaData`,
        {
            method:"POST",
            headers:{
                'Content-Type': 'application/json'
            }
        }
    )
    const json = await data.json()

    const map = {}
    json.forEach(({table,column,type,size}) => {
         const r = map[table] ? map[table] : []
        map[table] = [{column, type, size}, ...r]
    })

    return map
}

const displayTableInfo = (data) => {
    const tableBegin = `<table class="table table-bordered">
        <thead>
        <tr>
          <th scope="col" style="max-width: 10vw">Type</th>
          <th scope="col" style="max-width: 10vw">Field</th>
        </tr>
      </thead>
      <tbody>`

    const eles = Object.keys(data).map(table => {
        const values = data[table]
        const columns = values.map(({column, type, size}) => {
            return `
               <tr>
                  <th scope="col" style="max-width: 10vw">${column}</th>
                  <th scope="col" style="max-width: 10vw">${type}(${size})</th>
                </tr>
            `
        })
        return [`
            <div style="padding: 2rem; padding-bottom: 0">
            <h4 style="color:#e74c3c">${table.substring(0,1).toUpperCase() + table.split("_").join(" ").substring(1).toLowerCase()}</h4>`,
            `${tableBegin}`, ...columns, `</tbody></table>`,
            `</div>`
        ].join("")
    })
    const content = document.getElementById("content")

    content.innerHTML += eles.join("")

}

window.onload = async () => {

    const data = await fetchMetaData()
    //console.log(data)
    displayTableInfo(data)
}
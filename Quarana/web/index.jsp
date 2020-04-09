<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <meta name="viewport"
          content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
          integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <title>FabFlix</title>

  </head>
  <body>
    <table id="movies" class="table table-bordered table-hover">
      <thead class="thead-dark">
      <tr>
        <th>Title</th>
        <th>Year</th>
        <th>Director</th>
        <th>Genre's</th>
        <th>Star's</th>
        <th>Rating</th>
      </tr>
      </thead>
      <tbody id="movies_body"></tbody>
    </table>

    <script src="index.js"></script>
  </body>
</html>

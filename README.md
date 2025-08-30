


POST: Add Movie
curl --location 'localhost:8083/movie/add' \
--header 'Content-Type: application/json' \
--data '{
    "title" : "James Bond",
    "releaseYear" : "1995"
}'

Get: Get Movie by Id
curl --location 'localhost:8083/movie/5bbcd01e-04bc-4f77-83c1-d287636ee405'

PUT: Update Movie
curl --location --request PUT 'localhost:8083/movie/update/38f4fc2a-02ac-4894-a406-79422b51a64e' \
--header 'Content-Type: application/json' \
--data '{
    "title" : "DDLJ",
    "releaseYear" : "1999"
}'

GET: Delete a movie by id
curl --location --request DELETE 'localhost:8083/movie/ee2ab202-3be0-4b76-ab32-852d61770d26'

GET: PAgination by Offset
curl --location 'localhost:8083/movie/page/offset?page=1&size=415&sort=createdAt%2Cdesc%2Cid%2Casc'

GET: Pagination By Cursor
curl --location 'localhost:8083/movie/page/seek?page=22&cursorB64=eyJ2ZXJzaW9uIjoiVjEiLCJzb3J0Ijp7Im9yZGVycyI6W3sicHJvcGVydHkiOiJjcmVhdGVkQXQiLCJhc2MiOmZhbHNlfSx7InByb3BlcnR5IjoiaWQiLCJhc2MiOnRydWV9XX0sInNpemUiOjQxNSwibmV4dFBhZ2VOdW1iZXIiOjIsInRvdGFsU2l6ZSI6NTAwLCJtb2RlIjoiT0ZGU0VUIiwicHJldiI6e30sImxhc3RJZCI6IjEzZTRiMGZkLTNjY2MtNDMwOS1iMGM1LWM0ZmZiMmRiMjlkZSJ9'

GET:Search a movie by title and release year
curl --location 'localhost:8083/movie/search?title=Dali&releaseYear=1997'


Error Sample Json - Probelm Detail:

{
    "type": "http://example.com/problems/exception-guide",
    "title": "No Movie for this condition - id or title or release year",
    "status": 400,
    "detail": "Invalid Exception - Movie Dali Year 1997 is not found",
    "instance": "/movie/search"
}

Sample Offset response:

{"item":[{"id":"18f83315-0aca-4a6a-b5e0-a50b41ff3f42","title":"Seurat","releaseYear":"2008","createdAt":"2025-08-30T10:29:51.078341"}],"nextCursor":{"version":"V1","sort":{"orders":[{"property":"createdAt","asc":false},{"property":"id","asc":true}]},"size":1,"nextPage":2,"encodedCursor":"eyJ2ZXJzaW9uIjoiVjEiLCJzb3J0Ijp7Im9yZGVycyI6W3sicHJvcGVydHkiOiJjcmVhdGVkQXQiLCJhc2MiOmZhbHNlfSx7InByb3BlcnR5IjoiaWQiLCJhc2MiOnRydWV9XX0sInNpemUiOjEsIm5leHRQYWdlTnVtYmVyIjoyLCJ0b3RhbFNpemUiOjUwMCwibW9kZSI6Ik9GRlNFVCIsInByZXYiOnt9LCJsYXN0SWQiOiIxOGY4MzMxNS0wYWNhLTRhNmEtYjVlMC1hNTBiNDFmZjNmNDIifQ=="}}


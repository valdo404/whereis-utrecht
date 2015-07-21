To launch:
sbt run

To test:
sbt test

CURL:
cat jsons/juvisy.json | curl -v --data @- -H "Content-Type: application/json" -X POST http://localhost:8080/location

Happy testing !

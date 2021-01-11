# large-graph-pathfinder
Finding Pareto Optimal Solutions in Large Graphs Using Graph Databases

**Project description:** Implementation of a path finder for a large public transport network using Neo4j NoSQL
graph database in Java
- **Public Transport Network:** Fetch a large amount of data from OpenStreetMap (OSM) containing public
transport lines (metro, train, bus, etc.) and stations
- **Graph Model:** Transform the data into a graph stored in Neo4j, implement a generalized version of Dijkstra to
omit irrelevant paths according to the geographic location of stations, and to deliver pareto optimal solutions

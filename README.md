# Todo
- [x] Place Planner package under mrt package to increase cohesion & reduce coupling
- [x] Start populating the stations list json files for debugging
- [x] implement fuzzy string matching
- [ ] Standardise the station naming. Favour the convention adopted by LTA
- [ ] Redesign json to accomodate multiple types of exits and MacPherson has different exits for different directions of the same line. Restructure each transition as an object itself
- [ ] Same Station different line don't need to transit. Think of a solution
- [ ] Encapsulate the Route object and segment objects. RoutePlanner only responsible for getting path passing it to Route. Route will split it into segments. Then RoutePlanner will convert that into human readable instructions.
- [ ] Adopt more usages of `ImmutableList` & `final` keyword where applicable

transitions: [
	{
		"name": "NEX",
		"door": [13]
	},{
		"name": "EXIT",
		"door": [1, 13, 17]
	}
]
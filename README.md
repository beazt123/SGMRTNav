# Build instructions
1. `gradlew tasks --all`. Check for buildZip option
2. `gradlew buildZip`
3. `serverless deploy function --function buddy`. If there are updates to `serverless.yml`, then do `serverless deploy`.

# Useful links
- [Java Telegram bot API documentation](https://github.com/rubenlagus/TelegramBots/wiki)
- [Telgram HTTP API](https://core.telegram.org/bots/ap)
- [Interactive Singapore MRT Map](https://mrtmapsingapore.com/)

# Todo
- [x] Place Planner package under mrt package to increase cohesion & reduce coupling
- [x] Start populating the stations list json files for debugging
- [x] implement fuzzy string matching
- [x] Redesign json to accomodate multiple types of exits and MacPherson has different exits for different directions of the same line. Restructure each transition as an object itself
- [x] Same Station different line don't need to transit. Think of a solution
- [x] Encapsulate the Route object and segment objects. RoutePlanner only responsible for getting path passing it to Route. Route will split it into segments. Then RoutePlanner will convert that into human readable instructions.
- [ ] Adopt more usages of `ImmutableList` & `final` keyword where applicable
- [ ] Add option to specify station code in inputs where station name is requested.
- [ ] Add printouts of the station code to wherever station name is printed. For now, do on both "abilities".
- [x] adjust the wrapper to use the underlying implementation.

Problem:
If only the station name, there's no guarantee which station will be chosen. 
While this doesn't affect routing problems,
does affect when choosing stations by name.


transitions: [
	{
		"name": "NEX",
		"door": [13]
	},{
		"name": "EXIT",
		"door": [1, 13, 17]
	}
]

setup:
	sbt Docker/stage
	sbt Docker/publishLocal

run: 
	docker run play-scala

down:
	docker stop play-scala
	docker rm play-scala
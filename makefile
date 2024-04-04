setup-docker:
	sbt Docker/stage

run-docker: 
	docker build . -t "play_scala" -f "target/docker/stage/Dockerfile"
	docker run "play_scala"

down-docker:
	docker stop "play_scala"
	docker rm "play_scala"
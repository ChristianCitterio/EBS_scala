setup:
	docker build . -t play-scala

run: 
	docker run play-scala

down:
	docker stop play-scala
	docker rm play-scala

ngrok:
	ngrok http localhost:9000
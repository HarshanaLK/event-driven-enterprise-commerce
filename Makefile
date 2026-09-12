.PHONY: test build up down logs topics

test:
	mvn clean verify

build:
	docker compose build

up:
	docker compose up -d --build

down:
	docker compose down

logs:
	docker compose logs -f

topics:
	bash scripts/create-topics.sh

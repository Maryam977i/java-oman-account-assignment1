.PHONY: test run db-up db-down clean

test:
	mvn test

run:
	mvn spring-boot:run

db-up:
	docker compose up -d postgres

db-down:
	docker compose down

clean:
	mvn clean

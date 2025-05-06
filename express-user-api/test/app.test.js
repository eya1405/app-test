const request = require("supertest");
const app = require("../app");
const expect = require("chai").expect;
const db = require("../database");

describe("User API Integration Tests", () => {
    beforeEach(() => {
        // Clear the database and seed it with initial data
        db.exec("DELETE FROM users");
        const insert = db.prepare(
            "INSERT INTO users (name, email) VALUES (?, ?)"
        );
        insert.run("Alice", "alice@example.com");
        insert.run("Bob", "bob@example.com");
    });

    describe("GET /api/users", () => {
        it("should return a list of users", async () => {
            const res = await request(app)
                .get("/api/users")
                .expect("Content-Type", /json/)
                .expect(200);

            expect(res.body).to.be.an("array");
            expect(res.body).to.have.lengthOf(2);
            expect(res.body[0]).to.have.property("name", "Alice");
        });
    });

    describe("GET /api/users/:id", () => {
        it("should return a user by ID", async () => {
            const user = db
                .prepare("SELECT * FROM users WHERE name = ?")
                .get("Alice");

            const res = await request(app)
                .get(`/api/users/${user.id}`)
                .expect("Content-Type", /json/)
                .expect(200);

            expect(res.body).to.have.property("id", user.id);
            expect(res.body).to.have.property("name", "Alice");
        });

        it("should return 404 if user not found", async () => {
            await request(app)
                .get("/api/users/999")
                .expect("Content-Type", /json/)
                .expect(404);
        });
    });

    describe("POST /api/users", () => {
        it("should create a new user", async () => {
            const newUser = { name: "Charlie", email: "charlie@example.com" };

            const res = await request(app)
                .post("/api/users")
                .send(newUser)
                .expect("Content-Type", /json/)
                .expect(201);

            expect(res.body).to.have.property("name", "Charlie");
            expect(res.body).to.have.property("email", "charlie@example.com");

            const dbUser = db
                .prepare("SELECT * FROM users WHERE name = ?")
                .get("Charlie");
            expect(dbUser).to.exist;
            expect(dbUser.email).to.equal("charlie@example.com");
        });
    });
});
CREATE TABLE IF NOT EXISTS contract (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
                                        final_json TEXT,
                                        missing_data TEXT,
                                        email_resp VARCHAR(255),
                                        complete VARCHAR(30)
);